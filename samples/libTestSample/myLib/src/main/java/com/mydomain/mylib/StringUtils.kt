package com.mydomain.mylib

class StringUtils {
    companion object {
        fun hasThreeWords(text: String): Boolean {
            return text.split(" ").size == 3
        }

        fun isNumeric(text: String) = if (text.toIntOrNull() == null) false else true
    }
}