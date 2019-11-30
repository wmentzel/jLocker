package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 *
 * @author Willi
 * @version latest
 */
public class DataManager {
    /**
     * Singleton mechanism
     */
    final private static DataManager instance = new DataManager();

    /**
     * Returns the one and only instance of this singleton class
     *
     * @return
     */
    public static DataManager getInstance() {
        return instance;
    }

    /**
     * TODO remove
     * instance of the MainFrame object is need to call the setStatusMessage method
     */
    MainFrame mainFrame;

    final public static boolean ERROR = true;

    private boolean hasDataChanged;

    private File ressourceFilePath;
    private File sHomeDir;

    private List<Building> buildings;
    private List<User> users;
    private List<Task> tasks;
    private TreeMap settings;

    private SealedObject sealedBuildingsObject;

    private int currentBuildingIndex;
    private int currentFloorIndex;
    private int currentWalkIndex;
    private int currentColumnIndex;
    private int currentLockerIndex;
    private int currentUserIndex;

    private final String appVersion;
    private final String appTitle;

    /**
     * Initializes the class on the first call
     */
    public DataManager() {
        currentBuildingIndex = 0;
        currentFloorIndex = 0;
        currentWalkIndex = 0;
        currentColumnIndex = 0;
        currentLockerIndex = 0;
        currentUserIndex = 0;

        hasDataChanged = false;

        buildings = new LinkedList<>();

        determineAppDir();

        // Determine app version and name from resources
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("App");
        appTitle = bundle.getString("Application.title");
        appVersion = bundle.getString("Application.version");
    }

    /* *************************************************************************
        Load and save methods
    ***************************************************************************/

    /**
     * Saves all data and creates a backup file with a time stamp.
     */
    public void saveAndCreateBackup() {

        saveData(ressourceFilePath); // save to file jlocker.dat

        // Check if backup directory exists. If not, create it.
        File backupDirectoryFile = new File(sHomeDir, "Backup");

        if (!backupDirectoryFile.exists() && !backupDirectoryFile.mkdir()) {
            System.out.println("Backup failed!");
        }

        //
        // Check if a buildings.dat file exists to copy it to the backup directory.
        //
        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        File backupFile = new File(backupDirectoryFile, String.format("jlocker-%04d-%02d-%02d.dat",
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

        File filesHomeDir = new File(sHomeDir + "Backup");

        if (filesHomeDir.exists()) // if there are not backups yet, we dont have to delete any files
        {
            // This filter only returns files (and not directories)
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            };

            File[] files = filesHomeDir.listFiles(fileFilter);

            Integer iNumBackups = (Integer) settings.get("NumOfBackups");

            for (int i = 0; i < files.length - iNumBackups; i++) {
                System.out.print("* delete backup file: \"" + files[i].getName() + "\"...");

                if (files[i].delete()) {
                    System.out.println(" successful!");
                } else {
                    System.out.println(" failed!");
                }
            }
        }
    }

    /**
     * Only called by saveAndCreateBackup
     *
     * @param file Path to the jlocker.dat file
     * @return status (true for error)
     */
    private void saveData(File file) {
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            byte[] b = SecurityManager.serialize(buildings);
            sealedBuildingsObject = SecurityManager.encryptObject(b, users.get(0).getUserMasterKey());

            System.out.print("* saving " + file.getAbsolutePath() + "... ");

            oos.writeObject(users);
            oos.writeObject(sealedBuildingsObject);
            oos.writeObject(tasks);
            oos.writeObject(settings);

            System.out.println("successful");
            mainFrame.setStatusMessage("Speichern erfolgreich");
        } catch (Exception ex) {
            System.out.println("failed");
            mainFrame.setStatusMessage("Speichern fehlgeschlagen");
            ex.printStackTrace();
        }
    }

    public void loadDefaultFile() {
        loadFromCustomFile(ressourceFilePath);
    }

