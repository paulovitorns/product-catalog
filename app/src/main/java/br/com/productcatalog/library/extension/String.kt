package br.com.productcatalog.library.extension

import android.text.SpannableString
import br.com.productcatalog.library.spannable.TopAlignSuperscriptSpan

fun String.removeExtrasWhiteSpaces(): String {
    val input = this.trim()
    var clearString = ""
    var prevChar = ""

    input.forEach { char ->
        if ((prevChar == " " && char == ' ').not()) clearString += char
        prevChar = char.toString()
    }

    return clearString
}

fun String.topAlignDecimalChars(): SpannableString {

    val decimalChars = this
        .substring(this.length - 3, this.length)

    val amountWithoutDecimalChars = this.replace(decimalChars, "")
    // This is necessary because some currency could had more than one char
    val balanceSize = amountWithoutDecimalChars.length + decimalChars.length
    // this catches the fraction size plus the decimal char to transform it correctly

    val spannableString = SpannableString(amountWithoutDecimalChars + decimalChars)
    spannableString.setSpan(
        TopAlignSuperscriptSpan(0.25f),
        balanceSize - decimalChars.length,
        balanceSize,
        0
    )

    return spannableString
}