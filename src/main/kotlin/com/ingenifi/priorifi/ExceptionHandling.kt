package com.ingenifi.priorifi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(TaskValidationException::class)
    fun taskValidationExceptionHandler(exception: TaskValidationException): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError.from(exception), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun taskValidationExceptionHandler(exception: TaskNotFoundException): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError.from(exception), HttpStatus.NOT_FOUND)
    }

}

data class ApiError(val message: String, val errorMessages: List<String>) {
    companion object {
        fun from(exception: TaskValidationException): ApiError = ApiError(exception.message, exception.errors.map { it.errorMessage })
        fun from(exception: TaskNotFoundException): ApiError = ApiError(exception.message, listOf(exception.errorMessage))
    }
}

open class TaskException(override val message: String = "Something went wrong") : RuntimeException(message)

data class TaskValidationException(override val message: String = "Invalid Task", val errors: List<ValidationError>) : TaskException(message)
data class TaskNotFoundException(override val message: String = "Task Not Found", val errorMessage: String) : TaskException(message)