    /**
     * Loads the data from an arbitry file path and initializes the users,
     * buildings, tasks and settings objects. When called directly this is used
     * to load backup files. If you want to load the current "jlocker.dat" file
     * please use loadData() method instead.
     */
    public void loadFromCustomFile(File file) {

        System.out.print("* reading " + file.getName() + "... ");

        try (
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            users = (List<User>) ois.readObject();
            sealedBuildingsObject = (SealedObject) ois.readObject();
            tasks = (LinkedList<Task>) ois.readObject();
            settings = (TreeMap) ois.readObject();

            System.out.println("successful");
            mainFrame.setStatusMessage("Laden erfolgreich");
        } catch (Exception ex) {
            System.out.println("failed");
            mainFrame.setStatusMessage("Laden fehlgeschlagen");
            ex.printStackTrace();
        }
    }

    /**
     * When there was no settings object loaded, it is created by this method
     * with default values.
     */
    public void loadDefaultSettings() {
        settings = new TreeMap();
        settings.put("LockerOverviewFontSize", 20);
        settings.put("NumOfBackups", 10);

        List<Integer> iMinSizes = new LinkedList<>();

        iMinSizes.add(0); // size for bottom locker
        iMinSizes.add(0);
        iMinSizes.add(140);
        iMinSizes.add(150);
        iMinSizes.add(175); // size for top locker

        settings.put("LockerMinSizes", iMinSizes);
    }
    
    /* *************************************************************************
        Getter
    ***************************************************************************/

