package com.ingenifi.priorifi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PriorifiApplication

fun main(args: Array<String>) {
	runApplication<PriorifiApplication>(*args)
}
