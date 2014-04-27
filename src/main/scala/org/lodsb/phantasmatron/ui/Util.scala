package org.lodsb.phantasmatron.ui

import java.nio.ByteBuffer
import scalafx.scene.paint.Color

/**
 * Created by lodsb on 4/12/14.
 */
object Util {
  def typeString2Color(s: String) : Color = {
    val hash = hashString(s);

    val doubleBytes = ByteBuffer.allocate(4).putInt(hash).array().map( x => (x.abs % 16)*16)

    Color.rgb(doubleBytes(0),doubleBytes(1),doubleBytes(2));
  }

  private def hashString(s: String) : Int = {
    val prime = 17
    val prime2= 31

    var hash = prime

    for(i <- 0 to s.size-1) {
      hash = hash * prime2 + s.charAt(i)
    }

    println(hash)

    hash
  }

  def toRgba(c: Color, a: Double): String = {
    "rgba(" + (to255Int(c.red)) + "," + (to255Int(c.green)) + "," + (to255Int(c.blue)) + "," + a.toString + ")"
  }

  def to255Int(d: Double): Int = {
    (d * 255).toInt
  }

  def gradient(c: Color): String = {
    " -fx-background-color : radial-gradient(center 50% 25%,\n" +
      "        radius 75%,\n" +
      "        " + toRgba(c, 0.8) + " 0%,\n" +
      "        rgba(82,82,82,0.9) 100%);"
  }

}
