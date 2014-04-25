package org.lodsb.phantasmatron.ui.dataflow

import javafx.scene.control.Tooltip
import scalafx.Includes._
import javafx.scene.{Parent, Group, Node}
import scalafx.scene.shape.{LineTo, MoveTo, Circle, Path}

import scalafx.beans.property.DoubleProperty
import scalafx.scene.paint.Color
import org.lodsb.phantasmatron.core.dataflow.{CodeConnectorModel, ConnectionModel}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import org.lodsb.phantasmatron.core.messaging.MessageBus
import org.lodsb.phantasmatron.ui.Util

//import javafx.beans.property.DoubleProperty


/**
 * Created by lodsb on 4/23/14.
 */
class ConnectionVisualization(protected var skin: ConnectionVisualizationSkin = null, showCloseButton : Boolean = false ) extends Parent {
  val startXProperty = DoubleProperty(0)
  val startYProperty = DoubleProperty(0)

  val endXProperty = DoubleProperty(0)
  val endYProperty = DoubleProperty(0)

  if(skin == null) {
    skin = new LineConnectionVisualizationSkin(this, showCloseButton , 5)
  }


  this.getChildren.add(skin.getNode)

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
                                         s: ConnectionVisualizationSkin = null) extends ConnectionVisualization(s, true) {


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

