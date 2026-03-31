package com.solo4.aggry

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform