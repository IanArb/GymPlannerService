package com.ianarbuckle.gymplannerservice.validation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors =
            ex.bindingResult.allErrors.associate { error ->
                val fieldError = error as FieldError
                fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
            }

        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }
}
