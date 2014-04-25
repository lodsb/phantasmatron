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