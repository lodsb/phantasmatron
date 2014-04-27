/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: NodeVisualization.scala
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

import jfxtras.labs.scene.control.window.{Window, MinimizeIcon, CloseIcon}
import org.lodsb.phantasmatron.core.dataflow.{ConnectorModel, NodeModel, CodeNodeModel}


import javafx.beans.binding.DoubleBinding
import javafx.scene.{Group, Parent, Node}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Bounds
import javafx.event.EventHandler
import scalafx.collections.ObservableBuffer
import javafx.collections.ObservableList
import java.lang.reflect.Method
import org.lodsb.phantasmatron.core.messaging.MessageBus
import org.lodsb.phantasmatron.ui.code.CodeUIController
import org.lodsb.phantasmatron.ui.{NodeUtil}
import javafx.scene.layout.Pane

//import scalafx.scene.input.MouseEvent
import javafx.scene.input.MouseEvent

/**
 * Created by lodsb on 4/20/14.
 */


object NodeVisualization{
  def apply(nodeModel: NodeModel) : NodeVisualization = {
    nodeModel match {
      case codeNode:CodeNodeModel => {
        new CodeNodeVisualization(codeNode)
      }
    }
  }
}

class NodeVisualization(protected[dataflow] val model: NodeModel) extends Window {
  // use injection for ui stuff here?

  private var inputConnectors : List[ConnectorVisualization] = List.empty
  private var outputConnectors : List[ConnectorVisualization] = List.empty
  private var connectors : Map[ConnectorModel, ConnectorVisualization] = Map.empty

  model.connectors onChange { (source, changes) =>
    changes.foreach( change => {
      change match {
        case ObservableBuffer.Add(pos, added) => {
          added.foreach({ c =>
            addConnector(c.asInstanceOf[ConnectorModel])
          });
        }

        case ObservableBuffer.Remove(pos, removed) => {
          removed.foreach({ c =>
            removeConnector(c.asInstanceOf[ConnectorModel])
          });
        }

        case _ =>
      }

      computeInputConnectorSize
      computeOutputConnectorSize
      adjustConnectorSize

    })

    this.requestLayout()
  }





  def getConnectorVisualization(cm: ConnectorModel) : Option[ConnectorVisualization] = {
    connectors.get(cm)
  }

