package com.ingenifi.priorifi

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.ingenifi.engine.Engine
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val validationEngine: Engine) {
    fun createTask(request: Task): Either<List<ValidationError>, Task> {
        return when (val validationOrTask = validate(request)) {
            is Left -> validationOrTask
            is Right -> Right(taskRepository.insert(validationOrTask.value))
        }
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
    fun updateTask(request: Task): Either<List<ValidationError>, Task> {
        return when (val validationOrTask = validate(request)) {
            is Left -> validationOrTask
            is Right -> Right(taskRepository.save(validationOrTask.value))
        }
    }

    fun deleteById(id: String): Either<List<ValidationError>, Task> {
        val found = taskRepository.findById(id)
        return if (found.isPresent) {
            taskRepository.deleteById(id)
            Right(found.get())
        } else {
            Left(listOf(ValidationError("Cannot find task by id $id")))
        }
    }
}
