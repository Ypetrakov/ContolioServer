package com.example.server.plugins

import com.example.server.MouseMove
import com.sun.glass.events.KeyEvent
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.*
import java.awt.event.InputEvent
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.util.zip.Deflater
import javax.imageio.ImageIO

val robot = Robot()

suspend fun captureImageNonBlocking(): ByteArray = withContext(Dispatchers.IO) {
    val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)

    // Get the mouse pointer location
    val cord = MouseInfo.getPointerInfo().location
    val mouseX = cord.x
    val mouseY = cord.y

    // Capture the screen image
    val image = Robot().createScreenCapture(screenRect)

    // Draw the mouse pointer on the image
    val graphics = image.createGraphics()
    graphics.color = Color.RED
    graphics.fillOval(mouseX - 10, mouseY - 10, 20, 20)
    graphics.dispose()

    // Convert the image to bytes
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(image, "jpeg", outputStream)
    outputStream.toByteArray()
}

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        extensions {
            install(WebSocketDeflateExtension) {
                compressionLevel = Deflater.DEFAULT_COMPRESSION
                compressIfBiggerThan(bytes = 4 * 1024)
            }
        }
    }
    routing {
        webSocket("/screen") {
            // Use a dedicated thread for image capture
            while (true) {
                // Throttle the frame rate to 30 FPS

                val imageBytes = withContext(Dispatchers.IO) {
                    // Use a non-blocking image capture API
                    captureImageNonBlocking()
                }
                // Use WebSocket compression
                send(Frame.Binary(true, imageBytes))

                delay(16)
            }

        }
        webSocket("/mouse") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val moveMouseDirection = Json.decodeFromString<MouseMove>(frame.readText())
                val cord = MouseInfo.getPointerInfo().location
                robot.mouseMove(
                    cord.x + (moveMouseDirection.x * 10).toInt(),
                    cord.y + (moveMouseDirection.y * 10).toInt()
                )
            }
        }
        webSocket("/command") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                when (frame.readText()) {
                    "leftClickPress" -> {
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                    };
                    "rightClickPress" -> {
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
                    };
                }

            }

        }

        webSocket("/keyboard") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue

                when (frame.readText()) {
                    "clear" -> {
                        robot.keyPress(KeyEvent.VK_BACKSPACE)
                        robot.keyRelease(KeyEvent.VK_BACKSPACE)
                    };
                    else ->{
                        val char = frame.readText().firstOrNull()
                        val keyCode = char?.let { KeyEvent.getKeyCodeForChar(it) }

                        if (keyCode != null) {
                            // Simulate a button press for thef422222222uy2222 c
                            robot.keyPress(keyCode)
                            robot.keyRelease(keyCode)
                        }
                    };
                }

            }

        }

    }

}






