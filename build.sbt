name := "phantasmatron"

scalaVersion := "2.10.3"

version := "1.0"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += "org.lodsb" %% "reakt" % "0.2-SNAPSHOT"

libraryDependencies += "eu.mihosoft.vrl.workflow-8.0" % "vworkflows-fx" % "0.1-r2-SNAPSHOT"

libraryDependencies += "eu.mihosoft.vrl.workflow-8.0" % "vworkflows-core" % "0.1-r2-SNAPSHOT"

libraryDependencies += "org.jfxtras" % "jfxtras-labs" % "8.0-r1"

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.0.5"

libraryDependencies += "org.scalafx" % "scalafx_2.10" % "8.0.0-M1"

libraryDependencies += "com.thoughtworks.xstream" % "xstream" % "1.4.4"

libraryDependencies += "de.sciss" %% "scalainterpreterpane" % "1.6.+"

libraryDependencies += "com.googlecode.scalascriptengine" % "scalascriptengine" % "1.3.6-2.10.3"

//libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.3" // where i.e. ${scala.version} = 2.10.3

libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT"

libraryDependencies += "org.fxmisc.richtext" % "richtextfx" % "1.0.0-SNAPSHOT"

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME")+"jre/lib/ext/jfxrt.jar"))

fork := true
