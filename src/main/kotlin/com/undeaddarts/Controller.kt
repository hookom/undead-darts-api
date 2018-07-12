package com.undead-darts-api

import org.springframework.web.bind.annotation.*

data class Message(val text: String, val priority: String)

@RestController
class MessageController {
    @RequestMapping("/message")
    fun message(): Message {
        return Message("Hello from Google Cloud", "High")
    }
}