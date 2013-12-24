package org.lodsb.phantasmatron.core

import javafx.beans.value._
import javafx.collections.ListChangeListener
import java.nio.file.Path
import javafx.collections.ListChangeListener.Change
import scalafx.collections.ObservableBuffer
import org.lodsb.phantasmatron.ui.ObjectPalette.ObjectDescriptor
import scala.pickling._
import json._

/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-24 :: 15:21
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
object ObjectWatcher {
	private val fileWatcher = new FileWatcherTask(Config().codeLibrary)

	val knownObjects = ObservableBuffer[ObjectDescriptor]()

	// TODO:  currently rather primitive
	fileWatcher.fileList.addListener(new ListChangeListener[Path] {
		def onChanged(p1: Change[_ <: Path]): Unit = {
			rebuildFileList()
		}
	})

	private def rebuildFileList() = {
		knownObjects.clear()

		fileWatcher.fileList.foreach({x =>
			val file = x.toFile
			val fileExtension = file.getName.split('.').drop(1).lastOption

			if(fileExtension.isDefined && fileExtension.get.equals("json")) {
				val jsonString = scala.io.Source.fromFile(x.toFile).mkString

				val desc = jsonString.unpickle[ObjectDescriptor]

				knownObjects.add(desc)
			}

		})
	}



	println("starting object watcher")
	new Thread(fileWatcher).start()


	/*fileWatcher.messageProperty.addListener(new ChangeListener[String]() {
		override def changed(o: javafx.beans.value.ObservableValue[String], p1: String, p2: String) : Unit = {
			println("change : "+o)
		}
	})*/

	def foo = {}




}
