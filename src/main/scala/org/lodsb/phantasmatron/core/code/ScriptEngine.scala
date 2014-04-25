package org.lodsb.phantasmatron.core.code

import java.io.Writer

import de.sciss.scalainterpreter.Interpreter
import de.sciss.scalainterpreter.Interpreter.Success


object ScriptEngine {

  val writer = new Writer(){
    private var myString = ""
    def write(cbuf: Array[Char], off: Int, len: Int): Unit = {
      myString = myString + new String(cbuf.slice(off, off+len))
      println(myString)
    }

    def flush(): Unit = {}
    def close(): Unit = {}
    def clear() = { myString = "" }

    override def toString = {myString}
  }

  println("STARTING SCRIPT ENGINE")


  val intpCfg = Interpreter.Config()
  intpCfg.imports :+= "org.lodsb.phantasmatron.core.code._"

  intpCfg.out = Some(writer)

  val interp = Interpreter(intpCfg)

  def interpret(code: String) : ScriptResult = {
    println("interpreting: "+code)

    synchronized {
      writer.clear()

      val res = interp.interpret(code)

      val message = writer.toString


      val ret = res match  {
        case Success(x, y) => ScriptSuccess(value=y, message)
        case _ => ScriptError(message)
      }

      writer.clear()

      ret
    }
  }

  /*
  val sourceDir = new File("scriptengine/code")
  val outputDir = new File("scriptengine/classes")

  val compilationClassPath = ScalaScriptEngine.currentClassPath
  // runtime classpath (empty). All other classes are loaded by the parent classloader
  val runtimeClasspath = Set[File]()
  // the output dir for compiled classes

  val sse = new ScalaScriptEngine(Config(
    List(SourcePath(sourceDir, outputDir)),
    compilationClassPath,
    runtimeClasspath
  )) with RefreshAsynchronously with FromClasspathFirst {
    val recheckEveryMillis: Long = 1000 // each file will only be checked maximum once per second
  }

  sse.deleteAllClassesInOutputDirectory

  sse.refresh

  while (true) {
    val t = sse.newInstance[TestTrait]("my.TryMe")
    println("code version %d, result : %s".format(sse.versionNumber, t.exec))
    Thread.sleep(500)
  }     */


}