/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: AssetPane.scala
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

import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.Includes._
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets}
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.control.textfield.TextFields

class AssetPane extends VBox {
	private val assetPalette = new AssetPalette
	VBox.setVgrow(assetPalette, Priority.ALWAYS)

	private val searchField = TextFields.createSearchField()

	private val searchBox = new HBox{
		val node = FontAwesome.Glyph.SEARCH.create()
		node.margin = Insets(5)
		searchField.margin = Insets(5)

		children.add(node)
		children.add(searchField)
	}

	private val label = new Text{
			text = "Assets"
			margin = Insets(10)
			alignment = Pos.CENTER
		}

	children.add(label)
	children.add(searchBox)
	children.add(assetPalette)

	searchField.text.onChange(assetPalette.filter(searchField.getText))

}