   private def addConnector(connector: ConnectorModel) {
    //connectorList.add(connector)


    val connectorNode = ConnectorVisualization(connector)

    connectors = connectors + (connector -> connectorNode)

    if (connector.isInput) {
      inputConnectors = inputConnectors :+ connectorNode
    }
    if (connector.isOutput) {
      outputConnectors = outputConnectors :+ connectorNode
    }
    val startXBinding: DoubleBinding = new DoubleBinding {
      super.bind(layoutXProperty(), widthProperty())

      protected def computeValue: Double = {
        var posX: Double = getLayoutX
        if (connector.isOutput) {
          posX += getWidth
        }
        return posX
      }
    }
    val startYBinding: DoubleBinding = new DoubleBinding {

      super.bind(layoutYProperty(), heightProperty())


      protected def computeValue: Double = {
        val connectorHeight: Double = connectorNode.getRadius * 2
        val gap: Double = 5
        var numConnectors: Double = inputConnectors.size
        var connectorIndex: Int = inputConnectors.indexOf(connectorNode)
        if (connector.isOutput) {
          numConnectors = outputConnectors.size
          connectorIndex = outputConnectors.indexOf(connectorNode)
        }
        val totalHeight: Double = numConnectors * connectorHeight + (numConnectors - 1) * gap
        val midPointOfNode: Double = getLayoutY + getHeight / 2
        val startY: Double = midPointOfNode - totalHeight / 2
        val y: Double = startY + (connectorHeight + gap) * connectorIndex + (connectorHeight + gap) / 2
        return y
      }
    }
    connectorNode.layoutXProperty.bind(startXBinding)
    connectorNode.layoutYProperty.bind(startYBinding)


    startXBinding.get
    startYBinding.get()


    boundsInLocalProperty.addListener(new ChangeListener[Bounds] {
      def changed(observable: ObservableValue[_ <: Bounds], oldValue: Bounds, newValue: Bounds) {
        computeInputConnectorSize
        computeOutputConnectorSize
        adjustConnectorSize
      }
    })

    //jfxtras.util.NodeUtil.addToParent(getParent,connectorNode)
    getParent.asInstanceOf[Pane].getChildren.add(connectorNode)


    //connectorNode.onMouseEnteredProperty set {x: MouseEvent => connectorNode.toFront()}


    connectorNode.onMouseEnteredProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        connectorNode.toFront
        t.consume()
      }
    })

    connectorNode.onMouseExitedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        connectorNode.toBack
        t.consume()
      }
    })



     var currRec: ConnectorVisualization = null
    var currConn : ConnectionVisualization = null
        connectorNode.onMouseDraggedProperty.set(new EventHandler[MouseEvent] {
          def handle(event: MouseEvent) {

            if(currConn == null) {
              currConn = new ConnectionVisualization()
              getParentChildren.get.add(currConn)
              currConn.startXProperty <== connectorNode.layoutXProperty()
              currConn.startYProperty <== connectorNode.layoutYProperty()

              MessageBus.send(ConnectingStartedMessage(connector))

            }

            val point = getParent.sceneToLocal(event.getSceneX, event.getSceneY)

            currConn.endXProperty() = point.getX
            currConn.endYProperty() = point.getY
            currConn.toFront


            val res = NodeUtil.getDeepestNode(getParent, event.getSceneX, event.getSceneY, classOf[ConnectorVisualization])

            if(res != null) {
              val c = res.asInstanceOf[ConnectorVisualization]

              if(connectorNode.model.isCompatible(c.model)) {
                currRec = c
              } else {
                currRec = null
              }

            } else {
              currRec = null
            }

            if(currRec == null) {
              currConn.setValid(false)
            } else {
              currConn.setValid(true)
            }


      }})

    connectorNode.onMouseReleasedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {


        MessageBus.send(ConnectingEndedMessage(connector))

        if(currConn != null) {
          getParentChildren.get.remove(currConn)

          if(currRec != null) {
            MessageBus.send(CreateConnectionMessage(connectorNode.model, currRec.model))
          }

          currConn = null
          currRec = null
        }
      }}
    )

    this.setHeight(this.getHeight)
    this.setWidth(this.getWidth)

  }

  private def removeConnector(connector: ConnectorModel) {
    //connectorList.remove(connector)
    val connectorNode: Node = connectors.get(connector).get

    connectors = connectors - connector

    if (connectorNode != null && connectorNode.getParent != null) {
      if (connector.isInput) {
        inputConnectors = inputConnectors diff List(connectorNode)
      }
      else if (connector.isOutput) {
        outputConnectors = outputConnectors diff List(connectorNode)
      }
      jfxtras.util.NodeUtil.removeFromParent(connectorNode)
    }
  }

  var inputConnectorsSize = 0.0
  private def computeInputConnectorSize {
    var inset: Double = 120
    val minInset: Double = 60
    val minSize: Double = 8
    var connectorHeight: Double = computeConnectorSize(inset, inputConnectors.size)
    if (connectorHeight < minSize) {
      val diff: Double = minSize - connectorHeight
      inset = Math.max(inset - diff * inputConnectors.size, minInset)
      connectorHeight = computeConnectorSize(inset, inputConnectors.size)
    }
    inputConnectorsSize = connectorHeight
  }

  var outputConnectorsSize = 0.0
  private def computeOutputConnectorSize {
    var inset: Double = 120
    val minInset: Double = 60
    val minSize: Double = 8
    var connectorHeight: Double = computeConnectorSize(inset, outputConnectors.size)
    if (connectorHeight < minSize) {
      val diff: Double = minSize - connectorHeight
      inset = Math.max(inset - diff * outputConnectors.size, minInset)
      connectorHeight = computeConnectorSize(inset, outputConnectors.size)
    }
    outputConnectorsSize = connectorHeight
  }

  private def computeConnectorSize(inset: Double, numConnectors: Int): Double = {
    val maxSize: Double = 10
    var connectorHeight: Double = maxSize * 2
    val originalConnectorHeight: Double = connectorHeight
    val gap: Double = 5
    val totalHeight: Double = numConnectors * connectorHeight + (numConnectors - 1) * gap
    connectorHeight = Math.min(totalHeight, getPrefHeight - inset) / (numConnectors)
    connectorHeight = Math.min(connectorHeight, maxSize * 2)
    if (numConnectors == 1) {
      connectorHeight = originalConnectorHeight
    }
    return connectorHeight
  }

  private def adjustConnectorSize {
    if (!inputConnectors.isEmpty && !outputConnectors.isEmpty) {
      inputConnectorsSize = Math.min(inputConnectorsSize, outputConnectorsSize)
      outputConnectorsSize = inputConnectorsSize
    }
    for (connector <- inputConnectors) {
      connector.setRadius(inputConnectorsSize / 2.0)
    }
    for (connector <- outputConnectors) {
      connector.setRadius(outputConnectorsSize / 2.0)
    }
  }


  def getParentChildren : Option[ObservableList[Node]] = {
    var ret : Option[ObservableList[Node]] = None
    var protectedChildrenMethod: Method = null

    try {
      protectedChildrenMethod = classOf[Parent].getDeclaredMethod("getChildren")
      protectedChildrenMethod.setAccessible(true)
      ret = Some(protectedChildrenMethod.invoke(this.getParent.asInstanceOf[AnyRef]).asInstanceOf[ObservableList[Node]])
    } catch {
      case e:Throwable => System.err.println("Error getChildrenMethod")
    }

    ret
  }


  computeInputConnectorSize
  computeOutputConnectorSize
  adjustConnectorSize

}

class CodeNodeVisualization(model: CodeNodeModel) extends NodeVisualization(model) {
  getLeftIcons.add(new CloseIcon(this))
  getLeftIcons.add(new MinimizeIcon(this))

  new CodeUIController(model, this)
}