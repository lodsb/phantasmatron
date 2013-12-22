package org.lodsb.phantasmatron.core


/**
 * Created by lodsb on 12/20/13.
 */
class Code {

  var code = "new X"

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
          println("ddd "+x)
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

/**
 * Created by lodsb on 12/17/13.
 */

abstract class CompileResult(val message: String)

case class CompileSuccess(value: CodeNode, override val message: String) extends CompileResult(message)

case class CompileError(override val message:String) extends CompileResult(message)