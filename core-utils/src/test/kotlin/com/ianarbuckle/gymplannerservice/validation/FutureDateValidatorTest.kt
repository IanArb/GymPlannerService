package com.ianarbuckle.gymplannerservice.validation

import com.google.common.truth.Truth.assertThat
import jakarta.validation.Validation
import jakarta.validation.Validator
import java.time.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FutureDateValidatorTest {
    private data class Booking(
        @field:FutureDate val date: LocalDateTime,
    )

    private lateinit var validator: Validator
    private val factory = Validation.buildDefaultValidatorFactory()

    @BeforeTest
    fun setUp() {
        validator = factory.validator
    }

    @AfterTest
    fun tearDown() {
        factory.close()
    }

    @Test
    fun `a past date produces a constraint violation`() {
        // Also guards against ConstraintDefinitionException: @FutureDate must
        // declare the mandatory groups and payload members to even be validated.
        val violations = validator.validate(Booking(LocalDateTime.now().minusDays(1)))

        assertThat(violations).hasSize(1)
        assertThat(violations.first().message).isEqualTo("Date must be in the future")
    }

    @Test
    fun `a future date produces no violations`() {
        val violations = validator.validate(Booking(LocalDateTime.now().plusDays(1)))

        assertThat(violations).isEmpty()
    }
}
