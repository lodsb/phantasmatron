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


/**
 * Created by lodsb on 4/25/14.
 */

trait Asset {}

case class CodeAssetDescriptor(name: String, location: Option[String], tags: List[String], author: String = "lodsb", typeInfo: String ="code")

object CreateNewCodeNode extends CodeAssetDescriptor("New CodeNode", None, List.empty, "", "code")

object AssetDataFormat extends DataFormat("AssetDescriptor")