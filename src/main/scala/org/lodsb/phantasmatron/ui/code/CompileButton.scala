package org.lodsb.phantasmatron.ui.code

import scalafx.scene.control.Button

/*
  +1>>  This source code is licensed as GPLv3 if not stated otherwise.
    >>  NO responsibility taken for ANY harm, damage done
    >>  to you, your data, animals, etc.
    >>
  +2>>
    >>  Last modified:  2013-12-25 :: 17:19
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
class CompileButton(text: String) extends Button(text) {
	style = "-fx-background-color:\n        linear-gradient(#f0ff35, #a9ff00),\n        radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%);\n    -fx-background-radius: 6, 5;\n    -fx-background-insets: 0, 1;\n    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );\n    -fx-text-fill: #395306;"

}
