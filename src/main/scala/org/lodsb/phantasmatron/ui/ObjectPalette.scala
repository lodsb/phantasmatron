package org.lodsb.phantasmatron.ui

import scalafx.scene.control.{TreeView, TreeItem}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.{ClipboardContent, TransferMode, MouseEvent}
import javafx.event.EventHandler
import scalafx.Includes._
import org.lodsb.phantasmatron.ui.ObjectPalette.{CreateNewCodeNode, ObjectDescriptor}
import javafx.scene.input.DataFormat
import scala.pickling._
import json._
import org.lodsb.phantasmatron.core.ObjectWatcher
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import scalafx.application.Platform

/**
 * Created by lodsb on 12/22/13.
 */
object ObjectPalette { // list for locations?
  case class ObjectDescriptor(name: String, location: Option[String], tags: List[String], author: String = "lodsb", typeInfo: String ="code")

object CreateNewCodeNode extends ObjectDescriptor("New CodeNode", None, List.empty, "", "code")
  val dataFormat = new DataFormat("ObjectDescriptor")

}

class ObjectPalette extends TreeView[String] {

	ObjectWatcher.foo

  var knownObjectsMap = Map[String, ObjectDescriptor]()

  // TODO:  currently rather primitive
  ObjectWatcher.knownObjects.addListener(new ListChangeListener[ObjectDescriptor] {
	  def onChanged(p1: Change[_ <: ObjectDescriptor]): Unit = {
		  println("object palette update!")
		  val objList = ObjectWatcher.knownObjects.toList

		  Platform.runLater({root = buildTree(objList)})

	  }
  })


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

	// entry to create new node
    var catRoots = List(new TreeItem[String]{
				value = "Create New"
			})

	  knownObjectsMap = knownObjectsMap + ("Create New" -> CreateNewCodeNode)

		catRoots = catRoots ::: categories.map{ cat=>

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
