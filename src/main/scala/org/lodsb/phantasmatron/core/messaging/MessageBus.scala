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
