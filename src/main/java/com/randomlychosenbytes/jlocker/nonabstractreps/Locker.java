package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.manager.Utils;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Locker extends JLabel implements Cloneable {

    @Expose
    private String id;

    @Expose
    private String lastName;

    @Expose
    private String firstName;

    @Expose
    private int sizeInCm;

    @Expose
    private String schoolClassName;

    @Expose
    private String rentedFromDate; // TODO: use LocalDate

    @Expose
    private String rentedUntilDate; // TODO: use LocalDate

    @Expose
    private boolean hasContract;

    @Expose
    private int paidAmount;

    @Expose
    private int previoulyPaidAmount;

    @Expose
    private boolean isOutOfOrder;

    @Expose
    private String lockCode;

    @Expose
    private String note;

    @Expose
    private int currentCodeIndex;

    @Expose
    private String encryptedCodes[];

    // transient
    private Boolean isSelected = false;

    public Locker(
            String id, String firstName, String lastName, int sizeInCm,
            String schoolClass, String rentedFrom, String rentedUntil, boolean hasContract,
            int paidAmount, int currentCodeIndex, String lockCode,
            boolean isOutOfOrder, String note
    ) {
        this.id = id;
        this.lastName = firstName;
        this.firstName = lastName;
        this.sizeInCm = sizeInCm;
        this.schoolClassName = schoolClass;
        this.rentedFromDate = rentedFrom;
        this.rentedUntilDate = rentedUntil;
        this.hasContract = hasContract;
        this.paidAmount = paidAmount;
        this.previoulyPaidAmount = paidAmount;
        this.isOutOfOrder = isOutOfOrder;
        this.lockCode = lockCode;
        this.note = note;
        this.isSelected = false;

        this.currentCodeIndex = currentCodeIndex;
        this.encryptedCodes = null;

        // standard color
        setColor(FREE_COLOR);

        setText(this.id);

        // If true the component paints every pixel within its bounds.
        setOpaque(true);

        // change font      
        setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        // font aligment
        setHorizontalAlignment(SwingConstants.CENTER);

        // assign mouse events
        setUpMouseListener();
    }

    public String[] getCodes(SecretKey sukey) {
        String[] decCodes = new String[5];

        for (int i = 0; i < 5; i++) {
            decCodes[i] = getCode(i, sukey);
        }

        return decCodes;
    }

    private String getCode(int i, SecretKey sukey) {
        if (encryptedCodes == null) {
            return "00-00-00";
        }

        try {
            String code = Utils.decrypt(encryptedCodes[i], sukey);
            return code.substring(0, 2) + "-" + code.substring(2, 4) + "-" + code.substring(4, 6);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Locker.getCode: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return null;
        }
    }

    public long getRemainingTimeInMonths() {
        if (rentedUntilDate.equals("") || rentedFromDate.equals("") || isFree()) {
            return 0;
        }

        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        int iDay = Integer.parseInt(rentedUntilDate.substring(0, 2));
        int iMonth = Integer.parseInt(rentedUntilDate.substring(3, 5)) - 1;
        int iYear = Integer.parseInt(rentedUntilDate.substring(6, 10));

        Calendar end = new GregorianCalendar(iYear, iMonth, iDay);
        end.setLenient(false);
        end.getTime();

        long iDifferenceInMonths = Math.round(((double) end.getTimeInMillis() - today.getTimeInMillis()) / 2592000000.0); // 2592000000.0 = 24 * 60 * 60 * 1000 * 30

        if (iDifferenceInMonths < 0) {
            return 0;
        }

        return iDifferenceInMonths;
    }

    public static boolean isDateValid(String date) {
        if (date.length() < 10) {
            return false;
        }

        try {
            // try to extract day, month and year
            int iDay = Integer.parseInt(date.substring(0, 2));
            int iMonth = Integer.parseInt(date.substring(3, 5)) - 1; // month is zero-based, january is 0... what the fuck!?
            int iYear = Integer.parseInt(date.substring(6, 10));

            Calendar c = new GregorianCalendar(iYear, iMonth, iDay);

            c.setLenient(false);
            c.getTime();

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setAppropriateColor() {
        if (hasContract) {
            setColor(RENTED_COLOR);
        } else {
            setColor(NOCONTRACT_COLOR);
        }

        if (getRemainingTimeInMonths() <= 1) {
            setColor(ONEMONTHREMAINING_COLOR);
        }

        if (isFree()) {
            setColor(FREE_COLOR);
        }

        if (isOutOfOrder) {
            setColor(OUTOFORDER_COLOR);
        }
    }

    public void setCodes(String[] codes, SecretKey sukey) {
        // codes is unencrypted... encrypting and saving in encCodes

        // The Value of code[i] looks like "00-00-00"
        // it is saved without the "-", so we have
        // to remove them.

        encryptedCodes = new String[5];

        for (int i = 0; i < 5; i++) {
            codes[i] = codes[i].replace("-", "");
        }

        try {
            for (int i = 0; i < 5; i++) {
                encryptedCodes[i] = Utils.encrypt(codes[i], sukey);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Locker.setCodes: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void empty() {
        lastName = "";
        firstName = "";
        sizeInCm = 0;
        schoolClassName = "";
        rentedFromDate = "";
        rentedUntilDate = "";
        hasContract = false;
        paidAmount = 0;
        previoulyPaidAmount = 0;
        currentCodeIndex = (currentCodeIndex + 1) % encryptedCodes.length;

        setAppropriateColor();
    }

    public void setTo(Locker newdata) {
        lastName = newdata.lastName;
        firstName = newdata.firstName;
        sizeInCm = newdata.sizeInCm;
        schoolClassName = newdata.schoolClassName;
        rentedFromDate = newdata.rentedFromDate;
        rentedUntilDate = newdata.rentedUntilDate;
        hasContract = newdata.hasContract;
        paidAmount = newdata.paidAmount;
        previoulyPaidAmount = newdata.previoulyPaidAmount;

        setAppropriateColor();
    }

    public void setID(String id) {
        setText(this.id = id);
    }

    public void setLastName(String sirname) {
        lastName = sirname;
    }

    public void setOwnerName(String name) {
        firstName = name;
    }

    public void setOwnerSize(int size) {
        sizeInCm = size;
    }

    public void setClass(String _class) {
        schoolClassName = _class;
    }

    public void setFromDate(String fromdate) {
        rentedFromDate = fromdate;
    }

    public void setUntilDate(String untildate) {
        rentedUntilDate = untildate;
    }

    public void setContract(boolean hascontract) {
        hasContract = hascontract;
    }

    public void setMoney(int money) {
        paidAmount = money;
    }

    public void setPrevAmount(int amount) {
        previoulyPaidAmount = amount;
    }

    public void setCurrentCodeIndex(int index) {
        currentCodeIndex = index;
    }

    public void setNextCode() {
        currentCodeIndex = (currentCodeIndex + 1) % 5;
    }

    public void setOutOfOrder(boolean outoforder) {
        isOutOfOrder = outoforder;
    }

    public void setSelected() {
        isSelected = true;
        setColor(SELECTED_COLOR);
    }

    public void setLock(String lock) {
        lockCode = lock;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public final void setUpMouseListener() {
        if (this.getMouseListeners().length == 0) {
            addMouseListener(new MouseListener());
        }
    }

    public final void setColor(int index) {
        setBackground(BACKGROUND_COLORS[index]);
        setForeground(FOREGROUND_COLORS[index]);
    }

    public String getId() {
        return id;
    }

    public String getSurname() {
        return lastName;
    }

    public String getOwnerName() {
        return firstName;
    }

    public int getOwnerSize() {
        return sizeInCm;
    }

    public String getOwnerClass() {
        return schoolClassName;
    }

    public String getFromDate() {
        return rentedFromDate;
    }

    public String getUntilDate() {
        return rentedUntilDate;
    }

    public boolean hasContract() {
        return hasContract;
    }

    public int getMoney() {
        return paidAmount;
    }

    public int getPrevAmount() {
        return previoulyPaidAmount;
    }

    public int getCurrentCodeIndex() {
        return currentCodeIndex;
    }

    public boolean isOutOfOrder() {
        return isOutOfOrder;
    }

    public String getLock() {
        return lockCode;
    }

    public String getNote() {
        return note;
    }

    public boolean isFree() {
        return firstName.equals("");
    }

    public String getCurrentCode(SecretKey sukey) {
        return getCode(currentCodeIndex, sukey);
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public Locker getCopy() throws CloneNotSupportedException {
        return (Locker) this.clone();
    }

    public static final int OUTOFORDER_COLOR = 0;
    public static final int RENTED_COLOR = 1;
    public static final int FREE_COLOR = 2;
    public static final int SELECTED_COLOR = 3;
    public static final int NOCONTRACT_COLOR = 4;
    public static final int ONEMONTHREMAINING_COLOR = 5;

    // TODO: use enum
    private static final Color[] BACKGROUND_COLORS = new Color[]
            {
                    new Color(255, 0, 0),
                    new Color(0, 102, 0),
                    new Color(255, 255, 255),
                    new Color(255, 255, 0),
                    new Color(0, 0, 255),
                    new Color(255, 153, 0)
            };

    // TODO: use enum
    private static final Color[] FOREGROUND_COLORS = new Color[]
            {
                    new Color(255, 255, 255),
                    new Color(255, 255, 255),
                    new Color(0, 0, 0),
                    new Color(0, 0, 0),
                    new Color(255, 255, 255),
                    new Color(0, 0, 0)
            };

    /**
     * TODO move to MainFrame
     */
    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            DataManager dm = DataManager.getInstance();

            if (dm.getCurLockerList().size() > 0) {
                dm.getCurLocker().setAppropriateColor();
            }

            Locker locker = (Locker) e.getSource();
            dm.getCurWalk().setCurLockerIndex(locker);

            locker.setSelected();
            DataManager.getInstance().getMainFrame().showLockerInformation();
        }
    }
}
