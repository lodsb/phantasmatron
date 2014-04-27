/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: Compileable.scala
 *     >>
 *   +3>>
 *     >>  Copyright (c) 2014:
 *     >>
 *     >>     |             |     |
 *     >>     |    ,---.,---|,---.|---.
 *     >>     |    |   ||   |`---.|   |
 *     >>     `---'`---'`---'`---'`---'
 *     >>                    // Niklas Klügel
 *     >>
 *   +4>>
 *     >>  Made in Bavaria by fat little elves - since 1983.
 */

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
