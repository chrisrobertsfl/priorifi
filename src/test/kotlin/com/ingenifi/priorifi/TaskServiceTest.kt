package com.ingenifi.priorifi

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
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
        val taskValidationException = shouldThrow<TaskValidationException> { service.createTask(Task(null, "", "description")) }
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
        val exception = shouldThrow<TaskNotFoundException> { service.findById("id") }
        exception shouldBe taskNotFoundException
    }

    @Test
    fun `Find Task by Id should succeed when id is found`() {
        `when`(taskRepository.findById("id")).thenReturn(Optional.of(task))
        service.findById("id") shouldBe task
    }

    @Test
    fun `Delete Task by Id should raise exception when id is not found`() {
        val exception = shouldThrow<TaskNotFoundException> { service.deleteById("id") }
        exception shouldBe taskNotFoundException
    }

    @Test
    fun `Delete Task by Id should succeed when id is found`() {
        `when`(taskRepository.findById(task.id!!)).thenReturn(Optional.of(task))
        assertDoesNotThrow { service.findById("id") }
    }

    @Test
    fun `Update Task should raise exception when name is not there`() {
        val update = task.copy(name = "")
        `when`(taskRepository.findById(update.id!!)).thenReturn(Optional.of(task))
        val exception = shouldThrow<TaskValidationException> { service.updateTask(update) }
        exception shouldBe taskNameMissingException
    }

    @Test
    fun `Update Task should raise exception when id is not found`() {
        `when`(taskRepository.findById(task.id!!)).thenThrow(taskNotFoundException)
        val exception = shouldThrow<TaskNotFoundException> { service.updateTask(task) }
        exception shouldBe taskNotFoundException
    }

    @Test
    fun `Update Task by Id should succeed`() {
        val update = task.copy(name = "updated name")
        `when`(taskRepository.findById(update.id!!)).thenReturn(Optional.of(task))
        `when`(taskRepository.save(update)).thenReturn(update)
        service.updateTask(update) shouldBe update
    }


    companion object {
        val task = Task("id", "name", "description")
        val taskNotFoundException = TaskNotFoundException(errorMessage = "Task with id 'id' not found")
        val taskNameMissingException = TaskValidationException(errors = listOf(ValidationError("Task name is missing")))
    }
}

