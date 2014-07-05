/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: Asset.scala
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

package org.lodsb.phantasmatron.core.asset

import javafx.scene.input.DataFormat
import org.lodsb.phantasmatron.core.code.Code


/**
 * Created by lodsb on 4/25/14.
 */

trait Asset {}

trait AssetDescriptor[T] {
  self =>
  val name: String
  val location: Option[String]
  val tags: List[String]
  val author: String = "lodsb"
  val typeInfo: String ="code"

  val factory : String

  def instantiate = AssetManagerFactory.getAssetManager(factory).map( x => x.asInstanceOf[AssetManager[T]].instantiate(self) )
}


case class CodeAssetDescriptor(override val name: String, override val location: Option[String],
                               override val tags: List[String], override val author: String = "lodsb",
                               override val typeInfo: String ="code",
                               override val factory: String = CodeAssetManager.name) extends AssetDescriptor[Code]

object CreateNewCodeNode extends CodeAssetDescriptor("New CodeNode", None, List.empty, "", "code")

object AssetDataFormat extends DataFormat("AssetDescriptor")

case class Testtt(str: String)