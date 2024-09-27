package com.ianarbuckle.gymplannerservice.trainers.data

import com.ianarbuckle.gymplannerservice.clients.data.PersonalTrainer
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonalTrainerRepository : CoroutineCrudRepository<PersonalTrainer, String>
