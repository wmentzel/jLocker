package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.DataManager;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.randomlychosenbytes.jlocker.manager.Utils.*;

public class Locker extends JLabel implements Cloneable {

    @Expose
    private String id;

    @Expose
    private String lastName;

    @Expose
    private String firstName;

    @Expose
    private int heightInCm;

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

    public String[] getEncryptedCodes() {
        return encryptedCodes;
    }

    // transient
    private Boolean isSelected = false;

    public Locker(
            String id, String firstName, String lastName, int heightInCm,
            String schoolClass, String rentedFrom, String rentedUntil, boolean hasContract,
            int paidAmount, int previoulyPaidAmount, int currentCodeIndex, String[] encryptedCodes, String lockCode,
            boolean isOutOfOrder, String note
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.heightInCm = heightInCm;
        this.schoolClassName = schoolClass;
        this.rentedFromDate = rentedFrom;
        this.rentedUntilDate = rentedUntil;
        this.hasContract = hasContract;
        this.paidAmount = paidAmount;
        this.previoulyPaidAmount = previoulyPaidAmount;
        this.previoulyPaidAmount = paidAmount;
        this.isOutOfOrder = isOutOfOrder;
        this.lockCode = lockCode;
        this.note = note;
        this.isSelected = false;

        this.currentCodeIndex = currentCodeIndex;
        this.encryptedCodes = encryptedCodes;

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

        String code = decrypt(encryptedCodes[i], sukey);
        return code.substring(0, 2) + "-" + code.substring(2, 4) + "-" + code.substring(4, 6);
    }

    public void setCodes(String[] codes, SecretKey superUserMasterKey) {
        // codes is unencrypted... encrypting and saving in encCodes

        // The Value of code[i] looks like "00-00-00"
        // it is saved without the "-", so we have
        // to remove them.

        encryptedCodes = new String[5];

        for (int i = 0; i < 5; i++) {
            codes[i] = codes[i].replace("-", "");
        }

        for (int i = 0; i < 5; i++) {
            encryptedCodes[i] = encrypt(codes[i], superUserMasterKey);
        }
    }

    public long getRemainingTimeInMonths() {
        if (rentedUntilDate.equals("") || rentedFromDate.equals("") || isFree()) {
            return 0;
        }

        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        Calendar end = getCalendarFromString(rentedUntilDate);

        return getDifferenceInMonths(today, end);
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

    public void empty() {
        lastName = "";
        firstName = "";
        heightInCm = 0;
        schoolClassName = "";
        rentedFromDate = "";
        rentedUntilDate = "";
        hasContract = false;
        paidAmount = 0;
        previoulyPaidAmount = 0;

        if (encryptedCodes == null) {
            currentCodeIndex = 0;
        } else {
            currentCodeIndex = (currentCodeIndex + 1) % encryptedCodes.length;
        }
        setAppropriateColor();
    }

    public void setTo(Locker newdata) {
        lastName = newdata.lastName;
        firstName = newdata.firstName;
        heightInCm = newdata.heightInCm;
        schoolClassName = newdata.schoolClassName;
        rentedFromDate = newdata.rentedFromDate;
        rentedUntilDate = newdata.rentedUntilDate;
        hasContract = newdata.hasContract;
        paidAmount = newdata.paidAmount;
        previoulyPaidAmount = newdata.previoulyPaidAmount;

        setAppropriateColor();
    }

    public void setId(String id) {
        setText(this.id = id);
    }

    public void setLastName(String sirname) {
        lastName = sirname;
    }

    public void setFirstName(String name) {
        firstName = name;
    }

    public void setHeightInCm(int size) {
        heightInCm = size;
    }

    public void setSchoolClass(String _class) {
        schoolClassName = _class;
    }

    public void setFromDate(String fromdate) {
        rentedFromDate = fromdate;
    }

    public void setUntilDate(String untildate) {
        rentedUntilDate = untildate;
    }

    public void setHasContract(boolean hascontract) {
        hasContract = hascontract;
    }

    public void setPaidAmount(int money) {
        paidAmount = money;
    }

    public void setPreviouslyPaidAmount(int amount) {
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

    public void setLockCode(String lock) {
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

    public String getLastName() {
        return lastName;
    }

    public String getOwnerName() {
        return firstName;
    }

    public int getOwnerSize() {
        return heightInCm;
    }

    public String getOwnerClass() {
        return schoolClassName;
    }

    public String getRentedFromDate() {
        return rentedFromDate;
    }

    public String getRentedUntilDate() {
        return rentedUntilDate;
    }

    public boolean hasContract() {
        return hasContract;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public int getPreviouslyPaidAmount() {
        return previoulyPaidAmount;
    }

    public int getCurrentCodeIndex() {
        return currentCodeIndex;
    }

    public boolean isOutOfOrder() {
        return isOutOfOrder;
    }

    public String getLockCode() {
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
