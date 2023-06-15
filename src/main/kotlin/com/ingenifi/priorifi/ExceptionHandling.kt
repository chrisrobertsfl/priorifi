package com.ingenifi.priorifi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(TaskValidationException::class)
    fun taskValidationExceptionHandler(exception : TaskValidationException): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError.from(exception), HttpStatus.BAD_REQUEST)
    }
}

data class ApiError(val message : String, val errorMessages : List<String>) {
    companion object {
        fun from(taskValidationException: TaskValidationException): ApiError = ApiError(taskValidationException.message, taskValidationException.errors.map { it.errorMessage })
    }
}
data class TaskValidationException(override val message: String = "Something went wrong", val errors: List<ValidationError>) : RuntimeException(message)