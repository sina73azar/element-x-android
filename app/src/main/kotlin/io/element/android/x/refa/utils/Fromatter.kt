package com.drp.utils

fun accountMaskFormatter(account: String?): String {
    var unFormattedPan = ""
    if (account == null || account.isEmpty()) return ""
    val unFormattedSeq = account.filter { it.isDigit() }
    if (unFormattedPan.length == unFormattedSeq.length) {
        return ""
    }
    unFormattedPan = unFormattedSeq
    val formatted = StringBuilder()
    for (i in unFormattedPan.indices) {
        if (unFormattedPan.length > 4) {
            if (i > unFormattedPan.length - 5)
                formatted.append(unFormattedPan[i])
            else
                formatted.append("*")
        } else {
            if (i > unFormattedPan.length - 3)
                formatted.append(unFormattedPan[i])
            else
                formatted.append("*")
        }
    }
    return formatted.toString()
}

fun panMaskFormatter(pan: String?): String {
    val blockLengths = intArrayOf(4, 4, 4, 4)
    var unFormattedPan = ""
    if (pan == null || pan.isEmpty()) return ""
    val unFormattedSeq = pan.filter { it.isDigit() }
    if (unFormattedPan.length == unFormattedSeq.length) {
        return ""
    }
    unFormattedPan = unFormattedSeq
    if (unFormattedPan.length > 16) {
        unFormattedPan = unFormattedSeq.substring(0, 16)
    }
    val formatted = StringBuilder()
    var blockIndex = 0
    var currentBlock = 0
    for (i in unFormattedPan.indices) {
        if (currentBlock == blockLengths[blockIndex]) {
            formatted.append("-")
            currentBlock = 0
            blockIndex++
        }
        if (i > 11 || i < 6)
            formatted.append(unFormattedPan[i])
        else
            formatted.append("*")
        currentBlock++
    }
    return formatted.toString()
}

fun reversePan(pan: String?): String {
    val data = pan?.split("-")?.toMutableList()
    data?.reverse()
    val formatted = StringBuilder()
    var i = 0
    while (i < data?.size!!) {
        if (i == 2) {
            formatted.append("**" + data[i].substring(0, 2))
        } else
            formatted.append(data[i])

        if (i < 3)
            formatted.append("-")

        i++
    }

    return formatted.toString()
}
