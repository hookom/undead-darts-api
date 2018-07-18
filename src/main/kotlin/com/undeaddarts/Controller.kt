package com.undeaddarts

import com.google.cloud.datastore.*
import org.springframework.web.bind.annotation.*

data class PlayerStat(
    val name: String,
    val statversion: String,
    val season: String)

data class Change(
    val message: String,
    val timestamp: String)

data class CreatePlayerRequest(val name: String)

data class CreateSeasonRequest(
    val names: List<String>,
    val id: String,
    val statversion: String)

data class UpdateStatRequest(
    val row: PlayerStat,
    val change: String,
    val timestamp: String)

@CrossOrigin(origins = [
    "http://localhost:3000",
    "https://undead-darts-1.appspot.com",
    "http://undead-darts-1.appspot.com"])
@RestController
class Controller {

    @GetMapping("/stats/{season}")
    fun getStatsBySeason(@PathVariable season: String): List<Map<String, String>> {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("PlayerStatTest")
                .setFilter(StructuredQuery.PropertyFilter.eq("season", season))
                .build()

        return mapQueryResultsToListOfRows(datastore.run(query))
    }

    @GetMapping("/stats")
    fun getStats(): List<Map<String, String>> {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("PlayerStatTest")
                .build()

        return mapQueryResultsToListOfRows(datastore.run(query))
    }

    @GetMapping("/changelog")
    fun getChangelog(): List<Map<String, String>> {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("ChangelogTest")
                .setLimit(20)
                .setOrderBy(StructuredQuery.OrderBy.desc("timestamp"))
                .build()

        return mapQueryResultsToListOfRows(datastore.run(query))
    }
//
//    @GetMapping("/seasons")
//    fun getSeasons(): List<String> {
//        val datastore = DatastoreOptions
//                .newBuilder()
//                .setProjectId("undead-darts-1")
//                .build()
//                .service
//
//        val query = Query
//                .newProjectionEntityQueryBuilder()
//                .setKind("PlayerStatTest")
//                .setProjection("season")
//                .setDistinctOn("season")
//                .build()
//
//        return mapQueryResultsToListOfRows(datastore.run(query))
//                .map { row -> row.season }
//    }
//
//    @PostMapping("/add-player")
//    fun addPlayer(@RequestBody req: CreatePlayerRequest) {
//        val datastore = DatastoreOptions
//                .newBuilder()
//                .setProjectId("undead-darts-1")
//                .build()
//                .service
//
//        datastore.put(
//            Entity("PlayerStatTest").also {
//                // set fields to req fields
//            })
//    }
//
//    @PostMapping("/add-season")
//    fun addSeason(@RequestBody req: CreateSeasonRequest) {
//        val datastore = DatastoreOptions
//                .newBuilder()
//                .setProjectId("undead-darts-1")
//                .build()
//                .service
//
//        req.names.forEach { name ->
//            datastore.put(
//                Entity("PlayerStatTest").also {
//                    setProperty("statversion", req.statversion)
//                    setProperty("name", name)
//                    setProperty("season", req.id)
//                    // set other stat fields to 0
//                })
//        }
//    }
//
//    @PostMapping("/update-stat")
//    fun updateStat(@RequestBody req: UpdateStatRequest) {
//        val datastore = DatastoreOptions
//                .newBuilder()
//                .setProjectId("undead-darts-1")
//                .build()
//                .service
//
//        val query = Query
//                .newEntityQueryBuilder()
//                .setKind("PlayerStatTest")
//                .setFilter(PropertyFilter.eq("season", req.row.season))
//                .setFilter(PropertyFilter.eq("name", req.row.name))
//                .build()
//
//        val results = datastore.run(query)
//
//        datastore.put(
//            Entity("PlayerStatTest").also {
//                setKey(results[0].getKey())
//
//                // set fields to req.row
//            }
//        )
//
//        datastore.put(
//            Entity("ChangelogTest").also {
//                // set fields to req fields
//            })
//    }

    private fun mapQueryResultsToListOfRows(results: QueryResults<Entity>): List<Map<String, String>> {
        val resultsMapped = mutableListOf<Map<String, String>>()

        while (results.hasNext()) {
            val queryRow = results.next()
            val rowMap = mutableMapOf<String, String>()
            for (key in queryRow.names) {
                try {
                    rowMap[key] = queryRow.getLong(key).toString()
                } catch (e: ClassCastException) {
                    rowMap[key] = queryRow.getString(key)
                }
            }
            resultsMapped.add(rowMap)
        }

        return resultsMapped
    }
}