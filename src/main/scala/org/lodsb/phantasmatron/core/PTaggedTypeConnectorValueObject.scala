package org.lodsb.phantasmatron.core

/**
 * Created by lodsb on 4/17/14.
 */

import eu.mihosoft.vrl.workflow._
import javafx.beans.property.ObjectProperty
import java.lang.Object
import java.lang.String
import scala.Predef.String

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class PTaggedTypeConnectorValueObject(private var parent: VNode, private val c: Connector) extends ValueObject {

 /* def this(conn: Connector) = {
    //this.c = conn
    //this.parent = conn.getNode
  }*/

  def getParent: VNode = {
    return parent
  }

  def getConnector: Connector = {
    return c
  }

  def getValue: AnyRef = {
    throw new UnsupportedOperationException("Not supported yet.")
  }

  def setValue(o: AnyRef) {
    throw new UnsupportedOperationException("Not supported yet.")
  }

  def valueProperty: ObjectProperty[AnyRef] = {
    throw new UnsupportedOperationException("Not supported yet.")
  }

  def compatible(sender: ValueObject, flowType: String): CompatibilityResult = {
    return new CompatibilityResult {
      def isCompatible: Boolean = {
        val differentObjects: Boolean = sender ne PTaggedTypeConnectorValueObject.this
        var compatibleType: Boolean = false
        if (sender.isInstanceOf[PTaggedTypeConnectorValueObject]) {
          val senderConnectorVObj = sender.asInstanceOf[PTaggedTypeConnectorValueObject]
          compatibleType = (getConnector.asInstanceOf[PConnector].getTypeTag.tpe.<:<(senderConnectorVObj.getConnector.asInstanceOf[PConnector].getTypeTag.tpe.typeConstructor)) && getConnector.isInput && senderConnectorVObj.getConnector.isOutput
        }
        return differentObjects && compatibleType
      }

      def getMessage: String = {
        var senderId: String = sender.getParent.toString + ":undefined"
        if (sender.isInstanceOf[DefaultConnectorValueObject]) {
          senderId = (sender.asInstanceOf[DefaultConnectorValueObject]).getConnector.getId
        }
        return "incompatible: " + senderId + " -> " + getConnector.getId
      }

      def getStatus: String = {
        throw new UnsupportedOperationException("Not supported yet.")
      }
    }
  }

  private var v: VisualizationRequest = null

  def getVisualizationRequest: VisualizationRequest = {
    v
  }

  /**
   * @param parent the parent to set
   */
  def setParent(parent: VNode) {
    this.parent = parent
  }

  def setVisualizationRequest(vReq: VisualizationRequest): Unit = {
    this.v = vReq
  }
}
