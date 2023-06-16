package com.ingenifi.priorifi

import com.ingenifi.priorifi.IntegrationTests.asRegistry
import com.ingenifi.priorifi.IntegrationTests.dropTasks
import com.ingenifi.priorifi.IntegrationTests.findAllTasks
import com.ingenifi.priorifi.IntegrationTests.insertTasks
import com.ingenifi.priorifi.IntegrationTests.mongoTestContainer
import com.ingenifi.priorifi.IntegrationTests.numberOfTasks
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TaskResourceAcceptanceTest(@Autowired val webTestClient: WebTestClient, @Autowired val mongodb: MongoTemplate) {

    @Autowired
    lateinit var idGenerator: IdGenerator

    val client = WebClient(webTestClient, "tasks")

    @BeforeEach
    fun fixture() {
        mongodb.dropTasks()
        (idGenerator as NumericalIdGenerator).reset()
    }

    @Test
    fun `Create Task is successful`() = assertAll(
        { client.post(CreateTaskRequest("name virtual", "description virtual"), TaskResponse("1", "name virtual", "description virtual")) },
        { mongodb.numberOfTasks() shouldBe 1 })

    @Test
    fun `Create Task should create a bad request`() = assertAll(
        { client.post(CreateTaskRequest("", "description"), ApiError.from(taskNameIsMissing), BAD_REQUEST) },
        { mongodb.numberOfTasks() shouldBe 0 })

    @Test
    fun `Get all Tasks is successful`() {
        mongodb.insertTasks(1)
        assertAll("Get all Tasks is successful",
            { client.get(TaskListResponse(mongodb.findAllTasks())) },
            { mongodb.numberOfTasks() shouldBe 1 })
    }

    @Test
    fun `Find Task by Id should raise exception when not found`() = client.get("id", ApiError.from(taskWithIdNotFound), NOT_FOUND)

    @Test
    fun `Find Task by Id should be successful`() {
        mongodb.insertTasks(1)
        client.get("1", mongodb.findAllTasks().first())
    }

    @Test
    fun `Delete Task by Id should raise exception when not found`() = client.delete("id", ApiError.from(taskWithIdNotFound), NOT_FOUND)

    @Test
    fun `Delete Task by Id should be successful`() {
        mongodb.insertTasks(1)
        assertAll("Delete Task by Id should be successful",
            { client.delete("1", mongodb.findAllTasks().first()) },
            { mongodb.numberOfTasks() shouldBe 0 })
    }

    @Test
    fun `Update Task should raise exception because name is not there`() = client.put("1", UpdateTaskRequest("", "description"), ApiError.from(taskNameIsMissing), BAD_REQUEST)


    @Test
    fun `Update Task should raise exception when not found`() {
        mongodb.insertTasks(1)
        assertAll(
            "Update Task should raise exception when not found",
            { client.put("id", UpdateTaskRequest.from(mongodb.findAllTasks().first()), ApiError.from(taskWithIdNotFound), NOT_FOUND) },
            { mongodb.numberOfTasks() shouldBe 1 })
    }


    @Test
    fun `Update Task should be successful`() {
        mongodb.insertTasks(1)
        val updated = mongodb.findAllTasks().first().copy(description = "updated description")
        assertAll("Update Task should be successful",
            { client.put("1", UpdateTaskRequest.from(updated), TaskResponse(updated)) },
            { mongodb.numberOfTasks() shouldBe 1 })
    }

    companion object {
        @Container
        val mongo = mongoTestContainer()

        @JvmStatic
        @DynamicPropertySource
        fun registry(registry: DynamicPropertyRegistry) = mongo.asRegistry()

        val taskWithIdNotFound = TaskNotFoundException(errorMessage = "Task with id 'id' not found")
        val taskNameIsMissing = TaskValidationException("Invalid Task", listOf(ValidationError("Task name is missing")))
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun idGenerator(): IdGenerator = NumericalIdGenerator()
    }
}


