package com.fxj.simplecalculator.utils

class ButtonUtils {
    companion object {

        const val ACTION_APPEND = -1
        const val BUTTON_ID_0 = 0
        const val BUTTON_ID_1 = 1
        const val BUTTON_ID_2 = 2
        const val BUTTON_ID_3 = 3
        const val BUTTON_ID_4 = 4
        const val BUTTON_ID_5 = 5
        const val BUTTON_ID_6 = 6
        const val BUTTON_ID_7 = 7
        const val BUTTON_ID_8 = 8
        const val BUTTON_ID_9 = 9

        const val BUTTON_ID_CLEAR = 10
        const val BUTTON_ID_DEL = 11
        const val BUTTON_ID_PERCENT = 12
        const val BUTTON_ID_DIV = 13
        const val BUTTON_ID_MUL = 14
        const val BUTTON_ID_MINUS = 15
        const val BUTTON_ID_PLUS = 16
        const val BUTTON_ID_EQUAL = 17
        const val BUTTON_ID_DOT = 18

        const val BUTTON_TEXT_0 = '0'
        const val BUTTON_TEXT_1 = '1'
        const val BUTTON_TEXT_2 = '2'
        const val BUTTON_TEXT_3 = '3'
        const val BUTTON_TEXT_4 = '4'
        const val BUTTON_TEXT_5 = '5'
        const val BUTTON_TEXT_6 = '6'
        const val BUTTON_TEXT_7 = '7'
        const val BUTTON_TEXT_8 = '8'
        const val BUTTON_TEXT_9 = '9'
        const val BUTTON_TEXT_DOT = '.'
        const val BUTTON_TEXT_CLEAR = "AC"
        const val BUTTON_TEXT_DEL = "DEL"
        const val BUTTON_TEXT_PERCENT = '%'
        const val BUTTON_TEXT_DIV = '\u00f7' // ÷
        const val BUTTON_TEXT_MUL = '\u00d7' // ×
        const val BUTTON_TEXT_MINUS = '\u002d' // −
        const val BUTTON_TEXT_PLUS = '\u002b' // +
        const val BUTTON_TEXT_EQUAL = '='

        const val TEXT_COMMA = ','

        val BUTTONS = arrayOf(
            listOf(
                Pair(BUTTON_ID_CLEAR, BUTTON_TEXT_CLEAR),
            Pair(BUTTON_ID_DEL, BUTTON_TEXT_DEL),
                Pair(BUTTON_ID_PERCENT, BUTTON_TEXT_PERCENT.toString()),
            Pair(BUTTON_ID_DIV, BUTTON_TEXT_DIV.toString()),
            ),
            listOf(
                Pair(BUTTON_ID_7, BUTTON_TEXT_7),
                Pair(BUTTON_ID_8, BUTTON_TEXT_8),
                Pair(BUTTON_ID_9, BUTTON_TEXT_9),
                Pair(BUTTON_ID_MUL, BUTTON_TEXT_MUL.toString()),
            ),
            listOf(
                Pair(BUTTON_ID_4, BUTTON_TEXT_4),
                Pair(BUTTON_ID_5, BUTTON_TEXT_5),
                Pair(BUTTON_ID_6, BUTTON_TEXT_6),
                Pair(BUTTON_ID_MINUS, BUTTON_TEXT_MINUS.toString()),
            ),
            listOf(
                Pair(BUTTON_ID_1, BUTTON_TEXT_1),
                Pair(BUTTON_ID_2, BUTTON_TEXT_2),
                Pair(BUTTON_ID_3, BUTTON_TEXT_3),
                Pair(BUTTON_ID_PLUS, BUTTON_TEXT_PLUS.toString()),
            ),
            listOf(
                Pair(BUTTON_ID_0, BUTTON_TEXT_0),
                Pair(BUTTON_ID_DOT, BUTTON_TEXT_2),
                Pair(BUTTON_ID_EQUAL, BUTTON_TEXT_EQUAL),
            ),
        )

    }
}
