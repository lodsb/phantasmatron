package org.lodsb.phantasmatron.core

import java.io.File

case class PhantasmatronConfig(codeLibrary: String)

object Config {
	def apply() : PhantasmatronConfig = {
		PhantasmatronConfig("code_lib"+File.separator)
	}

}
