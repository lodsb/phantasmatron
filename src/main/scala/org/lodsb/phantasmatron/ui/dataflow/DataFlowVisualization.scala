package org.lodsb.phantasmatron.ui.dataflow

import java.io.File
import org.lodsb.phantasmatron.core.dataflow._
import javafx.event.EventHandler
import javafx.scene.input._
import javafx.scene.effect.BlendMode
import scala.util.Try

import org.controlsfx.dialog.Dialogs
import javafx.scene.layout.Pane
import org.lodsb.phantasmatron.core.messaging.{Message, MessageBus}

import scalafx.collections.ObservableBuffer
import org.lodsb.phantasmatron.core.dataflow.ConnectionModel

import scala.util.Failure

import scala.util.Success
import org.lodsb.phantasmatron.core.dataflow.CodeNodeModel
import javafx.scene.control.ScrollPane
import javafx.scene.Group
import org.lodsb.phantasmatron.core.asset.{AssetDataFormat, AssetDescriptor, CodeAssetManager}
import org.lodsb.phantasmatron.core.code.Code
import jfxtras.labs.internal.scene.control.skin.window.DefaultWindowSkin
import org.lodsb.phantasmatron.ui.NodeUtil

/**
 * Created by lodsb on 2/18/14.
 */

case class CreateConnectionMessage(src : ConnectorModel, dst: ConnectorModel) extends Message
case class RemoveConnectionMessage(connection: ConnectionModel) extends Message
case class RemoveAllNodeConnections(node: NodeModel) extends Message

class DataFlowVisualization(model: DataflowModel) extends ScrollPane {
  this.getStyleClass.setAll("vflow-background")
  this.getStylesheets.setAll((new File("default.css").toURI.toString))

  val contentPane = new Pane
  val contentGroup = new Group
  contentPane.getChildren.add(contentGroup)

  this.setContent(contentPane)

  this.setPannable(true)

  MessageBus.registerHandler({message: Message =>

    message match {
      case CreateConnectionMessage(src, dst) => {
        model.connections.add(ConnectionModel(src,dst))
      }

      case RemoveConnectionMessage(conn) => {
        model.connections.remove(conn)
      }

      case RemoveAllNodeConnections(nodeModel) => {
        model.disconnectAllConnectionsFromNode(nodeModel)
      }

      case ConnectingStartedMessage(f) => {
        setPannable(false)
      }

      case ConnectingEndedMessage(f) => {
        setPannable(true)
      }

      case _ =>

    }
  })

  model.connections.onChange { (source, changes) =>
    changes.foreach( change => {
      change match {
        case ObservableBuffer.Add(pos, added) => {
          added.foreach({ c =>
          println("add connection " +  c)

          val connectionModel = c.asInstanceOf[ConnectionModel]

            var sourceViz : ConnectorVisualization = null
            var destViz : ConnectorVisualization = null

            nodes.foreach({ x =>
              val s = x.getConnectorVisualization(connectionModel.source)

              if(s.isDefined) {
                sourceViz = s.get
              }

              val d = x.getConnectorVisualization(connectionModel.destination)

              if(d.isDefined) {
                destViz = d.get
              }
            })

            if(sourceViz != null && destViz != null) {
                addConnection(connectionModel, sourceViz,destViz)
            }

          });
        }

        case ObservableBuffer.Remove(pos, removed) => {
          removed.foreach({ c =>
            println("remove connection"+c)
            val connectionModel = c.asInstanceOf[ConnectionModel]

            removeConnection(connectionModel)
          });
        }

        case _ =>
      }
    })
  }


  private var curMouseX = 0.0
  private var curMouseY = 0.0
  this.setOnMouseMoved(new EventHandler[MouseEvent]{
    def handle(p1: MouseEvent): Unit = {
      curMouseX = p1.getX
      curMouseY = p1.getY
    }

  })

  this.setOnKeyPressed(new EventHandler[KeyEvent] {
    def handle(p1: KeyEvent): Unit = {
      p1.getCode match {
        case KeyCode.ENTER => {
          println(curMouseX+" "+curMouseY)


        }
        case _ =>
      }
    }
  })


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

            val codeModel = CodeNodeModel(cc)
            val viz = addNode(codeModel)

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


  private var nodes : List[NodeVisualization] = List.empty

  def addNode(nm: NodeModel) : NodeVisualization = {
    model.nodes.add(nm)

    val viz = NodeVisualization(nm)
    contentPane.getChildren.add(viz)

    viz.prefHeightProperty().set(480)

    viz.requestLayout()

    //viz.setPrefWidth(400)
    //contentGroup.requestLayout()

    this.requestLayout()

   // viz.setScaleX(0.75)
   // viz.setScaleY(0.75)




    nodes = nodes :+ viz

    viz
  }


  private var connections: Map[ConnectionModel, ConnectionVisualization] = Map.empty
  def addConnection(cmodel: ConnectionModel, source: ConnectorVisualization, destination: ConnectorVisualization) : EstablishedConnectionVisualization = {
    val viz = EstablishedConnectionVisualization(cmodel, source, destination)
    connections = connections + (cmodel -> viz)

    contentPane.getChildren.add(viz)
    viz.toBack()

    viz
  }

  def removeConnection(cmodel: ConnectionModel) = {
    val c = connections.get(cmodel)

    if(c.isDefined) {
      val viz = c.get

      contentPane.getChildren.remove(viz)
      connections = connections - cmodel
    }
  }

  /*
  this.setOnMouseDragged(new EventHandler[MouseEvent]{
    private var lastDragX = 0.0
    private var lastDragY = 0.0

    def handle(p1: MouseEvent): Unit = {
      val x = p1.getX
      val y = p1.getY

      if(lastDragX != 0.0 && lastDragY != 0.0) {
        val deltaX = lastDragX - x
        val deltaY = lastDragY - y

        getContent.getTransforms.add(new Translate(deltaX, deltaY))
      }

      lastDragX = x
      lastDragY = y
    }
  })*/



  this.addEventFilter(ScrollEvent.ANY, new EventHandler[ScrollEvent] {
    private var deltaScroll = 1.0

    def handle(p1: ScrollEvent): Unit = {
       if(p1.getDeltaY > 0) {
         deltaScroll = scala.math.max(deltaScroll + 0.05, 10)
       } else {
         deltaScroll = scala.math.max(deltaScroll - 0.05, 0.1)
       }


        val res = NodeUtil.getDeepestNode(getParent, p1.getSceneX, p1.getSceneY, classOf[NodeVisualization])

      if(res != null) {
        println("node")
        val nodeGroup = res.asInstanceOf[NodeVisualization]
        //nodeGroup.setScaleX(deltaScroll)
        //nodeGroup.setScaleY(deltaScroll)
      } else {

        println("group "+p1.isConsumed + " "+ res)

        getContent.setScaleX(deltaScroll)
        getContent.setScaleY(deltaScroll)
      }

      p1.consume()
    }
  })

}

