package com.undeaddarts

import com.google.cloud.datastore.*
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

data class CreatePlayersRequest(
        val names: List<String>,
        val id: String,
        val statversion: String)

data class PlayerStatChange(
        val field: String,
        val value: Int,
        val name: String,
        val season: String
)

data class Changelog(
        val message: String,
        val timestamp: String
)

data class UpdateStatRequest(
        val stat: PlayerStatChange,
        val changelog: Changelog
)

@CrossOrigin(origins = [
    "http://localhost:3000",
    "https://undead-darts-1.appspot.com",
    "http://undead-darts-1.appspot.com",
    "http://undeaddarts.com",
    "https://undeaddarts.com"])
@RestController
class Controller {

    @GetMapping("/stats")
    fun getStats(request: HttpServletRequest): List<Map<String, String>> {
        var testEnvSuffix = determineSuffix(request)

        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("PlayerStat" + testEnvSuffix)
                .build()

        return mapQueryResultsToListOfRows(datastore.run(query))
    }

    @GetMapping("/changelog")
    fun getChangelog(request: HttpServletRequest): List<Map<String, String>> {
        var testEnvSuffix = determineSuffix(request)
        
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("Changelog" + testEnvSuffix)
                .setLimit(20)
                .setOrderBy(StructuredQuery.OrderBy.desc("timestamp"))
                .build()

        return mapQueryResultsToListOfRows(datastore.run(query))
    }

    @PostMapping("/update-stat")
    fun updateStat(@RequestBody body: UpdateStatRequest, request: HttpServletRequest) {
        var testEnvSuffix = determineSuffix(request)
        
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val keyFactory = datastore.newKeyFactory().setKind("PlayerStat" + testEnvSuffix)

        val keyString = body.stat.name + "-" + body.stat.season
        val updatedRow = Entity.newBuilder(datastore.get(keyFactory.newKey(keyString)))
                .apply {
                    set(body.stat.field, body.stat.value.toLong()).build()
                }
                .build()

        datastore.update(updatedRow)

        datastore.put(
                Entity.newBuilder(keyFactory.setKind("Changelog" + testEnvSuffix).newKey())
                        .set("message", body.changelog.message)
                        .set("timestamp", body.changelog.timestamp)
                        .build())
    }

    @PostMapping("/add-season")
    fun addSeason(@RequestBody body: CreatePlayersRequest, request: HttpServletRequest) {
        var testEnvSuffix = determineSuffix(request)
        
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val keyFactory = datastore.newKeyFactory().setKind("PlayerStat" + testEnvSuffix)

        body.names.forEach { name ->
            val keyString = name + "-" + body.id
            datastore.put(
                    Entity.newBuilder(keyFactory.newKey(keyString))
                            .set("name", name)
                            .set("season", body.id)
                            .set("statversion", body.statversion)
                            .build())
        }
    }

    @PostMapping("/add-player")
    fun addPlayer(@RequestBody body: CreatePlayersRequest, request: HttpServletRequest) {
        var testEnvSuffix = determineSuffix(request)
        
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val keyFactory = datastore.newKeyFactory().setKind("PlayerStat" + testEnvSuffix)

        body.names.forEach { name ->
            val keyString = name + "-" + body.id
            datastore.put(
                    Entity.newBuilder(keyFactory.newKey(keyString))
                            .set("name", name)
                            .set("season", body.id)
                            .set("statversion", body.statversion)
                            .build())
        }
    }

    private fun mapQueryResultsToListOfRows(results: QueryResults<Entity>): List<Map<String, String>> {
        val resultsMapped = mutableListOf<Map<String, String>>()

        while (results.hasNext()) {
            val queryRow = results.next()
            val rowMap = mutableMapOf<String, String>()
            for (key in queryRow.names) {
                try {
                    rowMap[key] = queryRow.getLong(key).toString()
                } catch (e: ClassCastException) {
                    try {
                        rowMap[key] = queryRow.getString(key)
                    } catch (e: ClassCastException) {
                        rowMap[key] = queryRow.getDouble(key).toString()
                    }
                }
            }
            resultsMapped.add(rowMap)
        }

        return resultsMapped
    }

    private fun determineSuffix(request: HttpServletRequest): String {
        return if (request.requestURL.contains("localhost")) {
            "Test"
        } else { "" }
    }
}
