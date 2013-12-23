package org.lodsb.phantasmatron.ui

import scalafx.scene.control.{TreeView, TreeItem}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.{ClipboardContent, TransferMode, MouseEvent}
import javafx.event.EventHandler
import scalafx.Includes._
import org.lodsb.phantasmatron.ui.ObjectPalette.ObjectDescriptor
import javafx.scene.input.DataFormat
import scala.pickling._
import json._

/**
 * Created by lodsb on 12/22/13.
 */
object ObjectPalette {
  case class ObjectDescriptor(name: String, location: Option[String], tags: List[String], author: String = "lodsb", typeInfo: String ="code")
  val dataFormat = new DataFormat("ObjectDescriptor")

}

class ObjectPalette extends TreeView[String] {

  var knownObjects = List(
      ObjectDescriptor("test", Some("foo.scala"),    List("trash"))  ,
      ObjectDescriptor("test3", Some("foo2.scala"),  List("test","trash")) ,
      ObjectDescriptor("test4", Some("foo2.scala"),  List("foobar", "trash"))  ,
      ObjectDescriptor("test5", Some("foo4.scala"),  List("test"))
  )

  knownObjects.foreach{x=>  println(x.pickle)}

  var knownObjectsMap = Map[String, ObjectDescriptor]()


  this.root = buildTree(knownObjects)

  onDragDetected = (event: MouseEvent) => {
      val db = startDragAndDrop(TransferMode.MOVE)

      val selection = this.selectionModel.value.getSelectedItems



      if(selection.size() != 0){
        val itemName = selection(0).getValue
        val desc = knownObjectsMap.get(itemName)
        if(desc.isDefined) {
          println("DRAG "+ selection)

          val content = new ClipboardContent
          content.put(ObjectPalette.dataFormat, desc.get)

          db.setContent(content)
        }
      }

    event.consume()
  }


  private def buildTree(objectList: List[ObjectDescriptor]): TreeItem[String] = {
    val categories = objectList.map(x => x.tags).flatten.distinct

    val catRoots = categories.map{ cat=>

      val catItems = objectList.filter(x=> x.tags.contains(cat)).map{ item =>

        knownObjectsMap = knownObjectsMap + (item.name -> item)

        new TreeItem[String] {
          value = item.name
        }
      }

      new TreeItem[String]{
        value = "Category: "+cat

        children = ObservableBuffer(catItems)
      }
    }

    new TreeItem[String]{
      value = "Local"
      children = ObservableBuffer(catRoots)
    }
  }

}
