
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
