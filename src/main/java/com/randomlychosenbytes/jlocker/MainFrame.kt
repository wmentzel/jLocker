package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.dialogs.*
import com.randomlychosenbytes.jlocker.model.*
import com.randomlychosenbytes.jlocker.model.LockerCabinet.Companion.updateDummyRows
import com.randomlychosenbytes.jlocker.utils.isDateValid
import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener
import kotlin.system.exitProcess

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
class MainFrame : JFrame() {

    private var searchFrame: SearchFrame? = null
    private var tasksFrame: TasksFrame? = null

    private lateinit var timer: Timer

    fun initialize() {

        initComponents()

        // center on screen
        setLocationRelativeTo(null)

        //
        // Set application title from resources
        //
        title = "${dataManager.appTitle} ${dataManager.appVersion}"

        //
        // Ask to save changes on exit
        //
        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(winEvt: WindowEvent) {
                    exit()
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
        if (!dataManager.ressourceFile.exists()) {
            CreateUsersDialog(this, true).apply {
                isVisible = true
            }
        }

        //
        // LogIn
        //
        LogInDialog(this, true).apply {
            isVisible = true
        }

        //
        // Initialize UI
        //
        updateComboBoxes()

        // If the super user is logged in, he is allowed to change to passwords.
        changeUserPWMenuItem.isEnabled = dataManager.currentUser is SuperUser
    }

    /**
     * Put the lockers of the current walk in the layout manager.
     */
    fun drawLockerOverview() {
        // Remove old panels
        lockerOverviewPanel.removeAll()

        val moduleWrappers = dataManager.currentManagmentUnitList.map { oldModuleWrapper ->

            when (val oldModule = oldModuleWrapper.module) {
                is Room -> {
                    ModuleWrapper(Room(oldModule.roomName, oldModule.schoolClassName))
                }
                is LockerCabinet -> {
                    val newLockers = oldModule.lockers.map { Locker(it) }
                    ModuleWrapper(LockerCabinet()).apply {
                        (module as LockerCabinet).lockers = newLockers.toMutableList()
                    }
                }
                is Staircase -> {
                    ModuleWrapper(Staircase(oldModule.staircaseName))
                }
                else -> TODO("There is a new module type ${oldModuleWrapper.module::class.simpleName} which was not considered.")
            }
        }

        dataManager.currentWalk.moduleWrappers = moduleWrappers.toMutableList()

        val lockerCabinets = moduleWrappers.map { it.module }.filterIsInstance<LockerCabinet>()

        lockerCabinets.flatMap {
            it.lockers
        }.forEach {
            it.setAppropriateColor()
        }

        // add each ModuleWrapper to the lockerOverviewPanel
        moduleWrappers.forEach { moduleWrapper ->
            lockerOverviewPanel.add(moduleWrapper)
        }

        // set selected locker
        moduleWrappers.asSequence().mapIndexed { index, moduleWrapper ->
            index to moduleWrapper
        }.firstOrNull { (index, moduleWrapper) ->
            moduleWrapper.module is LockerCabinet
        }?.let { (moduleWrapperIndex, moduleWrapper) ->
            (moduleWrapper.module as? LockerCabinet)?.lockers?.firstOrNull()?.setSelected()
            dataManager.currentManagementUnitIndex = moduleWrapperIndex
            dataManager.currentLockerIndex = 0
        }

        updateDummyRows(lockerCabinets)
        showLockerInformation()
        lockerOverviewPanel.updateUI()
    }

    fun selectLocker(locker: Locker) {
        if (dataManager.currentLockerList.isNotEmpty()) {
            dataManager.currentLocker.setAppropriateColor()
        }

        val (mwIndex, lockerIndex) = dataManager.currentWalk.moduleWrappers.asSequence()
            .mapIndexed { index, moduleWrapper ->
                index to moduleWrapper
            }.mapNotNull { (index, moduleWrapper) ->
                (moduleWrapper.module as? LockerCabinet)?.let {
                    index to it
                }
            }.mapNotNull { (index, lockerCabinet) ->
                lockerCabinet.lockers.indexOfFirst { it === locker }.takeIf { it != -1 }?.let {
                    index to it
                }
            }.single()

        dataManager.currentLockerIndex = lockerIndex
        dataManager.currentManagementUnitIndex = mwIndex

        locker.setSelected()
        showLockerInformation()
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
            lastNameTextField.text = ""
            firstNameTextField.text = ""
            classTextField.text = ""
            heightInCmTextField.text = ""
            hasContractCheckbox.isSelected = false
            moneyTextField.text = ""
            previousAmountTextField.text = ""
            rentedFromDateTextField.text = ""
            rentedUntilDateTextField.text = ""
            remainingTimeInMonthsTextField.text = ""
        } else {
            lastNameTextField.text = locker.pupil.lastName
            firstNameTextField.text = locker.pupil.firstName
            classTextField.text = locker.pupil.schoolClassName
            heightInCmTextField.text = locker.pupil.heightInCm.toString()
            hasContractCheckbox.isSelected = locker.pupil.hasContract
            moneyTextField.text = locker.pupil.paidAmount.toString()
            previousAmountTextField.text = locker.pupil.previouslyPaidAmount.toString()
            rentedFromDateTextField.text = locker.pupil.rentedFromDate
            rentedUntilDateTextField.text = locker.pupil.rentedUntilDate
            remainingTimeInMonthsTextField.text = locker.pupil.remainingTimeInMonths.let {
                "$it ${if (it == 1) "Monat" else "Monate"}"
            }
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
    fun setLockerInformation() {
        val locker = dataManager.currentLocker
        val id = lockerIDTextField.text
        if (dataManager.currentLocker.id == id || dataManager.isLockerIdUnique(id)) {
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

        if (id.isBlank()) {
            JOptionPane.showMessageDialog(
                null,
                "Bitte geben Sie eine gültige Schließfach-ID ein.",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        locker.isOutOfOrder = outOfOrderCheckbox.isSelected
        locker.lockCode = lockTextField.text
        locker.note = noteTextArea.text
        val lastName = lastNameTextField.text
        if (lastName.isNotBlank()) {
            getOrCreatePupil(locker).lastName = lastName
        }
        val firstName = firstNameTextField.text
        if (firstName.isNotBlank()) {
            getOrCreatePupil(locker).firstName = firstName
        }
        val heightString = heightInCmTextField.text
        if (heightString.isNotBlank()) {
            try {
                val height = heightString.toInt()
                getOrCreatePupil(locker).heightInCm = height
            } catch (e: NumberFormatException) {
                JOptionPane.showMessageDialog(
                    null,
                    "Die eingegebene Größe ist ungültig!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        val from = rentedFromDateTextField.text
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
        val until = rentedUntilDateTextField.text
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
            remainingTimeInMonthsTextField.text = months.toString() + " " + if (months == 1) "Monat" else "Monate"
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
        combobox.model = DefaultComboBoxModel(list.map { it.name }.toTypedArray())
    }

    /**
     * Sets all three combo boxes to the current indices of the building,
     * floor and walk.
     */
    fun updateComboBoxes() {
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

    private fun exit() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie Ihre Änderungen speichern?",
            "Speichern und beenden",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.CANCEL_OPTION) {
            return
        }
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.saveAndCreateBackup()
        }

        exitProcess(0)
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
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
        lastNameTextField = JTextField()
        nameLabel = JLabel()
        firstNameTextField = JTextField()
        classLabel = JLabel()
        classTextField = JTextField()
        sizeLabel = JLabel()
        heightInCmTextField = JTextField()
        noteLabel = JLabel()
        noteTextArea = JTextField()
        middlePanel = JPanel()
        lockerPanel = JPanel()
        fromDateLabel = JLabel()
        rentedFromDateTextField = JTextField()
        untilDateLabel = JLabel()
        rentedUntilDateTextField = JTextField()
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
        preferredSize = Dimension(1280, 800)
        centerPanel.background = primaryColor
        centerPanel.layout = BorderLayout()
        comboBoxPanel.background = primaryColor
        comboBoxPanel.layout = FlowLayout(FlowLayout.LEFT)
        buildingsPanel.background = primaryColor
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
        addBuildingButton.background = primaryColor
        addBuildingButton.text = "+"
        addBuildingButton.addActionListener { evt -> addBuildingButtonActionPerformed(evt) }
        buildingsPanel.add(addBuildingButton)
        removeBuildingButton.background = primaryColor
        removeBuildingButton.text = "-"
        removeBuildingButton.addActionListener { evt -> removeBuildingButtonActionPerformed(evt) }
        buildingsPanel.add(removeBuildingButton)
        editBuildingButton.background = primaryColor
        editBuildingButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editBuildingButton.addActionListener { evt -> editBuildingButtonActionPerformed(evt) }
        buildingsPanel.add(editBuildingButton)
        comboBoxPanel.add(buildingsPanel)
        floorPanel.background = primaryColor
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
        addFloorButton.background = primaryColor
        addFloorButton.text = "+"
        addFloorButton.addActionListener { evt -> addFloorButtonActionPerformed(evt) }
        floorPanel.add(addFloorButton)
        removeFloorButton.background = primaryColor
        removeFloorButton.text = "-"
        removeFloorButton.addActionListener { evt -> removeFloorButtonActionPerformed(evt) }
        floorPanel.add(removeFloorButton)
        editFloorButton.background = primaryColor
        editFloorButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editFloorButton.addActionListener { evt -> editFloorButtonActionPerformed(evt) }
        floorPanel.add(editFloorButton)
        comboBoxPanel.add(floorPanel)
        walksPanel.background = primaryColor
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
        addWalkButton.background = primaryColor
        addWalkButton.text = "+"
        addWalkButton.addActionListener { evt -> addWalkButtonActionPerformed(evt) }
        walksPanel.add(addWalkButton)
        removeWalkButton.background = primaryColor
        removeWalkButton.text = "-"
        removeWalkButton.addActionListener { evt -> removeWalkButtonActionPerformed(evt) }
        walksPanel.add(removeWalkButton)
        editWalkButton.background = primaryColor
        editWalkButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editWalkButton.addActionListener { evt -> editWalkButtonActionPerformed(evt) }
        walksPanel.add(editWalkButton)
        comboBoxPanel.add(walksPanel)
        centerPanel.add(comboBoxPanel, BorderLayout.NORTH)
        lockerOverviewScrollPane.border = null
        lockerOverviewPanel.background = primaryColor
        lockerOverviewPanel.layout = GridLayout(1, 0, 5, 0)
        lockerOverviewScrollPane.setViewportView(lockerOverviewPanel)
        centerPanel.add(lockerOverviewScrollPane, BorderLayout.CENTER)
        containerPanel.background = primaryColor
        containerPanel.layout = FlowLayout(FlowLayout.LEFT)
        leftFlowLayout.background = primaryColor
        leftFlowLayout.layout = GridBagLayout()
        legendPanel.background = primaryColor
        noContractPanel.background = primaryColor
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
        freePanel.background = primaryColor
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
        rentedPanel.background = primaryColor
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
        outOfOrderPanel.background = primaryColor
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
        oneMonthRemainingPanel.background = primaryColor
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
        var gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        leftFlowLayout.add(legendPanel, gridBagConstraints)
        dataPanel.background = primaryColor
        dataPanel.layout = GridBagLayout()
        userPanel.background = primaryColor
        userPanel.layout = GridLayout(6, 2, 5, 3)
        lockerIDLabel.text = "Schließfach-ID           "
        userPanel.add(lockerIDLabel)
        userPanel.add(lockerIDTextField)
        surnameLabel.text = "Nachname"
        userPanel.add(surnameLabel)
        userPanel.add(lastNameTextField)
        nameLabel.text = "Vorname"
        userPanel.add(nameLabel)
        userPanel.add(firstNameTextField)
        classLabel.text = "Klasse"
        userPanel.add(classLabel)
        userPanel.add(classTextField)
        sizeLabel.text = "Größe"
        userPanel.add(sizeLabel)
        userPanel.add(heightInCmTextField)
        noteLabel.text = "Notiz"
        userPanel.add(noteLabel)
        userPanel.add(noteTextArea)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 20)
        dataPanel.add(userPanel, gridBagConstraints)
        middlePanel.background = primaryColor
        middlePanel.layout = GridBagLayout()
        lockerPanel.background = primaryColor
        lockerPanel.layout = GridLayout(5, 2, 5, 3)
        fromDateLabel.text = "von"
        lockerPanel.add(fromDateLabel)
        lockerPanel.add(rentedFromDateTextField)
        untilDateLabel.text = "bis"
        lockerPanel.add(untilDateLabel)
        lockerPanel.add(rentedUntilDateTextField)
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
        checkBoxPanel.background = primaryColor
        checkBoxPanel.layout = GridBagLayout()
        outOfOrderCheckbox.background = primaryColor
        outOfOrderCheckbox.text = "defekt"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        checkBoxPanel.add(outOfOrderCheckbox, gridBagConstraints)
        hasContractCheckbox.background = primaryColor
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
        moneyPanel.background = primaryColor
        moneyPanel.border =
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentColor), "Finanzen")
        moneyPanel.layout = GridBagLayout()
        gridLayoutPanel.background = primaryColor
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
        addAmountButton.background = primaryColor
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
        statusPanel.background = primaryColor
        statusPanel.layout = GridBagLayout()
        buttonPanel.background = primaryColor
        buttonPanel.layout = GridBagLayout()
        saveButton.background = primaryColor
        saveButton.text = "Speichern"
        saveButton.addActionListener { evt -> saveButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        buttonPanel.add(saveButton, gridBagConstraints)
        emptyButton.background = primaryColor
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
        exitMenu.addActionListener { evt -> exit() }
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
    } // </editor-fold>

    private fun removeBuildingButtonActionPerformed(evt: ActionEvent) {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie dieses Gebäude wirklich löschen?",
            "Gebäude löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.buildingList.removeAt(dataManager.currentBuildingIndex)
            dataManager.currentBuildingIndex = dataManager.buildingList.size - 1
            dataManager.currentFloorIndex = 0
            dataManager.currentWalkIndex = 0
            dataManager.currentManagementUnitIndex = 0
            dataManager.currentLockerIndex = 0
            updateComboBoxes()
        }
    }

    private fun removeFloorButtonActionPerformed(evt: ActionEvent) {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diese Etage wirklich löschen?",
            "Etage löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.currentFloorList.removeAt(dataManager.currentFloorIndex)
            dataManager.currentFloorIndex = dataManager.currentFloorList.size - 1
            dataManager.currentWalkIndex = 0
            dataManager.currentManagementUnitIndex = 0
            dataManager.currentLockerIndex = 0
            updateComboBoxes()
        }
    }

    private fun removeWalkButtonActionPerformed(evt: ActionEvent) {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diesen Gang wirklich löschen?",
            "Gang löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.currentWalkList.removeAt(dataManager.currentWalkIndex)
            dataManager.currentWalkIndex = dataManager.currentWalkList.size - 1
            dataManager.currentManagementUnitIndex = 0
            dataManager.currentLockerIndex = 0
            updateComboBoxes()
        }
    }

    private fun saveButtonActionPerformed(evt: ActionEvent) {
        setLockerInformation()
    }

    private fun codeTextFieldMouseClicked(evt: MouseEvent) {
        if (dataManager.currentUser is SuperUser) {
            val dialog = EditCodesDialog(this, true)
            dialog.isVisible = true
        }
    }

    private fun emptyButtonActionPerformed(evt: ActionEvent) {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie dieses Schließfach wirklich leeren?",
            "Schließfach leeren",
            JOptionPane.YES_NO_OPTION
        )

        if (answer == JOptionPane.YES_OPTION) {
            dataManager.currentLocker.empty()
            showLockerInformation()
        }
    }

