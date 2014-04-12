package org.lodsb.phantasmatron.ui

import eu.mihosoft.vrl.workflow.fx.{FlowNodeWindow, FXSkinFactory, FXFlowNodeSkinBase, FXFlowNodeSkin}
import eu.mihosoft.vrl.workflow.{Connector, VFlowModel, VFlow, VNode}
import scalafx.scene.control.{Button, Accordion, ScrollPane, TitledPane}
import de.sciss.scalainterpreter.CodePane
import javax.swing.{SwingUtilities, JPanel, JComponent}
import java.awt.{Dimension, FlowLayout}
import scalafx.scene.layout.{Pane, HBox, GridPane}
import jfxtras.labs.scene.control.window.Window
import org.lodsb.phantasmatron.core.Code
import scalafx.Includes._
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import javafx.scene.shape.Shape
import scalafx.scene.paint.Color
import javafx.scene.control.Tooltip

/**
 * Created by lodsb on 12/20/13.
 */
class CodeFlowSkin(skinFactory: FXSkinFactory, model: VNode, controller: VFlow)
  extends FXFlowNodeSkinBase(skinFactory, model, controller) {

  override def updateView : Unit = {
    super.updateView

    val code = getModel.getValueObject.getValue.asInstanceOf[Code]

    if( (!getModel.isInstanceOf[VFlowModel]) && (code != null)) {
      new CodeUIController(code, getNode, getModel)
    }
  }

  this.getModel.getConnectors.addListener( new ListChangeListener[Connector]{
    def onChanged(p1: Change[_ <: Connector]): Unit = {
      while(p1.next()) {

        if(p1.wasAdded()) {
          p1.getAddedSubList.toArray.foreach({ x =>

            val conn = x.asInstanceOf[Connector]
            val node = getConnectorNodeByReference(conn).asInstanceOf[Shape]
            node.getStyleClass.clear()
            node.setFill(Util.typeString2Color(conn.getType))

            val t = new Tooltip(conn.getType)
            Tooltip.install(node, t)

          })
        }
      }

    }
  });


}
