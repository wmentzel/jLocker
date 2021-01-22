package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.ShortenClassRoomDistances
import com.randomlychosenbytes.jlocker.State
import com.randomlychosenbytes.jlocker.model.*
import java.awt.*
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.*

/**
 * This dialog looks simple when you look at the two row GUI, but under the hood
 * this is the most complex part of jLocker.
 * The code in this dialog is responsible for optimizing the distances between
 * lookers and their respective class rooms.
 *
 *
 * It utilizes the Dijkstra shortest path algorithm implemented in the library
 * jGraphT.
 *
 *
 * SimpleWeightedGraph
 */
class MoveClassDialog(parent: Frame) : JDialog(parent, false) {
    private val dataManager = State.dataManager
    private val schoolClasses: MutableSet<String> = HashSet()
    private val lockerIdWithoutHeights: MutableSet<String>

    /**
     * Finds all classes that have a classroom. All other classes (the ones that
     * are only assigned to pupils and don't have a room) don't have to
     * be listed because the algorithm can't be used on them anyway.
     *
     *
     * Requires a classroom to be unique! (test to be implemented)
     */
    private fun findClassesAndClassRooms() {
        val buildings: List<Building> = dataManager.buildingList
        for (b in buildings.indices) {
            val floors: List<Floor> = buildings[b].floors
            for (f in floors.indices) {
                val walks: List<Walk> = floors[f].walks
                for (w in walks.indices) {
                    val moduleWrappers: List<ModuleWrapper> = walks[w].moduleWrappers
                    for (m in moduleWrappers.indices) {
                        val munit = moduleWrappers[m]
                        if (munit.module is Room) {
                            val className = (munit.module as Room).schoolClassName
                            if (!className.isEmpty()) {
                                schoolClasses.add(className)
                            }
                        }
                        val module = munit.module as? LockerCabinet ?: continue
                        val lockers: List<Locker> = module.lockers

                        //
                        // Find classes assigned to pupils and gather lockers of pupils with an invalid height
                        // The height is needed in order to know up to which lockers a pupil can reach.
                        //
                        for (locker in lockers) {
                            if (!locker.isFree) {
                                if (locker.pupil.heightInCm == 0) {
                                    lockerIdWithoutHeights.add(locker.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        classPanel = JPanel()
        classLabel = JLabel()
        classComboBox = JComboBox<String>()
        textAreaScrollPane = JScrollPane()
        textArea = JTextArea()
        buttonPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Klassenumzug"
        isResizable = false
        contentPane.layout = GridBagLayout()
        classPanel.layout = FlowLayout(FlowLayout.LEFT, 10, 5)
        classLabel.text = "Klasse"
        classPanel.add(classLabel)
        classPanel.add(classComboBox)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        contentPane.add(classPanel, gridBagConstraints)
        textAreaScrollPane.preferredSize = Dimension(500, 400)
        textArea.isEditable = false
        textArea.columns = 20
        textArea.rows = 5
        textAreaScrollPane.setViewportView(textArea)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        contentPane.add(textAreaScrollPane, gridBagConstraints)
        buttonPanel.layout = FlowLayout(FlowLayout.RIGHT, 10, 5)
        okButton.text = "Optimale Belegung finden"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        buttonPanel.add(okButton)
        cancelButton.text = "Schließen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        buttonPanel.add(cancelButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        contentPane.add(buttonPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private fun okButtonActionPerformed(evt: ActionEvent) {
        // TODO gather info from drop down menu
        val className = classComboBox.selectedItem as String
        if (!lockerIdWithoutHeights.isEmpty()) {
            val ids = StringBuilder()
            for (id in lockerIdWithoutHeights) {
                ids.append(id).append("\n")
            }
            JOptionPane.showMessageDialog(
                null, """Nicht alle Schüler der Klasse $className haben eine gültige Größe!
IDs der betroffenen Schließfächer: $ids""", "Fehler", JOptionPane.OK_OPTION
            )
            return
        }
        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie die Klasse ein!", "Fehler", JOptionPane.OK_OPTION)
            return
        }
        val scrd = ShortenClassRoomDistances(
            dataManager.buildingList,
            dataManager.settings.lockerMinSizes,
            className
        ) { taskText: String ->
            dataManager.tasks.add(Task(taskText))
            null
        }
        val status = scrd.check()
        var statusMessage = ""
        okButton.isEnabled = false
        when (status) {
            ShortenClassRoomDistances.Status.ClassRoomForSpecifiedClassDoesNotExist -> {
                statusMessage = "Die Klasse $className hat keinen noch keinen Raum. Bitte fügen Sie diesen erst hinzu!"
            }
            ShortenClassRoomDistances.Status.NoFreeLockersAvailable -> {
                statusMessage = "Es gibt momentan keine leeren Schließfächer im Gebäude des Klassenraums!"
            }
            ShortenClassRoomDistances.Status.ClassHasNoPupils -> {
                statusMessage = "Kein Schüler der Klasse $className hat momentan  ein Schließfach gemietet!"
            }
            ShortenClassRoomDistances.Status.NonReachableLockersExist -> {
                statusMessage =
                    "Folgende Schließfächer können vom Klassenraum nicht erreicht werden: " + scrd.idsOfUnreachableLockers
            }
            ShortenClassRoomDistances.Status.Success -> {
                val answer = JOptionPane.showConfirmDialog(
                    null,
                    "Soll der Klassenumzug ausgeführt werden?",
                    "Klassenumzug",
                    JOptionPane.YES_NO_OPTION
                )
                if (answer == JOptionPane.YES_OPTION) {
                    textArea.text = scrd.execute()
                }
                okButton.isEnabled = true
                return
            }
        }
        JOptionPane.showMessageDialog(null, statusMessage, "Fehler", JOptionPane.OK_OPTION)
        dispose()
    }

    // new DisplayGraphFrame(scrd.getWeightedGraph()).setVisible(true);
    private lateinit var buttonPanel: JPanel
    private lateinit var cancelButton: JButton
    private lateinit var classComboBox: JComboBox<String>
    private lateinit var classLabel: JLabel
    private lateinit var classPanel: JPanel
    private lateinit var okButton: JButton
    private lateinit var textArea: JTextArea
    private lateinit var textAreaScrollPane: JScrollPane

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)
        lockerIdWithoutHeights = HashSet()
        findClassesAndClassRooms()
        classComboBox.setModel(DefaultComboBoxModel(schoolClasses.sorted().toTypedArray()))
    }
}