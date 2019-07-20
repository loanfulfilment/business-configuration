package com.swapnilsankla.businessconfiguration

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.ServerAddress
import org.bson.BsonDocument
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.annotation.PostConstruct

@Component
class InsertData {

    @Value("\${mongodbHost}")
    lateinit var hostName: String

    val objectMapper = ObjectMapper()

    @PostConstruct
    fun insert() {
        val configurations = getResourceFiles("/configurations/")
        configurations.forEach { configuration ->
            run {
                val configResource = InsertData::class.java.getResource("/configurations/$configuration/config.json")
                val config = objectMapper.readValue(configResource, mutableMapOf<String, String>()::class.java)
                val documents = InsertData::class.java.getResource("/configurations/$configuration/data.json").readText()
                dropDatabase(config.getValue("dbName"))
                insert(config.getValue("dbName"), config.getValue("collectionName"), documents)
            }
        }
    }

    private fun dropDatabase(databaseName: String) {
        val mongoClient = MongoClient(ServerAddress(hostName, 27017), MongoClientOptions.builder().build())
        val database = mongoClient.getDatabase(databaseName)
        database.drop()
    }

    private fun insert(databaseName: String, collectionName: String, documents: String) {
        val mongoClient = MongoClient(ServerAddress(hostName, 27017), MongoClientOptions.builder().build())
        val database = mongoClient.getDatabase(databaseName)

        val list = objectMapper.readValue(documents, List::class.java)
        list
                .map { e -> BsonDocument.parse(objectMapper.writeValueAsString(e)) }
                .map {
                    database.getCollection(collectionName, BsonDocument::class.java)
                            .insertOne(it)
                }
    }

    fun getResourceFiles(path: String): List<String> {
        InsertData::class.java.getResourceAsStream(path).use {
            return if (it == null) emptyList()
            else BufferedReader(InputStreamReader(it)).readLines()
        }
    }
}