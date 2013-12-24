package org.lodsb.phantasmatron.core

import org.lodsb.phantasmatron.ui.ObjectPalette.{CreateNewCodeNode, ObjectDescriptor}
import scala.util.Try


/**
 * Created by lodsb on 12/20/13.
 */
class Code(var code: String, var descriptor: ObjectDescriptor) {

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

          val instance = x.asInstanceOf[CodeNode]
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

object Code {
  def apply(desc: ObjectDescriptor) : Try[Code] = {
	  def createCode = {
	 		val codeString = scala.io.Source.fromFile(desc.location.get).mkString
	 		new Code(codeString, desc)
	 	}

	  if(desc == CreateNewCodeNode) {
		  Try(new Code("", desc))
	  } else {
		  Try(createCode)
	  }
  }
}

/**
 * Created by lodsb on 12/17/13.
 */

abstract class CompileResult(val message: String)

case class CompileSuccess(value: CodeNode, override val message: String) extends CompileResult(message)

case class CompileError(override val message:String) extends CompileResult(message)
