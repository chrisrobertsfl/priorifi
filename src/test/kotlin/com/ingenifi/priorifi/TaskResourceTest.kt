package com.ingenifi.priorifi

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskResourceTest(@Autowired val webTestClient: WebTestClient, @Autowired val mongoTemplate: MongoTemplate) {

    @MockBean
    lateinit var taskService: TaskService

    @BeforeEach
    fun fixture() = mongoTemplate.dropCollection("tasks")

    @Test
    fun `Create Task is successful`() {
        `when`(taskService.createTask(task.copy(id = null))).thenReturn(task)

        webTestClient.post().uri("/tasks").contentType(APPLICATION_JSON).bodyValue(CreateTaskRequest(task.name, task.description)).exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { it -> it.responseBody?.shouldBe(taskResponse) }
    }

    @Test
    fun `Create Task should create a bad request`() {
        val createdTask = task.copy(name = "")
        `when`(taskService.createTask(createdTask.copy(id = null))).thenThrow(taskNameIsMissingException)

        webTestClient.post().uri("/tasks").contentType(APPLICATION_JSON).bodyValue(CreateTaskRequest("", createdTask.description)).exchange()
            .expectStatus().isBadRequest
            .expectBody<ApiError>()
            .consumeWith { it -> it.responseBody?.shouldBe(ApiError.from(taskNameIsMissingException)) }
    }

    @Test
    fun `Get all Tasks is successful`() {
        `when`(taskService.findAll()).thenReturn(listOf(task))

        webTestClient.get().uri("/tasks").exchange()
            .expectStatus().isOk
            .expectBody<TaskListResponse>()
            .consumeWith { it.responseBody?.shouldBe(taskListResponse) }
    }

    @Test
    fun `Find Task by Id should raise exception when not found`() {
        `when`(taskService.findById("id")).thenThrow(taskNotFoundException)

        webTestClient.get().uri("/tasks/{id}", "id").exchange()
            .expectStatus().isNotFound
            .expectBody<ApiError>()
            .consumeWith { it -> it.responseBody?.shouldBe(ApiError.from(taskNotFoundException)) }
    }

    @Test
    fun `Find Task by Id should be successful`() {
        `when`(taskService.findById(task.id!!)).thenReturn(task)

        webTestClient.get().uri("/tasks/{id}", task.id).exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { it -> it.responseBody?.shouldBe(taskResponse) }
    }

    @Test
    fun `Delete Task by Id should raise exception when not found`() {
        `when`(taskService.deleteById("id")).thenThrow(taskNotFoundException)

        webTestClient.delete().uri("/tasks/{id}", "id").exchange()
            .expectStatus().isNotFound
            .expectBody<ApiError>()
            .consumeWith { it -> it.responseBody?.shouldBe(ApiError.from(taskNotFoundException)) }
    }

    @Test
    fun `Delete Task by Id should be successful`() {
        `when`(taskService.deleteById(task.id!!)).thenReturn(task)

        webTestClient.delete().uri("/tasks/{id}", task.id).exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { it -> it.responseBody?.shouldBe(taskResponse) }
    }


    @Test
    fun `Update Task should raise exception because name is not there`() {
        val update = task.copy(name = "")
        `when`(taskService.updateTask(update)).thenThrow(taskNameIsMissingException)

        val request = UpdateTaskRequest(id = update.id!!, name = update.name, description = update.description)
        webTestClient.put().uri("/tasks/{id}", "id").contentType(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isBadRequest
            .expectBody<ApiError>()
            .consumeWith { it ->
                it.responseBody?.shouldBe(ApiError.from(taskNameIsMissingException))
            }
    }

    @Test
    fun `Update Task should raise exception when not found `() {
        `when`(taskService.updateTask(task)).thenThrow(taskNotFoundException)

        val request = UpdateTaskRequest(id = task.id!!, name = task.name, description = task.description)
        webTestClient.put().uri("/tasks/{id}", "id").contentType(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isNotFound
            .expectBody<ApiError>()
            .consumeWith { it -> it.responseBody?.shouldBe(ApiError.from(taskNotFoundException)) }
    }

    @Test
    fun `Update Task should be successful`() {

        val updated = task.copy(name = "updated name")
        val request = UpdateTaskRequest(id = updated.id!!, name = updated.name, description = updated.description)

        `when`(taskService.updateTask(updated)).thenReturn(updated)
        webTestClient.put().uri("/tasks/{id}", "id").contentType(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody<TaskResponse>()
            .consumeWith { it -> it.responseBody?.shouldBe(taskResponse.copy(name = "updated name")) }
    }


    companion object {
        val task = Task("generated-id", "name", "description")
        val taskResponse = TaskResponse(task.id, task.name, task.description)
        val taskNameIsMissingException = TaskValidationException("Invalid task request", listOf(ValidationError("Task name is missing")))
        val taskNotFoundException = TaskNotFoundException(errorMessage = "Task id 'id' not found")
        val taskListResponse = TaskListResponse(listOf(task))
    }
}


