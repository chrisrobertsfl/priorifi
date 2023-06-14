package com.ingenifi.priorifi

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("tasks")
data class Task(@Id var id: String?, val name: String, val description: String)