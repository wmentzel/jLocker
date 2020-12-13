package com.randomlychosenbytes.jlocker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit.*;
import static com.randomlychosenbytes.jlocker.manager.Utils.encrypt;
import static com.randomlychosenbytes.jlocker.manager.Utils.unsealAndDeserializeBuildings;
import static java.lang.System.out;

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 */
public class DataManager {

    final private static DataManager instance = new DataManager();

    public static DataManager getInstance() {
        return instance;
    }

    /**
     * TODO remove
     * instance of the MainFrame object is need to call the setStatusMessage method
     */
    MainFrame mainFrame;

    private boolean hasDataChanged = false;

    private File resourceFile;
    private File backupDirectory;

    private List<Building> buildings = new LinkedList<>();

    private RestrictedUser restrictedUser;
    private SuperUser superUser;

    private List<Task> tasks;
    private Settings settings;

    private String encryptedBuildingsBase64;

    private int currentBuildingIndex = 0;
    private int currentFloorIndex = 0;
    private int currentWalkIndex = 0;
    private int currentColumnIndex = 0;
    private int currentLockerIndex = 0;
    private User currentUser;

    private SecretKey userMasterKey;
    private SecretKey superUserMasterKey;

    public void setNewUsers(SuperUser superUser, RestrictedUser restrictedUser, SecretKey userMasterKey, SecretKey superUserMasterKey) {
        this.superUser = superUser;
        this.restrictedUser = restrictedUser;

        this.userMasterKey = userMasterKey;
        this.superUserMasterKey = superUserMasterKey;

        currentUser = superUser;
    }

    public SecretKey getUserMasterKey() {
        return userMasterKey;
    }

    public void setUserMasterKey(SecretKey userMasterKey) {
        this.userMasterKey = userMasterKey;
    }

    public SecretKey getSuperUserMasterKey() {
        return superUserMasterKey;
    }

    public void setSuperUserMasterKey(SecretKey superUserMasterKey) {
        this.superUserMasterKey = superUserMasterKey;
    }

    private ResourceBundle bundle = ResourceBundle.getBundle("App");

    public DataManager() {

        URL url = MainFrame.class.getProtectionDomain().getCodeSource().getLocation();
        File sHomeDir = new File(url.getFile());

        if (!sHomeDir.isDirectory()) {
            sHomeDir = sHomeDir.getParentFile();
        }

        resourceFile = new File(sHomeDir, "jlocker.json");
        backupDirectory = new File(sHomeDir, "Backup");

        out.println("* program directory is: \"" + sHomeDir + "\"");
        settings = new Settings();
    }

    /* *************************************************************************
        Load and save methods
    ***************************************************************************/

    /**
     * Saves all data and creates a backup file with a time stamp.
     */
    public void saveAndCreateBackup() {

        saveData(resourceFile); // save to file jlocker.dat

        // Check if backup directory exists. If not, create it.
        if (!backupDirectory.exists() && !backupDirectory.mkdir()) {
            out.println("Backup failed!");
        }

        //
        // Check if a buildings.dat file exists to copy it to the backup directory.
        //
        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        File backupFile = new File(backupDirectory, String.format("jlocker-%04d-%02d-%02d.dat",
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)));

        // if a backup from this day doesnt exist, create one!
        if (!backupFile.exists()) {
            saveData(backupFile);
        }

