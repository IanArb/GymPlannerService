package com.ianarbuckle.gymplannerservice.faultReporting.exception

class ReportAlreadyExistsException: Exception() {
    override val message: String = "Report already exists"
}