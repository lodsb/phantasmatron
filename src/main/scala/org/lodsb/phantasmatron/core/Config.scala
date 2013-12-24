package org.lodsb.phantasmatron.core

case class PhantasmatronConfig(codeLibrary: String)

object Config {
	def apply() : PhantasmatronConfig = {
		PhantasmatronConfig("code_lib")
	}

}
