package org.lodsb.phantasmatron.core.dataflow

import org.lodsb.phantasmatron.core._
import org.lodsb.phantasmatron.core.AssetDescriptor
import javafx.application.Platform
import org.lodsb.reakt.TVar
import org.lodsb.phantasmatron.core.messaging.{Message, MessageBus}

/**
 * Created by lodsb on 4/20/14.
 */

case class CompilationStarted(codeNodeModel: CodeNodeModel) extends Message
case class CompilationFinished(codeNodeModel: CodeNodeModel, compileResult: CompileResult) extends Message

case class CodeNodeModel(private val code: Code) extends NodeModel {

  def setCodeString(codeString: String) = {code.code = codeString}
  def getCodeString : String = code.code
  def getDescriptor = code.descriptor
  def setDescriptor(desc:AssetDescriptor ) = {code.descriptor = desc}
  def getCode = code

  private var codeObject : Option[CodeObject] = None
  def getCodeObject : Option[CodeObject] = {
    codeObject
  }

  def compile : Unit = {
    import scala.concurrent._
    import ExecutionContext.Implicits.global

    new Thread( new Runnable{
      def run {
        Platform.runLater(new Runnable {
          def run {
            MessageBus.send(CompilationStarted(CodeNodeModel.this))
          }
        })

      val compileResult = code.compile

      compileResult match {
        case x: CompileError => println("Error: " + x.message)
        case x: CompileSuccess => {
          Platform.runLater(new Runnable {

            def run {

              connectors.clear()
              codeObject = Some(x.value)

              codeObject.get.inputs.foreach({ input =>
                connectors.add(CodeConnectorModel(input.asInstanceOf[TaggedSignal[AnyRef, TVar[AnyRef]]], true))
              })

              codeObject.get.outputs.foreach({ output =>
                connectors.add(CodeConnectorModel(output.asInstanceOf[TaggedSignal[AnyRef, TVar[AnyRef]]], false))
              })
            }

          });
        }
      }
        Platform.runLater(new Runnable {
          def run {
            MessageBus.send(CompilationFinished(CodeNodeModel.this, compileResult))
          }
        })
      }

    }).start()

  }


}
