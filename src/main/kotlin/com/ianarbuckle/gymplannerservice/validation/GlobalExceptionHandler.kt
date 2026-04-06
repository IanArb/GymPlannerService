package com.ianarbuckle.gymplannerservice.validation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationExceptions(
        ex: WebExchangeBindException
    ): ResponseEntity<Map<String, String?>> {
        val errors =
            ex.bindingResult.allErrors.associate { error ->
                val fieldError = error as FieldError
                fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
            }

        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleDecodingExceptions(
        ex: ServerWebInputException
    ): ResponseEntity<Map<String, String?>> {
        val message = ex.cause?.cause?.message ?: ex.reason ?: "Invalid request body"
        return ResponseEntity(mapOf("error" to message), HttpStatus.BAD_REQUEST)
    }
}
