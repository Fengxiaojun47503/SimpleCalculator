package com.fxj.simplecalculator.utils

import android.text.TextUtils
import java.lang.Exception
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

object NumberFormatUtils {
    const val MAX_INPUT_LENGTH = 20
    const val MAX_INTEGER_DIGITS = 15
    private var sNumberFormat: NumberFormat? = null
    private var sDecimalFormat: DecimalFormat? = null
    fun formatCommaStyleFormula(formula: String): String {
        val result = StringBuilder()
        var last = 0
        var numIndex = findNextNumberStartIndex(last, formula)
        while (numIndex != -1) {
            result.append(formula.subSequence(last, numIndex))
            var numbers = getNextNumbers(numIndex, formula)

            var ellipsisLength = 0
            if (numbers.length > MAX_INPUT_LENGTH) {
                ellipsisLength = numbers.length - MAX_INPUT_LENGTH
                numbers = numbers.substring(0, MAX_INPUT_LENGTH)
            }
            val formated = formatCommaStyle(numbers)
            if (TextUtils.isEmpty(formated)) {
                result.append(numbers)
            } else {
                result.append(formated)
            }
            last = numIndex + numbers.length + ellipsisLength
            numIndex = findNextNumberStartIndex(last, formula)
        }
        result.append(formula.substring(last))
        return result.toString()
    }

    fun formatNormalStyleFormula(formula: String): String {
        return formula.replace(java.lang.String.valueOf(ButtonUtils.TEXT_COMMA).toRegex(), "")
    }

    private fun findNextNumberStartIndex(from: Int, formula: String): Int {
        var index = -1
        if (from < formula.length) {
            var i = from
            while (i < formula.length) {
                val c = formula[i]
                if (Character.isDigit(c)) {
                    index = i
                    break
                } else if (c == ButtonUtils.BUTTON_TEXT_DOT) {
                    ++i
                    while (i < formula.length && Character.isDigit(formula[i])) {
                        ++i
                    }
                }
                ++i
            }
        }
        return index
    }

    private fun getNextNumbers(from: Int, formula: String): String {
        val s = StringBuilder()
        if (from < formula.length) {
            for (i in from until formula.length) {
                val c = formula[i]
                if (Character.isDigit(c) || ButtonUtils.BUTTON_TEXT_DOT === c) {
                    s.append(c)
                } else {
                    break
                }
            }
        }
        return s.toString()
    }

    fun formatCommaStyle(number: String): String {
        var formated = number
        var valid = true
        var num = 0.0
        try {
            num = number.toDouble()
        } catch (e: Exception) {
            valid = false
        }
        if (valid) {
            val dotIndex: Int = number.indexOf(ButtonUtils.BUTTON_TEXT_DOT)
            formated = if (dotIndex == -1) {
                formatCommaStyle(num)
            } else {
                val integer = number.substring(0, dotIndex)
                if (integer != null && integer.length >= 19) {
                    formatCommaStyle(num)
                } else {
                    formatCommaStyle(integer.toLong()) + number.substring(dotIndex)
                }
            }
        }
        return formated
    }

    fun formatCommaStyle(number: Long): String {
        return sNumberFormat!!.format(number)
    }

    fun formatCommaStyle(number: Double): String {
        return sNumberFormat!!.format(number)
    }

    init {
        sNumberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
        sNumberFormat?.setMaximumIntegerDigits(MAX_INTEGER_DIGITS)
        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        sDecimalFormat = DecimalFormat("", symbols)
        sDecimalFormat!!.maximumIntegerDigits = MAX_INTEGER_DIGITS
    }
}