package io.perlmutter.ben.models

data class ErrorResponse(val status: Int, val message: String = "Error")
