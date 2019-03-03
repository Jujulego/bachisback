package net.capellari.julien.bachisback.db

import kotlin.random.Random

const val ALPHABET = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN0123456789"

fun generateFileName(length: Int): String {
    var name = ""

    for (i in 0 until length) {
        name += ALPHABET[Random.nextInt(ALPHABET.length)]
    }

    return name;
}