    private fun addBuildingButtonActionPerformed(evt: ActionEvent) {
        val dialog = BuildingDialog(this, true, BuildingDialog.ADD)
        dialog.isVisible = true
    }

    private fun addFloorButtonActionPerformed(evt: ActionEvent) {
        val dialog = FloorDialog(this, true, FloorDialog.ADD)
        dialog.isVisible = true
    }

    private fun editFloorButtonActionPerformed(evt: ActionEvent) {
        val dialog = FloorDialog(this, true, FloorDialog.EDIT)
        dialog.isVisible = true
    }

    private fun addWalkButtonActionPerformed(evt: ActionEvent) {
        val dialog = WalkDialog(this, true, WalkDialog.ADD)
        dialog.isVisible = true
    }

    private fun editWalkButtonActionPerformed(evt: ActionEvent) {
        val dialog = WalkDialog(this, true, WalkDialog.EDIT)
        dialog.isVisible = true
    }

    private fun editBuildingButtonActionPerformed(evt: ActionEvent) {
        val dialog = BuildingDialog(this, true, BuildingDialog.EDIT)
        dialog.isVisible = true
    }

    private fun buildingComboBoxPopupMenuWillBecomeInvisible(evt: PopupMenuEvent) {
        dataManager.currentBuildingIndex = buildingComboBox.selectedIndex
        initializeComboBox(dataManager.currentFloorList, floorComboBox)

        // move on to next combobox
        floorComboBoxPopupMenuWillBecomeInvisible(null)
    }

