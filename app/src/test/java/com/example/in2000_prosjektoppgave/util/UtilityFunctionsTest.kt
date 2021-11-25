package com.example.in2000_prosjektoppgave.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class UtilityFunctionsTest {

    private val tolerance: Double = 1E-14

    @Test
    fun `convertToX with speed 10 and directions 4 returns expected value`() {
        val speed = 10.0
        val direction = 4.0
        val result = convertToX(speed, direction)

        val expectedResult = -0.697564737441253

        assertThat(result).isWithin(tolerance).of(expectedResult)
    }

    @Test
    fun `convertToX with speed 10 and directions 0 returns 0`() {
        val speed = 10.0
        val direction = 0.0
        val result = convertToX(speed, direction)

        val expectedResult = 0.0

        assertThat(result).isWithin(tolerance).of(expectedResult)
    }

    @Test
    fun `convertToX with speed 10 and directions 180 returns 0`() {
        val speed = 10.0
        val direction = 180.0
        val result = convertToX(speed, direction)

        val expectedResult = 0.0

        assertThat(result).isWithin(tolerance).of(expectedResult)
    }

    @Test
    fun `convertToX with speed 10 and directions 270 returns 10`() {
        val speed = 10.0
        val direction = 270.0
        val result = convertToX(speed, direction)

        val expectedResult = 10.0

        assertThat(result).isWithin(tolerance).of(expectedResult)
    }

    @Test
    fun `convertToX with speed 10 and directions 0 and 360 returns same output`() {
        val speed = 10.0

        val direction0 = 0.0
        val result0 = convertToX(speed, direction0)

        val direction360 = 360.0
        val result360 = convertToX(speed, direction360)

        assertThat(result0).isWithin(1E-14).of(result360)
    }

    @Test
    fun `convertToY with speed 10 and directions 0 returns approx -10`() {
        val speed = 10.0
        val direction = 0.0
        val result = convertToY(speed, direction)

        val expectedResult = -10.0

        assertThat(result).isWithin(tolerance).of(expectedResult)
    }

    @Test
    fun `convertToY with negative and positive direction`(){
        val speed = 1.0
        val direction1 = 0.0
        val result1 = convertToY(speed, direction1)
        val direction2 = -360.0
        val result2 = convertToY(speed, direction2)
        val direction3 = -180.0
        val result3 = convertToY(speed, direction3)
        val direction4 = 180.0
        val result4 = convertToY(speed, direction4)

        val expectedResult = -1.0

        assertThat(result1).isWithin(tolerance).of(expectedResult)
        assertThat(result2).isWithin(tolerance).of(expectedResult)
        assertThat(result3).isWithin(tolerance).of(-expectedResult)
        assertThat(result4).isWithin(tolerance).of(-expectedResult)
    }
    @Test
    fun `convertToX with negative and positive direction`(){
        val speed = 1.0
        val direction1 = 90.0
        val result1 = convertToX(speed, direction1)
        val direction2 = direction1 - 360.0
        val result2 = convertToX(speed, direction2)
        val direction3 = direction1 - 180.0
        val result3 = convertToX(speed, direction3)
        val direction4 = direction1 + 180.0
        val result4 = convertToX(speed, direction4)

        val expectedResult = -1.0

        assertThat(result1).isWithin(tolerance).of(expectedResult)
        assertThat(result2).isWithin(tolerance).of(expectedResult)
        assertThat(result3).isWithin(tolerance).of(-expectedResult)
        assertThat(result4).isWithin(tolerance).of(-expectedResult)
    }

    @Test
    fun `convertDMStoDD with decimal 10 speed 30 seconds 42 returns approx 10 point 5`() {
        val decimal = 10
        val minutes = 30
        val seconds = 42.toDouble()
        val result = convertDMStoDD(decimal, minutes, seconds)

        val expectedResult = 10.51166

        assertThat(result).isWithin(1E-4).of(expectedResult)
    }

}