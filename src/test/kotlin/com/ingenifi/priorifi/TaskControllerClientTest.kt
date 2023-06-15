package com.ingenifi.priorifi

import io.kotest.assertions.asClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerClientTest(@Autowired val webTestClient: WebTestClient, @Autowired val mongoTemplate: MongoTemplate) {
    private fun insertTasks(howMany: Int) = mongoTemplate.insert((1..howMany).map { Task(null, "Task $it", "Description $it") }, "tasks")
    private fun numberOfTasks() = mongoTemplate.count(Query(), "tasks")

    @BeforeEach
    fun fixture() = mongoTemplate.dropCollection("tasks")

    @Test
    fun `should create a task`() {
        val request = CreateTaskRequest("name", "This is a test task")
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
        numberOfTasks() shouldBe 1
    }

    @Test
    fun `should create a bad message`() {
        val request = CreateTaskRequest("", "description")
        webTestClient.post().uri("/tasks").contentType(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isBadRequest
            .expectBody<ApiError>()
            .consumeWith { it -> it.responseBody?.shouldBe(ApiError("Invalid task request", listOf("Task name is missing"))) }
        numberOfTasks() shouldBe 0
    }

    @Test
    fun `should retrieve all tasks`() {
        insertTasks(5)
        webTestClient.get().uri("/tasks").exchange()
            .expectStatus().isOk
            .expectBody<List<TaskResponse>>()
            .consumeWith { result ->
                result.responseBody?.asClue {
                    it.size shouldBe 5
                }
            }
    }

    @Test
    fun `should update task`() {
        val inserted = insertTasks(1).first()
        val request = UpdateTaskRequest(inserted.id!!, inserted.name, inserted.description + " now updated")
        webTestClient.put().uri("/tasks/{id}", inserted.id).contentType(APPLICATION_JSON).bodyValue(request).exchange()
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
    fun `should find task by id`() {

    }

    @Test
    fun `should delete task`() {
        val inserted = insertTasks(1).first()
        val taskId = inserted.id!!
        webTestClient.delete().uri("/tasks/{id}", taskId).exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { result ->
                result.responseBody?.asClue {
                    it.id.shouldNotBeNull()
                    it.name shouldBe "Task 1"
                    it.description shouldBe "Description 1"
                }
            }
    }
}


