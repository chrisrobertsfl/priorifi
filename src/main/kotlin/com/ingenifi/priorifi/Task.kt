package com.ingenifi.priorifi

import org.springframework.data.mongodb.core.mapping.Document

@Document("tasks")
data class Task(var id: String?, val name: String, val description: String)