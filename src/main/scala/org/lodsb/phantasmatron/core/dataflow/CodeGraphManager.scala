/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: CodeGraphManager.scala
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

package org.lodsb.phantasmatron.core.dataflow

import scalafx.collections.ObservableBuffer
import org.lodsb.reakt.TVar

/**
 * Created by lodsb on 4/25/14.
 */
class CodeGraphManager(model: DataflowModel) {
  model.connections.onChange { (source, changes) =>
    changes.foreach( change => {
      change match {
        case ObservableBuffer.Add(pos, added) => {
          added.foreach({ c =>
            println("add code connection " +  c)

            val conn = c.asInstanceOf[ConnectionModel]

            conn.source match {
              case CodeConnectorModel(srcSignal,_) => {
                conn.destination match {
                  case CodeConnectorModel(destSignal,_) => {
                    (destSignal.asInstanceOf[TVar[Any]]) <~ srcSignal.asInstanceOf[TVar[Any]]
                  }
                  case _ =>
                }
              }
              case _ =>
            }

          });
        }

        case ObservableBuffer.Remove(pos, removed) => {
          removed.foreach({ c =>
            println("remove code connection"+c)

            val conn = c.asInstanceOf[ConnectionModel]

            conn.source match {
              case CodeConnectorModel(srcSignal,_) => {
                conn.destination match {
                  case CodeConnectorModel(destSignal,_) => {
                    (destSignal.asInstanceOf[TVar[Any]]).disconnect(srcSignal.asInstanceOf[TVar[Any]])
                  }
                  case _ =>
                }
              }
              case _ =>
            }


          });
        }

        case _ =>
      }
    })
  }

}
