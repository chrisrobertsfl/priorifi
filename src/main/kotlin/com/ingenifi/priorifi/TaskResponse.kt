package com.ingenifi.priorifi

data class TaskResponse(val id: String?, val name: String, val description: String) {
    constructor(task: Task) : this(task.id, task.name, task.description)
}
