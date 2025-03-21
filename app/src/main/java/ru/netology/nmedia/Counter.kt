package ru.netology.nmedia

object Counter {
    fun localizeCount(number: UInt): String {
        when {
            number < 1000u -> return number.toString()
            number < 10_000u -> {
                return String.format("%.1fK", ((number / 100u).toInt() / 10.0))
            }
            number < 1_000_000u -> {
                return String.format("%dK", (number / 1000u).toInt())
            }
            number == 1_000_000u -> return "1M"
            1_000_000u < number -> {
                return String.format("%.1fM", (number / 1_000_000u))
            }
            else -> return ""
        }
    }
}