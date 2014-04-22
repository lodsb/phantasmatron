package org.lodsb.phantasmatron.ui.dataflow

import scalafx.scene.shape.Circle
import org.lodsb.phantasmatron.core.dataflow.{CodeConnectorModel, ConnectorModel}
import org.lodsb.phantasmatron.ui.Util
import scalafx.scene.paint.Color
import javafx.scene.control.Tooltip

import scalafx.Includes._

/**
 * Created by lodsb on 4/21/14.
 */
class ConnectorVisualization(protected val model: ConnectorModel) extends Circle {
  this.radius = 20

}

class CodeConnectorVisualization[X](model: CodeConnectorModel[X]) extends ConnectorVisualization(model) {
  val typeColor = Util.typeString2Color(model.signal.typeString)

  var signalInfo =""
  var signalName =""

  signalName = model.signal.name+": "

  this.fill = typeColor

  if(model.signal.isAsync) {
    stroke = Color.WHITE
    strokeWidth = 2
    strokeDashArray.addAll(3d)
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
