package se.dansdata.backstage.app

import org.apache.commons.text.WordUtils
import se.dansdata.backstage.utilities.StringUtils

fun main() {
    val tokens = StringUtils.split(MessageUtils.getMessage())
    val result = StringUtils.join(tokens)
    println(WordUtils.capitalize(result))
}
