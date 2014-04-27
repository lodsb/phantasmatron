/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: ConnectorVisualization.scala
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

import org.lodsb.phantasmatron.core.dataflow.{CodeConnectorModel, ConnectorModel}
import org.lodsb.phantasmatron.ui.Util
import scalafx.scene.paint.Color
import javafx.scene.control.Tooltip

import scalafx.Includes._
import javafx.scene.shape.Circle
import org.lodsb.phantasmatron.core.messaging.{Message, MessageBus}
import scalafx.scene.effect.Bloom

//import javafx.scene.shape.Circle

/**
 * Created by lodsb on 4/21/14.
 */

case class ConnectingStartedMessage(model: ConnectorModel) extends Message
case class ConnectingEndedMessage(model: ConnectorModel) extends Message

class ConnectorVisualization(protected[dataflow] val model: ConnectorModel) extends Circle {
  this.radius = 20
  private val effect = new Bloom
  effect.threshold = 0.1



  //TODO: add unregister
  MessageBus.registerHandler({x: Message =>
    x match {
      case ConnectingStartedMessage(m2)=> {
           if(model.isCompatible(m2)){
             this.setEffect(effect)
           }
      }

      case ConnectingEndedMessage(m2) => {
        this.setEffect(null)
      }

      case _ =>
    }
  })

}

class CodeConnectorVisualization[X](model: CodeConnectorModel[X]) extends ConnectorVisualization(model) {
  val typeColor = Util.typeString2Color(model.signal.typeString)

  var signalInfo =""
  var signalName =""

  signalName = model.signal.name+": "

  this.fill = typeColor

  if(model.signal.isAsync) {
    this.setStroke(Color.WHITE)
    this.setStrokeWidth(2)

    this.getStrokeDashArray.add(3)
    signalInfo = ", async"
  }


  val t = new Tooltip(signalName + model.signal.typeString+signalInfo)
  Tooltip.install(this, t)



}

object ConnectorVisualization {
  def apply(model: ConnectorModel) : ConnectorVisualization =  {
    model match {
      case c:CodeConnectorModel[_] => new CodeConnectorVisualization(c.asInstanceOf[CodeConnectorModel[Any]])
      case _ => new ConnectorVisualization(model)
    }
  }
}
