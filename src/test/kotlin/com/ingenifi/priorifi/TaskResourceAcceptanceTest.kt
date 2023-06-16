package com.ingenifi.priorifi

import com.mongodb.client.MongoClient
import io.kotest.assertions.asClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TaskResourceAcceptanceTest(@Autowired val webTestClient: WebTestClient, @Autowired val mongoTemplate: MongoTemplate, @Autowired val taskRepository: TaskRepository) {

    companion object {
        @Container
        val mongodbContainer = MongoDBContainer("mongo:5.0").apply {
            withExposedPorts(27017)
       }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceConfiguration(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host", mongodbContainer::getHost)
            registry.add("spring.data.mongodb.port", mongodbContainer::getFirstMappedPort)

        }

    }

    private fun insertTasks(howMany: Int) = mongoTemplate.insert((1..howMany).map { Task(null, "Task $it", "Description $it") }, "tasks")
    private fun numberOfTasks() = mongoTemplate.count(Query(), "tasks")

    @BeforeEach
    fun fixture() = mongoTemplate.dropCollection("tasks")

    @Test
    fun `Create Task is successful`() {
        webTestClient.post().uri("/tasks").contentType(APPLICATION_JSON).bodyValue(CreateTaskRequest("name virtual", "description virtual")).exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { it ->
                it.responseBody?.asClue {
                    it.name shouldBe "name virtual"
                    it.description shouldBe "description virtual"
                }
            }
        numberOfTasks() shouldBe 1
    }
}


