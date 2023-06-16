package com.ingenifi.priorifi

import arrow.core.Either.*
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskResource(private val taskService: TaskService) {

    @PostMapping
    @ResponseBody
    fun createTask(@RequestBody request: CreateTaskRequest): TaskResponse = TaskResponse(taskService.createTask(request.toTask()))

    @GetMapping
    @ResponseBody
    fun getAllTasks(): List<TaskResponse> = taskService.findAll().map { TaskResponse(it) }

    @GetMapping("/{id}")
    @ResponseBody
    fun findTaskById(@PathVariable id: String): TaskResponse = TaskResponse(taskService.findById(id))

    @DeleteMapping("/{id}")
    @ResponseBody
    fun deleteTaskById(@PathVariable id: String): TaskResponse = TaskResponse(taskService.deleteById(id))

    @PutMapping("/{id}")
    @ResponseBody
    fun updateTask(@PathVariable id: String, @RequestBody request: UpdateTaskRequest): TaskResponse = TaskResponse(taskService.updateTask(request.toTask()))

}