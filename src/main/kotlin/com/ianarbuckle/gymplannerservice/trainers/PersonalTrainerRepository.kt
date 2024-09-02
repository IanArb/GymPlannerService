package com.ianarbuckle.gymplannerservice.trainers

import com.ianarbuckle.gymplannerservice.model.PersonalTrainer
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonalTrainerRepository : CoroutineCrudRepository<PersonalTrainer, String>