package com.ianarbuckle.gymplannerservice.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

suspend fun <T> Flow<T>.isEmpty(): Boolean = this.firstOrNull() == null
