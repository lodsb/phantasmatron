/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: DataFlowVisualization.scala
 *     >>
 *   +3>>
 *     >>  Copyright (c) 2014:
 *     >>
 *     >>     |             |     |
 *     >>     |    ,---.,---|,---.|---.
 *     >>     |    |   ||   |`---.|   |
 *     >>     `---'`---'`---'`---'`---'
 *     >>                    // Niklas Klügel
 *     >>
 *   +4>>
 *     >>  Made in Bavaria by fat little elves - since 1983.
 */

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
import org.lodsb.phantasmatron.core.asset.{AssetDescriptor, AssetDataFormat, CodeAssetDescriptor, CodeAssetManager}
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

  private var currentAssetBox: Option[AssetBox] = None
  this.setOnKeyPressed(new EventHandler[KeyEvent] {
    def handle(p1: KeyEvent): Unit = {
      println(p1)
      p1.getCode match {
        case KeyCode.ENTER => {
          println(curMouseX+" "+curMouseY)

          if(currentAssetBox.isEmpty || (currentAssetBox.isDefined && !contentPane.getChildren.contains(currentAssetBox.get))) {

            if(currentAssetBox.isDefined) {
              println(currentAssetBox.get.isVisible)
            }

            val box = new AssetBox(DataFlowVisualization.this)

            contentPane.getChildren.add(box)
            box.setLayoutX(curMouseX)
            box.setLayoutY(curMouseY)

            box.inputBox.requestFocus()

            currentAssetBox = Some(box)
          }

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
        val asset: CodeAssetDescriptor = db.getContent(AssetDataFormat).asInstanceOf[CodeAssetDescriptor]

        loadAsset(asset, dragEvent.getX, dragEvent.getY)

        dragEvent.setDropCompleted(true)
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
    this.requestLayout()

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

  this.addEventFilter(ScrollEvent.ANY, new EventHandler[ScrollEvent] {
    private var deltaScroll = 1.0

    def handle(p1: ScrollEvent): Unit = {
       if(p1.getDeltaY > 0) {
         deltaScroll = scala.math.min(deltaScroll + 0.15, 10)
       } else {
         deltaScroll = scala.math.max(deltaScroll - 0.15, 0.1)
       }

      println(deltaScroll + " " +p1.getDeltaY)

        val res = NodeUtil.getDeepestNode(getParent, p1.getSceneX, p1.getSceneY, classOf[NodeVisualization])

      if(res != null) {
        println("node")
        val nodeGroup = res.asInstanceOf[NodeVisualization]
        //nodeGroup.setScaleX(deltaScroll)
        //nodeGroup.setScaleY(deltaScroll)
      } else {

        println("group "+p1.isConsumed + " "+ res)

        contentPane.setScaleX(deltaScroll)
        contentPane.setScaleY(deltaScroll)
      }

      p1.consume()
    }
  })

  def loadAsset(a: AssetDescriptor[Code], x: Double, y: Double) = {
    System.out.println("correct drop format")
    val c: Try[Code] = CodeAssetManager.load(a)

    c match {
      case Success(cc) => {

        val codeModel = CodeNodeModel(cc)
        val viz = addNode(codeModel)

        viz.setLayoutX(x)
        viz.setLayoutY(y)
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

      }
      case Failure(v) => {
        Dialogs.create().title("Error")
          .masthead("Sorry - there was an exception while creating the CodeNode")
          .showException(v)
      }
    }
  }

}

