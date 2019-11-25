package com.randomlychosenbytes.jlocker.dialogs;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import javax.swing.*;
import java.awt.*;

/**
 * @author Willi
 */
public class DisplayGraphDialog extends JDialog {
    /**
     * Creates new form DisplayGraphForm
     *
     * @param parent
     * @param g
     * @param modal
     */
    public DisplayGraphDialog(final Frame parent, SimpleWeightedGraph g, boolean modal) {
        super(parent, modal);

        JGraphXAdapter<String, MyEdge> graphAdapter = new JGraphXAdapter<>(g);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        this.add(new mxGraphComponent(graphAdapter));

        this.pack();
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    public static class MyEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return String.valueOf(getWeight());
        }
    }
}
