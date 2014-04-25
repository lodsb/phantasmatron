package org.lodsb.phantasmatron.core.dataflow

import scalafx.collections.ObservableBuffer
import scalafx.beans.value.ObservableValue
import scalafx.Includes._
import scalafx.beans.property.StringProperty
import scala.reflect.runtime.universe._

/**
 * Created by lodsb on 4/20/14.
 */

trait IdEntity {
  val name = new StringProperty("")
  val id = IdGenerator.genID
}

trait ConnectorModel extends IdEntity {

  val isInput: Boolean = false
  def isOutput:Boolean = !isInput

  def isCompatible(that: ConnectorModel) : Boolean
}

case class ConnectionModel(source: ConnectorModel, destination: ConnectorModel) extends IdEntity

trait NodeModel extends IdEntity {
  val connectors = new ObservableBuffer[ConnectorModel]()
}

class DataflowModel extends IdEntity {
  val nodes = new ObservableBuffer[NodeModel]()
  val connections = new ObservableBuffer[ConnectionModel]()

  //TODO: this is all slow, should be fixed lateron
  def disconnectAllConnectionsFromNode(node: NodeModel) = {
    node.connectors.foreach({x=>
      val toRemove = connections.filter({ p => p.source == x || p.destination == x})

      toRemove.foreach{ r =>
        connections.remove(r)
      }
    })
  }
}

object IdGenerator {
  private var currentID = 0;

  def genID : Int = {
    synchronized {
      currentID = currentID + 1;

      currentID
    }
  }
}
