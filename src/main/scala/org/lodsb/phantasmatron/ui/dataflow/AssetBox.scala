/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: AssetBox.scala
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

package org.lodsb.phantasmatron.ui.dataflow

import jfxtras.labs.scene.control.window.{CloseIcon, Window}
import scalafx.scene.layout.HBox
import org.controlsfx.control.textfield.TextFields
import org.lodsb.phantasmatron.core.asset.CodeAssetManager
import scala.collection.JavaConversions._
import scalafx.scene.input.{MouseEvent, KeyEvent, KeyCode}
import javafx.event.EventHandler
import scalafx.Includes._
import org.lodsb.phantasmatron.ui.Util
import scalafx.scene.paint.Color

/**
 * Created by lodsb on 4/26/14.
 */
class AssetBox(dataflowViz: DataFlowVisualization) extends Window {
  setWindowColor(Color.RED)


  val assetMap = CodeAssetManager.knownObjects.map( x=> (x.name, x)).toMap
  val assetList = assetMap.toList.map(x => x._1)


  val inputBox = TextFields.createSearchField()

  inputBox.onMouseClicked = { x:MouseEvent =>
    val text = inputBox.getText
    val asset = assetMap.get(text)

    if(asset.isEmpty) {
      setWindowColor(Color.RED)
    } else {
      setWindowColor(Color.GREEN)
    }

  }

  this.onKeyPressed = { x:KeyEvent =>
    x.code match {
      case KeyCode.ESCAPE => {
        close()
      }
      case _ =>  inputBox.requestFocus()
    }
  }

  inputBox.onKeyPressed = { x:KeyEvent =>

    val text = inputBox.getText
    val asset = assetMap.get(text)

    println("foo" + " | "+text+ "+ " + asset)

    if(asset.isEmpty) {
      setWindowColor(Color.RED)
    } else {
      setWindowColor(Color.GREEN)
    }

    x.code match {
      case KeyCode.ESCAPE => {
        close()
      }
      case KeyCode.ENTER => {
        x.consume()

        if(asset.isDefined) {
          dataflowViz.loadAsset(asset.get, getLayoutX, getLayoutY)
          close()
        }

      }
      case _ =>
    }

    //x.consume()
  }

  TextFields.bindAutoCompletion[String](inputBox, assetList )

  val box = new HBox{
    children.add(inputBox)
  }

  this.setContentPane(box)

  this.setTitle("Insert new Node")

  getLeftIcons.add(new CloseIcon(this))

  private def setWindowColor(c: Color) = {
    this.style = "-fx-background-color: " + Util.toRgba(c, 0.9)
  }

  this.layout()

  this.setResizableWindow(false)

}
