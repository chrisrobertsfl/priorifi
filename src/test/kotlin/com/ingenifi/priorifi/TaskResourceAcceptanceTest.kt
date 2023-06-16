package com.ingenifi.priorifi

import com.ingenifi.priorifi.IntegrationTests.asRegistry
import com.ingenifi.priorifi.IntegrationTests.dropTasks
import com.ingenifi.priorifi.IntegrationTests.mongoTestContainer
import com.ingenifi.priorifi.IntegrationTests.numberOfTasks
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TaskResourceAcceptanceTest(@Autowired val webTestClient: WebTestClient, @Autowired val mongoTemplate: MongoTemplate) {

    @Autowired
    lateinit var idGenerator: IdGenerator

    @BeforeEach
    fun fixture() {
        mongoTemplate.dropTasks()
        (idGenerator as NumericalIdGenerator).reset()
    }

    @Test
    fun `First - Create Task is successful`() {
        val response = webTestClient.post().uri("/tasks")
            .contentType(APPLICATION_JSON)
            .bodyValue(CreateTaskRequest("name virtual", "description virtual"))
            .exchange()
            .expectStatus().isOk
            .returnResult(TaskResponse::class.java)
            .responseBody
            .blockFirst()

        response shouldBe TaskResponse("1", "name virtual", "description virtual")
        mongoTemplate.numberOfTasks() shouldBe 1
    }

    @Test
    fun `Second - Create Task is successful`() {
        val response = webTestClient.post().uri("/tasks")
            .contentType(APPLICATION_JSON)
            .bodyValue(CreateTaskRequest("name virtual", "description virtual"))
            .exchange()
            .expectStatus().isOk
            .returnResult(TaskResponse::class.java)
            .responseBody
            .blockFirst()

        response shouldBe TaskResponse("1", "name virtual", "description virtual")
        mongoTemplate.numberOfTasks() shouldBe 1
    }

    companion object {
        @Container
        val mongo = mongoTestContainer()

        @JvmStatic
        @DynamicPropertySource
        fun registry(registry: DynamicPropertyRegistry) = mongo.asRegistry()
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun idGenerator(): IdGenerator = NumericalIdGenerator()
    }
}


