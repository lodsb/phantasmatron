package org.lodsb.phantasmatron.ui

import eu.mihosoft.vrl.workflow.fx.{FXSkinFactory, FXFlowNodeSkinBase, FXFlowNodeSkin}
import eu.mihosoft.vrl.workflow.{VFlowModel, VFlow, VNode}
import scalafx.scene.control.{Accordion, ScrollPane, TitledPane}
import de.sciss.scalainterpreter.CodePane
import javax.swing.{SwingUtilities, JPanel, JComponent}
import java.awt.{Dimension, FlowLayout}
import scalafx.scene.layout.{Pane, HBox, GridPane}
import jfxtras.labs.scene.control.window.Window
import org.lodsb.phantasmatron.core.Code

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
}
