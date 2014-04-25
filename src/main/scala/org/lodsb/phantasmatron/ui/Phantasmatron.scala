package org.lodsb.phantasmatron.ui

/**
 * Created by lodsb on 12/22/13.
 */

import scalafx.scene.Scene
import java.io.File
import scalafx.scene.control._
import scalafx.application.JFXApp
import scalafx.scene.layout._

import org.lodsb.phantasmatron.ui.dataflow.DataFlowVisualization
import org.lodsb.phantasmatron.core.dataflow.{CodeGraphManager, DataflowModel}
import org.lodsb.phantasmatron.ui.asset.AssetPane
import org.lodsb.phantasmatron.ui.code.CodeShell

object Phantasmatron extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Phantasmatron"
    width = 1024
    height = 800


    val model = new DataflowModel()

    val codeGraph = new CodeGraphManager(model)

    val flow = new DataFlowVisualization(model)
    val assetPane = new AssetPane
    val toolBar = new ToolBar(model)

    val splitPane = new SplitPane {
      items.add(assetPane)
      items.add(flow)
    }

    val codeShell = new CodeShell

    val borderpane = new BorderPane {
      this.top = toolBar
      this.center = splitPane
      this.bottom = new VBox {
        val v = new TitledPane {
          text = "Shell"
          content = codeShell

          expanded = false
        }

        children.add(v)
      }

    }

    scene = new Scene(borderpane, 1000, 1000) {
      stylesheets add (new File("default.css").toURI.toString)
    }

    splitPane.dividerPositions = 0.15

  }
}
