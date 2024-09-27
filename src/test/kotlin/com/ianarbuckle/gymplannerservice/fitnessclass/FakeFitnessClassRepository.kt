package com.ianarbuckle.gymplannerservice.fitnessclass

import com.ianarbuckle.gymplannerservice.data.FitnessClassDataProvider
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.time.DayOfWeek

class FakeFitnessClassRepository : FitnessClassRepository {

    override suspend fun count(): Long {
        return FitnessClassDataProvider.createFitnessClasses().size.toLong()
    }

    override suspend fun findFitnessClassesByDayOfWeek(dayOfWeek: DayOfWeek): Flow<FitnessClass> {
        return FitnessClassDataProvider.createFitnessClasses().asFlow()
    }

    override suspend fun delete(entity: FitnessClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll(entities: Iterable<FitnessClass>) {
        TODO("Not yet implemented")
    }

    override suspend fun <S : FitnessClass> deleteAll(entityStream: Flow<S>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllById(ids: Iterable<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findAll(): Flow<FitnessClass> {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: Iterable<String>): Flow<FitnessClass> {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: Flow<String>): Flow<FitnessClass> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: String): FitnessClass? {
        TODO("Not yet implemented")
    }

    override suspend fun <S : FitnessClass> save(entity: S): FitnessClass {
        return FitnessClassDataProvider.createClass()
    }

    override fun <S : FitnessClass> saveAll(entities: Iterable<S>): Flow<S> {
        TODO("Not yet implemented")
    }

    override fun <S : FitnessClass> saveAll(entityStream: Flow<S>): Flow<S> {
        TODO("Not yet implemented")
    }
}