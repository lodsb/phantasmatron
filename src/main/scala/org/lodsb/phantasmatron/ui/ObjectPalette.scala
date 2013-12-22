package org.lodsb.phantasmatron.ui

import scalafx.scene.control.{TreeView, TreeItem}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.{TransferMode, MouseEvent}
import javafx.event.EventHandler
import scalafx.Includes._

/**
 * Created by lodsb on 12/22/13.
 */
class ObjectPalette extends TreeView[String] {
  case class ObjectDescriptor(name: String, location: String, categories: List[String])

  var knownObjects = List(
      ObjectDescriptor("test", "foo.scala", List("trash"))  ,
      ObjectDescriptor("test3", "foo2.scala", List("test","trash")) ,
      ObjectDescriptor("test4", "foo2.scala", List("foobar", "trash"))  ,
      ObjectDescriptor("test5", "foo4.scala", List("test"))
  )

  var knownObjectsMap = Map[String, ObjectDescriptor]()


  this.root = buildTree(knownObjects)

  onDragDetected = (event: MouseEvent) => {
      val db = startDragAndDrop(TransferMode.MOVE)

      val selection = this.selectionModel.value.getSelectedItems



      if(selection.size() != 0){
        val itemName = selection(0).getValue
        if(knownObjectsMap.contains(itemName))
          println("DRAG "+ selection)
      }

    event.consume()
  }


  private def buildTree(objectList: List[ObjectDescriptor]): TreeItem[String] = {
    val categories = objectList.map(x => x.categories).flatten.distinct

    val catRoots = categories.map{ cat=>

      val catItems = objectList.filter(x=> x.categories.contains(cat)).map{ item =>

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
