package org.lodsb.phantasmatron.core.dataflow

import org.lodsb.phantasmatron.core._
import org.lodsb.phantasmatron.core.AssetDescriptor
import javafx.application.Platform
import org.lodsb.reakt.TVar

/**
 * Created by lodsb on 4/20/14.
 */

case class CodeNodeModel(private val code: Code) extends NodeModel {
  def setCodeString(codeString: String) = {code.code = codeString}
  def getCodeString : String = code.code
  def getDescriptor = code.descriptor
  def setDescriptor(desc:AssetDescriptor ) = {code.descriptor = desc}
  def getCode = code

  def compile : CompileResult = {
    val compileResult = code.compile

    compileResult match {
      case x: CompileError => println("Error: " + x.message)
      case x: CompileSuccess => {
        Platform.runLater(new Runnable {

          def run {

            connectors.clear()
            val codeObject = x.value

            codeObject.inputs.foreach({ input =>
              connectors.add(CodeConnectorModel(input.asInstanceOf[TaggedSignal[AnyRef, TVar[AnyRef]]], true))
            })

            codeObject.outputs.foreach({ output =>
              connectors.add(CodeConnectorModel(output.asInstanceOf[TaggedSignal[AnyRef, TVar[AnyRef]]], false))
            })
          }

        });
      }
    }

    compileResult
  }

}
