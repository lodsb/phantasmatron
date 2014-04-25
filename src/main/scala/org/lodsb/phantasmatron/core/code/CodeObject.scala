package org.lodsb.phantasmatron.core.code

import org.lodsb.reakt.async.VarA
import org.lodsb.reakt.sync.VarS
import scalafx.application.Platform
import scalafx.beans.property.StringProperty

import org.lodsb.reakt.TVar
import scalafx.scene.layout.{GridPane, Pane}
import scalafx.scene.text.Text

import scala.reflect.runtime.universe._

/**
 * Created by lodsb on 12/20/13.
 */
trait CodeObject {
  var inputs : Seq[TaggedSignal[_,_ <: TVar[_]]] = List.empty
  var outputs: Seq[TaggedSignal[_,_ <: TVar[_]]] = List.empty

  var controlPanel: Pane = new GridPane{
    add(new Text("No controls"), 0,0)
  }

  var propertyPanel: Pane = new GridPane{
    add(new Text("No properties"), 0,0)
  }

  //TODO: integrate
  def destroy() = {}
}

/**
 * Created by lodsb on 12/20/13.
 */
class TT extends CodeObject

class X extends TT {
  import scalafx.scene.control.Slider

  val in = Input[Double]("foo", 0.0, false)
  val out= Output[Double]("slider", 0.0)
  val out2: TaggedSignal[String, _ <: TVar[String]] =Output[String]("sf", "")

  inputs = List(in)
  outputs = outputs :+ out
  outputs = outputs :+ out2
  outputs = outputs :+ Output[Boolean]("farrrr", true)

  in.observe({x=> println("connected value: "+x); true})

  val slider = new Slider
  slider.value.onChange({(x,y,z) => out.emit(x.value)})

  val text = new Text("foo")
  val ov = new StringProperty("muhuhu")

  in.observe({x=> Platform.runLater(text.setText(x+"")); true})



  controlPanel = new GridPane{
    add(slider, 0,0)
    add(text, 1,0)
  }

}

object Output{
  def apply[T:TypeTag](name: String, default: T, async: Boolean=true) = {
    if(async)
      new TaggedVarA[T](name, default)
    else
      new TaggedVarS[T](name, default)
  }
}

object Input{
  def apply[T:TypeTag](name: String, default: T, async: Boolean=true) = {
    if(async)
      new TaggedVarA[T](name, default)
    else
      new TaggedVarS[T](name, default)
  }
}

trait TaggedSignal[T, S <: TVar[T]] {
  this : S =>

  val signal: S = this

  protected val async = false;
  protected val signalName = "";

  def isAsync = async
  def name = signalName

  var outerTypeTag : TypeTag[S]
  var innerTypeTag : TypeTag[T]

  def typeString : String = {
    innerTypeTag.toString()
  }

  def typeTag = innerTypeTag
}

class TaggedVarA[T](val varName: String, default: T)(implicit ev: TypeTag[TaggedVarA[T]], ev2: TypeTag[T]) extends VarA(default) with TaggedSignal[T, TaggedVarA[T]]{
  var outerTypeTag = ev
  var innerTypeTag = ev2

  override val async = true
  override val signalName = varName
}

class TaggedVarS[T](val varName: String, default: T)(implicit ev: TypeTag[TaggedVarS[T]], ev2: TypeTag[T]) extends VarS(default) with TaggedSignal[T, TaggedVarS[T]]{
  var outerTypeTag = ev
  var innerTypeTag = ev2

  override val async = false
  override val signalName = varName
}