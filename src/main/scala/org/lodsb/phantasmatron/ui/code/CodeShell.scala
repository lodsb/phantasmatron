package org.lodsb.phantasmatron.ui.code

import scalafx.scene.layout.VBox
import scalafx.scene.control.{TextField, TextArea}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.Includes._
import javafx.application.Platform
import org.lodsb.phantasmatron.core.code.{ScriptResult, ScriptEngine, ScriptSuccess, ScriptError}

/**
 * Created by lodsb on 4/10/14.
 */
class CodeShell extends VBox {
  private val shellOutput = new TextArea
  private val shellInputField = new TextField

  private var commandHistory = List[String]()
  private var commandHistoryIndex = 0;

  shellInputField.onKeyReleased = {e: KeyEvent =>
      e.getCode match {
        case KeyCode.ENTER => {
          val code = shellInputField.text.value
          shellOutput.appendText("> "+code+"\n")

          commandHistory = commandHistory :+ code
          commandHistoryIndex = commandHistory.size-1

          new Thread(new Runnable{
            def run(): Unit = {

              val scriptResult = compile(code)

              Platform.runLater(new Runnable {
                def run {

                  scriptResult match {
                    case ScriptSuccess(value, message ) => shellOutput.appendText(message)
                    case ScriptError(message ) => shellOutput.appendText(message)
                  }

                  shellInputField.text = ""
                  shellOutput.setScrollTop(Double.MaxValue);

                }
              })

            }
          }).start()
        }

        case KeyCode.UP => {
          commandHistoryIndex = scala.math.max( commandHistoryIndex - 1 , 0 ) % commandHistory.size

          shellInputField.text = commandHistory(commandHistoryIndex)
        }

        case KeyCode.DOWN => {
          commandHistoryIndex = ( commandHistoryIndex + 1 ) % commandHistory.size

          shellInputField.text = commandHistory(commandHistoryIndex)
        }

        case _ =>
      }
  }

  private def compile(codeString: String) : ScriptResult = {
    ScriptEngine.interpret(codeString)
  }


  children.add( shellOutput )
  children.add( shellInputField )

}
