/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/28/14 1:06 AM
 *     >>  Origin: phantasmatron :: AssetManager.scala
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

import scalafx.collections.ObservableBuffer
import scala.util.Try

abstract class AssetManager[T](val name: String) {
  AssetManagerFactory.registerAssetManager(name, this)

  val knownObjects = ObservableBuffer[AssetDescriptor[T]]()

  def instantiate(asset: AssetDescriptor[T]) : Try[T]
}

object AssetManagerFactory {

  def registerAssetManager(name: String, assetManager: AssetManager[_]) {

  }

  def getAssetManager(name: String) : Option[AssetManager[_]] = {
    None
  }
}