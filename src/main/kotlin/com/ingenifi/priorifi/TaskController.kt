package com.ingenifi.priorifi

import arrow.core.Either.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {


    @PostMapping
    fun createTask(@RequestBody request: CreateTaskRequest): ResponseEntity<Any> =
        when (val task = taskService.createTask(request.toTask())) {
            is Right -> ok(TaskResponse(task.value))
            is Left -> badRequest().body(task.value)
        }

    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> = ok(taskService.findAll().map { TaskResponse(it) })

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody request: UpdateTaskRequest): ResponseEntity<Any> {
        val updatedTask = request.toTask()
        return when (val task = taskService.updateTask(updatedTask)) {
            is Right -> ok(TaskResponse(task.value))
            is Left -> badRequest().body(task.value)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: String): ResponseEntity<Any> {
        return when (val task = taskService.deleteById(id)) {
            is Right -> ok(TaskResponse(task.value))
            is Left -> badRequest().body(task.value)
        }
    }
}