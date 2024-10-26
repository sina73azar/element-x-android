package com.drp.data.model.motor_violation

data class MotorViolationRequest(
    val left: String,
    val right: String,
    val mobileNumber: String,
    val nationalID: String,
    val walletIdentifier: String,
    val traceNumber: String
)
