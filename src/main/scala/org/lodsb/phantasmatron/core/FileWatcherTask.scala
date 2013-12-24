package org.lodsb.phantasmatron.core

import java.nio.file._
import java.io.File
import scalafx.concurrent.Task
import javax.management.remote.rmi._RMIConnection_Stub


/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-24 :: 05:15
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
class FileWatcherTask(val directory: String) extends Task(new javafx.concurrent.Task[Path]() {
	val watch = FileSystems.getDefault().newWatchService();
	val dir = new File(directory).toPath;

	try{
		val key = dir.register(watch, 	StandardWatchEventKinds.ENTRY_CREATE,
										StandardWatchEventKinds.ENTRY_DELETE,
										StandardWatchEventKinds.ENTRY_MODIFY);

	} catch {
		case t: Throwable => t.printStackTrace()
	}

	def call(): Path = {
		while(true){

			try{
				var key : WatchKey = watch.take()
				val events = key.pollEvents().iterator()

				while(events.hasNext) {
					val event = events.next()

					if(event.kind() != StandardWatchEventKinds.OVERFLOW) {
						val file = event.context().asInstanceOf[Path]
						val child= dir.resolve(file)

						// do update here
					}
				}

			} catch {
				case t: Throwable => t.printStackTrace()
			}


		}
	}

})
