package com.undeaddarts

import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Query
import org.springframework.web.bind.annotation.*

data class Message(val text: String, val priority: String)

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
class MessageController {
    @RequestMapping("/stats")
    fun message(): Message {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("PlayerStatTest")
                .build()

        val results = datastore.run(query)

        results.forEach {
            println(it)
        }
        return Message("Hello from Google Cloud", "Low")
    }
}