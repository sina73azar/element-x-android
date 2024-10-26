package com.drp.data.model.vehicle_violation

data class VehicleViolationResponse(
    val parameters: VehicleViolationParameters?,
    val status: VehicleViolationStatus,
)

data class VehicleViolationParameters(
    val amount: Long
)

data class VehicleViolationStatus(
    val description: String
)
