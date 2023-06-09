package com.ingenifi.priorifi

import arrow.core.Either.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@RequestBody request: CreateTaskRequest): ResponseEntity<Any> =
        when (val task = taskService.createTask(request.toTask())) {
            is Right -> ok(TaskResponse(task.value))
            is Left -> badRequest().body(task.value)
        }

}