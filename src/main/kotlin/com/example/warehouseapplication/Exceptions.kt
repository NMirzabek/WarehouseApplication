package com.example.warehouseapplication



open class NotFoundException(message: String) : RuntimeException(message)

open class BusinessException(message: String) : RuntimeException(message)