    private fun floorComboBoxPopupMenuWillBecomeInvisible(evt: PopupMenuEvent?) {
        dataManager.currentFloorIndex = floorComboBox.selectedIndex
        initializeComboBox(dataManager.currentWalkList, walkComboBox)

        // move on to next combobox
        walkComboBoxPopupMenuWillBecomeInvisible(null)
    }

    private fun walkComboBoxPopupMenuWillBecomeInvisible(evt: PopupMenuEvent?) {
        dataManager.currentWalkIndex = walkComboBox.selectedIndex
        dataManager.currentManagementUnitIndex = 0
        removeBuildingButton.isEnabled = dataManager.buildingList.size > 1
        removeFloorButton.isEnabled = dataManager.currentFloorList.size > 1
        removeWalkButton.isEnabled = dataManager.currentWalkList.size > 1
        drawLockerOverview()
    }

    private fun addAmountButtonActionPerformed(evt: ActionEvent) {
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
    }

    private fun showTasksMenuItemActionPerformed(evt: ActionEvent) {
        if (tasksFrame != null) {
            tasksFrame?.dispose()
        }
        tasksFrame = TasksFrame().apply {
            isVisible = true
        }
    }

    private fun saveMenuItemActionPerformed(evt: ActionEvent) {
        dataManager.saveAndCreateBackup()
    }

