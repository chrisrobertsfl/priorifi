package com.ingenifi.priorifi

import org.springframework.stereotype.Component
import java.util.*

interface IdGenerator {
    fun generate() : String
}

@Component
object UUIdGenerator : IdGenerator {
    override fun generate(): String {
        return UUID.randomUUID().toString()
    }
}