package com.drp.data.model.vehicle_violation

data class VehicleViolationRequest(
    val left: String,
    val alphabet: String,
    val mid: String,
    val right: String,
    val mobileNumber: String,
    val nationalID: String,
    val walletIdentifier: String,
    val traceNumber: String,
)