    private fun loadMenuItemActionPerformed(evt: ActionEvent) {
        dataManager.loadFromCustomFile(loadAsSuperUser = dataManager.currentUser is SuperUser)
        dataManager.initBuildingObject()
        drawLockerOverview()
    }

    private fun aboutMenuItemActionPerformed(evt: ActionEvent) {
        val dialog = AboutBox(this, true)
        dialog.isVisible = true
    }

    private fun changeUserPWMenuItemActionPerformed(evt: ActionEvent) {
        val dialog = CreateUsersDialog(this, true)
        dialog.isVisible = true
    }

    private fun settingsMenuItemActionPerformed(evt: ActionEvent) {
        val dialog = SettingsDialog(this, true)
        dialog.isVisible = true
    }

    private fun changeClassMenuItemActionPerformed(evt: ActionEvent) {
        val dialog = RenameClassDialog(this, true)
        dialog.isVisible = true
    }

    private fun searchMenuItemActionPerformed(evt: ActionEvent) {
        searchFrame?.dispose()
        searchFrame = SearchFrame().apply {
            isVisible = true
        }
    }

    private fun moveClassMenuItemActionPerformed(evt: ActionEvent) {
        val dialog = MoveClassDialog(this)
        dialog.isVisible = true
    }

    private fun moveLockerMenuItemActionPerformed(evt: ActionEvent) {
        val dialog = MoveLockerDialog(this, true)
        dialog.isVisible = true
    }

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
    private lateinit var rentedFromDateTextField: JTextField
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
    private lateinit var firstNameTextField: JTextField
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
    private lateinit var heightInCmTextField: JTextField
    private lateinit var statusMessageLabel: JLabel
    private lateinit var statusPanel: JPanel
    private lateinit var surnameLabel: JLabel
    private lateinit var lastNameTextField: JTextField
    private lateinit var untilDateLabel: JLabel
    private lateinit var rentedUntilDateTextField: JTextField
    private lateinit var userPanel: JPanel
    private lateinit var userScrollPane: JScrollPane
    private lateinit var walkComboBox: JComboBox<String>
    private lateinit var walkLabel: JLabel
    private lateinit var walksPanel: JPanel
}