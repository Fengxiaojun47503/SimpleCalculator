package com.fxj.simplecalculator.engine

import android.text.TextUtils
import android.util.Log
import com.fxj.simplecalculator.utils.ButtonUtils
import com.fxj.simplecalculator.utils.NumberFormatUtils
import org.javia.arity.Symbols
import java.util.*

class Engine {

    companion object {
        const val TAG = "Engine"

        const val DEFAULT_TEXT = "0"
        const val INFINITY = "Infinity"
        const val INFINITY_TEXT = "\u221e" // ∞
        const val NAN = "NaN"
        const val ERROR = "error"
        const val RESULT_ZERO = "8.53773646e−7"


        private const val MAX_LINE_LENGTH = 15
        private const val MAX_PRECISION = 9
        private const val MIN_PRECISION = 6

        private const val NEGATIVE_0 = "\u22120" // -0

        private var mSymbols = Symbols()

        fun getSuggested(equationContent: String?, origin: String?, button: Pair<Int, String>, isCommaStyle: Boolean): String {
            var original = origin
            original = original ?: ""
            if (original.equals(ButtonUtils.BUTTON_TEXT_0.toString())) {
                original = ""
            }
            original = NumberFormatUtils.formatNormalStyleFormula(original!!)
            var s: String = original
            val btnId = button.first
            when (btnId) {
                ButtonUtils.BUTTON_ID_CLEAR -> {
                    s = DEFAULT_TEXT
                }
                ButtonUtils.BUTTON_ID_DEL -> {
                    s = deleteType(original)
                }
                ButtonUtils.BUTTON_ID_EQUAL ->{}
                else -> {
                    if (TextUtils.equals(INFINITY_TEXT, s)) {
                        return original
                    }
                    var newInput: String = ""
                    if (btnId != ButtonUtils.ACTION_APPEND) newInput = button.second
                    if (!TextUtils.isEmpty(newInput)) {
                        val formatedEquationText = NumberFormatUtils.formatNormalStyleFormula(equationContent!!)
                        s = normalType(formatedEquationText, original, newInput,)
                    }
                }
            }
            if (isCommaStyle) {
                if (TextUtils.equals(INFINITY_TEXT, s)) {
                    return s
                }
                val res = NumberFormatUtils.formatCommaStyleFormula(s)
                val formattedRes = NumberFormatUtils.formatNormalStyleFormula(res)
                s = if (formattedRes != s) {
                    NumberFormatUtils.formatCommaStyleFormula(original)
                } else {
                    res
                }
            }
            return s
        }

        private fun deleteType(origin: String): String {
            var original = origin
            if (!TextUtils.isEmpty(original)) {
                val ol = original.length
                original = original.substring(0, ol - 1)

                var hasNumber = false
                for (i in 0 until original.length) {
                    val charText = original[i]
                    if (Character.isDigit(charText)) {
                        hasNumber = true
                    }
                }
                if (!hasNumber  || TextUtils.isEmpty(original)) {
                    original = DEFAULT_TEXT
                }
            }
            return original
        }

        private fun normalType(equationContent: String, original: String, delta0: String,): String {
            var original = original
            var delta = delta0
            val l = delta.length
            if (java.lang.String.valueOf(ButtonUtils.BUTTON_TEXT_0) == original) {
                if (l != 1) {
                    original = ""
                } else {
                    val c = delta[0]
                    if ((!isBaseOperator(c) && c == ButtonUtils.BUTTON_TEXT_MINUS)
                        && c != ButtonUtils.BUTTON_TEXT_DOT && c != ButtonUtils.BUTTON_TEXT_PERCENT
                    ) {
                        original = ""
                    }
                }
            }
            return if (l == 1) {
                val c = delta[0]
                if (c == ButtonUtils.BUTTON_TEXT_PERCENT) {
                    var p = original.length - 1
                    while (p >= 0) {
                        if (!isBaseOperator(original[p])) {
                            --p
                        } else {
                            break
                        }
                    }
                    val num = original.substring(p + 1)
                    if (!TextUtils.isEmpty(num)) {
                        var symbol = original.substring(0, p + 1)
                        symbol = if (TextUtils.isEmpty(symbol)) "" else symbol
                        val s = symbol + getPercentageSuggestResult(symbol, equationContent, num,)
                            original = s
                    }
                    return original
                }
                if (c == ButtonUtils.BUTTON_TEXT_DOT) {
                    var p = original.length - 1
                    while (p >= 0 && (Character.isDigit(original[p]))
                    ) {
                        --p
                    }
                    if (p >= 0 && original[p] == ButtonUtils.BUTTON_TEXT_DOT) {
                        return original
                    }
                }
                var ol = original.length
                var pre = if (ol > 0) original[ol - 1] else '\u0000'
                if (isBaseOperator(c)) {
                    while (isBaseOperator(pre)) {
                        --ol
                        original = original.substring(0, ol)
                        pre = if (ol > 0) original[ol - 1] else '\u0000'
                    }
                }

                if (ol <= 0 && isBaseOperator(c)) {
                    return ButtonUtils.BUTTON_TEXT_0.toString() + delta
                }
                original + delta
            } else {
                original + delta
            }
        }

        private fun getPercentageSuggestResult(symbol: String, equationContent: String, num: String,): String? {
            var suggestResult: String = ""
            if (!TextUtils.isEmpty(equationContent)) {
                    if (TextUtils.equals(symbol, java.lang.String.valueOf(ButtonUtils.BUTTON_TEXT_PLUS)) || TextUtils.equals(
                            symbol, java.lang.String.valueOf(ButtonUtils.BUTTON_TEXT_MINUS)
                        )
                    ) {
                        var frontEquationText = equationContent.substring(0, equationContent.lastIndexOf(num) - 1)
                        frontEquationText = if (TextUtils.isEmpty(frontEquationText)) "1" else frontEquationText
                        suggestResult = evaluate( "(" + frontEquationText + ")"
                                    + ButtonUtils.BUTTON_TEXT_MUL + num + ButtonUtils.BUTTON_TEXT_PERCENT
                        )
                    } else {
                            suggestResult = evaluate(num + ButtonUtils.BUTTON_TEXT_PERCENT)
                    }
            }
            return suggestResult
        }


        fun isDividedByZero(input: String): Boolean {
            val divIndex: Int = input.lastIndexOf(ButtonUtils.BUTTON_TEXT_DIV)
            if (divIndex <= 0) {
                return false
            }
            val str = input.substring(divIndex)
            val length = str.length
            return if (length >= 2 && ButtonUtils.BUTTON_TEXT_DIV == str[0]) {
                if (str[1] != ButtonUtils.BUTTON_TEXT_0) {
                    false
                } else {
                    val indexSymbolDot: Int = str.indexOf(ButtonUtils.BUTTON_TEXT_DOT)
                    if (indexSymbolDot == -1) {
                        true
                    } else {
                        var containOtherNum = false
                        for (i in indexSymbolDot + 1 until length) {
                            if (str[i] != ButtonUtils.BUTTON_TEXT_0) {
                                containOtherNum = true
                                break
                            }
                        }
                        if (containOtherNum) {
                            false
                        } else {
                            true
                        }
                    }
                }
            } else false
        }


        fun evaluate(input: String): String {
            return evaluate(input, false)
        }

        fun evaluate(input: String, commaStyle: Boolean): String {
            return evaluate(input, commaStyle, NAN)
        }

        fun evaluate(input0: String, commaStyle: Boolean, originResult: String): String {
            var input = input0
            if (input.trim { it <= ' ' } == "" || TextUtils.equals(input, ButtonUtils.BUTTON_TEXT_0.toString())) {
                return ButtonUtils.BUTTON_TEXT_0.toString()
            }
            input = NumberFormatUtils.formatNormalStyleFormula(input)

            var size = input.length
            while (size > 0 && isBaseOperator(input[size - 1])) {
                input = input.substring(0, size - 1)
                --size
            }

            var result = originResult
            try {
                var value: Double = mSymbols.eval(input)
                for (precision in MAX_PRECISION downTo MIN_PRECISION + 1) {
                    result = tryFormattingWithPrecision(value, precision)
                    if (result.length < MAX_LINE_LENGTH || INFINITY == result) {
                        break
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Engine Exception", e)
            }
            result = result.replace('-', ButtonUtils.BUTTON_TEXT_MINUS).replace(INFINITY, INFINITY_TEXT)
            if (commaStyle) {
                result = NumberFormatUtils.formatCommaStyleFormula(result)
            }
            if (TextUtils.equals(NEGATIVE_0, result) || TextUtils.equals(RESULT_ZERO, result)) {
                result = ButtonUtils.BUTTON_TEXT_0.toString()
            }
            return result
        }

        fun isBaseOperator(c: Char): Boolean {
            return c == ButtonUtils.BUTTON_TEXT_PLUS || c == ButtonUtils.BUTTON_TEXT_MINUS || c == ButtonUtils.BUTTON_TEXT_MUL || c == ButtonUtils.BUTTON_TEXT_DIV
                    || c == '+' || c == '-' || c == '*' || c == '/'
        }

        private fun tryFormattingWithPrecision(value: Double, precision: Int): String {
            var result = String.format(Locale.US, "%" + MAX_LINE_LENGTH + "." + precision + "g", value).trim { it <= ' ' }
            if (result != NAN) {
                var mantissa = result
                var exponent: String? = null
                val e = result.indexOf('e')
                if (e != -1) {
                    mantissa = result.substring(0, e)

                    // Strip "+" and unnecessary 0's from the exponent
                    exponent = result.substring(e + 1)
                    if (exponent.startsWith("+")) {
                        exponent = exponent.substring(1)
                    }
                    exponent = exponent.toInt().toString()
                } else {
                    mantissa = result
                }
                var period = mantissa.indexOf('.')
                if (period == -1) {
                    period = mantissa.indexOf(',')
                }
                if (period != -1) {
                    while (mantissa.length > 0 && mantissa.endsWith(ButtonUtils.BUTTON_TEXT_0)) {
                        mantissa = mantissa.substring(0, mantissa.length - 1)
                    }
                    if (mantissa.length == period + 1) {
                        mantissa = mantissa.substring(0, mantissa.length - 1)
                    }
                }
                result = if (exponent != null) {
                    mantissa + 'e' + exponent
                } else {
                    mantissa
                }
            }
            return result
        }


    }
}