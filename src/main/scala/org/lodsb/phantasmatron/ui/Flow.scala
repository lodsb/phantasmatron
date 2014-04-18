package org.lodsb.phantasmatron.ui

import eu.mihosoft.vrl.workflow._
import eu.mihosoft.vrl.workflow.fx.{FXValueSkinFactory, FXSkinFactory, ScalableContentPane}
import org.lodsb.phantasmatron.core._
import java.io.File
import javafx.scene.input.{DragEvent, Dragboard, TransferMode}
import javafx.scene.effect.BlendMode
import javafx.event.EventHandler
import scala.util.{Failure, Success, Try}
import org.controlsfx.control.action.Action
import org.controlsfx.dialog.Dialogs
import scala.util.Success
import scala.util.Failure
import javafx.beans.property.ObjectProperty
import scala.util.Success
import org.lodsb.phantasmatron.core.AssetDescriptor
import scala.util.Failure

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
	val flow = PFlowFactory.newFlow()
	val skinFactory = new PValueSkinFactory(this.getContentPane)

	flow.setVisible(true)

  this.setMaxScaleX(1.0);
  this.setMaxScaleY(1.0);

  this.getStyleClass.setAll("vflow-background")
	this.getStylesheets.setAll((new File("default.css").toURI.toString))


	skinFactory.addSkinClassForValueType(classOf[Code], classOf[CodeFlowSkin])
//	skinFactory.addSkinClassForValueType(classOf[String], classOf[StringFlowNodeSkin])

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
				if (db.getContentTypes.contains(AssetDataFormat)) {
					val o: AssetDescriptor = db.getContent(AssetDataFormat).asInstanceOf[AssetDescriptor]
					System.out.println("correct drop format")
					val c: Try[Code] = CodeAssetManager.load(o)

					c match {
						case Success(cc) => {

              val pv = new PValueObject()
              pv.setValue(cc)
              val v: VNode = flow.newNode(pv)

							v.setX(dragEvent.getX)
							v.setY(dragEvent.getY)
							v.setWidth(300)
							v.setHeight(200)

							//val skin: VNodeSkin[_ <: VNode] = skinFactory.createSkin(v, flow)
              //skin.add

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


