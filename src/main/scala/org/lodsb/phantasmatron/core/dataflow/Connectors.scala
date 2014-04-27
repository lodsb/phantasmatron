/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: Connectors.scala
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

import org.lodsb.reakt.TVar
import org.lodsb.phantasmatron.core.code.TaggedSignal

/**
 * Created by lodsb on 4/20/14.
 */


case class CodeConnectorModel[X](signal: TaggedSignal[X,_ <: TVar[X]], override val isInput: Boolean) extends ConnectorModel {

  name() = signal.name

  def isCompatible(that: ConnectorModel): Boolean = {
    that match {
      case CodeConnectorModel(thatSignal,thatIsInput) => {
        val diffObj = this != that
        val diffTypes= this.signal.typeTag.tpe.<:<(thatSignal.typeTag.tpe.typeConstructor)
        val inOut =  (this.isInput && !thatIsInput) || (this.isOutput && thatIsInput)

        diffObj && diffTypes && inOut
      }

      case _ => false
    }
  }
}