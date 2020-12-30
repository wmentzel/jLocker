package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.abstractreps.ModuleWrapper;
import com.randomlychosenbytes.jlocker.algorithms.ShortenClassRoomDistances;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This dialog looks simple when you look at the two row GUI, but under the hood
 * this is the most complex part of jLocker.
 * The code in this dialog is responsible for optimizing the distances between
 * lookers and their respective class rooms.
 *
 * It utilizes the Dijkstra shortest path algorithm implemented in the library
 * jGraphT.
 *
 * SimpleWeightedGraph
 */
public class MoveClassDialog extends JDialog {
    private final DataManager dataManager = DataManager.INSTANCE;
    private Map<String, String> classToClassRoomNodeId;
    private Set<String> lockerIdWithoutHeights;

    public MoveClassDialog(Frame parent) {
        super(parent, false);
        initComponents();

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        classToClassRoomNodeId = new HashMap<>();
        lockerIdWithoutHeights = new HashSet<>();

        findClassesAndClassRooms();

        List<String> classNames = new LinkedList<>(classToClassRoomNodeId.keySet());
        Collections.sort(classNames);

        classComboBox.setModel(new DefaultComboBoxModel(classNames.toArray()));
    }

    /**
     * Finds all classes that have a classroom. All other classes (the ones that
     * are only assigned to pupils and don't have a room) don't have to
     * be listed because the algorithm can't be used on them anyway.
     *
     * Requires a classroom to be unique! (test to be implemented)
     */
    private void findClassesAndClassRooms() {
        Set<String> assignedClasses = new HashSet<>();
        List<Building> buildings = dataManager.getBuildingList();

        for (int b = 0; b < buildings.size(); b++) {
            List<Floor> floors = buildings.get(b).getFloors();

            for (int f = 0; f < floors.size(); f++) {
                List<Walk> walks = floors.get(f).getWalks();

                for (int w = 0; w < walks.size(); w++) {
                    List<ModuleWrapper> moduleWrappers = walks.get(w).getModuleWrappers();

                    for (int m = 0; m < moduleWrappers.size(); m++) {
                        ModuleWrapper munit = moduleWrappers.get(m);

                        if (munit.getModule() instanceof Room) {
                            String className = munit.getRoom().getSchoolClassName();

                            if (!className.isEmpty()) {
                                classToClassRoomNodeId.put(className, b + "-" + f + "-" + w + "-" + m);
                            }
                        }

                        //
                        // Find classes assigned to pupils and gather lockers of pupils with an invalid height
                        // The height is needed in order to know up to which lockers a pupil can reach.
                        //
                        List<Locker> lockers = munit.getLockerCabinet().getLockers();

                        for (Locker locker : lockers) {

                            if (locker.isFree()) {
                                continue;
                            }

                            String className = locker.getPupil().getSchoolClassName();

                            if (!className.isEmpty()) {
                                assignedClasses.add(className);

                                if (locker.getPupil().getHeightInCm() == 0) {
                                    lockerIdWithoutHeights.add(locker.getId());
                                }
                            }
                        }
                    }
                }
            }

            Map<String, String> map = new HashMap<>();

            //
            // Remove the class from the map that are not assigned to a pupil
            //
            for (String className : classToClassRoomNodeId.keySet()) {
                if (assignedClasses.contains(className)) {
                    map.put(className, classToClassRoomNodeId.get(className));
                }
            }

            classToClassRoomNodeId = map;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        classPanel = new javax.swing.JPanel();
        classLabel = new javax.swing.JLabel();
        classComboBox = new javax.swing.JComboBox();
        textAreaScrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Klassenumzug");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        classPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        classLabel.setText("Klasse");
        classPanel.add(classLabel);

        classPanel.add(classComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(classPanel, gridBagConstraints);

        textAreaScrollPane.setPreferredSize(new java.awt.Dimension(500, 400));

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textAreaScrollPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(textAreaScrollPane, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        okButton.setText("Optimale Belegung finden");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText("Schließen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(buttonPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        // TODO gather info from drop down menu
        String className = (String) classComboBox.getSelectedItem();
        String classRoomNodeId = classToClassRoomNodeId.get(className);

        if (!lockerIdWithoutHeights.isEmpty()) {
            StringBuilder ids = new StringBuilder();

            for (String id : lockerIdWithoutHeights) {
                ids.append(id).append("\n");
            }

            JOptionPane.showMessageDialog(null, "Nicht alle Schüler der Klasse " + className + " haben eine gültige Größe!\n"
                    + "IDs der betroffenen Schließfächer: " + ids, "Fehler", JOptionPane.OK_OPTION);
            return;
        }

        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie die Klasse ein!", "Fehler", JOptionPane.OK_OPTION);
            return;
        }

        ShortenClassRoomDistances scrd = new ShortenClassRoomDistances(
                dataManager.getBuildingList(),
                dataManager.getSettings().getLockerMinSizes(),
                classRoomNodeId,
                className,
                (taskText) -> {
                    dataManager.getTasks().add(new Task(taskText));
                    return null;
                },
                (l1, l2) -> {
                    dataManager.moveLockers(l1, l2);
                    return null;
                }
        );

        final ShortenClassRoomDistances.Status status = scrd.check();
        String statusMessage = "";

        okButton.setEnabled(false);

        switch (status) {
            case ClassHasNoClassRoom: {
                statusMessage = "Die Klasse " + className + " hat keinen noch keinen Raum. Bitte fügen Sie diesen erst hinzu!";
                break;
            }
            case NoFreeLockersAvailable: {
                statusMessage = "Es gibt momentan keine leeren Schließfächer im Gebäude des Klassenraums!";
                break;
            }
            case ClassHasNoPupils: {
                statusMessage = "Kein Schüler der Klasse " + className + " hat momentan  ein Schließfach gemietet!";
                break;
            }
            case NonReachableLockersExist: {
                statusMessage = "Folgende Schließfächer können vom Klassenraum nicht erreicht werden: " + scrd.getIdsOfUnreachableLockers();
                break;
            }
            case Success: {
                int answer = JOptionPane.showConfirmDialog(null, "Soll der Klassenumzug ausgeführt werden?", "Klassenumzug", JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.YES_OPTION) {
                    textArea.setText(scrd.execute());
                }
                okButton.setEnabled(true);

                return;
            }
        }

        JOptionPane.showMessageDialog(null, statusMessage, "Fehler", JOptionPane.OK_OPTION);
        this.dispose();

    }//GEN-LAST:event_okButtonActionPerformed

    // new DisplayGraphFrame(scrd.getWeightedGraph()).setVisible(true);
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox classComboBox;
    private javax.swing.JLabel classLabel;
    private javax.swing.JPanel classPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextArea textArea;
    private javax.swing.JScrollPane textAreaScrollPane;
    // End of variables declaration//GEN-END:variables
}