        //
        // Just keep a certain number of last saved building files
        //
        if (backupDirectory.exists()) { // if there are not backups yet, we dont have to delete any files

            // This filter only returns files (and not directories)
            FileFilter fileFilter = file -> !file.isDirectory();

            File[] files = backupDirectory.listFiles(fileFilter);

            for (int i = 0; i < files.length - settings.numOfBackups; i++) {
                out.print("* delete backup file: \"" + files[i].getName() + "\"...");

                if (files[i].delete()) {
                    out.println(" successful!");
                } else {
                    out.println(" failed!");
                }
            }
        }
    }

    private void saveData(File file) {

        out.print("* saving " + file.getName() + "... ");

        try {

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            encryptedBuildingsBase64 = encrypt(gson.toJson(buildings), userMasterKey);

            try (Writer writer = new FileWriter(file)) {

                gson.toJson(new JsonRoot(
                        encryptedBuildingsBase64,
                        settings,
                        tasks,
                        superUser,
                        restrictedUser
                ), writer);

                out.println("successful");
                mainFrame.setStatusMessage("Speichern erfolgreich");
            }
        } catch (Exception ex) {
            out.println("failed");
            mainFrame.setStatusMessage("Speichern fehlgeschlagen");
            ex.printStackTrace();
        }
    }

    public void loadDefaultFile(boolean loadAsSuperUser) {
        loadFromCustomFile(resourceFile, loadAsSuperUser);
    }

    /**
     * Loads the data from an arbitry file path and initializes the users,
     * buildings, tasks and settings objects. When called directly this is used
     * to load backup files. If you want to load the current "jlocker.dat" file
     * please use loadData() method instead.
     */
    public void loadFromCustomFile(File file, boolean loadAsSuperUser) {

        out.print("* reading " + file.getName() + "... ");

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        try (Reader reader = new FileReader(file)) {

            JsonRoot root = gson.fromJson(reader, JsonRoot.class);

            if (superUser == null && restrictedUser == null) {
                superUser = root.superUser;
                restrictedUser = root.restrictedUser;
            }

            if (loadAsSuperUser) {
                currentUser = superUser;
            } else {
                currentUser = restrictedUser;
            }

            encryptedBuildingsBase64 = root.encryptedBuildingsBase64;
            tasks = root.tasks;
            settings = root.settings;

            out.println("successful");
            mainFrame.setStatusMessage("Laden erfolgreich");
        } catch (Exception ex) {
            out.println("failed");
            mainFrame.setStatusMessage("Laden fehlgeschlagen");
            ex.printStackTrace();
        }
    }

    public Locker getLockerByID(String id) {
        for (Building building : buildings) {
            List<Floor> floors = building.getFloors();

            for (Floor floor : floors) {
                List<Walk> walks = floor.getWalks();

                for (Walk walk : walks) {
                    List<ManagementUnit> mus = walk.getManagementUnitList();

                    for (ManagementUnit mu : mus) {
                        List<Locker> lockers = mu.getLockerList();

                        for (Locker locker : lockers) {
                            if (locker.getId().equals(id)) {
                                return locker;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Moves a student from one locker to another.
     */
    public void moveLockers(Locker sourceLocker, Locker destLocker, boolean withCodes) throws CloneNotSupportedException {
        Locker destCopy = destLocker.getCopy();

        destLocker.setTo(sourceLocker);
        sourceLocker.setTo(destCopy);

        if (withCodes) {
            destLocker.setCodes(sourceLocker.getCodes(superUserMasterKey), superUserMasterKey);
            sourceLocker.setCodes(destCopy.getCodes(superUserMasterKey), superUserMasterKey);
        }
    }

    public void updateAllCabinets() {
        List<ManagementUnit> mus = DataManager.getInstance().getCurManagmentUnitList();

        int maxRows = mus.stream().mapToInt(mu -> mu.getLockerCabinet().getLockers().size()).max().orElse(0);

        mus.stream()
                .map(ManagementUnit::getLockerCabinet)
                .forEach(c -> c.updateCabinet(maxRows));
    }


    public List<ManagementUnit> reinstantiateManagementUnits(List<ManagementUnit> managementUnits) {
        return managementUnits.stream().map(mu -> {

            ManagementUnit newMu = new ManagementUnit(mu.type);

            switch (mu.type) {
                case ROOM: {
                    newMu.getRoom().setCaption(mu.getRoom().getRoomName(), mu.getRoom().getSchoolClassName());
                    break;
                }
                case LOCKER_CABINET: {
                    List<Locker> newLockers = mu.getLockerList().stream().map(l -> new Locker(
                                    l.getId(),
                                    l.getSurname(),
                                    l.getOwnerName(),
                                    l.getOwnerSize(),
                                    l.getOwnerClass(),
                                    l.getFromDate(),
                                    l.getUntilDate(),
                                    l.hasContract(),
                                    l.getMoney(),
                                    l.getCurrentCodeIndex(),
                                    l.getEncryptedCodes(),
                                    l.getLock(),
                                    l.isOutOfOrder(),
                                    l.getNote()
                            )
                    ).collect(Collectors.toList());
                    newMu.getLockerCabinet().setLockers(newLockers);
                    break;
                }
                case STAIRCASE: {
                    newMu.getStaircase().setCaption(mu.getStaircase().getStaircaseName());
                    break;
                }
            }

            return newMu;
        }).collect(Collectors.toList());
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public String getAppTitle() {
        return bundle.getString("Application.title");
    }

    public String getAppVersion() {
        return bundle.getString("Application.version");
    }

    public Settings getSettings() {
        return settings;
    }

    public File getRessourceFile() {
        return resourceFile;
    }

    public File getBackupDirectory() {
        return backupDirectory;
    }

    public boolean isLockerIdUnique(String id) {
        return getLockerByID(id) == null;
    }

    public String getEncryptedBuildingsBase64() {
        return encryptedBuildingsBase64;
    }

    public List<Building> getBuildingList() {
        return buildings;
    }

    public int getCurBuildingIndex() {
        return currentBuildingIndex;
    }

    public Building getCurBuilding() {
        return buildings.get(currentBuildingIndex);
    }

    public List<Floor> getCurFloorList() {
        return getCurBuilding().getFloors();
    }

    public Floor getCurFloor() {
        return getCurFloorList().get(currentFloorIndex);
    }

    public int getCurFloorIndex() {
        return currentFloorIndex;
    }

    public List<Walk> getCurWalkList() {
        return getCurFloor().getWalks();
    }

    public Walk getCurWalk() {
        return getCurWalkList().get(currentWalkIndex);
    }

    public int getCurWalkIndex() {
        return currentWalkIndex;
    }

    public List<ManagementUnit> getCurManagmentUnitList() {
        return getCurWalk().getManagementUnitList();
    }

    public ManagementUnit getCurManamentUnit() {
        return getCurManagmentUnitList().get(currentColumnIndex);
    }

    public int getCurManagementUnitIndex() {
        return currentColumnIndex;
    }

    public List<Locker> getCurLockerList() {
        return getCurManamentUnit().getLockerList();
    }

    public Locker getCurLocker() {
        return getCurLockerList().get(currentLockerIndex);
    }

    public int getCurLockerIndex() {
        return currentLockerIndex;
    }

    public Room getCurRoom() {
        return getCurManamentUnit().getRoom();
    }

    public LockerCabinet getCurLockerCabinet() {
        return getCurManamentUnit().getLockerCabinet();
    }

    public boolean hasDataChanged() {
        return hasDataChanged;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void initBuildingObject() {
        try {
            this.buildings = unsealAndDeserializeBuildings(getEncryptedBuildingsBase64(), userMasterKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setDataChanged(boolean changed) {
        hasDataChanged = changed;
    }

    public void setCurrentBuildingIndex(int index) {
        currentBuildingIndex = index;
    }

    public void setCurrentFloorIndex(int index) {
        currentFloorIndex = index;
    }

    public void setCurrentWalkIndex(int index) {
        currentWalkIndex = index;
    }

    public void setCurrentMUnitIndex(int index) {
        currentColumnIndex = index;
    }

    public void setCurrentLockerIndex(int index) {
        currentLockerIndex = index;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addTask(String description) {
        tasks.add(new Task(description));
    }

    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }
}
