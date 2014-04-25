package org.lodsb.phantasmatron.ui

import scalafx.scene.control.{Separator, Button}
import scalafx.scene.layout.{Priority, HBox, Region}
import scalafx.event.Event
import scalafx.Includes._
import org.lodsb.phantasmatron.core.dataflow.{CodeNodeModel, DataflowModel}
import org.lodsb.phantasmatron.ui.code.CompileButton

/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-25 :: 17:16
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
class ToolBar(model: DataflowModel) extends scalafx.scene.control.ToolBar {
	private val reg = new Region
	HBox.setHgrow(reg, Priority.ALWAYS)

	private val compileButton = new CompileButton("Compile all")

	items.addAll(
		new Button("New"),
		new Button("Open"),
		new Button("Save"),
		new Separator(),
		reg,
		new Separator(),
		compileButton
	)

	// crude workaround, have to think of something...
	compileButton.onAction = (ev: Event) => {

    model.nodes.foreach({ n =>
      n match {
        case x:CodeNodeModel => x.compile
        case _ =>
      }
    })
	}

}
