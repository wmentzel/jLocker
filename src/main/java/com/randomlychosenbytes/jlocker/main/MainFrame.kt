package com.randomlychosenbytes.jlocker.main

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit
import com.randomlychosenbytes.jlocker.dialogs.*
import com.randomlychosenbytes.jlocker.manager.DataManager
import com.randomlychosenbytes.jlocker.manager.isDateValid
import com.randomlychosenbytes.jlocker.nonabstractreps.Entity
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker
import com.randomlychosenbytes.jlocker.nonabstractreps.LockerCabinet.Companion.updateDummyRows
import com.randomlychosenbytes.jlocker.nonabstractreps.Pupil
import com.randomlychosenbytes.jlocker.nonabstractreps.SuperUser
import java.awt.*
import java.awt.event.*
import java.util.stream.Collectors
import javax.swing.*
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
class MainFrame : JFrame() {

    private var searchFrame: SearchFrame? = null
    private var tasksFrame: TasksFrame? = null

    private lateinit var timer: Timer
    private var dataManager = DataManager

    fun initialize() {
        initComponents()

        // center on screen
        setLocationRelativeTo(null)

        //
        // Set application title from resources
        //
        title = dataManager.appTitle + " " + dataManager.appVersion

        // TODO remove in later versions
        dataManager.mainFrame = this

        //
        // Ask to save changes on exit
        //
        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(winEvt: WindowEvent) {
                    if (dataManager.hasDataChanged) {
                        val answer = JOptionPane.showConfirmDialog(
                            null,
                            "Wollen Sie Ihre Änderungen speichern?",
                            "Speichern und beenden",
                            JOptionPane.YES_NO_CANCEL_OPTION
                        )
                        if (answer == JOptionPane.CANCEL_OPTION) {
                            return
                        }
                        if (answer == JOptionPane.YES_OPTION) {
                            dataManager.saveAndCreateBackup()
                        }
                    }
                    System.exit(0)
                }
            }
        )

        //
        // Initialize status message timer
        //
        val resetStatusMessage = ActionListener { statusMessageLabel.text = "" }
        timer = Timer(5000, resetStatusMessage).apply {
            isRepeats = true
        }

        //
        // Show CreateUserDialog if there are none
        //
        val newResFile = dataManager.ressourceFile
        if (!newResFile.exists()) {
            val dialog = CreateUsersDialog(this, true)
            dialog.isVisible = true
        }

        //
        // LogIn
        //
        val dialog = LogInDialog(this, true)
        dialog.isVisible = true

        //
        // Initialize UI
        //
        setComboBoxes2CurIndizes()

        // If the super user is logged in, he is allowed to change to passwords.
        changeUserPWMenuItem.isEnabled = dataManager.currentUser is SuperUser
    }

    /**
     * Put the lockers of the current walk in the layout manager.
     */
    fun drawLockerOverview() {
        // Remove old panels
        lockerOverviewPanel.removeAll()

        val mus = dataManager.currentManagmentUnitList.map { it to ManagementUnit(it.type) }.map { (oldMu, newMu) ->
            ManagementUnit(oldMu.type).also { newMu ->
                when (oldMu.type) {
                    ManagementUnit.ROOM -> {
                        newMu.room.setCaption(oldMu.room.roomName, oldMu.room.schoolClassName)
                    }
                    ManagementUnit.LOCKER_CABINET -> {
                        val newLockers = oldMu.lockerCabinet.lockers.map { locker: Locker -> Locker(locker) }
                        newMu.lockerCabinet.lockers = newLockers.toMutableList()
                    }
                    ManagementUnit.STAIRCASE -> {
                        newMu.staircase.setCaption(oldMu.staircase.staircaseName)
                    }
                }
            }
        }

        dataManager.currentWalk.managementUnits = mus
        val numManagementUnits = mus.size
        var firstLockerFound = false
        for (i in 0 until numManagementUnits) {
            val mu = mus[i]
            lockerOverviewPanel.add(mu)
            val lockers: List<Locker> = mu.lockerCabinet.lockers
            for (locker in lockers) {
                // always set a standard locker as selected
                if (!lockers.isEmpty() && !firstLockerFound) {
                    lockers[0].setSelected()
                    dataManager.currentManagementUnitIndex = i
                    dataManager.currentLockerIndex = 0
                    firstLockerFound = true
                } else {
                    locker.setAppropriateColor()
                }
            }
        }
        updateDummyRows(mus.stream().map { obj: ManagementUnit -> obj.lockerCabinet }.collect(Collectors.toList()))
        showLockerInformation()
        lockerOverviewPanel.updateUI()
    }

    /**
     * When a locker is clicked, it's data is displayed in the respective
     * GUI components (surname, name, etc.)
     */
    fun showLockerInformation() {
        if (dataManager.currentLockerList.isEmpty()) {
            containerPanel.isVisible = false
            return
        }

        //
        // Initialize all childs of userDataPanel
        //
        containerPanel.isVisible = true
        val locker = dataManager.currentLocker
        if (locker.isFree) {
            surnameTextField.text = ""
            nameTextField.text = ""
            classTextField.text = ""
            sizeTextField.text = ""
            hasContractCheckbox.isSelected = false
            moneyTextField.text = ""
            previousAmountTextField.text = ""
            fromDateTextField.text = ""
            untilDateTextField.text = ""
            remainingTimeInMonthsTextField.text = ""
        } else {
            surnameTextField.text = locker.pupil.lastName
            nameTextField.text = locker.pupil.firstName
            classTextField.text = locker.pupil.schoolClassName
            sizeTextField.text = Integer.toString(locker.pupil.heightInCm)
            hasContractCheckbox.isSelected = locker.pupil.hasContract
            outOfOrderCheckbox.isSelected = locker.isOutOfOrder
            moneyTextField.text = Integer.toString(locker.pupil.paidAmount)
            previousAmountTextField.text = Integer.toString(locker.pupil.previouslyPaidAmount)
            fromDateTextField.text = locker.pupil.rentedFromDate
            untilDateTextField.text = locker.pupil.rentedUntilDate
            val months = locker.pupil.remainingTimeInMonths
            remainingTimeInMonthsTextField.text =
                months.toString() + " " + if (months == 1L) "Monat" else "Monate"
        }
        lockerIDTextField.text = locker.id
        outOfOrderCheckbox.isSelected = locker.isOutOfOrder

        // Combobox initialization
        if (dataManager.currentUser is SuperUser) {
            codeTextField.text = locker.getCurrentCode(dataManager.superUserMasterKey)
        } else {
            codeTextField.text = "00-00-00"
        }
        lockTextField.text = locker.lockCode
        noteTextArea.text = locker.note
    }

    private fun getOrCreatePupil(locker: Locker): Pupil {
        if (locker.isFree) {
            locker.moveInNewOwner(Pupil())
        }
        return locker.pupil
    }

    /**
     * When executed the data from the GUI components is written into the locker
     * object.
     */
    private fun setLockerInformation() {
        val locker = dataManager.currentLocker
        val id = lockerIDTextField.text
        if (dataManager.currentLocker.id != id && dataManager.isLockerIdUnique(id) && !id.isBlank()) {
            locker.id = id
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Diese Schließfach-ID existiert bereits! Wählen Sie eine andere.",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        locker.isOutOfOrder = outOfOrderCheckbox.isSelected
        locker.lockCode = lockTextField.text
        locker.note = noteTextArea.text
        val lastName = surnameTextField.text
        if (lastName.isNotBlank()) {
            getOrCreatePupil(locker).lastName = lastName
        }
        val firstName = nameTextField.text
        if (firstName.isNotBlank()) {
            getOrCreatePupil(locker).firstName = firstName
        }
        val heightString = sizeTextField.text
        if (heightString.isNotBlank()) {
            try {
                val height = heightString.toInt()
                getOrCreatePupil(locker).heightInCm = height
            } catch (e: NumberFormatException) {
                JOptionPane.showMessageDialog(
                    null,
                    "Die eigegebene Größe ist ungültig!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        val from = fromDateTextField.text
        if (from.isNotBlank()) {
            if (isDateValid(from)) {
                getOrCreatePupil(locker).rentedFromDate = from
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Das Anfangsdatum ist ungültig (Format DD.MM.YYYY)!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        val until = untilDateTextField.text
        if (until.isNotBlank()) {
            if (isDateValid(until)) {
                getOrCreatePupil(locker).rentedUntilDate = until
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Das Enddatum ist ungültig (Format DD.MM.YYYY)!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        if (!locker.isFree) {
            val months = locker.pupil.remainingTimeInMonths
            remainingTimeInMonthsTextField.text = months.toString() + " " + if (months == 1L) "Monat" else "Monate"
        }
        val schoolClassName = classTextField.text
        if (schoolClassName.isNotBlank()) {
            getOrCreatePupil(locker).schoolClassName = schoolClassName
        }
        if (hasContractCheckbox.isSelected) {
            getOrCreatePupil(locker).hasContract = hasContractCheckbox.isSelected
        }
    }

    fun setStatusMessage(message: String?) {
        if (timer.isRunning) {
            timer.restart()
        } else {
            timer.start()
        }
        statusMessageLabel.text = message
    }

    /**
     * Determines the scroll position to bring a certain locker into sight.
     */
    fun bringCurrentLockerInSight() {
        val r = dataManager.currentManamentUnit.bounds
        lockerOverviewScrollPane.horizontalScrollBar.value = r.x
        lockerOverviewScrollPane.verticalScrollBar.value = dataManager.currentLocker.bounds.y
    }

    /**
     * Initializes a combo box with a given list of entities.
     */
    private fun initializeComboBox(list: List<Entity>, combobox: JComboBox<String>) {
        combobox.setModel(DefaultComboBoxModel(list.map { it.name }.toTypedArray()))
    }

    /**
     * Sets all three combo boxes to the current indices of the building,
     * floor and walk.
     */
    fun setComboBoxes2CurIndizes() {
        initializeComboBox(dataManager.buildingList, buildingComboBox)
        buildingComboBox.selectedIndex = dataManager.currentBuildingIndex
        initializeComboBox(dataManager.currentFloorList, floorComboBox)
        floorComboBox.selectedIndex = dataManager.currentFloorIndex
        initializeComboBox(dataManager.currentWalkList, walkComboBox)
        walkComboBox.selectedIndex = dataManager.currentWalkIndex
        removeBuildingButton.isEnabled = dataManager.buildingList.size > 1
        removeFloorButton.isEnabled = dataManager.currentFloorList.size > 1
        removeWalkButton.isEnabled = dataManager.currentWalkList.size > 1
        drawLockerOverview()
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        centerPanel = JPanel()
        comboBoxPanel = JPanel()
        buildingsPanel = JPanel()
        buildingsLabel = JLabel()
        buildingComboBox = JComboBox()
        addBuildingButton = JButton()
        removeBuildingButton = JButton()
        editBuildingButton = JButton()
        floorPanel = JPanel()
        floorLabel = JLabel()
        floorComboBox = JComboBox()
        addFloorButton = JButton()
        removeFloorButton = JButton()
        editFloorButton = JButton()
        walksPanel = JPanel()
        walkLabel = JLabel()
        walkComboBox = JComboBox()
        addWalkButton = JButton()
        removeWalkButton = JButton()
        editWalkButton = JButton()
        lockerOverviewScrollPane = JScrollPane()
        lockerOverviewPanel = JPanel()
        userScrollPane = JScrollPane()
        containerPanel = JPanel()
        leftFlowLayout = JPanel()
        legendPanel = JPanel()
        noContractPanel = JPanel()
        noContractLabel = JLabel()
        noContractColorLabel = JPanel()
        freePanel = JPanel()
        freeLabel = JLabel()
        freeColorPanel = JPanel()
        rentedPanel = JPanel()
        rentedLabel = JLabel()
        rentedColorPanel = JPanel()
        outOfOrderPanel = JPanel()
        outOfOrderLabel = JLabel()
        outOfOrderColorPanel = JPanel()
        oneMonthRemainingPanel = JPanel()
        oneMonthRemainingLabel = JLabel()
        oneMonthRemainingColorPanel = JPanel()
        dataPanel = JPanel()
        userPanel = JPanel()
        lockerIDLabel = JLabel()
        lockerIDTextField = JTextField()
        surnameLabel = JLabel()
        surnameTextField = JTextField()
        nameLabel = JLabel()
        nameTextField = JTextField()
        classLabel = JLabel()
        classTextField = JTextField()
        sizeLabel = JLabel()
        sizeTextField = JTextField()
        noteLabel = JLabel()
        noteTextArea = JTextField()
        middlePanel = JPanel()
        lockerPanel = JPanel()
        fromDateLabel = JLabel()
        fromDateTextField = JTextField()
        untilDateLabel = JLabel()
        untilDateTextField = JTextField()
        remainingTimeInMonthsLabel = JLabel()
        remainingTimeInMonthsTextField = JTextField()
        currentPinLabel = JLabel()
        codeTextField = JTextField()
        lockLabel = JLabel()
        lockTextField = JTextField()
        checkBoxPanel = JPanel()
        outOfOrderCheckbox = JCheckBox()
        hasContractCheckbox = JCheckBox()
        moneyPanel = JPanel()
        gridLayoutPanel = JPanel()
        moneyLabel = JLabel()
        moneyTextField = JTextField()
        previousAmountLabel = JLabel()
        previousAmountTextField = JTextField()
        currentAmountTextField = JTextField()
        addAmountButton = JButton()
        statusPanel = JPanel()
        buttonPanel = JPanel()
        saveButton = JButton()
        emptyButton = JButton()
        statusMessageLabel = JLabel()
        menuBar = JMenuBar()
        fileMenu = JMenu()
        showTasksMenuItem = JMenuItem()
        saveMenuItem = JMenuItem()
        loadMenuItem = JMenuItem()
        exitMenu = JMenuItem()
        editMenu = JMenu()
        changeClassMenuItem = JMenuItem()
        moveLockerMenuItem = JMenuItem()
        moveClassMenuItem = JMenuItem()
        changeUserPWMenuItem = JMenuItem()
        settingsMenuItem = JMenuItem()
        searchMenu = JMenu()
        searchMenuItem = JMenuItem()
        helpMenu = JMenu()
        aboutMenuItem = JMenuItem()
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        preferredSize = Dimension(1000, 600)
        centerPanel.background = Color(189, 205, 149)
        centerPanel.layout = BorderLayout()
        comboBoxPanel.background = Color(189, 205, 149)
        comboBoxPanel.layout = FlowLayout(FlowLayout.LEFT)
        buildingsPanel.background = Color(189, 205, 149)
        buildingsLabel.text = "Gebäude"
        buildingsPanel.add(buildingsLabel)
        buildingComboBox.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuCanceled(evt: PopupMenuEvent) {}
            override fun popupMenuWillBecomeInvisible(evt: PopupMenuEvent) {
                buildingComboBoxPopupMenuWillBecomeInvisible(evt)
            }

            override fun popupMenuWillBecomeVisible(evt: PopupMenuEvent) {}
        })
        buildingsPanel.add(buildingComboBox)
        addBuildingButton.background = Color(189, 205, 149)
        addBuildingButton.text = "+"
        addBuildingButton.addActionListener { evt -> addBuildingButtonActionPerformed(evt) }
        buildingsPanel.add(addBuildingButton)
        removeBuildingButton.background = Color(189, 205, 149)
        removeBuildingButton.text = "-"
        removeBuildingButton.addActionListener { evt -> removeBuildingButtonActionPerformed(evt) }
        buildingsPanel.add(removeBuildingButton)
        editBuildingButton.background = Color(189, 205, 149)
        editBuildingButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editBuildingButton.addActionListener { evt -> editBuildingButtonActionPerformed(evt) }
        buildingsPanel.add(editBuildingButton)
        comboBoxPanel.add(buildingsPanel)
        floorPanel.background = Color(189, 205, 149)
        floorLabel.text = "Etage"
        floorPanel.add(floorLabel)
        floorComboBox.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuCanceled(evt: PopupMenuEvent) {}
            override fun popupMenuWillBecomeInvisible(evt: PopupMenuEvent) {
                floorComboBoxPopupMenuWillBecomeInvisible(evt)
            }

            override fun popupMenuWillBecomeVisible(evt: PopupMenuEvent) {}
        })
        floorPanel.add(floorComboBox)
        addFloorButton.background = Color(189, 205, 149)
        addFloorButton.text = "+"
        addFloorButton.addActionListener { evt -> addFloorButtonActionPerformed(evt) }
        floorPanel.add(addFloorButton)
        removeFloorButton.background = Color(189, 205, 149)
        removeFloorButton.text = "-"
        removeFloorButton.addActionListener { evt -> removeFloorButtonActionPerformed(evt) }
        floorPanel.add(removeFloorButton)
        editFloorButton.background = Color(189, 205, 149)
        editFloorButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editFloorButton.addActionListener { evt -> editFloorButtonActionPerformed(evt) }
        floorPanel.add(editFloorButton)
        comboBoxPanel.add(floorPanel)
        walksPanel.background = Color(189, 205, 149)
        walkLabel.text = "Gang"
        walksPanel.add(walkLabel)
        walkComboBox.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuCanceled(evt: PopupMenuEvent) {}
            override fun popupMenuWillBecomeInvisible(evt: PopupMenuEvent) {
                walkComboBoxPopupMenuWillBecomeInvisible(evt)
            }

            override fun popupMenuWillBecomeVisible(evt: PopupMenuEvent) {}
        })
        walksPanel.add(walkComboBox)
        addWalkButton.background = Color(189, 205, 149)
        addWalkButton.text = "+"
        addWalkButton.addActionListener { evt -> addWalkButtonActionPerformed(evt) }
        walksPanel.add(addWalkButton)
        removeWalkButton.background = Color(189, 205, 149)
        removeWalkButton.text = "-"
        removeWalkButton.addActionListener { evt -> removeWalkButtonActionPerformed(evt) }
        walksPanel.add(removeWalkButton)
        editWalkButton.background = Color(189, 205, 149)
        editWalkButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editWalkButton.addActionListener { evt -> editWalkButtonActionPerformed(evt) }
        walksPanel.add(editWalkButton)
        comboBoxPanel.add(walksPanel)
        centerPanel.add(comboBoxPanel, BorderLayout.NORTH)
        lockerOverviewScrollPane.border = null
        lockerOverviewPanel.background = Color(189, 205, 149)
        lockerOverviewPanel.layout = GridLayout(1, 0, 5, 0)
        lockerOverviewScrollPane.setViewportView(lockerOverviewPanel)
        centerPanel.add(lockerOverviewScrollPane, BorderLayout.CENTER)
        containerPanel.background = Color(189, 205, 149)
        containerPanel.layout = FlowLayout(FlowLayout.LEFT)
        leftFlowLayout.background = Color(189, 205, 149)
        leftFlowLayout.layout = GridBagLayout()
        legendPanel.background = Color(189, 205, 149)
        noContractPanel.background = Color(189, 205, 149)
        noContractLabel.text = "kein Vertrag"
        noContractPanel.add(noContractLabel)
        noContractColorLabel.background = Color(0, 0, 255)
        noContractColorLabel.preferredSize = Dimension(8, 8)
        val noContractColorLabelLayout = GroupLayout(noContractColorLabel)
        noContractColorLabel.layout = noContractColorLabelLayout
        noContractColorLabelLayout.setHorizontalGroup(
            noContractColorLabelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        noContractColorLabelLayout.setVerticalGroup(
            noContractColorLabelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        noContractPanel.add(noContractColorLabel)
        legendPanel.add(noContractPanel)
        freePanel.background = Color(189, 205, 149)
        freeLabel.text = "Frei"
        freePanel.add(freeLabel)
        freeColorPanel.background = Color(255, 255, 255)
        freeColorPanel.border = BorderFactory.createLineBorder(Color(0, 0, 0))
        freeColorPanel.preferredSize = Dimension(8, 8)
        val freeColorPanelLayout = GroupLayout(freeColorPanel)
        freeColorPanel.layout = freeColorPanelLayout
        freeColorPanelLayout.setHorizontalGroup(
            freeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 6, Short.MAX_VALUE.toInt())
        )
        freeColorPanelLayout.setVerticalGroup(
            freeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 6, Short.MAX_VALUE.toInt())
        )
        freePanel.add(freeColorPanel)
        legendPanel.add(freePanel)
        rentedPanel.background = Color(189, 205, 149)
        rentedLabel.text = "Vermietet"
        rentedPanel.add(rentedLabel)
        rentedColorPanel.background = Color(0, 102, 0)
        rentedColorPanel.preferredSize = Dimension(8, 8)
        val rentedColorPanelLayout = GroupLayout(rentedColorPanel)
        rentedColorPanel.layout = rentedColorPanelLayout
        rentedColorPanelLayout.setHorizontalGroup(
            rentedColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        rentedColorPanelLayout.setVerticalGroup(
            rentedColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        rentedPanel.add(rentedColorPanel)
        legendPanel.add(rentedPanel)
        outOfOrderPanel.background = Color(189, 205, 149)
        outOfOrderLabel.text = "Defekt"
        outOfOrderPanel.add(outOfOrderLabel)
        outOfOrderColorPanel.background = Color(255, 0, 0)
        outOfOrderColorPanel.preferredSize = Dimension(8, 8)
        val outOfOrderColorPanelLayout = GroupLayout(outOfOrderColorPanel)
        outOfOrderColorPanel.layout = outOfOrderColorPanelLayout
        outOfOrderColorPanelLayout.setHorizontalGroup(
            outOfOrderColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        outOfOrderColorPanelLayout.setVerticalGroup(
            outOfOrderColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        outOfOrderPanel.add(outOfOrderColorPanel)
        legendPanel.add(outOfOrderPanel)
        oneMonthRemainingPanel.background = Color(189, 205, 149)
        oneMonthRemainingLabel.text = "Mietdauer >= 1 Monat"
        oneMonthRemainingPanel.add(oneMonthRemainingLabel)
        oneMonthRemainingColorPanel.background = Color(255, 153, 0)
        oneMonthRemainingColorPanel.preferredSize = Dimension(8, 8)
        val oneMonthRemainingColorPanelLayout = GroupLayout(oneMonthRemainingColorPanel)
        oneMonthRemainingColorPanel.layout = oneMonthRemainingColorPanelLayout
        oneMonthRemainingColorPanelLayout.setHorizontalGroup(
            oneMonthRemainingColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        oneMonthRemainingColorPanelLayout.setVerticalGroup(
            oneMonthRemainingColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        oneMonthRemainingPanel.add(oneMonthRemainingColorPanel)
        legendPanel.add(oneMonthRemainingPanel)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        leftFlowLayout.add(legendPanel, gridBagConstraints)
        dataPanel.background = Color(189, 205, 149)
        dataPanel.layout = GridBagLayout()
        userPanel.background = Color(189, 205, 149)
        userPanel.layout = GridLayout(6, 2, 5, 3)
        lockerIDLabel.text = "Schließfach-ID           "
        userPanel.add(lockerIDLabel)
        userPanel.add(lockerIDTextField)
        surnameLabel.text = "Nachname"
        userPanel.add(surnameLabel)
        userPanel.add(surnameTextField)
        nameLabel.text = "Vorname"
        userPanel.add(nameLabel)
        userPanel.add(nameTextField)
        classLabel.text = "Klasse"
        userPanel.add(classLabel)
        userPanel.add(classTextField)
        sizeLabel.text = "Größe"
        userPanel.add(sizeLabel)
        userPanel.add(sizeTextField)
        noteLabel.text = "Notiz"
        userPanel.add(noteLabel)
        userPanel.add(noteTextArea)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 20)
        dataPanel.add(userPanel, gridBagConstraints)
        middlePanel.background = Color(189, 205, 149)
        middlePanel.layout = GridBagLayout()
        lockerPanel.background = Color(189, 205, 149)
        lockerPanel.layout = GridLayout(5, 2, 5, 3)
        fromDateLabel.text = "von"
        lockerPanel.add(fromDateLabel)
        lockerPanel.add(fromDateTextField)
        untilDateLabel.text = "bis"
        lockerPanel.add(untilDateLabel)
        lockerPanel.add(untilDateTextField)
        remainingTimeInMonthsLabel.text = "verbleibende Monate"
        lockerPanel.add(remainingTimeInMonthsLabel)
        lockerPanel.add(remainingTimeInMonthsTextField)
        currentPinLabel.text = "aktueller Code"
        lockerPanel.add(currentPinLabel)
        codeTextField.isEditable = false
        codeTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                codeTextFieldMouseClicked(evt)
            }
        })
        lockerPanel.add(codeTextField)
        lockLabel.text = "Schloss"
        lockerPanel.add(lockLabel)
        lockerPanel.add(lockTextField)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        middlePanel.add(lockerPanel, gridBagConstraints)
        checkBoxPanel.background = Color(189, 205, 149)
        checkBoxPanel.layout = GridBagLayout()
        outOfOrderCheckbox.background = Color(189, 205, 149)
        outOfOrderCheckbox.text = "defekt"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        checkBoxPanel.add(outOfOrderCheckbox, gridBagConstraints)
        hasContractCheckbox.background = Color(189, 205, 149)
        hasContractCheckbox.text = "Vertrag"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        checkBoxPanel.add(hasContractCheckbox, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.anchor = GridBagConstraints.WEST
        middlePanel.add(checkBoxPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 30)
        dataPanel.add(middlePanel, gridBagConstraints)
        moneyPanel.background = Color(189, 205, 149)
        moneyPanel.border =
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color(131, 150, 81)), "Finanzen")
        moneyPanel.layout = GridBagLayout()
        gridLayoutPanel.background = Color(189, 205, 149)
        gridLayoutPanel.layout = GridLayout(3, 2, 10, 3)
        moneyLabel.text = "Kontostand"
        gridLayoutPanel.add(moneyLabel)
        moneyTextField.isEditable = false
        gridLayoutPanel.add(moneyTextField)
        previousAmountLabel.text = "zuletzt eingezahlt"
        gridLayoutPanel.add(previousAmountLabel)
        previousAmountTextField.isEditable = false
        gridLayoutPanel.add(previousAmountTextField)
        currentAmountTextField.columns = 3
        gridLayoutPanel.add(currentAmountTextField)
        addAmountButton.background = Color(189, 205, 149)
        addAmountButton.text = "Einzahlen"
        addAmountButton.addActionListener { evt -> addAmountButtonActionPerformed(evt) }
        gridLayoutPanel.add(addAmountButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(5, 5, 5, 5)
        moneyPanel.add(gridLayoutPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.anchor = GridBagConstraints.NORTH
        dataPanel.add(moneyPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(5, 10, 0, 0)
        leftFlowLayout.add(dataPanel, gridBagConstraints)
        statusPanel.background = Color(189, 205, 149)
        statusPanel.layout = GridBagLayout()
        buttonPanel.background = Color(189, 205, 149)
        buttonPanel.layout = GridBagLayout()
        saveButton.background = Color(189, 205, 149)
        saveButton.text = "Speichern"
        saveButton.addActionListener { evt -> saveButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        buttonPanel.add(saveButton, gridBagConstraints)
        emptyButton.background = Color(189, 205, 149)
        emptyButton.text = "Leeren"
        emptyButton.addActionListener { evt -> emptyButtonActionPerformed(evt) }
        buttonPanel.add(emptyButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.anchor = GridBagConstraints.WEST
        statusPanel.add(buttonPanel, gridBagConstraints)
        statusMessageLabel.text = "Status"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(0, 20, 0, 0)
        statusPanel.add(statusMessageLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(10, 10, 10, 0)
        leftFlowLayout.add(statusPanel, gridBagConstraints)
        containerPanel.add(leftFlowLayout)
        userScrollPane.setViewportView(containerPanel)
        centerPanel.add(userScrollPane, BorderLayout.SOUTH)
        contentPane.add(centerPanel, BorderLayout.CENTER)
        fileMenu.text = "Datei"
        showTasksMenuItem.text = "Aufgaben anzeigen"
        showTasksMenuItem.addActionListener { evt -> showTasksMenuItemActionPerformed(evt) }
        fileMenu.add(showTasksMenuItem)
        saveMenuItem.text = "Speichern"
        saveMenuItem.addActionListener { evt -> saveMenuItemActionPerformed(evt) }
        fileMenu.add(saveMenuItem)
        loadMenuItem.text = "Laden"
        loadMenuItem.addActionListener { evt -> loadMenuItemActionPerformed(evt) }
        fileMenu.add(loadMenuItem)
        exitMenu.text = "Beenden"
        exitMenu.addActionListener { evt -> exitMenuActionPerformed(evt) }
        fileMenu.add(exitMenu)
        menuBar.add(fileMenu)
        editMenu.text = "Bearbeiten"
        changeClassMenuItem.text = "Klassenänderung"
        changeClassMenuItem.addActionListener { evt -> changeClassMenuItemActionPerformed(evt) }
        editMenu.add(changeClassMenuItem)
        moveLockerMenuItem.text = "Schließfachumzug"
        moveLockerMenuItem.addActionListener { evt -> moveLockerMenuItemActionPerformed(evt) }
        editMenu.add(moveLockerMenuItem)
        moveClassMenuItem.text = "Klassenumzug"
        moveClassMenuItem.addActionListener { evt -> moveClassMenuItemActionPerformed(evt) }
        editMenu.add(moveClassMenuItem)
        changeUserPWMenuItem.text = "Benutzerpasswörter ändern"
        changeUserPWMenuItem.addActionListener { evt -> changeUserPWMenuItemActionPerformed(evt) }
        editMenu.add(changeUserPWMenuItem)
        settingsMenuItem.text = "Einstellungen"
        settingsMenuItem.addActionListener { evt -> settingsMenuItemActionPerformed(evt) }
        editMenu.add(settingsMenuItem)
        menuBar.add(editMenu)
        searchMenu.text = "Suche"
        searchMenuItem.text = "Suche"
        searchMenuItem.addActionListener { evt -> searchMenuItemActionPerformed(evt) }
        searchMenu.add(searchMenuItem)
        menuBar.add(searchMenu)
        helpMenu.text = "Hilfe"
        aboutMenuItem.text = "Über jLocker"
        aboutMenuItem.addActionListener { evt -> aboutMenuItemActionPerformed(evt) }
        helpMenu.add(aboutMenuItem)
        menuBar.add(helpMenu)
        jMenuBar = menuBar
        pack()
    } // </editor-fold>//GEN-END:initComponents

    private fun removeBuildingButtonActionPerformed(evt: ActionEvent) { //GEN-FIRST:event_removeBuildingButtonActionPerformed
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie dieses Gebäude wirklich löschen?",
            "Gebäude löschen",
            JOptionPane.YES_NO_CANCEL_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.buildingList.removeAt(dataManager.currentBuildingIndex)
            dataManager.currentBuildingIndex = dataManager.buildingList.size - 1
            dataManager.currentFloorIndex = 0
            dataManager.currentWalkIndex = 0
            dataManager.currentManagementUnitIndex = 0
            dataManager.currentLockerIndex = 0
            setComboBoxes2CurIndizes()
        }
    } //GEN-LAST:event_removeBuildingButtonActionPerformed

    private fun removeFloorButtonActionPerformed(evt: ActionEvent) { //GEN-FIRST:event_removeFloorButtonActionPerformed
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diese Etage wirklich löschen?",
            "Etage löschen",
            JOptionPane.YES_NO_CANCEL_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.currentFloorList.removeAt(dataManager.currentFloorIndex)
            dataManager.currentFloorIndex = dataManager.currentFloorList.size - 1
            dataManager.currentWalkIndex = 0
            dataManager.currentManagementUnitIndex = 0
            dataManager.currentLockerIndex = 0
            setComboBoxes2CurIndizes()
        }
    } //GEN-LAST:event_removeFloorButtonActionPerformed

    private fun removeWalkButtonActionPerformed(evt: ActionEvent) { //GEN-FIRST:event_removeWalkButtonActionPerformed
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diesen Gang wirklich löschen?",
            "Gang löschen",
            JOptionPane.YES_NO_CANCEL_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.currentWalkList.removeAt(dataManager.currentWalkIndex)
            dataManager.currentWalkIndex = dataManager.currentWalkList.size - 1
            dataManager.currentManagementUnitIndex = 0
            dataManager.currentLockerIndex = 0
            setComboBoxes2CurIndizes()
        }
    } //GEN-LAST:event_removeWalkButtonActionPerformed

    private fun saveButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_saveButtonActionPerformed
    { //GEN-HEADEREND:event_saveButtonActionPerformed
        setLockerInformation()
        dataManager.hasDataChanged = true
    } //GEN-LAST:event_saveButtonActionPerformed

    private fun codeTextFieldMouseClicked(evt: MouseEvent) //GEN-FIRST:event_codeTextFieldMouseClicked
    { //GEN-HEADEREND:event_codeTextFieldMouseClicked
        if (dataManager.currentUser is SuperUser) {
            val dialog = EditCodesDialog(this, true)
            dialog.isVisible = true
        }
    } //GEN-LAST:event_codeTextFieldMouseClicked

    private fun emptyButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_emptyButtonActionPerformed
    { //GEN-HEADEREND:event_emptyButtonActionPerformed
        dataManager.hasDataChanged = true
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie dieses Schließfach wirklich leeren?",
            "Schließfach leeren",
            JOptionPane.YES_NO_CANCEL_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            surnameTextField.text = ""
            nameTextField.text = ""
            classTextField.text = ""
            sizeTextField.text = "0"
            hasContractCheckbox.isSelected = false
            moneyTextField.text = "0"
            previousAmountTextField.text = "0"
            remainingTimeInMonthsTextField.text = ""
            fromDateTextField.text = ""
            untilDateTextField.text = ""
            noteTextArea.text = ""
            val locker = dataManager.currentLocker
            locker.empty()

            // Combobox initialization
            if (dataManager.currentUser is SuperUser) {
                codeTextField.text = locker.getCurrentCode(dataManager.superUserMasterKey)
            } else {
                codeTextField.text = "00-00-00"
            }
        }
    } //GEN-LAST:event_emptyButtonActionPerformed

    private fun addBuildingButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_addBuildingButtonActionPerformed
    { //GEN-HEADEREND:event_addBuildingButtonActionPerformed
        val dialog = BuildingDialog(this, true, BuildingDialog.ADD)
        dialog.isVisible = true
    } //GEN-LAST:event_addBuildingButtonActionPerformed

    private fun addFloorButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_addFloorButtonActionPerformed
    { //GEN-HEADEREND:event_addFloorButtonActionPerformed
        val dialog = FloorDialog(this, true, FloorDialog.ADD)
        dialog.isVisible = true
    } //GEN-LAST:event_addFloorButtonActionPerformed

    private fun editFloorButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_editFloorButtonActionPerformed
    { //GEN-HEADEREND:event_editFloorButtonActionPerformed
        val dialog = FloorDialog(this, true, FloorDialog.EDIT)
        dialog.isVisible = true
    } //GEN-LAST:event_editFloorButtonActionPerformed

    private fun addWalkButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_addWalkButtonActionPerformed
    { //GEN-HEADEREND:event_addWalkButtonActionPerformed
        val dialog = WalkDialog(this, true, WalkDialog.ADD)
        dialog.isVisible = true
    } //GEN-LAST:event_addWalkButtonActionPerformed

    private fun editWalkButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_editWalkButtonActionPerformed
    { //GEN-HEADEREND:event_editWalkButtonActionPerformed
        val dialog = WalkDialog(this, true, WalkDialog.EDIT)
        dialog.isVisible = true
    } //GEN-LAST:event_editWalkButtonActionPerformed

    private fun editBuildingButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_editBuildingButtonActionPerformed
    { //GEN-HEADEREND:event_editBuildingButtonActionPerformed
        val dialog = BuildingDialog(this, true, BuildingDialog.EDIT)
        dialog.isVisible = true
    } //GEN-LAST:event_editBuildingButtonActionPerformed

    private fun buildingComboBoxPopupMenuWillBecomeInvisible(evt: PopupMenuEvent) //GEN-FIRST:event_buildingComboBoxPopupMenuWillBecomeInvisible
    { //GEN-HEADEREND:event_buildingComboBoxPopupMenuWillBecomeInvisible
        dataManager.currentBuildingIndex = buildingComboBox.selectedIndex
        initializeComboBox(dataManager.currentFloorList, floorComboBox)

        // move on to next combobox
        floorComboBoxPopupMenuWillBecomeInvisible(null)
    } //GEN-LAST:event_buildingComboBoxPopupMenuWillBecomeInvisible

    private fun floorComboBoxPopupMenuWillBecomeInvisible(evt: PopupMenuEvent?) //GEN-FIRST:event_floorComboBoxPopupMenuWillBecomeInvisible
    { //GEN-HEADEREND:event_floorComboBoxPopupMenuWillBecomeInvisible
        dataManager.currentFloorIndex = floorComboBox.selectedIndex
        initializeComboBox(dataManager.currentWalkList, walkComboBox)

        // move on to next combobox
        walkComboBoxPopupMenuWillBecomeInvisible(null)
    } //GEN-LAST:event_floorComboBoxPopupMenuWillBecomeInvisible

    private fun walkComboBoxPopupMenuWillBecomeInvisible(evt: PopupMenuEvent?) //GEN-FIRST:event_walkComboBoxPopupMenuWillBecomeInvisible
    { //GEN-HEADEREND:event_walkComboBoxPopupMenuWillBecomeInvisible
        dataManager.currentWalkIndex = walkComboBox.selectedIndex
        dataManager.currentManagementUnitIndex = 0
        removeBuildingButton.isEnabled = dataManager.buildingList.size > 1
        removeFloorButton.isEnabled = dataManager.currentFloorList.size > 1
        removeWalkButton.isEnabled = dataManager.currentWalkList.size > 1
        drawLockerOverview()
    } //GEN-LAST:event_walkComboBoxPopupMenuWillBecomeInvisible

    private fun addAmountButtonActionPerformed(evt: ActionEvent) //GEN-FIRST:event_addAmountButtonActionPerformed
    { //GEN-HEADEREND:event_addAmountButtonActionPerformed
        try {
            val amount: Int = currentAmountTextField.text.toInt()
            dataManager.currentLocker.pupil.previouslyPaidAmount = amount
            val iNewFullAmount = dataManager.currentLocker.pupil.paidAmount + amount
            dataManager.currentLocker.pupil.paidAmount = iNewFullAmount
            previousAmountTextField.text = Integer.toString(amount)
            moneyTextField.text = Integer.toString(iNewFullAmount)
            currentAmountTextField.text = ""
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(
                null,
                "Bitte geben Sie einen gültigen Geldbetrag ein!",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
        }
    } //GEN-LAST:event_addAmountButtonActionPerformed

    private fun showTasksMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_showTasksMenuItemActionPerformed
    { //GEN-HEADEREND:event_showTasksMenuItemActionPerformed
        if (tasksFrame != null) {
            tasksFrame?.dispose()
        }
        tasksFrame = TasksFrame().apply {
            isVisible = true
        }
    } //GEN-LAST:event_showTasksMenuItemActionPerformed

    private fun saveMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_saveMenuItemActionPerformed
    { //GEN-HEADEREND:event_saveMenuItemActionPerformed
        dataManager.saveAndCreateBackup()
    } //GEN-LAST:event_saveMenuItemActionPerformed

    private fun loadMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_loadMenuItemActionPerformed
    { //GEN-HEADEREND:event_loadMenuItemActionPerformed
        dataManager.loadFromCustomFile(loadAsSuperUser = dataManager.currentUser is SuperUser)
        dataManager.initBuildingObject()
        drawLockerOverview()
    } //GEN-LAST:event_loadMenuItemActionPerformed

    private fun exitMenuActionPerformed(evt: ActionEvent) //GEN-FIRST:event_exitMenuActionPerformed
    { //GEN-HEADEREND:event_exitMenuActionPerformed
        System.exit(0)
    } //GEN-LAST:event_exitMenuActionPerformed

    private fun aboutMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_aboutMenuItemActionPerformed
    { //GEN-HEADEREND:event_aboutMenuItemActionPerformed
        val dialog = AboutBox(this, true)
        dialog.isVisible = true
    } //GEN-LAST:event_aboutMenuItemActionPerformed

    private fun changeUserPWMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_changeUserPWMenuItemActionPerformed
    { //GEN-HEADEREND:event_changeUserPWMenuItemActionPerformed
        val dialog = CreateUsersDialog(this, true)
        dialog.isVisible = true
    } //GEN-LAST:event_changeUserPWMenuItemActionPerformed

    private fun settingsMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_settingsMenuItemActionPerformed
    { //GEN-HEADEREND:event_settingsMenuItemActionPerformed
        val dialog = SettingsDialog(this, true)
        dialog.isVisible = true
    } //GEN-LAST:event_settingsMenuItemActionPerformed

    private fun changeClassMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_changeClassMenuItemActionPerformed
    { //GEN-HEADEREND:event_changeClassMenuItemActionPerformed
        val dialog = RenameClassDialog(this, true)
        dialog.isVisible = true
    } //GEN-LAST:event_changeClassMenuItemActionPerformed

    private fun searchMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_searchMenuItemActionPerformed
    { //GEN-HEADEREND:event_searchMenuItemActionPerformed
        searchFrame?.dispose()
        searchFrame = SearchFrame(this).apply {
            isVisible = true
        }
    } //GEN-LAST:event_searchMenuItemActionPerformed

    private fun moveClassMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_moveClassMenuItemActionPerformed
    { //GEN-HEADEREND:event_moveClassMenuItemActionPerformed
        val dialog = MoveClassDialog(this)
        dialog.isVisible = true
    } //GEN-LAST:event_moveClassMenuItemActionPerformed

    private fun moveLockerMenuItemActionPerformed(evt: ActionEvent) //GEN-FIRST:event_moveLockerMenuItemActionPerformed
    { //GEN-HEADEREND:event_moveLockerMenuItemActionPerformed
        val dialog = MoveLockerDialog(this, true)
        dialog.isVisible = true
    } //GEN-LAST:event_moveLockerMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private lateinit var aboutMenuItem: JMenuItem
    private lateinit var addAmountButton: JButton
    private lateinit var addBuildingButton: JButton
    private lateinit var addFloorButton: JButton
    private lateinit var addWalkButton: JButton
    private lateinit var buildingComboBox: JComboBox<String>
    private lateinit var buildingsLabel: JLabel
    private lateinit var buildingsPanel: JPanel
    private lateinit var buttonPanel: JPanel
    private lateinit var centerPanel: JPanel
    private lateinit var changeClassMenuItem: JMenuItem
    private lateinit var changeUserPWMenuItem: JMenuItem
    private lateinit var checkBoxPanel: JPanel
    private lateinit var classLabel: JLabel
    private lateinit var classTextField: JTextField
    private lateinit var codeTextField: JTextField
    private lateinit var comboBoxPanel: JPanel
    private lateinit var containerPanel: JPanel
    private lateinit var currentAmountTextField: JTextField
    private lateinit var currentPinLabel: JLabel
    private lateinit var dataPanel: JPanel
    private lateinit var editBuildingButton: JButton
    private lateinit var editFloorButton: JButton
    private lateinit var editMenu: JMenu
    private lateinit var editWalkButton: JButton
    private lateinit var emptyButton: JButton
    private lateinit var exitMenu: JMenuItem
    private lateinit var fileMenu: JMenu
    private lateinit var floorComboBox: JComboBox<String>
    private lateinit var floorLabel: JLabel
    private lateinit var floorPanel: JPanel
    private lateinit var freeColorPanel: JPanel
    private lateinit var freeLabel: JLabel
    private lateinit var freePanel: JPanel
    private lateinit var fromDateLabel: JLabel
    private lateinit var fromDateTextField: JTextField
    private lateinit var gridLayoutPanel: JPanel
    private lateinit var hasContractCheckbox: JCheckBox
    private lateinit var helpMenu: JMenu
    private lateinit var leftFlowLayout: JPanel
    private lateinit var legendPanel: JPanel
    private lateinit var loadMenuItem: JMenuItem
    private lateinit var lockLabel: JLabel
    private lateinit var lockTextField: JTextField
    private lateinit var lockerIDLabel: JLabel
    private lateinit var lockerIDTextField: JTextField
    private lateinit var lockerOverviewPanel: JPanel
    private lateinit var lockerOverviewScrollPane: JScrollPane
    private lateinit var lockerPanel: JPanel
    private lateinit var menuBar: JMenuBar
    private lateinit var middlePanel: JPanel
    private lateinit var moneyLabel: JLabel
    private lateinit var moneyPanel: JPanel
    private lateinit var moneyTextField: JTextField
    private lateinit var moveClassMenuItem: JMenuItem
    private lateinit var moveLockerMenuItem: JMenuItem
    private lateinit var nameLabel: JLabel
    private lateinit var nameTextField: JTextField
    private lateinit var noContractColorLabel: JPanel
    private lateinit var noContractLabel: JLabel
    private lateinit var noContractPanel: JPanel
    private lateinit var noteLabel: JLabel
    private lateinit var noteTextArea: JTextField
    private lateinit var oneMonthRemainingColorPanel: JPanel
    private lateinit var oneMonthRemainingLabel: JLabel
    private lateinit var oneMonthRemainingPanel: JPanel
    private lateinit var outOfOrderCheckbox: JCheckBox
    private lateinit var outOfOrderColorPanel: JPanel
    private lateinit var outOfOrderLabel: JLabel
    private lateinit var outOfOrderPanel: JPanel
    private lateinit var previousAmountLabel: JLabel
    private lateinit var previousAmountTextField: JTextField
    private lateinit var remainingTimeInMonthsLabel: JLabel
    private lateinit var remainingTimeInMonthsTextField: JTextField
    private lateinit var removeBuildingButton: JButton
    private lateinit var removeFloorButton: JButton
    private lateinit var removeWalkButton: JButton
    private lateinit var rentedColorPanel: JPanel
    private lateinit var rentedLabel: JLabel
    private lateinit var rentedPanel: JPanel
    private lateinit var saveButton: JButton
    private lateinit var saveMenuItem: JMenuItem
    private lateinit var searchMenu: JMenu
    private lateinit var searchMenuItem: JMenuItem
    private lateinit var settingsMenuItem: JMenuItem
    private lateinit var showTasksMenuItem: JMenuItem
    private lateinit var sizeLabel: JLabel
    private lateinit var sizeTextField: JTextField
    private lateinit var statusMessageLabel: JLabel
    private lateinit var statusPanel: JPanel
    private lateinit var surnameLabel: JLabel
    private lateinit var surnameTextField: JTextField
    private lateinit var untilDateLabel: JLabel
    private lateinit var untilDateTextField: JTextField
    private lateinit var userPanel: JPanel
    private lateinit var userScrollPane: JScrollPane
    private lateinit var walkComboBox: JComboBox<String>
    private lateinit var walkLabel: JLabel
    private lateinit var walksPanel: JPanel
}

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    // Create and display the form
    EventQueue.invokeLater {
        val mainFrame = MainFrame()
        mainFrame.initialize()
        mainFrame.isVisible = true
    }
}