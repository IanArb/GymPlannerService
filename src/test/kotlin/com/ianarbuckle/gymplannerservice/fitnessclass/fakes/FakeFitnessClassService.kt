package com.ianarbuckle.gymplannerservice.fitnessclass.fakes

import com.ianarbuckle.gymplannerservice.mocks.FitnessClassDataProvider
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFitnessClassService : FitnessClassesService {

    override suspend fun fitnessClasses(): Flow<FitnessClass> {
        return flowOf(FitnessClassDataProvider.createClass())
    }

    override suspend fun fitnessClassesByDayOfWeek(dayOfWeek: String): Flow<FitnessClass> {
        return flowOf(FitnessClassDataProvider.createClass())
    }

    override suspend fun createFitnessClass(fitnessClass: FitnessClass): FitnessClass {
        return FitnessClassDataProvider.createClass()
    }

    override suspend fun updateFitnessClass(fitnessClass: FitnessClass) {
        //noop
    }

    override suspend fun deleteFitnessClassById(id: String) {
        //noop
    }
}