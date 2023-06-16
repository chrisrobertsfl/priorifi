package com.ingenifi.priorifi

import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class WebClient(val webTestClient: WebTestClient, collectionName: String) {
    val path: String

    init {
        path = "/$collectionName"
    }

    fun post(request: Any, expectedResponse: Any, expectedStatus: HttpStatus = HttpStatus.OK) {
        val actualResponse = webTestClient.post().uri(path)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .returnResult(expectedResponse.javaClass)
            .responseBody
            .blockFirst()
        actualResponse shouldBe expectedResponse
    }

    fun get(expectedResponse: Any, expectedStatus: HttpStatus = HttpStatus.OK) {
        val actualResponse = webTestClient.get().uri(path)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .returnResult(expectedResponse.javaClass)
            .responseBody
            .blockFirst()
        actualResponse shouldBe expectedResponse
    }
}