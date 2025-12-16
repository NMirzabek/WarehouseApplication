package com.example.warehouseapplication

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ApiResponse<Any>> {
        val response = ApiResponse<Any>(
            success = false,
            message = ex.message,
            errors = listOfNotNull(ex.message)
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(ex: BusinessException): ResponseEntity<ApiResponse<Any>> {
        val response = ApiResponse<Any>(
            success = false,
            message = ex.message,
            errors = listOfNotNull(ex.message)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        val errorMessages = ex.bindingResult.fieldErrors.map { fe ->
            "${fe.field}: ${fe.defaultMessage}"
        }

        val response = ApiResponse<Any>(
            success = false,
            message = "Validation failed",
            errors = errorMessages
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        ex.printStackTrace()

        val response = ApiResponse<Any>(
            success = false,
            message = "Internal server error",
            errors = listOfNotNull(ex.message)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}