package com.ianarbuckle.gymplannerservice.faultReporting.exception

class FaultReportAlreadyExistsException : Exception() {
    override val message: String = "Report already exists"
}
