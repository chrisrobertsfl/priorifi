package com.ingenifi.priorifi

import arrow.core.Either
import com.ingenifi.engine.ClasspathResource
import com.ingenifi.engine.Engine
import com.ingenifi.engine.Option
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TaskService {
    fun createTask(request: Task): Mono<Either<List<ValidationError>, Task>> {
        val validationErrors = Engine(
            ruleResources = listOf(ClasspathResource("task-service-validations.drl")),
            facts = listOf(request),
            options = listOf(Option.TRACK_RULES, Option.SHOW_FACTS)
        )
            .executeRules()
            .retrieveFacts { it is ValidationError }
            .map { it as ValidationError }

        return Mono.just(
            if (validationErrors.isEmpty())
                Either.Right(Task("1", request.name, request.description))
            else Either.Left(validationErrors)
        )

    }
}