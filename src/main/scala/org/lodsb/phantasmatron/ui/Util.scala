package org.lodsb.phantasmatron.ui

import javafx.scene.paint.Color
import java.nio.ByteBuffer

/**
 * Created by lodsb on 4/12/14.
 */
object Util {
  def typeString2Color(s: String) : Color = {
    val hash = hashString(s);

    val doubleBytes = ByteBuffer.allocate(4).putInt(hash).array().map( x => (x.toDouble).abs / 255.0)

    new Color(doubleBytes(0),doubleBytes(1),doubleBytes(2),1.0);
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
}
