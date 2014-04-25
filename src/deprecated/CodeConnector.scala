package org.lodsb.phantasmatron.core

import eu.mihosoft.vrl.workflow._
import javafx.event.EventHandler
import javafx.beans.property.{SimpleObjectProperty, ObjectProperty}
import java.util

/**
 * Created by lodsb on 12/20/13.
 */
class CodeConnector(val node: VNode, val output : Boolean,
                    val typeS: String, var id: String ) extends Connector {

  var vReq : VisualizationRequest = null

  private final val vOProperty: ObjectProperty[ValueObject] = new SimpleObjectProperty[ValueObject]
  private var connectionEventHandlers: util.ArrayList[EventHandler[ConnectionEvent]] = null
  private var clickEventHandlers: util.ArrayList[EventHandler[ClickEvent]] = null

  def getType: String = typeS

  def isInput: Boolean = !output

  def isOutput: Boolean = output

  def getId: String = node.getId+ ":" + this.getLocalId

  def getLocalId: String = id

  def setLocalId(id: String): Unit = {this.id = id}

  def getNode: VNode = node

  def setValueObject(obj: ValueObject): Unit = {vOProperty.set(obj)}

  def getValueObject: ValueObject = vOProperty.get()

  def valueObjectProperty(): ObjectProperty[ValueObject] = vOProperty

  def getVisualizationRequest: VisualizationRequest = vReq

  def setVisualizationRequest(vReq: VisualizationRequest): Unit = {this.vReq = vReq}

  private def getEventHandlers: util.ArrayList[EventHandler[ConnectionEvent]] = {
    if (this.getConnectionEventHandlers == null) {
      this.connectionEventHandlers = new util.ArrayList[EventHandler[ConnectionEvent]]
    }
    return this.getConnectionEventHandlers
  }

  def addConnectionEventListener(handler: EventHandler[ConnectionEvent]) {
    getEventHandlers.add(handler)
  }

  def removeConnectionEventListener(handler: EventHandler[ConnectionEvent]) {
    getEventHandlers.remove(handler)
    if (getEventHandlers.isEmpty) {
      this.connectionEventHandlers = null
    }
  }

  def getConnectionEventHandlers: util.ArrayList[EventHandler[ConnectionEvent]] = {
    return connectionEventHandlers
  }

  def addClickEventListener(handler: EventHandler[ClickEvent]) {
    getClickEventHandlers.add(handler)
  }

  def removeClickEventListener(handler: EventHandler[ClickEvent]) {
    getClickEventHandlers.remove(handler)
    if (getClickEventHandlers.isEmpty) {
      this.clickEventHandlers = null
    }
  }

  def getClickEventHandlers: util.ArrayList[EventHandler[ClickEvent]] = {
    if (clickEventHandlers == null) {
      this.clickEventHandlers = new util.ArrayList[EventHandler[ClickEvent]]
    }
    return clickEventHandlers
  }

  def click(btn: MouseButton, event: AnyRef) {
    if (clickEventHandlers == null) {
      return
    }
    val evt: ClickEvent = new ClickEvent(ClickEvent.ANY, this, btn, event)
    import scala.collection.JavaConversions._
    for (evth <- clickEventHandlers) {
      evth.handle(evt)
    }
  }


}
