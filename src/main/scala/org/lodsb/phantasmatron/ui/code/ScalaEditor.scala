/*
 * +1>>  This source code is licensed as GPLv3 if not stated otherwise.
 *     >>  NO responsibility taken for ANY harm, damage done
 *     >>  to you, your data, animals, etc.
 *     >>
 *   +2>>
 *     >>  Last modified:  4/27/14 10:09 AM
 *     >>  Origin: phantasmatron :: ScalaEditor.scala
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

package org.lodsb.phantasmatron.ui.code

/**
 * Created by lodsb on 4/18/14.
 */

import java.util.Collection;
import java.util.Collections;

import java.util.regex.Pattern;

import org.fxmisc.richtext.{CodeArea, StyleSpansBuilder}
;



import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


;


object ScalaEditor {

  val keywords = List(
    "abstract", "assert", "boolean", "break", "byte",
    "case", "catch", "char", "class", "const",
    "continue", "default", "do", "double", "else",
    "enum", "extends", "final", "finally", "float",
    "for", "goto", "if", "implements", "import",
    "instanceof", "int", "interface", "long", "native",
    "new", "package", "private", "protected", "public",
    "return", "short", "static", "strictfp", "super",
    "switch", "synchronized", "this", "throw", "throws",
    "transient", "try", "void", "volatile", "while"
  )

  val keywordPattern = Pattern.compile("\\b(" + keywords.foldRight(""){(x,y) => x+"|"+y} + ")\\b");

  def apply() = {
    val  codeArea = new CodeArea();

    codeArea.setWrapText(true)
    codeArea.requestFocus()

    codeArea.textProperty().addListener(new ChangeListener[String]() {

      /*public void changed(ObservableValue<? extends String> observable,
      String oldText, String newText) {
        Matcher matcher = KEYWORD_PATTERN.matcher(newText);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
          = new StyleSpansBuilder<>();
        while(matcher.find()) {
          spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
          spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
          lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), newText.length() - lastKwEnd);
        codeArea.setStyleSpans(0, spansBuilder.create());
      } */
      def changed(p1: ObservableValue[_ <: String], oldText: String, newText: String): Unit ={
        val matcher = keywordPattern.matcher(newText)

        var lastKwEnd = 0;
        val spansBuilder: StyleSpansBuilder[Collection[String]] = new StyleSpansBuilder[Collection[String]]();

        while(matcher.find()) {
          println(matcher.toString)
          spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd)
          spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
          //codeArea.setStyleClass(matcher.start(), matcher.end(), "keyword")
          lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), newText.length() - lastKwEnd);
        codeArea.setStyleSpans(0, spansBuilder.create());
      }

    });
    codeArea
  }

}
