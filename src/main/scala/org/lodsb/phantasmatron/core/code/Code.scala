/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: Code.scala
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

package org.lodsb.phantasmatron.core.code

import org.lodsb.phantasmatron.core.asset.{Asset, CodeAssetDescriptor}

/**
 * Created by lodsb on 12/20/13.
 */
class Code(var code: String, var descriptor: CodeAssetDescriptor) extends Asset {

    /*"new TT { \n" +
    "inputs = List(Input[Float](\"first\", 0.32f))\n" +
    "outputs = List(Output[Float](\"first\", 0.32f))\n" +
    "}"*/

  def compile : CompileResult = {
    val codeString = this.code

    val result = ScriptEngine.interpret(codeString)
    //argh...
    var ret : CompileResult = null
    println("Compiling: "+codeString)

    result match {
      case ScriptSuccess(x,y) => {
        try {

          val instance = x.asInstanceOf[CodeObject]

                                ret = CompileSuccess(instance, message = y)
        } catch {
          case t: Throwable =>  ret = CompileError(y + "\n" + t.getMessage)
        }
      }
      case x:ScriptError =>     ret =CompileError(x.message)
    }

    ret
  }

}

/**
 * Created by lodsb on 12/17/13.
 */

abstract class CompileResult(val message: String)

case class CompileSuccess(value: CodeObject, override val message: String) extends CompileResult(message)

case class CompileError(override val message:String) extends CompileResult(message)