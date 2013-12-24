package org.lodsb.phantasmatron.ui

import eu.mihosoft.vrl.workflow.{VNodeSkin, VNode, FlowFactory}
import eu.mihosoft.vrl.workflow.fx.{FXValueSkinFactory, FXSkinFactory, ScalableContentPane}
import org.lodsb.phantasmatron.core.Code
import java.io.File
import javafx.scene.input.{DragEvent, Dragboard, TransferMode}
import javafx.scene.effect.BlendMode
import javafx.event.EventHandler
import scala.util.{Failure, Success, Try}
import org.controlsfx.control.action.Action
import org.controlsfx.dialog.Dialogs

/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-24 :: 20:16
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
class Flow extends ScalableContentPane {
	val flow = FlowFactory.newFlow()
	val skinFactory = new FXValueSkinFactory(this.getContentPane)

	flow.setVisible(true)

	this.getStyleClass.setAll("vflow-background")
	this.getStylesheets.setAll((new File("default.css").toURI.toString))


	skinFactory.addSkinClassForValueType(classOf[Code], classOf[CodeFlowSkin])
	flow.addSkinFactories(skinFactory)

	this.setOnDragEntered(new EventHandler[DragEvent] {
			def handle(dragEvent: DragEvent) {
				System.out.println("DRAG ENTERED" + dragEvent.getDragboard.getContentTypes)
				dragEvent.acceptTransferModes(TransferMode.MOVE, TransferMode.COPY)
				setBlendMode(BlendMode.RED)
			}
		})



		this.setOnDragOver(new EventHandler[DragEvent] {
			def handle(dragEvent: DragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE, TransferMode.COPY)
			}
		})



		this.setOnDragExited(new EventHandler[DragEvent] {
			def handle(dragEvent: DragEvent) {
				System.out.println("DRAG Exit" + dragEvent.getDragboard.getContentTypes)
				setBlendMode(BlendMode.MULTIPLY)
			}
		})



		this.setOnDragDropped(new EventHandler[DragEvent] {
			def handle(dragEvent: DragEvent) {
				System.out.println("DRAG Drop" + dragEvent.getDragboard.getContentTypes)
				val db: Dragboard = dragEvent.getDragboard
				if (db.getContentTypes.contains(ObjectPalette.dataFormat)) {
					val o: ObjectPalette.ObjectDescriptor = db.getContent(ObjectPalette.dataFormat).asInstanceOf[ObjectPalette.ObjectDescriptor]
					System.out.println("correct drop format")
					val c: Try[Code] = Code.apply(o)

					c match {
						case Success(cc) => {
							val v: VNode = flow.newNode
							v.getValueObject.setValue(cc)
							v.setTitle("Node " + v.getId)
							v.setX(dragEvent.getX)
							v.setY(dragEvent.getY)
							v.setWidth(300)
							v.setHeight(200)
							val skin: VNodeSkin[_ <: VNode] = skinFactory.createSkin(v, flow)
							skin.add
							dragEvent.setDropCompleted(true)
						}
							case Failure(v) => {
								Dialogs.create().title("Error")
												.masthead("Sorry - there was an exception while creating the CodeNode")
												.showException(v)
							}
					}
				}
				setBlendMode(BlendMode.MULTIPLY)
			}
		})




}


