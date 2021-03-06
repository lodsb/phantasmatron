/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: CodeUIController.scala
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

package org.lodsb.phantasmatron.ui.code


import scalafx.scene.control._
import scalafx.scene.layout._
import org.controlsfx.dialog.Dialogs
import scala.util.Failure
import org.lodsb.phantasmatron.core.dataflow.{CompilationStarted, CompilationFinished, CodeNodeModel}
import org.lodsb.phantasmatron.ui.dataflow.RemoveAllNodeConnections
import org.lodsb.phantasmatron.core.messaging.{Message, MessageBus}
import org.lodsb.phantasmatron.core.asset.{CodeAssetDescriptor, CodeAssetManager}
import org.lodsb.phantasmatron.core.code.{CompileResult, CompileSuccess, CompileError}
import jfxtras.labs.scene.layout.ScalableContentPane
import jfxtras.labs.scene.control.window.Window
import jfxtras.labs.internal.scene.control.skin.window.DefaultWindowSkin
import javafx.geometry.Bounds
import javafx.beans.value.{ChangeListener, ObservableValue}

//import javafx.scene.layout.GridPane

import scalafx.geometry.{Pos, Insets}
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.ContextMenuEvent
import org.controlsfx.control.PopOver
import scalafx.scene.text.Text
import scalafx.concurrent.Task
import scalafx.scene.Node
import javafx.application.Platform
import scalafx.Includes._
import scala.Some
import scalafx.event.Event
import scalafx.scene.paint.Color


/**
 * Created by lodsb on 12/20/13.
 */

class CodeUIController(private val code: CodeNodeModel, private val window: Window) {
	outer =>

	println("UI CONTROLLER")

	// state monad?
	private var compileResult: Option[CompileResult] = None
	private var editor: TextArea = null
	//private var propertiesPane: Option[TitledPane] = None
	private var controlsPane: Option[TitledPane] = None

	private var compile = {() => println("NOT DEFINED?!?!?!?")}

  var progressIndicator: ProgressBar =new ProgressBar() {
    style = " -fx-accent: green;"
    /*minHeight = 40
      minWidth = 40 */
    maxHeight = 25
    maxWidth = 50
  }

//	CodeUIControllerManager.register(this)
	this.setWindowUI(code, window)


/*	// crude workaround :-/
	def uiCompile = {
		if(model.getFlow.getNodes.toList.contains(model))  {
			// if this node is still part of the flow
				this.compile()
		}
	}

  */

	private def setWindowUI(code: CodeNodeModel, window: Window) = {

		val scalablePane = new ScalableContentPane
		val flowPane = new FlowPane

		val view = createView(code)

    //scalablePane.prefWidth <== window.widthProperty()
    //scalablePane.prefHeight <== window.heightProperty()

		flowPane.children.add(view)

	 scalablePane.setContentPane(flowPane)


    flowPane.setMaxSize(500,500)

    flowPane.autosize()
		window.setContentPane(scalablePane)

    //window.setMinSize(300,300)

    //window.setResizableWindow(false)

    //window.resize(400,400)

    window.boundsInParent.addListener(new ChangeListener[Bounds] {
      def changed(ov: ObservableValue[_ <: Bounds], t: Bounds, t1: Bounds) {
        val h = ov.getValue.getHeight
        val w = ov.getValue.getWidth

        println(w+ " "+h)

        editor.prefColumnCount = 10+(w/10).toInt
        editor.prefRowCount = 5+(h/40).toInt
      }
    })

    window.requestLayout()
    scalablePane.setPrefSize(350,350)

    scalablePane.setAspectScale(true)
    scalablePane.setAutoRescale(true)
    window.setPrefSize(400,400)

    //window.setLayoutX(window.getLayoutX)
    //window.setLayoutY(window.getLayoutY)

    //scalablePane.resize(400,400)
    scalablePane.requestLayout()

    println(window.getPrefHeight + " " + window.getPrefWidth)
    println(scalablePane.getPrefHeight + " " + scalablePane.getPrefWidth)
    println(flowPane.getPrefHeight + " " + flowPane.getPrefWidth)

    flowPane.layout()
    scalablePane.layout()

    window.layout()
    window.setTitle(code.getDescriptor.name)

    scalablePane.requestScale()

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

			//grid.getRowConstraints.add(new RowConstraints(50))

			content = grid

		}

