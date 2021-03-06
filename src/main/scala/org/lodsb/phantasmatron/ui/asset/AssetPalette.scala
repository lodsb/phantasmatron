/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: AssetPalette.scala
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

package org.lodsb.phantasmatron.ui.asset

import scalafx.scene.control.{TreeView, TreeItem}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.{ClipboardContent, TransferMode, MouseEvent}
import scalafx.Includes._
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import scalafx.application.Platform
import org.lodsb.phantasmatron.core.asset._
import org.lodsb.phantasmatron.core.code.Code
import org.lodsb.phantasmatron.core.asset.CodeAssetDescriptor

/**
 * Created by lodsb on 12/22/13.
 */


class AssetPalette extends TreeView[String] {

	var knownObjectsMap = Map[String, AssetDescriptor[Code]]()

	// TODO:  currently rather primitive
	CodeAssetManager.knownObjects.addListener(new ListChangeListener[AssetDescriptor[Code]] {
		def onChanged(p1: Change[_ <: AssetDescriptor[Code]]): Unit = {
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

        println(desc.get)

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


	private def buildTree(objectList: List[AssetDescriptor[Code]]): TreeItem[String] = {
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
