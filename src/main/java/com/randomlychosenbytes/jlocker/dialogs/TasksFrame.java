/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.print.PrinterException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mu
 */
public class TasksFrame extends javax.swing.JFrame {
    private final List<String> columnData;
    private final JTable table;
    private DefaultTableModel tablemodel;
    private final DataManager dataManager;

    /**
     * Creates new form TasksFrame
     *
     * @param dataManager
     */
    public TasksFrame(DataManager dataManager) {
        initComponents();

        this.dataManager = dataManager;

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        table = new JTable(); // model, header

        columnData = new LinkedList<>();
        columnData.add("Datum");
        columnData.add("Aufgabe");
        columnData.add("erledigt");

        createTableModel();

        tableScrollPane.setViewportView(table);
    }

    /**
     *
     */
    private void createTableModel() {
        List tableData = new LinkedList();

        List<Task> tasks = dataManager.getTasks();

        final int numTasks = tasks.size();

        for (int t = 0; t < numTasks; t++) {
            Task task = tasks.get(numTasks - t - 1);

            List v = new LinkedList();
            v.add(task.getSDate());
            v.add(task.getSDescription());
            v.add(task.isDone());
            tableData.add(v);
        }

        // Workaround to avoid obsolete Vector class
        Object tableDataArray[][] = new Object[tableData.size()][];

        for (int i = 0; i < tableData.size(); i++) {
            tableDataArray[i] = ((List) tableData.get(i)).toArray();
        }

        tablemodel = new DefaultTableModel(tableDataArray, columnData.toArray()) {
            Class[] types = new Class[]
                    {
                            java.lang.String.class,
                            java.lang.String.class,
                            java.lang.Boolean.class
                    };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        table.setModel(tablemodel);
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

        tableScrollPane = new javax.swing.JScrollPane();
        componentsPanel = new javax.swing.JPanel();
        middlePanel = new javax.swing.JPanel();
        deleteDoneTasksButton = new javax.swing.JButton();
        deleteAllButton = new javax.swing.JButton();
        printTasksButton = new javax.swing.JButton();
        tasksPanel = new javax.swing.JPanel();
        descriptionTextField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Aufgaben");
        setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().add(tableScrollPane, java.awt.BorderLayout.CENTER);

        componentsPanel.setLayout(new java.awt.GridBagLayout());

        deleteDoneTasksButton.setText("Erledigte Aufgaben löschen");
        deleteDoneTasksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDoneTasksButtonActionPerformed(evt);
            }
        });
        middlePanel.add(deleteDoneTasksButton);

        deleteAllButton.setText("Alle Löschen");
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });
        middlePanel.add(deleteAllButton);

        printTasksButton.setText("Drucken");
        printTasksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printTasksButtonActionPerformed(evt);
            }
        });
        middlePanel.add(printTasksButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        componentsPanel.add(middlePanel, gridBagConstraints);

        tasksPanel.setLayout(new java.awt.BorderLayout(10, 0));
        tasksPanel.add(descriptionTextField, java.awt.BorderLayout.CENTER);

        addButton.setText("Hinzufügen");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        tasksPanel.add(addButton, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        componentsPanel.add(tasksPanel, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        componentsPanel.add(bottomPanel, gridBagConstraints);

        getContentPane().add(componentsPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteDoneTasksButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteDoneTasksButtonActionPerformed
    {//GEN-HEADEREND:event_deleteDoneTasksButtonActionPerformed
        List<Task> tasks = dataManager.getTasks();
        List<Task> newTasksList = new LinkedList<>();

        final int size = table.getRowCount();

        for (int i = 0; i < size; i++) {
            if (!((Boolean) table.getValueAt(size - i - 1, 2))) {
                newTasksList.add(tasks.get(i));
            }
        }

        if (newTasksList.size() == tasks.size()) {
            JOptionPane.showMessageDialog(null, "Sie haben keine Aufgaben ausgewählt!", "Löschen", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie wirklich alle erledigten Aufgaben löschen?", "Löschen", JOptionPane.YES_NO_CANCEL_OPTION);

            if (answer == JOptionPane.YES_OPTION) {
                dataManager.setTaskList(newTasksList);

                createTableModel();
            }
        }
    }//GEN-LAST:event_deleteDoneTasksButtonActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteAllButtonActionPerformed
    {//GEN-HEADEREND:event_deleteAllButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie wirklich alle Aufgaben löschen?", "Löschen", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            while (tablemodel.getRowCount() > 0) {
                tablemodel.removeRow(0);
            }

            dataManager.getTasks().clear();
            tableScrollPane.updateUI();
        }
    }//GEN-LAST:event_deleteAllButtonActionPerformed

    private void printTasksButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_printTasksButtonActionPerformed
    {//GEN-HEADEREND:event_printTasksButtonActionPerformed
        try {
            table.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(null, "Das Drucken ist aufgrund eines Fehlers nicht möglich!", "Fehler", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_printTasksButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addButtonActionPerformed
    {//GEN-HEADEREND:event_addButtonActionPerformed
        String description = descriptionTextField.getText();
        List<Task> tasks = dataManager.getTasks();

        if (description.equals("")) {
            JOptionPane.showMessageDialog(null, "Sie müssen eine Beschreibung eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
        } else {
            tasks.add(new Task(description));
            createTableModel();
            descriptionTextField.setText("");
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        final int size = table.getRowCount();
        List<Task> tasks = dataManager.getTasks();

        for (int i = 0; i < size; i++) {
            tasks.get(i).setDone((Boolean) table.getValueAt(i, 2));
        }

        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton deleteDoneTasksButton;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JPanel middlePanel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton printTasksButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JPanel tasksPanel;
    // End of variables declaration//GEN-END:variables
}
