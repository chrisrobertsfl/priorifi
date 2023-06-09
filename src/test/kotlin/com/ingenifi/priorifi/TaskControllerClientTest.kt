package com.ingenifi.priorifi

import io.kotest.assertions.asClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerClientTest(@Autowired val webTestClient: WebTestClient) {

    @Test
    fun `should create a task`() {
        val request = CreateTaskRequest("Test task", "This is a test task")

        webTestClient.post()
            .uri("/tasks")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { result ->
                result.responseBody?.asClue {
                    it.id.shouldNotBeNull()
                    it.name shouldBe request.name
                    it.description shouldBe request.description
                }
            }
    }

    @Test
    fun `should create a bad message`() {
        val request = CreateTaskRequest("", "This is a test task")

        webTestClient.post()
            .uri("/tasks")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<List<ValidationError>>()
            .consumeWith { result ->
                result.responseBody?.asClue {
                    it.size shouldBe 1
                    it[0].errorMessage shouldBe "Task name is missing"
                }
            }
    }
}


