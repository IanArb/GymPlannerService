package com.ianarbuckle.gymplannerservice.faultReporting.data

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FaultReportRepository : CoroutineCrudRepository<Fault, String>