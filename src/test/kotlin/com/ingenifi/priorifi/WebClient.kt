package com.ingenifi.priorifi

import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.reflect.KClass

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

    fun get(id : String, expectedResponse : Any, expectedStatus : HttpStatus = HttpStatus.OK) {
        val actualResponse = webTestClient.get().uri("$path/{id}", id)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .returnResult(expectedResponse.javaClass)
            .responseBody
            .blockFirst()
        actualResponse shouldBe expectedResponse
    }


    fun delete(id : String, expectedResponse : Any, expectedStatus : HttpStatus = HttpStatus.OK) {
        val actualResponse = webTestClient.delete().uri("$path/{id}", id)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .returnResult(expectedResponse.javaClass)
            .responseBody
            .blockFirst()
        actualResponse shouldBe expectedResponse
    }


    fun put(id : String, request: Any, expectedResponse: Any, expectedStatus: HttpStatus = HttpStatus.OK) {
        val actualResponse = webTestClient.put().uri("$path/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .returnResult(expectedResponse.javaClass)
            .responseBody
            .blockFirst()
        actualResponse shouldBe expectedResponse
    }

}