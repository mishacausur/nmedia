package ru.netology.nmedia.utils

import java.net.MalformedURLException
import java.net.URL

fun String.toURLOrNull(): URL? =
    try {
        URL(this)
    } catch (e: MalformedURLException) {
        null
    }