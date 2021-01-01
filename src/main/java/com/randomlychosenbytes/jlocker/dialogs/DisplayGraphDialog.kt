package com.randomlychosenbytes.jlocker.dialogs

import com.mxgraph.layout.mxCircleLayout
import com.mxgraph.layout.mxIGraphLayout
import com.mxgraph.swing.mxGraphComponent
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.SimpleWeightedGraph
import java.awt.Frame
import javax.swing.JDialog

// only needed for debugging
@Suppress("unused")
class DisplayGraphDialog(parent: Frame, g: SimpleWeightedGraph<*, *>, modal: Boolean) : JDialog(parent, modal) {

    private val graphAdapter = JGraphXAdapter(g)

    val layout: mxIGraphLayout = mxCircleLayout(graphAdapter).apply {
        execute(graphAdapter.defaultParent)
    }

    init {
        add(mxGraphComponent(graphAdapter))
        pack()
        isLocationByPlatform = true
        isVisible = true
    }
}