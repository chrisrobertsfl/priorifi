package com.ingenifi.priorifi

import arrow.core.Either.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@RequestBody request: Mono<CreateTaskRequest>): Mono<ResponseEntity<Any>> = request
        .map { it.toTask() }
        .flatMap { taskService.createTask(it) }
        .map {
            when (it) {
                is Right -> ok(TaskResponse(it.value))
                is Left -> badRequest().body(it.value)
            }
        }

}