package org.lodsb.phantasmatron.ui.dataflow

import eu.mihosoft.vrl.workflow.fx.ScalableContentPane
import java.io.File

/**
 * Created by lodsb on 2/18/14.
 */
class DataFlow extends ScalableContentPane {
  this.getStyleClass.setAll("vflow-background")
  this.getStylesheets.setAll((new File("default.css").toURI.toString))



}
