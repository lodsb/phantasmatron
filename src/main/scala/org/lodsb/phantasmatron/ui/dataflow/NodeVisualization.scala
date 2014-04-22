package org.lodsb.phantasmatron.ui.dataflow

import jfxtras.labs.scene.control.window.{MinimizeIcon, CloseIcon, Window}
import javafx.scene.control.Control
import org.lodsb.phantasmatron.core.dataflow.{ConnectorModel, NodeModel, CodeNodeModel}
import org.lodsb.phantasmatron.ui.CodeUIController
import eu.mihosoft.vrl.workflow.fx.{FXNewConnectionSkin, NodeUtil, ConnectorCircle, FlowNodeWindow}

import scalafx.Includes._
import javafx.scene.shape.Circle
import javafx.beans.binding.DoubleBinding
import javafx.scene.input.MouseEvent
import javafx.scene.Node
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Bounds
import javafx.event.EventHandler
import scalafx.collections.ObservableBuffer

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

class NodeVisualization(model: NodeModel) extends Window {
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
    })
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


    boundsInLocalProperty.addListener(new ChangeListener[Bounds] {
      def changed(observable: ObservableValue[_ <: Bounds], oldValue: Bounds, newValue: Bounds) {
        computeInputConnectorSize
        computeOutputConnectorSize
        adjustConnectorSize
      }
    })
    NodeUtil.addToParent(getParent, connectorNode)

    //connectorNode.onMouseEnteredProperty set {x: MouseEvent => connectorNode.toFront()}

    connectorNode.onMouseEnteredProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        connectorNode.toFront
      }
    })

    /*
    connectorNode.onMousePressedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        if (controller.getConnections(connector.getType).isInputConnected(connector)) {
          return
        }
        t.consume
        newConnectionPressEvent = t
      }
    })

    connectorNode.onMouseDraggedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        if (controller.getConnections(connector.getType).isInputConnected(connector)) {
          return
        }
        if (newConnectionSkin == null) {
          newConnectionSkin = new FXNewConnectionSkin(getSkinFactory, getParent, connector, getController, connector.getType)
          newConnectionSkin.add
          MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector, newConnectionPressEvent)
        }
        t.consume
        MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector, t)
        t.consume
        MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector, t)
      }
    })
    connectorNode.onMouseReleasedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        connector.click(NodeUtil.mouseBtnFromEvent(t), t)
        if (controller.getConnections(connector.getType).isInputConnected(connector)) {
          return
        }
        t.consume
        try {
          MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector, t)
        }
        catch {
          case ex: Exception => {
          }
        }
        newConnectionSkin = null
      }
    })
    */
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
      NodeUtil.removeFromParent(connectorNode)
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
    import scala.collection.JavaConversions._
    for (connector <- inputConnectors) {
      connector.setRadius(inputConnectorsSize / 2.0)
    }
    import scala.collection.JavaConversions._
    for (connector <- outputConnectors) {
      connector.setRadius(outputConnectorsSize / 2.0)
    }
  }

}

class CodeNodeVisualization(model: CodeNodeModel) extends NodeVisualization(model) {
  getLeftIcons.add(new CloseIcon(this))
  getLeftIcons.add(new MinimizeIcon(this))

  new CodeUIController(model, this, null)
}