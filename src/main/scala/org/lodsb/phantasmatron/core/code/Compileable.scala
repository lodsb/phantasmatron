package org.lodsb.phantasmatron.core.code

import org.lodsb.phantasmatron.core.asset.CodeAssetDescriptor


/**
 * Created by lodsb on 4/27/14.
 */
trait Compileable {
  protected var code: Code

  def setCodeString(codeString: String) = {code.code = codeString}
  def getCodeString : String = code.code
  def getDescriptor = code.descriptor
  def setDescriptor(desc:CodeAssetDescriptor ) = {code.descriptor = desc}
  def getCode = code

  protected var codeObject : Option[CodeObject] = None
  def getCodeObject : Option[CodeObject] = {
    codeObject
  }

  def compile : Unit


}
