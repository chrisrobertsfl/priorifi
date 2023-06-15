package com.ingenifi.priorifi

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
class TaskServiceTest(@Autowired val service: TaskService) {

    @MockBean
    lateinit var taskRepository: TaskRepository

    @MockBean
    lateinit var idGenerator: IdGenerator

    @Test
    fun `Create Task should raise exception when name is not there`() {
        val taskValidationException = assertThrows<TaskValidationException> { service.createTask(Task(null, "", "description")) }
        taskValidationException shouldBe taskNameMissingException
    }

    @Test
    fun `Create Task should succeed when name is there`() {
        `when`(idGenerator.generate()).thenReturn("generated-id")
        val createdTask = Task("generated-id", "name", "description")
        `when`(taskRepository.insert(createdTask)).thenReturn(createdTask)
        service.createTask(Task(null, createdTask.name, createdTask.description)) shouldBe createdTask
    }
    @Test
    fun `Find Task by Id should raise exception when id is not found`() {
        val exception = assertThrows<TaskNotFoundException> { service.findById("id") }
        exception shouldBe taskNotFoundException
    }

    @Test
    fun `Find Task by Id should succeed when id is found`() {
        val found = Task("id", "name", "description")
        `when`(taskRepository.findById("id")).thenReturn(Optional.of(found))
        service.findById("id") shouldBe found
    }

    @Test
    fun `Delete Task by Id should raise exception when id is not found`() {
        val exception = assertThrows<TaskNotFoundException> { service.deleteById("id") }
        exception shouldBe taskNotFoundException
    }

    companion object {
        val taskNotFoundException = TaskNotFoundException(errorMessage = "Task with id 'id' not found")
        val taskNameMissingException = TaskValidationException(errors = listOf(ValidationError("Task name is missing")))
    }
}

