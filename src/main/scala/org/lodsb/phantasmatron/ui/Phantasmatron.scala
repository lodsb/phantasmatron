package org.lodsb.phantasmatron.ui

/**
 * Created by lodsb on 12/22/13.
 */

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.{Node, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage
import java.io.File
import scalafx.scene.control._
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane
import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.animation._
import scalafx.animation.Animation.INDEFINITE
import scalafx.stage.{ Stage, WindowEvent }
import scalafx.event.ActionEvent
import scalafx.scene.chart._
import scalafx.scene.control.TabPane.TabClosingPolicy._
import scalafx.scene.layout.{FlowPane, BorderPane, AnchorPane, StackPane}
import scalafx.scene.image.{ Image, ImageView }
import scalafx.geometry.Pos._
import scalafx.geometry.Orientation._
import scalafx.geometry.Side._
import scalafx.util.Duration._
import scalafx.util.converter.NumberStringConverter


object Phantasmatron extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Phantasmatron"
    width = 1024
    height = 800


    val flow = new Flow
    val assetPane = new AssetPane
	  val toolBar = new ToolBar(flow.flow)

    val splitPane = new SplitPane{
      items.add(assetPane)
      items.add(flow)
    }

    val borderpane = new BorderPane {
		  this.top = toolBar
      this.center = splitPane
    }

    scene = new Scene(borderpane, 1000,1000)

	splitPane.dividerPositions = 0.15

  }
}
