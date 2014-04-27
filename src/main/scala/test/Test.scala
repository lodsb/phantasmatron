/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: Test.scala
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

package test

import de.sciss.scalainterpreter.{Style, CodePane}

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
