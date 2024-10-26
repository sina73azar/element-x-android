package com.drp.data.model.motor_violation

data class MotorViolationResponse(
    val parameters: MotorViolationParameters?,
    val status: MotorViolationStatus
)

data class MotorViolationParameters(
    val amount: Long
)

data class MotorViolationStatus(
    val description: String
)
