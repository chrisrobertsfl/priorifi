package com.ingenifi.priorifi

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.ingenifi.engine.Engine
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val validationEngine: Engine, private val idGenerator: IdGenerator) {
    fun createTask(request: Task): Task {
        val validateRequest = validateRequest(request)
        println("validateRequest = ${validateRequest}")
        println("idGenerator = ${idGenerator}")
        val id = idGenerator.generate()
        println("id = ${id}")
        val copy = validateRequest.copy(id = id)
        println("taskRepository = ${taskRepository}")
        println("copy = ${copy}")
        return taskRepository.insert(copy)
    }

    private fun validateRequest(request: Task): Task {
        val validationErrors = validationEngine
            .clear()
            .executeRules(facts = listOf(request))
            .retrieveFacts { it is ValidationError }
            .map { it as ValidationError }
        if (validationErrors.isNotEmpty()) {
            throw TaskValidationException("Invalid task request", validationErrors)
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
    fun updateTask(request: Task): Either<List<ValidationError>, Task> {
        return when (val validationOrTask = validate(request)) {
            is Left -> validationOrTask
            is Right -> Right(taskRepository.save(validationOrTask.value))
        }
    }

    fun deleteById(id: String): Either<List<ValidationError>, Task> {
        return when (val validationOrTask = findById(id)) {
            is Left -> validationOrTask
            is Right -> {
                taskRepository.deleteById(id)
                validationOrTask
            }
        }
    }

    fun findById(id: String): Either<List<ValidationError>, Task> {
        val found = taskRepository.findById(id)
        return if (found.isPresent)
            Right(found.get())
        else
            Left(listOf(ValidationError("Cannot find task by id '$id'")))
    }
    fun findId(id: String): Task = taskRepository.findById(id).orElseThrow { TaskValidationException("Task not found", listOf(ValidationError("Task with id '$id' not found"))) }

    fun deleteId(id: String) : Task  {
        val found = findId(id)
        taskRepository.deleteById(id)
        return found
    }


}
