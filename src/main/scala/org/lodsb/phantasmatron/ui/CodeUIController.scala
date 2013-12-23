package org.lodsb.phantasmatron.ui

import jfxtras.labs.scene.control.window.Window
import scalafx.scene.control._
import scalafx.scene.layout._
import eu.mihosoft.vrl.workflow._
import de.sciss.scalainterpreter.CodePane
import javax.swing.{JEditorPane, SwingUtilities, JPanel, JComponent}
import java.awt.{Dimension, FlowLayout}
import scalafx.geometry.{Pos, Insets}
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.ContextMenuEvent
import org.controlsfx.control.PopOver
import scalafx.scene.text.Text
import scalafx.concurrent.Task
import scala.Some
import scalafx.scene.Node
import javafx.application.Platform
import scalafx.Includes._
import scala.Some
import javax.swing.event.{ChangeEvent, ChangeListener}
import org.lodsb.phantasmatron.core._
import scala.Some
import org.lodsb.phantasmatron.core.CompileSuccess
import org.lodsb.phantasmatron.core.CompileError
import org.lodsb.reakt.{TVar, TSignal}
import scalafx.event.Event
import scalafx.scene.paint.Color
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane


/**
 * Created by lodsb on 12/20/13.
 */

class CodeUIController(private val code: Code, private val window: Window, private val model: VNode) {
  outer =>


  println("UI CONTROLLER")

  // state monad?
  private var compileResult: Option[CompileResult] = None
  private var editor: TextArea = null
  //private var propertiesPane: Option[TitledPane] = None
  private var controlsPane: Option[TitledPane] = None

  this.setWindowUI(code, window)

  private def setWindowUI(code: Code, window: Window) = {
    println(code + " --- "+window)

    val scalablePane = new ScalableContentPane
    val flowPane = new FlowPane

    val view = createView(code)

    flowPane.children.add(view)

    scalablePane.setContentPane(flowPane)
    
    window.setContentPane(scalablePane)

    println("ok?")
  }

  val width = 400

  private def createCodePane: TitledPane = {
    val csp = createEditorPane
    val ccp = createCodePaneControls

    val codePane = new TitledPane {
      text = "Code"

      val grid = new GridPane {
        alignment = Pos.CENTER

        add(ccp, 0, 0)
        add(csp, 0, 1)
      }

      grid.getRowConstraints.add(new RowConstraints(50))

      content = grid

    }

    //ccp.minWidth <== codePane.width

    codePane

  }

  private def createControlPane: TitledPane = {
    new TitledPane {
      text = "Control"
    }
  }

  private def toRgba(c: Color, a: Double): String = {
    "rgba("+(to255Int(c.red))+","+(to255Int(c.green))+","+(to255Int(c.blue))+","+a.toString+")"
  }

  private def to255Int(d: Double): Int =  {
    (d * 255).toInt
  }

  private def gradient(c: Color) : String = {
    " -fx-background-color : radial-gradient(center 50% 25%,\n" +
      "        radius 75%,\n" +
      "        "+toRgba(c,0.8)+" 0%,\n" +
      "        rgba(82,82,82,0.9) 100%);"
  }


  private def createPropertiesPane : TitledPane = {
    val colorPicker = new ColorPicker{
      margin = Insets(0,50,0,10)
    }
    colorPicker.onAction = {event:Event => window.style = "-fx-background-color: "+toRgba(colorPicker.getValue,0.9)}

    val textField = new TextField {
      margin = Insets(0,0,0,10)
    }
    textField.onAction = {event: Event => window.setTitle(textField.getText)}

    val reg = new Region
    HBox.setHgrow(reg, Priority.ALWAYS)

    new TitledPane {
      text = "Properties"
      val vbox = new VBox
      val grid = new GridPane {
        add(new Text("color: "),0,0)
        add(colorPicker, 1,0)
        add(reg,2,0)
        add(new Text("name: "),3,0)
        add(textField, 4, 0)

        alignment = Pos.CENTER
      }
      vbox.children.add(grid)
      val sep = new Separator
      sep.margin = Insets(10,0,0,10)
      vbox.children.add(sep)
      content = vbox
    }
  }

