package org.lodsb.phantasmatron.core

/**
 * Created by lodsb on 4/18/14.
 */

import eu.mihosoft.vrl.workflow._
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import scala.reflect.api.TypeTags
import java.util.ArrayList
import java.util.List
import java.lang.String
import scala.Predef.String

/**
 * Created by lodsb on 4/17/14.
 */
/*
 * ConnectorImpl.java
 *
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class PConnector(private val node: VNode, private val typeTag: TypeTags#TypeTag[_],
                 private var localId: String, private val input: Boolean) extends Connector {

  /*def this(node: VNode, typeTag: TypeTags#TypeTag[_], localId: String, input: Boolean) {
    this()
    this.`type` = `type`
    this.localId = localId
    this.node = node
    this.input = input
    this.output = !input
    setValueObject(new PTaggedTypeConnectorValueObject(node, this))
  } */

  private var vRequest: VisualizationRequest = null
  private val output: Boolean = !input
  
  private final val pvalueObjectProperty: ObjectProperty[ValueObject] = new SimpleObjectProperty[ValueObject]
  setValueObject(new PTaggedTypeConnectorValueObject(node, this))

  private var connectionEventHandlers: List[EventHandler[ConnectionEvent]] = null
  private var clickEventHandlers: List[EventHandler[ClickEvent]] = null

  def getTypeTag = this.typeTag

  def getType: String = {
    return this.typeTag.toString
  }

  def getId: String = {
    return this.node.getId + ":" + this.localId
  }

  def getLocalId: String = {
    return this.localId
  }

  def setLocalId(id: String) {
    this.localId = id
  }

  def getNode: VNode = {
    return this.node
  }

  def getVisualizationRequest: VisualizationRequest = {
    return this.vRequest
  }

  def setVisualizationRequest(vReq: VisualizationRequest) {
    this.vRequest = vReq
  }

  /**
   * @return the input
   */
  def isInput: Boolean = {
    return input
  }

  /**
   * @return the output
   */
  def isOutput: Boolean = {
    return output
  }

  final def setValueObject(vObj: ValueObject) {
    System.err.println("SET VAL OBJ" + vObj)
    pvalueObjectProperty.set(vObj)
  }

  def getValueObject: ValueObject = {
    return pvalueObjectProperty.get
  }

  def valueObjectProperty: ObjectProperty[ValueObject] = {
    return this.pvalueObjectProperty
  }

  private def getEventHandlers: List[EventHandler[ConnectionEvent]] = {
    if (this.getConnectionEventHandlers == null) {
      this.connectionEventHandlers = new ArrayList[EventHandler[ConnectionEvent]]
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

  /**
   * @return the connectionEventHandlers
   */
  def getConnectionEventHandlers: List[EventHandler[ConnectionEvent]] = {
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

  /**
   * @return the clickEventHandlers
   */
  def getClickEventHandlers: List[EventHandler[ClickEvent]] = {
    if (clickEventHandlers == null) {
      this.clickEventHandlers = new ArrayList[EventHandler[ClickEvent]]
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

