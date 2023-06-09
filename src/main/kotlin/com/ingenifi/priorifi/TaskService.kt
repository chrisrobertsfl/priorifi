package com.ingenifi.priorifi

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.ingenifi.engine.Engine
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val validationEngine: Engine) {
    fun createTask(request: Task): Either<List<ValidationError>, Task> {
        return when (val validationOrTask = validate(request)) {
            is Left -> validationOrTask
            is Right -> Right(taskRepository.save(validationOrTask.value))
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
}

@Repository
interface TaskRepository : MongoRepository<Task, String>
