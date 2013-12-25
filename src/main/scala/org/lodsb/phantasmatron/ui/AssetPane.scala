package org.lodsb.phantasmatron.ui

import scalafx.scene.layout.{HBox, Priority, VBox, GridPane}
import org.controlsfx.control.TextFields
import scalafx.Includes._
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets}
import org.controlsfx.glyphfont.{FontAwesome, GlyphFontRegistry}
import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import scalafx.event.Event

/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-25 :: 17:14
    >>  Origin: phantasmatron
    >>
  +3>>
    >>  Copyright (c) 2013:
    >>
    >>     |             |     |
    >>     |    ,---.,---|,---.|---.
    >>     |    |   ||   |`---.|   |
    >>     `---'`---'`---'`---'`---'
    >>                    // Niklas Klügel
    >>
  +4>>
    >>  Made in Bavaria by fat little elves - since 1983.
 */
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
