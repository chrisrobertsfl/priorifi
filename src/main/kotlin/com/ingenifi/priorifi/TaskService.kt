package com.ingenifi.priorifi

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.ingenifi.engine.Engine
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val validationEngine: Engine, private val idGenerator: IdGenerator) {
    fun createTask(request: Task): Task = taskRepository.insert(validateRequest(request).copy(id = idGenerator.generate()))

    private fun validateRequest(request: Task): Task {
        val validationErrors = validationEngine
            .clear()
            .executeRules(facts = listOf(request))
            .retrieveFacts { it is ValidationError }
            .map { it as ValidationError }
        if (validationErrors.isNotEmpty()) {
            throw TaskValidationException(errors = validationErrors)
        }
        return request
    }

    private fun validate(request: Task): Either<List<ValidationError>, Task> {
        val validationErrors = validationEngine
            .clear()
            .executeRules(facts = listOf(request))
            .retrieveFacts { it is ValidationError }
            .map { it as ValidationError }
        return if (validationErrors.isEmpty()) Right(request) else Left(validationErrors)
    }

    fun findAll(): List<Task> = taskRepository.findAll()
    fun updateTask(request: Task): Task {
        validateRequest(request)
        findById(request.id!!)
        return taskRepository.save(request)
    }

    fun findById(id: String): Task = taskRepository.findById(id).orElseThrow { TaskNotFoundException(errorMessage = "Task with id '$id' not found") }

    fun deleteById(id: String): Task {
        val found = findById(id)
        taskRepository.deleteById(id)
        return found
    }
}
