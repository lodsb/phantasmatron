package org.lodsb.phantasmatron.core.dataflow

import org.lodsb.phantasmatron.core.TaggedSignal
import org.lodsb.reakt.TVar

/**
 * Created by lodsb on 4/20/14.
 */


case class CodeConnectorModel[X](signal: TaggedSignal[X,_ <: TVar[X]], override val isInput: Boolean) extends ConnectorModel {
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