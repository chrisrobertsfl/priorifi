package com.ingenifi.priorifi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(TaskService.TaskValidationException::class)
    fun taskValidationExceptionHandler(exception : TaskService.TaskValidationException): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError(exception.message, exception.errors.map { it.errorMessage }), HttpStatus.BAD_REQUEST)
    }
}