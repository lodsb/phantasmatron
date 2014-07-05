/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: Nodes.scala
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


import javafx.application.Platform
import org.lodsb.reakt.TVar
import org.lodsb.phantasmatron.core.messaging.{Message, MessageBus}
import org.lodsb.phantasmatron.core.code._
import org.lodsb.phantasmatron.core.code.CompileError
import org.lodsb.phantasmatron.core.asset.CodeAssetDescriptor
import scala.Some
import org.lodsb.phantasmatron.core.code.macros
import org.lodsb.phantasmatron.core.code.macros.MacroType

/**
 * Created by lodsb on 4/20/14.
 */

case class CompilationStarted(codeNodeModel: Compileable) extends Message
case class CompilationFinished(codeNodeModel: Compileable, compileResult: CompileResult) extends Message

case class CodeNodeModel(override var code: Code) extends NodeModel with Compileable {

  def compile : Unit = {

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

              if(codeObject.isDefined) {
                codeObject.get.destroy()
              }

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

case class CodeMacroNodeModel(override var code: Code, macroType: MacroType) extends NodeModel with Compileable {

  def compile : Unit = {

    new Thread( new Runnable{
      def run {
        Platform.runLater(new Runnable {
          def run {
            MessageBus.send(CompilationStarted(CodeMacroNodeModel.this))
          }
        })

        val compileResult = code.compile

        compileResult match {
          case x: CompileError => println("Error: " + x.message)
          case x: CompileSuccess => {
            Platform.runLater(new Runnable {

              def run {

                connectors.clear()

                if(codeObject.isDefined) {
                  codeObject.get.destroy()
                }

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
            MessageBus.send(CompilationFinished(CodeMacroNodeModel.this, compileResult))
          }
        })
      }

    }).start()

  }


}




