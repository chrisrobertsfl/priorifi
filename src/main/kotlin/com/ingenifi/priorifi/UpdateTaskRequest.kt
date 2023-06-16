package com.ingenifi.priorifi

data class UpdateTaskRequest(val name: String, val description: String) {
    fun toTask(id : String): Task = Task(id = id, name = this.name, description = this.description)

    companion object {
        fun from(task : Task) : UpdateTaskRequest = UpdateTaskRequest(task.name, task.description)
    }
}
