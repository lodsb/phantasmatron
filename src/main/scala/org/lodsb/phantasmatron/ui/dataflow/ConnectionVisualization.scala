package org.lodsb.phantasmatron.ui.dataflow

import javafx.scene.control.Tooltip
import scalafx.Includes._
import javafx.scene.{Parent, Group, Node}
import scalafx.scene.shape._

import scalafx.beans.property.DoubleProperty
import scalafx.scene.paint.Color
import org.lodsb.phantasmatron.core.dataflow.{CodeConnectorModel, ConnectionModel}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import org.lodsb.phantasmatron.core.messaging.MessageBus
import org.lodsb.phantasmatron.ui.Util
import org.lodsb.phantasmatron.core.dataflow.ConnectionModel
import org.lodsb.phantasmatron.core.dataflow.CodeConnectorModel

//import javafx.beans.property.DoubleProperty


/**
 * Created by lodsb on 4/23/14.
 */
class ConnectionVisualization(showCloseButton : Boolean = false ) extends Parent {
  val startXProperty = DoubleProperty(0)
  val startYProperty = DoubleProperty(0)

  val endXProperty = DoubleProperty(0)
  val endYProperty = DoubleProperty(0)

  protected var skin: ConnectionVisualizationSkin = new LineConnectionVisualizationSkin(this, showCloseButton , 5)
  this.getChildren.add(skin.getNode)

  def setSkin(s: ConnectionVisualizationSkin) {
    this.getChildren.remove(skin.getNode)
    this.getChildren.add(s.getNode)
    this.skin = s
  }

  def setValid(v : Boolean) = {
    if(v) {
      skin.setStrokeColor(Color.GREEN)
    } else {
      skin.setStrokeColor(Color.RED)
    }
  }

  def removeConnection = {}
}

class EstablishedConnectionVisualization( protected[dataflow] val model: ConnectionModel,
                                          source: ConnectorVisualization,
                                         destination: ConnectorVisualization,
                                         s: ConnectionVisualizationSkin = null) extends ConnectionVisualization(true) {

  this.setSkin(new QuadConnectionVisualizationSkin(this, true))

  this.startXProperty <== source.layoutXProperty()
  this.startYProperty <== source.layoutYProperty()

  this.endXProperty <== destination.layoutXProperty()
  this.endYProperty <== destination.layoutYProperty()

  this.onMouseEnteredProperty.set(new EventHandler[MouseEvent] {
    def handle(t: MouseEvent) {
      toFront
      t.consume()
    }
  })


  // removing the connection has to be deferred until mouse exit, otherwise picking will...
  // ...*EXPLODE*
  var rmConn = false
  this.onMouseExitedProperty.set(new EventHandler[MouseEvent] {
    def handle(t: MouseEvent) {

      toBack
      t.consume()

      if(rmConn) {
        MessageBus.send(RemoveConnectionMessage(model))
      }
    }
  })

  override def removeConnection = {
    rmConn = true
  }

  protected[dataflow] def setStrokeColor(c: Color) = {
    skin.setStrokeColor(c)
  }

  protected[dataflow] def setDashed(b: Boolean) = {
    skin.setDashed(b)
  }

}

object EstablishedConnectionVisualization {
  def apply(model: ConnectionModel, source: ConnectorVisualization,
            destination: ConnectorVisualization) : EstablishedConnectionVisualization= {

    val viz = new EstablishedConnectionVisualization(model, source, destination)

    var signalInfo =""

    source.model match {
      case CodeConnectorModel(thatSignal,thatIsInput) => {
        val typeColor = Util.typeString2Color(thatSignal.typeString)

        viz.setStrokeColor(typeColor)

        signalInfo = thatSignal.typeString

        if(thatSignal.isAsync) {
          viz.setDashed(true)
          signalInfo = signalInfo + ", async"
        }

      }
    }

    val name: String = source.model.name() + " -> " + destination.model.name()

    val t = new Tooltip(name +" ,"+ signalInfo)
    Tooltip.install(viz, t)



    viz
  }
}

abstract class ConnectionVisualizationSkin(protected val control: ConnectionVisualization) {

  def getNode : Node
    // destroy method?

  def setStrokeColor(color: Color)
  def setDashed(enable: Boolean)
}

