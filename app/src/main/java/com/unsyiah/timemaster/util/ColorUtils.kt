package com.unsyiah.timemaster.util

import androidx.compose.ui.graphics.Color
import com.unsyiah.timemaster.ui.theme.*

fun generateColorFromString(s: String): Color {
    val firstLetter = s.slice(IntRange(0,0)).lowercase()
    val length = s.length
    val inFirstThirdOfAlphabet = Regex("[a-j]").matches(firstLetter)
    val inSecondThirdOfAlphabet = Regex("[k-s]").matches(firstLetter)
    return when {
        length >= 15 -> {
            when {
                inFirstThirdOfAlphabet -> Blue200
                inSecondThirdOfAlphabet -> Teal700
                else -> Green700
            }
        }
        length >= 10 -> {
            when {
                inFirstThirdOfAlphabet -> Blue100
                inSecondThirdOfAlphabet -> Teal500
                else -> Green500
            }

        }
        length >= 5  -> {
            when {
                inFirstThirdOfAlphabet -> Purple300
                inSecondThirdOfAlphabet -> Teal300
                else -> Green300
            }
        }
        else -> {
            when {
                inFirstThirdOfAlphabet -> Purple100
                inSecondThirdOfAlphabet -> Teal100
                else -> Green100
            }
        }
    }
}