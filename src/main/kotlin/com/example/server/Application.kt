package com.example.server

import com.example.server.plugins.configureRouting
import com.example.server.plugins.configureSockets
import io.ktor.server.application.*

fun Application.module() {
    configureSockets()
    configureRouting()
}
