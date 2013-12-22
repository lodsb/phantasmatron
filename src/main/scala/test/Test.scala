package test

import de.sciss.scalainterpreter.{Style, CodePane}
import javax.swing.{JPanel, JComponent}
import java.awt.{FlowLayout, Dimension}

/**
 * Created by lodsb on 12/17/13.
 */
object Test {

  def codePane : CodePane  = {
    val codeCfg     = CodePane.Config()         // creates a new configuration _builder_
    codeCfg.style   = Style.Light               // use a light color scheme
    codeCfg.text    = """new Foo extends CodeNode"""       // initial text to show in the widget
    codeCfg.font    = scala.collection.immutable.Seq("Helvetica" -> 16)    // list of preferred fonts
    // add a custom key action
    codeCfg.keyMap += javax.swing.KeyStroke.getKeyStroke("control U") -> { () =>
      println("Action!")
    }

    CodePane(codeCfg)
  }

}
