package com.ingenifi.priorifi

data class CreateTaskRequest(val name: String, val description: String) {
    fun toTask(): Task = Task(id = null, name = this.name, description = this.description)
}