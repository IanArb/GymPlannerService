package com.ianarbuckle.gymplannerservice.faultReporting

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FaultReportRepository : CoroutineCrudRepository<FaultReport, String>
