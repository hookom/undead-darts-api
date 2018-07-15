package com.undeaddarts

import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Query
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

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
class Controller {

    @GetMapping("/stats/{season}")
    fun getStatsBySeason(@PathVariable season: String): List<PlayerStat> {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("PlayerStatTest")
                .setFilter(PropertyFilter.eq("season", season))
                .build()

        return datastore.run(query)
    }

    @GetMapping("/changelog")
    fun getChangelog(): String {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("ChangelogTest")
                .setLimit(20)
                .setOrderBy(OrderBy.desc("timestamp"))
                .build()

        val results = datastore.run(query)
    }

    @GetMapping("/seasons")
    fun getSeasons(): List<String> {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newProjectionEntityQueryBuilder()
                .setKind("PlayerStatTest")
                .setProjection("season")
                .setDistinctOn("season")
                .build()

        val seasons = datastore.run(query)
            .first
            .map { row -> row.season }

        return seasons
    }

    @PostMapping("/add-player")
    fun addPlayer(@RequestBody req: CreatePlayerRequest) {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        datastore.put(
            Entity("PlayerStatTest").also {
                // set fields to req fields
            })
    }

    @PostMapping("/add-season")
    fun addSeason(@RequestBody req: CreateSeasonRequest) {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        req.names.forEach { name ->
            datastore.put(
                Entity("PlayerStatTest").also {
                    setProperty("statversion", req.statversion)
                    setProperty("name", name)
                    setProperty("season", req.id)
                    // set other stat fields to 0
                })
        }
    }

    @PostMapping("/update-stat")
    fun updateStat(@RequestBody req: UpdateStatRequest) {
        val datastore = DatastoreOptions
                .newBuilder()
                .setProjectId("undead-darts-1")
                .build()
                .service

        val query = Query
                .newEntityQueryBuilder()
                .setKind("PlayerStatTest")
                .setFilter(PropertyFilter.eq("season", req.row.season))
                .setFilter(PropertyFilter.eq("name", req.row.name))
                .build()

        val results = datastore.run(query)

        datastore.put(
            Entity("PlayerStatTest").also {
                setKey(results[0].getKey())

                // set fields to req.row
            }
        )

        datastore.put(
            Entity("ChangelogTest").also {
                // set fields to req fields
            })
    }
}