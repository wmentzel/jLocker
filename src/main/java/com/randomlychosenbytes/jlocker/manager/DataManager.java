package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;
import com.randomlychosenbytes.jlocker.nonabstractreps.Floor;
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker;
import com.randomlychosenbytes.jlocker.nonabstractreps.LockerCabinet;
import com.randomlychosenbytes.jlocker.nonabstractreps.Room;
import com.randomlychosenbytes.jlocker.nonabstractreps.Task;
import com.randomlychosenbytes.jlocker.nonabstractreps.User;
import com.randomlychosenbytes.jlocker.nonabstractreps.Walk;

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 * 
 * @author Willi
 * 
 * @version latest
 */
public class DataManager 
{
    /**
     * Singleton mechanism
     */
    final private static DataManager instance = new DataManager();
    
    /**
     * Returns the one and only instance of this singleton class
     * @return 
     */
    public static DataManager getInstance () 
    { 
        return instance; 
    }
    
    /**
     * TODO remove
     * instance of the MainFrame object is need to call the setStatusMessage method
     */
    MainFrame mainFrame;
    
    final public static boolean ERROR = true;

    private boolean hasDataChanged;
    
    private String ressourceFilePath;
    private String sHomeDir;

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
    public DataManager()
    {
        currentBuildingIndex = 0;
        currentFloorIndex = 0;
        currentWalkIndex = 0;
        currentColumnIndex = 0;
        currentLockerIndex = 0;
        currentUserIndex = 0;
        
        hasDataChanged = false;

        buildings = new LinkedList<>();

        determineAppDir();
        
        // Deterimine app version and name from resources       
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
    public void saveAndCreateBackup()
    {
        String status;
        
        if(saveData(ressourceFilePath)) // save to file jlocker.dat
        {
            status = "fehlgeschlagen"; 
        }
        else
        {
            status = "erfolgreich"; 
        }
        
        mainFrame.setStatusMessage("Speichern " + status + "!");
        
        //
        // Create Backup
        //
        
        String backupDirectoryPath = sHomeDir + "Backup/";
        File backupDirectoryFile = new File(backupDirectoryPath);
        
        //
        // Check if backup directory exists. If not, create it.
        //
        if(!(backupDirectoryFile.exists()))
        {
            if(backupDirectoryFile.mkdir() == false)
            {
                System.out.println("Backup failed!");
            }  
        }
        
        //
        // Check if a buildings.dat file exists to copy it to the backup directory.
        //
        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        String path = String.format(backupDirectoryPath + "jlocker-%04d-%02d-%02d.dat",
        today.get(Calendar.YEAR),
        today.get(Calendar.MONTH),
        today.get(Calendar.DAY_OF_MONTH));
        
        // if a backup from this day doesnt exist, create one!
        if(!(new File(path).exists()))
        {
            saveData(path);
        }
        
        //
        // Just keep a certain number of last saved building files
        //
        
        // The list of files can also be retrieved as File objects
        
        File filesHomeDir = new File(sHomeDir + "Backup");
        
        if(filesHomeDir.exists()) // if there are not backups yet, we dont have to delete any files
        {
            // This filter only returns directories
            FileFilter fileFilter = new FileFilter() 
            {
                @Override
                public boolean accept(File file) 
                {
                    return !file.isDirectory();
                }
            };

            File[] files = filesHomeDir.listFiles(fileFilter);

            Integer iNumBackups = (Integer) settings.get("NumOfBackups");
            
            for(int i = 0; i < files.length - iNumBackups; i++)
            {
                System.out.print("* delete backup file: \"" + files[i].getName() + "\"...");
                
                if(files[i].delete())
                {
                   System.out.println(" successful!");
                }
                else
                {
                   System.out.println(" failed!");
                }
            }
        }
    }
    
    /**
     * Only called by saveAndCreateBackup
     * 
     * @param path Path to the jlocker.dat file
     * @return status (true for error)
     */
    private boolean saveData(String path)
    {
        byte b[] = SecurityManager.serialize(buildings);
        sealedBuildingsObject = SecurityManager.encryptObject(b, users.get(0).getUserMasterKey()); 
        
        System.out.print("* saving " + path + "... ");
        
        ObjectOutputStream oos = SecurityManager.getOos(path);
        
        try
        {
            oos.writeObject(users);
            oos.writeObject(sealedBuildingsObject);
            oos.writeObject(tasks);
            oos.writeObject(settings);
            
            oos.flush();
            oos.close();
        }
        catch(IOException e)
        {
            System.out.println("failed!");
            return true;
        }
        System.out.println("successful!");
        
        return false;
    }
    
    /**
     * Wrapper for loadData(String path). This is used to load the current
     * "jlocker.dat" file.
     */
    public void loadData()
    {
        String status;
        
        if(!loadFromCustomFile(ressourceFilePath))
           status = "erfolgreich"; 
        else
           status = "fehlgeschlagen";
        
        mainFrame.setStatusMessage("Laden " + status + "!");
    }
    
    /**
     * Loads the data from an arbitry file path and initializes the users, 
     * buildings, tasks and settings objects. When called directly this is used
     * to load backup files. If you want to load the current "jlocker.dat" file
     * please use loadData() method instead.
     * 
     * @param path File path to the jlocker.dat file
     * @return status (true for error)
     */
    public boolean loadFromCustomFile(String path)
    {
        ObjectInputStream ois = SecurityManager.getOis(path);
        System.out.print("* loading " + path + "... ");
        
        try 
        {
            users = (LinkedList<User>) ois.readObject(); 
            sealedBuildingsObject = (SealedObject) ois.readObject();
            tasks = (LinkedList<Task>) ois.readObject();
            settings = (TreeMap) ois.readObject();
            
            ois.close();
        } 

        catch (IOException | ClassNotFoundException ex) 
        {
            System.out.println("failed!");
            return true;
        }
        
        System.out.println("successful!");
        
        return false;
    }
    
    /**
     * When there was no settings object loaded, it is created by this method
     * with default values.
     */
    public void loadDefaultSettings()
    {
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
    
    /**
     * 
     * @param id
     * @return 
     */
    public Locker getLockerByID (String id)
    {
        for(Building building : buildings)
        {
            List<Floor> floors =  building.getFloorList();

            for(Floor floor : floors)
            {
                List<Walk> walks =  floor.getWalkList();

                for (Walk walk : walks)
                {
                    List<ManagementUnit> mus = walk.getManagementUnitList();

                    for(ManagementUnit mu : mus)
                    {
                        List<Locker> lockers = mu.getLockerList();

                        for (Locker locker : lockers)
                        {
                            if(locker.getId().equals(id))
                            {
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
     * @param name
     * @return 
     */
    public boolean isBuildingNameUnique (String name)
    {
        int iSize = buildings.size();

        for(int i = 0; i < iSize; i++)
        {
            if(((Building) buildings.get(i)).getName().equals(name))
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
    public void moveLockers(Locker sourceLocker, Locker destLocker, boolean withCodes) throws CloneNotSupportedException
    {
        Locker destCopy = destLocker.getCopy();

        destLocker.setTo(sourceLocker);
        sourceLocker.setTo(destCopy);

        if(withCodes)
        {
            SecretKey key = getCurUser().getSuperUMasterKey();

            destLocker.setCodes(sourceLocker.getCodes(key), key);
            sourceLocker.setCodes(destCopy.getCodes(key), key);
        }
    }
    
    /**
     * 
     * @return 
     */
    public MainFrame getMainFrame()
    { 
        return mainFrame; 
    }
    
    /**
     * 
     * @return 
     */
    public String getAppTitle() 
    { 
        return appTitle;
    }
    
    /**
     * 
     * @return 
     */
    public String getAppVersion()
    { 
        return appVersion; 
    }
    /**
     * 
     * @return 
     */
    public TreeMap getSettings() 
    { 
        return settings; 
    }
    /**
     * 
     * @return 
     */
    public String getHomePath()
    { 
        return sHomeDir; 
    }
    
    /**
     * 
     * @return 
     */
    public String getRessourceFilePath()
    { 
        return ressourceFilePath; 
    }
    
    /**
     * Returns whether this ID already exists or not.
     * @param id
     * @return 
     */
    public boolean isLockerIdUnique(String id)
    {
        return getLockerByID(id) == null;
    }
    
    /**
     * 
     * @return 
     */
    public SealedObject getSealedBuildingsObject()
    {   
        return sealedBuildingsObject; 
    }

    /**
     * 
     * @return 
     */
    public User getCurUser()
    { 
        return users.get(currentUserIndex);
    }
    
    /**
     * 
     * @return 
     */
    public List<User> getUserList()               
    { 
        return users; 
    }
    
    /**
     * 
     * @return 
     */
    public List<Building> getBuildingList()
    { 
        return buildings; 
    }
    
    /**
     * 
     * @return 
     */
    public int getCurBuildingIndex()          
    { 
        return currentBuildingIndex; 
    }
    
    /**
     * 
     * @return 
     */
    public Building getCurBuilding()          
    { 
        return buildings.get(currentBuildingIndex); 
    }
    
    /**
     * 
     * @return 
     */
    public List<Floor> getCurFloorList()  
    { 
        return getCurBuilding().getFloorList(); 
    }
    
    /**
     * 
     * @return 
     */
    public Floor getCurFloor()           
    { 
        return getCurFloorList().get(currentFloorIndex); 
    }
    
    /**
     * 
     * @return 
     */
    public int getCurFloorIndex()         
    { 
        return currentFloorIndex; 
    }
    
    /**
     * 
     * @return 
     */
    public List<Walk> getCurWalkList()
    { 
        return getCurFloor().getWalkList(); 
    }
    
    /**
     * 
     * @return 
     */
    public Walk getCurWalk()                
    { 
        return getCurWalkList().get(currentWalkIndex); 
    }
    
    /**
     * 
     * @return 
     */
    public int getCurWalkIndex()            
    { 
        return currentWalkIndex; 
    }
    
    /**
     * 
     * @return 
     */
    public List<ManagementUnit> getCurManagmentUnitList()       
    { 
        return getCurWalk().getManagementUnitList(); 
    }
    
    /**
     * 
     * @return 
     */
    public ManagementUnit getCurManamentUnit()                
    { 
        return getCurManagmentUnitList().get(currentColumnIndex); 
    }
    
    /**
     * 
     * @return 
     */
    public int getCurManagementUnitIndex()
    { 
        return currentColumnIndex; 
    } 
    
    /**
     * 
     * @return 
     */
    public List<Locker> getCurLockerList()
    { 
        return getCurManamentUnit().getLockerList(); 
    }
    
    /**
     * 
     * @return 
     */
    public Locker getCurLocker()                    
    { 
        return getCurLockerList().get(currentLockerIndex); 
    }
    
    /**
     * 
     * @return 
     */
    public int getCurLockerIndex()                  
    { 
        return currentLockerIndex; 
    }
    
    /**
     * 
     * @return 
     */
    public Room getCurRoom() 
    { 
        return getCurManamentUnit().getRoom(); 
    }
    
    /**
     * 
     * @return 
     */
    public LockerCabinet getCurLockerCabinet()  
    { 
        return getCurManamentUnit().getLockerCabinet(); 
    }
    
    /**
     * 
     * @return 
     */
    public boolean hasDataChanged()
    { 
        return hasDataChanged; 
    }
    
    /**
     * 
     * @return 
     */
    public List<Task> getTasks()          
    { 
        return tasks; 
    }
     
    /* *************************************************************************
        Setter
    ***************************************************************************/
    public void setBuildingsObject(List<Building> buildings)
    {
        this.buildings = buildings;
    }
    
    /**
     * Setter 
     * @param mainFrame
     */
    public void setMainFrame(MainFrame mainFrame) 
    { 
        this.mainFrame = mainFrame; 
    }
   
    /**
     * Setter 
     * @param users
     */
    public void setUserList(List<User> users) 
    { 
        this.users = users; 
    }
    
    /**
     * Setter
     * @param changed 
     */
    public void setDataChanged(boolean changed) 
    { 
        hasDataChanged = changed; 
    }
    
    /**
     * Setter
     * @param index 
     */
    public void setCurrentBuildingIndex(int index) 
    { 
        currentBuildingIndex = index; 
    }
    
    /**
     * Setter
     * @param index 
     */
    public void setCurrentFloorIndex(int index) 
    { 
        currentFloorIndex = index; 
    }
    
    /**
     * Setter
     * @param index 
     */
    public void setCurrentWalkIndex(int index) 
    { 
        currentWalkIndex = index; 
    }
    
    /**
     * Setter
     * @param index 
     */
    public void setCurrentMUnitIndex(int index) 
    { 
        currentColumnIndex = index; 
    }
    
    /**
     * Setter
     * @param index
     */
    public void setCurrentLockerIndex(int index) 
    {
        currentLockerIndex = index;
    }
    
    /**
     * Setter
     * @param index 
     */
    public void setCurrentUserIndex(int index) 
    { 
        currentUserIndex = index; 
    }
    
    /**
     * Setter
     * @param description 
     */
    public void addTask(String description) 
    { 
        tasks.add(new Task(description)); 
    }
    
    /**
     * Setter
     * @param tasks 
     */
    public void setTaskList(List<Task> tasks)
    { 
        this.tasks = tasks; 
    }
    
    /* *************************************************************************
        Private Methods
    ***************************************************************************/
    
    private void determineAppDir()
    {
        URL url = MainFrame.class.getProtectionDomain().getCodeSource().getLocation();
        File file = new File (url.getFile());
        
        System.out.print("* determine program directory... ");
        
        try
        {
            sHomeDir = URLDecoder.decode(file.getAbsolutePath(), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            System.out.println("failed!");
            System.exit(0);
        }
        
        if(file.isDirectory())
            sHomeDir += '\\';
        else
        {
           int index = sHomeDir.lastIndexOf('\\');
           sHomeDir = sHomeDir.substring(0, index + 1);
        }
        
        ressourceFilePath = sHomeDir + "jlocker.dat";
        
        System.out.println("successful!");
        System.out.println("  program directory is: \"" + sHomeDir + "\"");
    }
    
    /* *************************************************************************
        Methods for the upcoming 1.6 release which saves the data in a new 
        format
    ***************************************************************************/
    
    /**
     * Saves users, buildings, tasks and settings object to a file serialized as 
     * XML
     * @param path
     * @return 
     */
    private boolean saveXmlData(String path)
    {        
        System.out.println("* saving in XML format " + path + "... ");
        
        try
        {
            ObjectOutputStream oos = SecurityManager.getOos(path);
            
            oos.writeObject(SecurityManager.serializeXml(users));
            System.out.println("* users written");
            
            byte[] xmlBuildingBytes = SecurityManager.serializeXml(buildings);
            System.out.println("* buildings -> bytes");
            
            byte[] encryptedXMLBuildingBytes = SecurityManager.encryptByteArray(xmlBuildingBytes, users.get(0).getUserMasterKey());
            System.out.println("* bytes -> encrypted bytes");
            
            oos.writeObject(encryptedXMLBuildingBytes);
            System.out.println("* buildings written");
            
            oos.writeObject(SecurityManager.serializeXml(tasks));
            System.out.println("* tasks written");
            
            oos.writeObject(SecurityManager.serializeXml(settings));
            System.out.println("* settings written");
            
            oos.flush();
            oos.close();
        } 
        catch (IOException ex)
        {
            System.out.println("failed! " + ex);
            return true;
        }
        
        System.out.println("successful!");
        
        return false;
    }
    /**
     * Reads XML serialized users, buildings, taks and settings objects 
     * from a file XML
     * @param path
     * @return 
     */
    private boolean readXmlData(String path)
    {
        //
        // serialize buildings object
        //
        System.out.println("* reading from XML format " + path + "... ");
        
        // we got to remember the entered password, because it will be overwritten
        String decUserPW = getCurUser().getUserPW();
        
        try
        {
            ObjectInputStream ois = SecurityManager.getOis(path);
            
            users = (LinkedList<User>) SecurityManager.deserializeXml((byte[])ois.readObject());
            System.out.println("* user read");
 
            // decrypt password
            getCurUser().isPasswordCorrect(decUserPW);

            byte[] encryptedXMLBuildingBytes = (byte[]) ois.readObject();
            byte[] decryptedXMLBuildingBytes = (byte[]) SecurityManager.decryptByteArray(encryptedXMLBuildingBytes, users.get(0).getUserMasterKey());
            
            buildings = (LinkedList<Building>) SecurityManager.deserializeXml(decryptedXMLBuildingBytes);
            System.out.println("* buildings read");

            tasks = (LinkedList<Task>) SecurityManager.deserializeXml((byte[])ois.readObject());
            System.out.println("* tasks read");
            
            settings = (TreeMap) SecurityManager.deserializeXml((byte[])ois.readObject());
            System.out.println("* settings read");
            
            ois.close();
        } 
        catch (ClassNotFoundException | IOException ex)
        {
            System.out.println("failed! " + ex);
            return true;
        }
        
        System.out.println("successful!");
        
        return false;
    }
    
   /**
    * Calls loadXMLData(String path) with path as the standard path to the
    * jLocker.data file and outputs a status message on the MainFrame
    */
    public void loadXMLData()
    {
        String status;

        if(!readXmlData(ressourceFilePath))
           status = "erfolgreich"; 
        else
           status = "fehlgeschlagen";

        mainFrame.setStatusMessage("Laden " + status + "!");
    }
   
   /**
    * http://viralpatel.net/blogs/getting-jvm-heap-size-used-memory-total-memory-using-java-runtime/
    */
    public void outputJavaMemoryStatistics()
    {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
}