package org.lodsb.phantasmatron.ui.dataflow

import eu.mihosoft.vrl.workflow.fx.ScalableContentPane
import java.io.File
import org.lodsb.phantasmatron.core.dataflow.{CodeNodeModel, NodeModel, DataflowModel}
import javafx.event.EventHandler
import javafx.scene.input.{Dragboard, TransferMode, DragEvent}
import javafx.scene.effect.BlendMode
import org.lodsb.phantasmatron.core._
import scala.util.{Failure, Success, Try}
import org.lodsb.phantasmatron.core.AssetDescriptor
import eu.mihosoft.vrl.workflow.VNode
import org.controlsfx.dialog.Dialogs

/**
 * Created by lodsb on 2/18/14.
 */
class DataFlowVisualization(model: DataflowModel) extends ScalableContentPane {
  this.getStyleClass.setAll("vflow-background")
  this.getStylesheets.setAll((new File("default.css").toURI.toString))


  this.setOnDragEntered(new EventHandler[DragEvent] {
    def handle(dragEvent: DragEvent) {
      System.out.println("DRAG ENTERED" + dragEvent.getDragboard.getContentTypes)
      dragEvent.acceptTransferModes(TransferMode.MOVE, TransferMode.COPY)
      setBlendMode(BlendMode.RED)
    }
  })



  this.setOnDragOver(new EventHandler[DragEvent] {
    def handle(dragEvent: DragEvent) {
      dragEvent.acceptTransferModes(TransferMode.MOVE, TransferMode.COPY)
    }
  })



  this.setOnDragExited(new EventHandler[DragEvent] {
    def handle(dragEvent: DragEvent) {
      System.out.println("DRAG Exit" + dragEvent.getDragboard.getContentTypes)
      setBlendMode(BlendMode.MULTIPLY)
    }
  })



  this.setOnDragDropped(new EventHandler[DragEvent] {
    def handle(dragEvent: DragEvent) {
      System.out.println("DRAG Drop" + dragEvent.getDragboard.getContentTypes)
      val db: Dragboard = dragEvent.getDragboard
      if (db.getContentTypes.contains(AssetDataFormat)) {
        val o: AssetDescriptor = db.getContent(AssetDataFormat).asInstanceOf[AssetDescriptor]
        System.out.println("correct drop format")
        val c: Try[Code] = CodeAssetManager.load(o)

        c match {
          case Success(cc) => {

            val viz = NodeVisualization(CodeNodeModel(cc))
            getChildren.add(viz)
            viz.setLayoutX(dragEvent.getX)
            viz.setLayoutY(dragEvent.getY)
            //viz.setX(dragEvent.getX)
            //viz.setY(dragEvent.getY)
            //viz.setWidth(300)
            //viz.setHeight(200)


            /*val pv = new PValueObject()
            pv.setValue(cc)
            val v: VNode = flow.newNode(pv)

            v.setX(dragEvent.getX)
            v.setY(dragEvent.getY)
            v.setWidth(300)
            v.setHeight(200)

            //val skin: VNodeSkin[_ <: VNode] = skinFactory.createSkin(v, flow)
            //skin.add*/

            dragEvent.setDropCompleted(true)
          }
          case Failure(v) => {
            Dialogs.create().title("Error")
              .masthead("Sorry - there was an exception while creating the CodeNode")
              .showException(v)
          }
        }
      }
      setBlendMode(BlendMode.MULTIPLY)
    }
  })




}
