/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: MessageBus.scala
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

package org.lodsb.phantasmatron.core.messaging

/**
 * Created by lodsb on 4/25/14.
 */
object MessageBus {
  type MessageHandler = Message => Unit

  private var handlerList : List[MessageHandler] = List.empty
  private val handlerListMutex = new Object

  def registerHandler(mh: MessageHandler) = {
    handlerListMutex.synchronized {
      handlerList = handlerList :+ mh
    }
  }

  def unregisterHandler(mh: MessageHandler) = {
    handlerListMutex.synchronized {
      handlerList = handlerList diff List(mh)
    }
  }

  def send(msg: Message) = {
    handlerListMutex.synchronized {

      handlerList.foreach({ mh =>
        mh(msg)
      })
    }
  }



}

abstract class Message