    VBox.setVgrow(codePane, Priority.ALWAYS)

		//ccp.minWidth <== codePane.width

		codePane

	}

	private def createControlPane: TitledPane = {
		new TitledPane {
			text = "Control"
		}
	}

	private def toRgba(c: Color, a: Double): String = {
		"rgba(" + (to255Int(c.red)) + "," + (to255Int(c.green)) + "," + (to255Int(c.blue)) + "," + a.toString + ")"
	}

	private def to255Int(d: Double): Int = {
		(d * 255).toInt
	}

	private def gradient(c: Color): String = {
		" -fx-background-color : radial-gradient(center 50% 25%,\n" +
			"        radius 75%,\n" +
			"        " + toRgba(c, 0.8) + " 0%,\n" +
			"        rgba(82,82,82,0.9) 100%);"
	}


	private def createPropertiesPane: TitledPane = {
		val colorPicker = new ColorPicker {
			margin = Insets(0, 50, 0, 10)
		}
		colorPicker.onAction = {
			event: Event => window.style = "-fx-background-color: " + toRgba(colorPicker.getValue, 0.9)
		}

		val nodeNameField = new TextField {
			margin = Insets(0, 0, 0, 10)
			text = code.getDescriptor.name
		}


		nodeNameField.onAction = {
			event: Event => window.setTitle(nodeNameField.getText)
		}

		val reg = new Region
		HBox.setHgrow(reg, Priority.ALWAYS)

		new TitledPane {
			text = "Properties"
			val vbox = new scalafx.scene.layout.VBox
			val grid1 = new GridPane {
				add(new Text("color: "), 0, 0)
				add(colorPicker, 1, 0)
				add(reg, 2, 0)
				add(new Text("node name: "), 3, 0)
				add(nodeNameField, 4, 0)

				alignment = Pos.CENTER
			}

			vbox.children.add(grid1);

			//val sep = new Separator
			//sep.margin = Insets(10,0,0,10)
			//vbox.children.add(sep)

			val author = new TextField {
				margin = Insets(0, 10, 0, 10)
			}

			val src = new TextField {
				margin = Insets(0, 10, 0, 10)
			}

			val tags = new TextField {
				margin = Insets(0, 10, 0, 10)
			}

			val saveButton = new Button("add to library") {
				margin = Insets(0, 10, 0, 10)
			}

			val grid2 = new GridPane {
				add(new Text("author: "), 0, 0)
				add(author, 1, 0)

				add(new Text("src location: "), 2, 0)
				add(src, 3, 0)

				add(new Text("tags: "), 0, 1)
				add(tags, 1, 1)
				add(saveButton, 3, 1)


				alignment = Pos.CENTER

				margin = Insets(10)
			}

			val f = new TitledPane {
				text = "Asset"
				content = grid2
				margin = Insets(10)
			}
			vbox.children.add(f)

			author.setText(code.getDescriptor.author)

			val loc = code.getDescriptor.location

			if (loc.isDefined) {
				src.setText(loc.get)
			}

			val tagString = code.getDescriptor.tags.foldRight("") {
				(x, y) => x + ";" + y
			}

			tags.setText(tagString)

			saveButton.onAction = (ev: Event) => {
				val tagList: List[String] = tags.getText.split(";").toList.map {
					x => x.trim
				}
				val currentDesc = CodeAssetDescriptor(nodeNameField.getText,
					Some(src.getText),
					tagList,
					author.getText)
        code.setDescriptor(currentDesc)
        code.setCodeString(editor.getText)

				val res = CodeAssetManager.save(code.getCode)

				res match {
					case Failure(v) => {
						Dialogs.create().title("Error")
							.masthead("Sorry - could not safe CodeNode")
							.showException(v)
					}
					case _ =>
				}
			}


			content = vbox
		}
	}

	private def createView(c: CodeNodeModel): Pane = {
		val control = createControlPane
		val code = createCodePane
		val prop = createPropertiesPane

		window.setTitle(c.getDescriptor.name)

		this.controlsPane = Some(control)


		val accordion = new VBox {
			minWidth = outer.width

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

	private def compileAction(ed: TextArea, pi: ProgressIndicator): Unit = {
		val compileString = ed.getText

    code.setCodeString(compileString)

    code.compile

    // disconnect everything from the node
	}

  private def startCompileUIFeedback(pi: ProgressIndicator) = {
    MessageBus.send(RemoveAllNodeConnections(code))

    pi.setStyle(" -fx-accent: orange;");
    new Thread(new CompileTask(pi)).start()
  }

  private var compilationFinished = true
  private val compMutex = new Object

	private class CompileTask(pi: ProgressIndicator) extends Task(new javafx.concurrent.Task[Unit]() {

		//pi.progressProperty.unbind()
		pi.progressProperty().bind(this.progressProperty())

		def call(): Unit = {
			try {
				println("TASK")
				updateProgress(-1, 10)


				//outer.compileResult = Some(code.compile)

				/*
				outer.compileResult.get match {
				  case x: CompileSuccess => pi.setStyle(" -fx-progress-color: green;");
				  case _ => pi.setStyle(" -fx-progress-color: red;");
				} */

        var done = false
        while(!done) {
          Thread.sleep(100)
          compMutex.synchronized {
            done = compilationFinished
          }
        }

        updateProgress(10, 10)

				Platform.runLater(new Runnable {
					def run {

						compileResult.get match {
							case x: CompileSuccess => {
                pi.setStyle(" -fx-accent: green;")

                if(code.getCodeObject.isDefined) {
                  val codeNodeControls = code.getCodeObject.get.controlPanel

                  if (controlsPane.isDefined) {
                    val nodeControlsPane = controlsPane.get

                    nodeControlsPane.setContent(codeNodeControls)
                  }
                }
              };

              case _ => {
                pi.setStyle(" -fx-accent: red;")
              };
						}

            window.requestLayout()
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

		/* progressIndicator = new ProgressBar() {
			style = " -fx-accent: green;"
			/*minHeight = 40
			  minWidth = 40 */
			maxHeight = 25
			maxWidth = 50
		} */

		progressIndicator.setProgress(1)

		val popOver = new PopOver()

		progressIndicator onContextMenuRequested = new EventHandler[ContextMenuEvent] {
			def handle(p1: ContextMenuEvent): Unit = popUpAction(popOver, progressIndicator, p1)
		}

		val compileButton = new CompileButton("Compile")

		val ta = this.editor

		compileButton onAction = new EventHandler[ActionEvent] {
			def handle(p1: ActionEvent): Unit = compileAction(ta, progressIndicator)
		}

		this.compile = {() => compileAction(ta, progressIndicator)}


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
		this.editor.setText(code.getCodeString)

		this.editor
	}







  MessageBus.registerHandler({message: Message =>

    message match {
      case CompilationStarted(cnode) => {
        println("COMPILE?!")
        if(cnode == code) {
          compMutex.synchronized {
          compilationFinished = false

          startCompileUIFeedback(progressIndicator)
          }
        }

      }

      case CompilationFinished(cnode, compResult) => {
        if(cnode == code) {
            compMutex.synchronized{
              compileResult = Some(compResult)
              compilationFinished = true
          }
        }
      }

      case _ =>
    }
  })




}
