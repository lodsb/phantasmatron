package org.lodsb.phantasmatron.core

import java.io.File

case class PhantasmatronConfig(codeLibrary: String, imports: List[String])

object Config {
	def apply() : PhantasmatronConfig = {
		PhantasmatronConfig("code_lib"+File.separator,
      List("org.lodsb.phantasmatron.core.code._")
    )
	}

}