  private def createView(code: Code): Pane = {
    val control = createControlPane
    val code = createCodePane
    val prop = createPropertiesPane

    this.controlsPane = Some(control)


    val accordion = new VBox {
      minWidth = outer.width

      //TODO: properties, node name, node color
      //panes.addAll(control, code)
      children.add(control)
      children.add(code)
      children.add(prop)
      //add(new Text("sdfsdf"),0,1)
    }

    val nodePane = new GridPane {
      minWidth = outer.width
    }
    nodePane.children.add(accordion)

    HBox.setHgrow(accordion, Priority.ALWAYS)
    HBox.setHgrow(nodePane, Priority.ALWAYS)
    HBox.setHgrow(control, Priority.ALWAYS)
    HBox.setHgrow(code, Priority.ALWAYS)

    //HBox.setHgrow(control, Priority.ALWAYS)

    nodePane
  }

  private def compileAction(ed: TextArea, pi: ProgressIndicator, ae: ActionEvent): Unit = {
    val compileString = ed.getText

    code.code = compileString

    pi.setStyle(" -fx-accent: orange;");
    new Thread(new CompileTask(pi)).start()
  }

  private class CompileTask(pi: ProgressIndicator) extends Task(new javafx.concurrent.Task[Unit]() {

    //pi.progressProperty.unbind()
    pi.progressProperty().bind(this.progressProperty())

    def call(): Unit = {
      try {
        println("TASK")
        updateProgress(-1, 10)


        outer.compileResult = Some(code.compile)
        updateProgress(10, 10)

        /*
        outer.compileResult.get match {
          case x: CompileSuccess => pi.setStyle(" -fx-progress-color: green;");
          case _ => pi.setStyle(" -fx-progress-color: red;");
        } */


        println("before update")
        Platform.runLater(new Runnable {
          def run {

            compileResult.get match {
              case x: CompileSuccess => pi.setStyle(" -fx-accent: green;");
              case _ => pi.setStyle(" -fx-accent: red;");
            }

            println("running update")
            updateModel
          }
        })
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }

    }
  })

  private def connect(src: Connector, dst: Connector) = {
    ConnectionManager.connect(src, dst)
  }

  private def disconnect(src: Connector, dst: Connector) = {
    ConnectionManager.disconnect(src, dst)
  }

  private def updateModel = {

    println("UPDATE MODEL")

    try {

      if (compileResult.isDefined) {
        compileResult.get match {
          case x: CompileError => println("Error: " + x.message)
          case x: CompileSuccess => {

            val codeNode = x.value
            val vnode = this.model

            vnode.getConnectors.clear()
            ConnectionManager.diconnectAll(vnode)

            codeNode.inputs.foreach {
              x =>
                val conn = vnode.addInput(x.typeString)
                val cdesc = ConnectorDescriptor(vnode, conn, x.asInstanceOf[TaggedSignal[AnyRef, TVar[AnyRef]]])
                ConnectionManager.addConnectorDescr(cdesc)

                /*conn.addClickEventListener(new EventHandler[ClickEvent] {
                  def handle(p1: ClickEvent): Unit = {
                    if(p1.getButton == MouseButton.SECONDARY) {
                      println("foo")
                    }
                  }
                })*/
            }

            codeNode.outputs.foreach {
              x =>
                val conn = vnode.addOutput(x.typeString)
                val cdesc = ConnectorDescriptor(vnode, conn, x.asInstanceOf[TaggedSignal[AnyRef, TVar[AnyRef]]])
                ConnectionManager.addConnectorDescr(cdesc)

                /*conn.addClickEventListener(new EventHandler[ClickEvent] {
                  def handle(p1: ClickEvent): Unit = {
                    if(p1.getButton == MouseButton.SECONDARY) {
                      val t = new Tooltip
                      t.setText(("Type: "+x.typeString))
                      t.autoHide = true
                    }
                  }
                })*/
            }


            val connIterator = model.getConnectors.iterator()

            while (connIterator.hasNext) {
              val connector = connIterator.next()
              connector.addConnectionEventListener(new EventHandler[ConnectionEvent] {
                def handle(p1: ConnectionEvent): Unit = {
                  p1.getEventType match {
                    case ConnectionEvent.ADD => connect(p1.getSenderConnector, p1.getReceiverConnector)
                    case ConnectionEvent.REMOVE => disconnect(p1.getSenderConnector, p1.getReceiverConnector)
                  }
                }
              })


            }

            val codeNodeControls = codeNode.controlPanel

            if (this.controlsPane.isDefined) {
              val nodeControlsPane = this.controlsPane.get

              nodeControlsPane.setContent(codeNodeControls)
            }

          }
        }
      }
    } catch {
      case e: Throwable => e.printStackTrace
    }

  }

