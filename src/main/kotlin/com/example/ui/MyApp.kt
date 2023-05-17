package com.example.ui

import com.example.server.module
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornadofx.*
import java.net.ServerSocket

private var serverJob: Job? = null
private var serverEngine: ApplicationEngine? = null

class MyApp : App(MyView::class)

class MyView : View() {
    override val root = vbox {
        button("Start") {
            action {
                serverJob = CoroutineScope(Dispatchers.IO).launch {
                    serverEngine = embeddedServer(Netty, port = findFreePort(8080), module = Application::module)
                    serverEngine?.start()

                }
            }
        }

        button("Stop") {
            action {
                serverEngine?.stop(1000, 5000)
                serverJob?.cancel()
            }
        }
        
    }
}

fun isPortInUse(port: Int): Boolean {
    return try {
        ServerSocket(port).use {
            false
        }
    } catch (e: Exception) {
        true
    }
}

fun findFreePort(port: Int): Int{
    if (isPortInUse(port)){
        return findFreePort(port+1)
    }
    return port
}

