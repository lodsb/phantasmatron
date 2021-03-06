/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: FileWatcherTask.scala
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

package org.lodsb.phantasmatron.core.io

import java.nio.file._
import java.io.File
import java.nio.file.attribute.BasicFileAttributes
import scalafx.collections.ObservableBuffer


class FileWatcherTask(val directory: String) extends javafx.concurrent.Task[Unit]() {
	val dir = new File(directory).toPath;

	val fileList = new ObservableBuffer[Path]()

  //TODO: fixme - use message bus? !!

	def rescan = {
		fileList.clear()

		Files.walkFileTree(dir, new SimpleFileVisitor[Path]{
			    override def visitFile(file: Path, attr: BasicFileAttributes): FileVisitResult = {
					val msg = file.toAbsolutePath + ";ENTRY_CREATE"

					updateMessage(msg)

					fileList.add(file)

					FileVisitResult.CONTINUE
				}

		})
	}

	def call(): Unit = {

		val watch = FileSystems.getDefault().newWatchService();
		try{
			val key = dir.register(watch, 	StandardWatchEventKinds.ENTRY_CREATE,
											StandardWatchEventKinds.ENTRY_DELETE,
											StandardWatchEventKinds.ENTRY_MODIFY);

		} catch {
			case t: Throwable => t.printStackTrace()
		}

		this.rescan

		while(true){

			try{
				var key : WatchKey = watch.take()

				val events = key.pollEvents().iterator()

				while(events.hasNext) {
					val event = events.next()

					if(event.kind() != StandardWatchEventKinds.OVERFLOW) {
						val file = event.context().asInstanceOf[Path]
						val child= dir.resolve(file)

						event.kind() match {
							case StandardWatchEventKinds.ENTRY_CREATE => fileList.add(file)
							case StandardWatchEventKinds.ENTRY_DELETE => {println("bogus delete, fixme! (path ref object)"); fileList.remove(file)}
							case _ =>
						}

						val msg = file.toAbsolutePath + ";" + event.kind()
						println(msg)
						updateMessage(msg)

						// do update here
					}
				}

			} catch {
				case t: Throwable => t.printStackTrace()
			}


		}
	}

}
