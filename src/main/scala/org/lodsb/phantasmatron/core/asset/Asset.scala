package org.lodsb.phantasmatron.core.asset

import javafx.scene.input.DataFormat


/**
 * Created by lodsb on 4/25/14.
 */
/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-25 :: 04:45
    >>  Origin: phantasmatron
    >>
  +3>>
    >>  Copyright (c) 2013:
    >>
    >>     |             |     |
    >>     |    ,---.,---|,---.|---.
    >>     |    |   ||   |`---.|   |
    >>     `---'`---'`---'`---'`---'
    >>                    // Niklas Klügel
    >>
  +4>>
    >>  Made in Bavaria by fat little elves - since 1983.
 */
trait Asset {}

case class AssetDescriptor(name: String, location: Option[String], tags: List[String], author: String = "lodsb", typeInfo: String ="code")

object CreateNewCodeNode extends AssetDescriptor("New CodeNode", None, List.empty, "", "code")

object AssetDataFormat extends DataFormat("AssetDescriptor")