    public Locker getLockerByID(String id) {
        for (Building building : buildings) {
            List<Floor> floors = building.getFloorList();

            for (Floor floor : floors) {
                List<Walk> walks = floor.getWalkList();

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
     * Determines whether the given name is already assigned to a building.
     */
    public boolean isBuildingNameUnique(String name) {
        int iSize = buildings.size();

        for (int i = 0; i < iSize; i++) {
            if (((Building) buildings.get(i)).getName().equals(name))
                return false;
        }

        return true;
    }

    /**
     * Moves a student from one locker to another.
     *
     * @param sourceLocker
     * @param destLocker
     * @param withCodes
     * @throws CloneNotSupportedException
     */
    public void moveLockers(Locker sourceLocker, Locker destLocker, boolean withCodes) throws CloneNotSupportedException {
        Locker destCopy = destLocker.getCopy();

        destLocker.setTo(sourceLocker);
        sourceLocker.setTo(destCopy);

        if (withCodes) {
            SecretKey key = getCurUser().getSuperUMasterKey();

            destLocker.setCodes(sourceLocker.getCodes(key), key);
            sourceLocker.setCodes(destCopy.getCodes(key), key);
        }
    }

    /**
     * @return
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * @return
     */
    public String getAppTitle() {
        return appTitle;
    }

    /**
     * @return
     */
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * @return
     */
    public TreeMap getSettings() {
        return settings;
    }

    /**
     * @return
     */
    public File getHomePath() {
        return sHomeDir;
    }

    /**
     * @return
     */
    public File getRessourceFilePath() {
        return ressourceFilePath;
    }

    /**
     * Returns whether this ID already exists or not.
     *
     * @param id
     * @return
     */
    public boolean isLockerIdUnique(String id) {
        return getLockerByID(id) == null;
    }

    /**
     * @return
     */
    public SealedObject getSealedBuildingsObject() {
        return sealedBuildingsObject;
    }

    /**
     * @return
     */
    public User getCurUser() {
        return users.get(currentUserIndex);
    }

    /**
     * @return
     */
    public List<User> getUserList() {
        return users;
    }

    /**
     * @return
     */
    public List<Building> getBuildingList() {
        return buildings;
    }

    /**
     * @return
     */
    public int getCurBuildingIndex() {
        return currentBuildingIndex;
    }

    /**
     * @return
     */
    public Building getCurBuilding() {
        return buildings.get(currentBuildingIndex);
    }

    /**
     * @return
     */
    public List<Floor> getCurFloorList() {
        return getCurBuilding().getFloorList();
    }

    /**
     * @return
     */
    public Floor getCurFloor() {
        return getCurFloorList().get(currentFloorIndex);
    }

    /**
     * @return
     */
    public int getCurFloorIndex() {
        return currentFloorIndex;
    }

    /**
     * @return
     */
    public List<Walk> getCurWalkList() {
        return getCurFloor().getWalkList();
    }

    /**
     * @return
     */
    public Walk getCurWalk() {
        return getCurWalkList().get(currentWalkIndex);
    }

    /**
     * @return
     */
    public int getCurWalkIndex() {
        return currentWalkIndex;
    }

    /**
     * @return
     */
    public List<ManagementUnit> getCurManagmentUnitList() {
        return getCurWalk().getManagementUnitList();
    }

    /**
     * @return
     */
    public ManagementUnit getCurManamentUnit() {
        return getCurManagmentUnitList().get(currentColumnIndex);
    }

    /**
     * @return
     */
    public int getCurManagementUnitIndex() {
        return currentColumnIndex;
    }

    /**
     * @return
     */
    public List<Locker> getCurLockerList() {
        return getCurManamentUnit().getLockerList();
    }

    /**
     * @return
     */
    public Locker getCurLocker() {
        return getCurLockerList().get(currentLockerIndex);
    }

    /**
     * @return
     */
    public int getCurLockerIndex() {
        return currentLockerIndex;
    }

    /**
     * @return
     */
    public Room getCurRoom() {
        return getCurManamentUnit().getRoom();
    }

    /**
     * @return
     */
    public LockerCabinet getCurLockerCabinet() {
        return getCurManamentUnit().getLockerCabinet();
    }

    /**
     * @return
     */
    public boolean hasDataChanged() {
        return hasDataChanged;
    }

    /**
     * @return
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /* *************************************************************************
        Setter
    ***************************************************************************/
    public void initBuildingObject() {
        this.buildings = SecurityManager.unsealAndDeserializeBuildings(
                getSealedBuildingsObject(), getUserList().get(0).getUserMasterKey()
        );
    }

    /**
     * Setter
     *
     * @param mainFrame
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Setter
     *
     * @param users
     */
    public void setUserList(List<User> users) {
        this.users = users;
    }

    /**
     * Setter
     *
     * @param changed
     */
    public void setDataChanged(boolean changed) {
        hasDataChanged = changed;
    }

    /**
     * Setter
     *
     * @param index
     */
    public void setCurrentBuildingIndex(int index) {
        currentBuildingIndex = index;
    }

    /**
     * Setter
     *
     * @param index
     */
    public void setCurrentFloorIndex(int index) {
        currentFloorIndex = index;
    }

    /**
     * Setter
     *
     * @param index
     */
    public void setCurrentWalkIndex(int index) {
        currentWalkIndex = index;
    }

    /**
     * Setter
     *
     * @param index
     */
    public void setCurrentMUnitIndex(int index) {
        currentColumnIndex = index;
    }

    /**
     * Setter
     *
     * @param index
     */
    public void setCurrentLockerIndex(int index) {
        currentLockerIndex = index;
    }

    /**
     * Setter
     *
     * @param index
     */
    public void setCurrentUserIndex(int index) {
        currentUserIndex = index;
    }

    /**
     * Setter
     *
     * @param description
     */
    public void addTask(String description) {
        tasks.add(new Task(description));
    }

    /**
     * Setter
     *
     * @param tasks
     */
    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    /* *************************************************************************
        Private Methods
    ***************************************************************************/

    private void determineAppDir() {
        URL url = MainFrame.class.getProtectionDomain().getCodeSource().getLocation();
        sHomeDir = new File(url.getFile());

        if (!sHomeDir.isDirectory()) {
            sHomeDir = sHomeDir.getParentFile();
        }

        ressourceFilePath = new File(sHomeDir, "jlocker.dat");

        System.out.println("* program directory is: \"" + sHomeDir + "\"");
    }
}
