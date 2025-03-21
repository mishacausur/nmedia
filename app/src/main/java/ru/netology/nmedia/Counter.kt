package ru.netology.nmedia

object Counter {
    fun localizeCount(number: UInt): String {
        return when {
            number < 1000u -> number.toString()
            number < 10_000u -> {
                String.format("%.1f", ((number / 100u).toInt() / 10.0)).let {
                    if (it.last().toString() == "0") {
                        return it.dropLast(2) + "K"
                    }
                    return it + "K"
                }
            }

            number < 1_000_000u -> {
                String.format("%dK", (number / 1000u).toInt())
            }

            number == 1_000_000u -> "1M"
            1_000_000u < number -> {
                String.format("%.1f", ((number / 100u).toInt() / 10.0)).let {
                    if (it.last().toString() == "0") {
                        return it.dropLast(2) + "M"
                    }
                    return it + "M"
                }
            }

            else -> ""
        }
    }
}