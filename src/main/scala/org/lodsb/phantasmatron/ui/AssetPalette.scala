package org.lodsb.phantasmatron.ui

import scalafx.scene.control.{TreeView, TreeItem}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.{ClipboardContent, TransferMode, MouseEvent}
import javafx.event.EventHandler
import scalafx.Includes._
import javafx.scene.input.DataFormat
import scala.pickling._
import json._
import org.lodsb.phantasmatron.core.{CreateNewCodeNode, AssetDataFormat, AssetDescriptor, CodeAssetManager}
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import scalafx.application.Platform

/**
 * Created by lodsb on 12/22/13.
 */


class AssetPalette extends TreeView[String] {

	var knownObjectsMap = Map[String, AssetDescriptor]()

	// TODO:  currently rather primitive
	CodeAssetManager.knownObjects.addListener(new ListChangeListener[AssetDescriptor] {
		def onChanged(p1: Change[_ <: AssetDescriptor]): Unit = {
			val objList = CodeAssetManager.knownObjects.toList

			Platform.runLater({
				root = buildTree(objList)
			})

		}
	})


	root = buildTree(List.empty)

	onDragDetected = (event: MouseEvent) => {
		val db = startDragAndDrop(TransferMode.MOVE)

		val selection = this.selectionModel.value.getSelectedItems

		if (selection.size() != 0) {
			val itemName = selection(0).getValue
			val desc = knownObjectsMap.get(itemName)
			if (desc.isDefined) {
				println("DRAG " + selection)

				val content = new ClipboardContent
				content.put(AssetDataFormat, desc.get)

				db.setContent(content)
			}
		}

		event.consume()
	}

	def filter(filterString : String) {
		if(filterString == "") {
			val obj = CodeAssetManager.knownObjects.toList
			root = buildTree(obj)
		} else {
			val obj = CodeAssetManager.knownObjects.toList.filter { x=>
				x.name.contains(filterString) //|| x.author.contains(filterString)
			}
			root = buildTree(obj)
		}

	}


	private def buildTree(objectList: List[AssetDescriptor]): TreeItem[String] = {
		val categories = objectList.map(x => x.tags).flatten.distinct

		// entry to create new node
		var catRoots = List(new TreeItem[String] {
			value = "Create New"
		})

		knownObjectsMap = knownObjectsMap + ("Create New" -> CreateNewCodeNode)

		catRoots = catRoots ::: categories.map {
			cat =>

				val catItems = objectList.filter(x => x.tags.contains(cat)).map {
					item =>

						knownObjectsMap = knownObjectsMap + (item.name -> item)

						new TreeItem[String] {
							value = item.name
						}
				}

				new TreeItem[String] {
					value = "Category: " + cat
					expanded = true

					children = ObservableBuffer(catItems)
				}
		}

		new TreeItem[String] {
			value = "Local"
			children = ObservableBuffer(catRoots)
			expanded = true
		}
	}

}
