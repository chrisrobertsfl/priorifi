package com.ingenifi.priorifi

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
class IntegrationTests {

    @Container
    val container = MongoDBContainer("mongo:5.0").apply {
        withExposedPorts(27017)
        withEnv("MONGO_INIT_DB_ROOT_USERNAME", "mongo")
        withEnv("MONGO_INIT_DB_ROOT_PASSWORD", "mongo")
    }


}