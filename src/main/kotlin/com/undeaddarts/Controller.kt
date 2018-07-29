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

    @GetMapping("/update-stat")
    fun updateStat(): Map<String, String> {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val keyFactory = datastore.newKeyFactory().setKind("PlayerStatTest")

        val entity = datastore.get(keyFactory.newKey("Isaac-27"))
        val rowMap = mutableMapOf<String, String>()
        for (key in entity.names) {
            try {
                rowMap[key] = entity.getLong(key).toString()
            } catch (e: ClassCastException) {
                try {
                    rowMap[key] = entity.getString(key)
                } catch (e: ClassCastException) {
                    rowMap[key] = entity.getDouble(key).toString()
                }
            }
        }
        return rowMap

//        val query = Query
//                .newEntityQueryBuilder()
//                .setKind("PlayerStatTest")
//                .setFilter(StructuredQuery.PropertyFilter.eq("season", req.row.season))
//                .setFilter(StructuredQuery.PropertyFilter.eq("name", req.row.name))
//                .build()
//
//        val results = datastore.run(query)
//
//        val updatedRow = results.next().also {
//            it.names.forEach { key -> it.get(key ) }
//        }
//        datastore.put(updatedRow)
//
//        val changelogRow = Entity.newBuilder().build()
//        datastore.put(changelogRow)
    }
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

//    private fun toPlayerStat(entity: Entity): PlayerStat {
//
//    }
}