package org.lodsb.phantasmatron.core

import javafx.beans.value._
import javafx.collections.ListChangeListener
import java.nio.file.Path
import javafx.collections.ListChangeListener.Change
import scalafx.collections.ObservableBuffer
import org.lodsb.phantasmatron.ui.ObjectPalette.{CreateNewCodeNode, ObjectDescriptor}
import scala.pickling._
import json._
import scala.util.Try
import java.io.File

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
object CodeObjectManager {
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

	def load(desc: ObjectDescriptor) : Try[Code] = {
		  def createCode = {
				val location = Config().codeLibrary+desc.location.get
				val codeString = scala.io.Source.fromFile(location).mkString
				new Code(codeString, desc)
			}

		  if(desc == CreateNewCodeNode) {
			  Try(new Code("", desc))
		  } else {
			  Try(createCode)
		  }
	}

	private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
	  val p = new java.io.PrintWriter(f)
	  try { op(p) } finally { p.close() }
	}

	def save(code: Code) : Try[Unit] = {
	    val desc = code.descriptor
		val codeString = code.code

		println("CODE "+codeString)
		println("desc "+desc)

		def s = {
			if(desc.name == "") throw new Exception("Missing CodeNode-name")
			if(desc.location.get == "") throw new Exception("Missing file name")
			if(desc.tags == List.empty) throw new Exception("Missing tags")

			val loc = new File(Config().codeLibrary+desc.location.get)
			if(loc.exists()) throw new Exception("File exists")

			val jsonLoc = new File(Config().codeLibrary+desc.location.get+".json")
			val jsonDesc = desc.pickle.value
			printToFile(loc)(p => p.println(code.code))
			printToFile(jsonLoc)(p => p.println(jsonDesc))
		}

		fileWatcher.rescan

		Try(s)
	}



	println("starting object watcher")
	new Thread(fileWatcher).start()


	/*fileWatcher.messageProperty.addListener(new ChangeListener[String]() {
		override def changed(o: javafx.beans.value.ObservableValue[String], p1: String, p2: String) : Unit = {
			println("change : "+o)
		}
	})*/

	//def foo = {}




}