  private def popUpAction(popOver: PopOver, pi: ProgressIndicator, ctx: ContextMenuEvent): Unit = {
    if (compileResult.isDefined) {

      val message = compileResult.get match {
        case x: CompileSuccess => x.message
        case x: CompileError => x.message
      }

      popOver.setContentNode(new Text(message))
      popOver.show(pi, ctx.getScreenX, ctx.getScreenY)
    }
  }


  //TODO: progress indicator should move into the window's title (so it can be seen when minimized)
  private def createCodePaneControls: HBox = {
    val reg = new Region()
    val reg2 = new Region()
    reg2.setPrefWidth(10)
    HBox.setHgrow(reg, Priority.ALWAYS)
    HBox.setHgrow(reg2, Priority.NEVER)

    val progressIndicator = new ProgressBar() {
      style = " -fx-accent: green;"
      /*minHeight = 40
      minWidth = 40 */
      maxHeight = 25
      maxWidth = 50
    }

    progressIndicator.setProgress(1)

    val popOver = new PopOver()

    progressIndicator onContextMenuRequested = new EventHandler[ContextMenuEvent] {
      def handle(p1: ContextMenuEvent): Unit = popUpAction(popOver, progressIndicator, p1)
    }

    val compileButton = new Button("Compile")
    compileButton.style = "-fx-background-color:\n        linear-gradient(#f0ff35, #a9ff00),\n        radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%);\n    -fx-background-radius: 6, 5;\n    -fx-background-insets: 0, 1;\n    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );\n    -fx-text-fill: #395306;"

    val ta = this.editor

    compileButton onAction = new EventHandler[ActionEvent] {
      def handle(p1: ActionEvent): Unit = compileAction(ta, progressIndicator, p1)
    }

    val hbox = new HBox {
      padding = Insets(20)
      alignment = Pos.CENTER_RIGHT

      children.addAll(reg, compileButton, reg2, progressIndicator)
    }

    VBox.setVgrow(hbox, Priority.NEVER)
    HBox.setHgrow(hbox, Priority.ALWAYS)

    hbox
  }


  // test for now

  private def createEditorPane: Node = {
    /*val swingNode = new SwingNode
    val cp: CodePane = Test.codePane

    editor = Some(cp.editor)

    val comp: JComponent = cp.component
    val panel: JPanel = new JPanel(new FlowLayout)

    System.out.println("my TEXT: " + cp.editor.getText)

    panel.add(cp.component)
    comp.setPreferredSize(new Dimension(450, 450))
    SwingUtilities.invokeLater(new Runnable {
      def run {
        swingNode.setContent(panel)
      }
    })*/

    this.editor = new TextArea()
    this.editor.setText(code.code)
    this.editor
  }
}
