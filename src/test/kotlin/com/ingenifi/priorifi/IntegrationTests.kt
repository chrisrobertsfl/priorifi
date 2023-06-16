package com.ingenifi.priorifi

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.MongoDBContainer


object IntegrationTests {
    fun mongoTestContainer(dockerImageName: String = "mongo:5.0") = MongoDBContainer(dockerImageName).apply { withExposedPorts(27017) }

    fun MongoDBContainer.asRegistry(): (DynamicPropertyRegistry) -> Unit {
        return fun(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host", ::getHost)
            registry.add("spring.data.mongodb.port", ::getFirstMappedPort)

        }
    }

    fun MongoTemplate.insertTasks(howMany: Int, modify: (Task) -> Task = { it }) = this.insert((1..howMany).map { modify(Task("$it", "Task $it", "Description $it")) }, "tasks")

    //fun MongoTemplate.insertTasks(howMany: Int) = this.insert((1..howMany).map { Task("$it", "Task $it", "Description $it") }, "tasks")
    fun MongoTemplate.numberOfTasks() = this.count(Query(), "tasks")

    fun MongoTemplate.findAllTasks() = this.findAll(Task::class.java, "tasks")
    fun MongoTemplate.dropTasks() = this.dropCollection("tasks")
}

data class NumericalIdGenerator(val startAt: Int = 1) : IdGenerator {
    var counter: Int

    init {
        counter = startAt
    }

    override fun generate(): String {
        val id = "$counter"
        counter += 1
        return id
    }

    fun reset() {
        counter = startAt
    }
}