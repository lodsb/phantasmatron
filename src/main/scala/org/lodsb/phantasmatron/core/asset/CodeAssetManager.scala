/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: CodeAssetManager.scala
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

package org.lodsb.phantasmatron.core.asset

import javafx.collections.ListChangeListener
import java.nio.file.Path
import javafx.collections.ListChangeListener.Change
import scalafx.collections.ObservableBuffer
import scala.pickling._
import json._
import scala.util.{Failure, Try}
import java.io.File
import org.lodsb.phantasmatron.core.io.FileWatcherTask
import org.lodsb.phantasmatron.core.Config
import org.lodsb.phantasmatron.core.code.Code

object CodeAssetManager extends AssetManager[Code]("CodeAssetManager") {

	private val fileWatcher = new FileWatcherTask(Config().codeLibrary)

	//val knownObjects = ObservableBuffer[CodeAssetDescriptor]()

	// TODO:  currently rather primitive
	fileWatcher.fileList.addListener(new ListChangeListener[Path] {
		def onChanged(p1: Change[_ <: Path]): Unit = {
			println("codeasset "+p1)
			rebuildFileList()
		}
	})

	private def rebuildFileList() = {
		knownObjects.clear()

		fileWatcher.fileList.foreach({x =>
			val file = x.toFile
			val fileExtension = file.getName.split('.').drop(1).lastOption

			println("--- L ---"+x)

			if(fileExtension.isDefined && fileExtension.get.equals("json")) {
				val jsonString = scala.io.Source.fromFile(x.toFile).mkString

				val desc = jsonString.unpickle[CodeAssetDescriptor]

				knownObjects.add(desc)
			}

		})
	}

  def instantiate(assetDescriptor: AssetDescriptor[Code]) : Try[Code] = {
    assetDescriptor match {
      case x:CodeAssetDescriptor => {
        load(x)
      }
      case _ =>  Failure(new Exception("Unsupported Asset"))
    }
  }

	def load(desc: AssetDescriptor[Code], path: String = Config().codeLibrary) : Try[Code] = {
		  def createCode = {
				val location = path+desc.location.get
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

	def save(code: Code, path: String = Config().codeLibrary) : Try[Unit] = {
	    val desc = code.descriptor
		val codeString = code.code

		def s = {
			if(desc.name == "") throw new Exception("Missing CodeNode-name")
			if(desc.location.get == "") throw new Exception("Missing file name")
			if(desc.tags == List.empty) throw new Exception("Missing tags")

			val loc = new File(path+desc.location.get)
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

}
