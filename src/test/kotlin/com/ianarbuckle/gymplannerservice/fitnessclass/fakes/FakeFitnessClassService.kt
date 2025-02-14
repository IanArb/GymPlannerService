package com.ianarbuckle.gymplannerservice.fitnessclass.fakes

import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import com.ianarbuckle.gymplannerservice.mocks.FitnessClassDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFitnessClassService : FitnessClassesService {
    override suspend fun fitnessClasses(): Flow<FitnessClass> =
        flowOf(FitnessClassDataProvider.createClass())

    override suspend fun fitnessClassesByDayOfWeek(dayOfWeek: String): Flow<FitnessClass> =
        flowOf(FitnessClassDataProvider.createClass())

    override suspend fun createFitnessClass(fitnessClass: FitnessClass): FitnessClass =
        FitnessClassDataProvider.createClass()

    override suspend fun updateFitnessClass(fitnessClass: FitnessClass) {
        // noop
    }

    override suspend fun deleteFitnessClassById(id: String) {
        // noop
    }
}
