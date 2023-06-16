package com.ingenifi.priorifi

data class UpdateTaskRequest(val id : String, val name: String, val description: String) {
    fun toTask(): Task = Task(id = this.id, name = this.name, description = this.description)

    companion object {
        fun from(task : Task) : UpdateTaskRequest = UpdateTaskRequest(task.id!!, task.name, task.description)
    }
}