class QuadConnectionVisualizationSkin(control: ConnectionVisualization, showCloseButton: Boolean = false, lRadius: Double = 1) extends ConnectionVisualizationSkin(control) {
  val moveto = MoveTo(0,0)
  val quadto = QuadCurveTo(0,0,0,0)
  val quadt2 = QuadCurveTo(0,0,0,0)

  private val offset = 20

  val s = new Circle {
    radius = lRadius
  }

  val e = new Circle {
    radius = lRadius
  }

  s.centerX <== control.startXProperty
  s.centerY <== control.startYProperty

  e.centerX <== control.endXProperty
  e.centerY <== control.endYProperty

  moveto.x <== control.startXProperty
  moveto.y <== control.startYProperty

  val yy = ((control.endYProperty-control.startYProperty))
  val xx = ((control.endXProperty-control.startXProperty))

  val distSqr = max((xx*xx)+(yy*yy),1)

  val centerX = s.centerX + ( (e.centerX-s.centerX) * 0.5)
  val centerY = s.centerY + ( (e.centerY-s.centerY) * 0.5)

  //val interm = min(distSqr/10000, 100)



  quadto.controlX <== control.startXProperty  + (( (control.endXProperty-control.startXProperty)/distSqr) * 10000)
  quadto.controlY <== control.startYProperty// + offset

  quadto.x <== centerX
  quadto.y <== centerY

  quadt2.controlX <== control.endXProperty   + (( (control.startXProperty-control.endXProperty)/distSqr) * 10000)
  quadt2.controlY <== control.endYProperty// - offset

  quadt2.x <== control.endXProperty
  quadt2.y <== control.endYProperty

  val p = new Path {
    elements = List(
      moveto,
      quadto,
      quadt2
    )
  }

  p.setStrokeWidth(3)


  /*
  val g = FontAwesome.Glyph.REMOVE_CIRCLE.create()
  val c = new Circle {
    radius = 10
  }
  */

  val node : Group = if(showCloseButton){

    val m = new Circle {
      radius = 10
    }

    m.onMouseClickedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        control.removeConnection
      }
    })

    m.layoutXProperty() <== centerX
    m.layoutYProperty() <== centerY
    m.fill = Color.RED
    m.stroke = Color.WHITE

    new Group(p,s,e, m)

  } else {
    new Group(p,s,e)
  }

  def getNode = node

  def setStrokeColor(color: Color) = {
    p.setStroke(color)
    e.setFill(color)
    s.setFill(color)
  }



  def setDashed(enable: Boolean) = {
    p.getStrokeDashArray.add(4)
  }

}

class LineConnectionVisualizationSkin(control: ConnectionVisualization, showCloseButton: Boolean = false, lRadius: Double = 1) extends ConnectionVisualizationSkin(control) {
  val moveto = MoveTo(0,0)
  val lineto = LineTo(0,0)

  moveto.x <== control.startXProperty
  moveto.y <== control.startYProperty

  lineto.x <== control.endXProperty
  lineto.y <== control.endYProperty

  val p = new Path {
    elements = List(
      moveto,
      lineto
    )
  }

  p.setStrokeWidth(2.5)

  val s = new Circle {
    radius = lRadius
  }

  val e = new Circle {
    radius = lRadius
  }



  s.centerX <== control.startXProperty
  s.centerY <== control.startYProperty

  e.centerX <== control.endXProperty
  e.centerY <== control.endYProperty

  /*
  val g = FontAwesome.Glyph.REMOVE_CIRCLE.create()
  val c = new Circle {
    radius = 10
  }
  */

  val node : Group = if(showCloseButton){

    val m = new Circle {
      radius = 10
    }

    m.onMouseClickedProperty.set(new EventHandler[MouseEvent] {
      def handle(t: MouseEvent) {
        control.removeConnection
      }
    })

    m.layoutXProperty() <== s.centerX + ( (e.centerX-s.centerX) * 0.5)
    m.layoutYProperty() <== s.centerY + ( (e.centerY-s.centerY) * 0.5)
    m.fill = Color.RED
    m.stroke = Color.WHITE

    new Group(p,s,e, m)

  } else {
    new Group(p,s,e)
  }

  def getNode = node

  def setStrokeColor(color: Color) = {
    p.setStroke(color)
    e.setFill(color)
    s.setFill(color)
  }



  def setDashed(enable: Boolean) = {
    p.getStrokeDashArray.add(3)
  }

}

