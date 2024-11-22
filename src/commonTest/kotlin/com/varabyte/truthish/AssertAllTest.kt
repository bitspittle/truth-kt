package com.varabyte.truthish

import com.varabyte.truthish.failure.*
import kotlin.test.Test

class AssertAllTest {
    @Test
    fun assertAllCollectsMultipleErrors() {
        val a = "A"
        val b = "B"
        val c = "C"

        assertThrows<MultipleReportsError> {
            assertAll("Just an assertAll test") {
                that(a).isEqualTo(a) // ✅
                that(a).isEqualTo(b) // ❌
                that(b).isEqualTo(b) // ✅
                that(b).isEqualTo(c) // ❌
                that(c).isEqualTo(a) // ❌
                that(c).isEqualTo(c) // ✅
                that(a).isEqualTo(c) // ❌
            }
        }.let { e ->
            assertAll {
                that(e.summary).isEqualTo("Just an assertAll test")
                that(e.reports.size).isEqualTo(4)
                // that(a).isEqualTo(b)
                with(e.reports[0].details) {
                    that(this.find(DetailsFor.EXPECTED)!!).isEqualTo(b)
                    that(this.find(DetailsFor.BUT_WAS)!!).isEqualTo(a)
                }
                // that(a).isEqualTo(c)
                with(e.reports[3].details) {
                    that(this.find(DetailsFor.EXPECTED)!!).isEqualTo(c)
                    that(this.find(DetailsFor.BUT_WAS)!!).isEqualTo(a)
                }
            }
        }
    }

    @Test
    fun assertAllWithMessageWorks() {
        assertThrows<MultipleReportsError> {
            val a = "A"
            val b = "B"
            val c = "C"

            assertAll {
                withMessage("A is equal to B").that(a).isEqualTo(b)
                withMessage("B is equal to C").that(b).isEqualTo(c)
            }
        }.let { e ->
            assertAll {
                that(e.summary).isNull()
                that(e.reports.size).isEqualTo(2)
                // that(a).isEqualTo(b)
                assertThat(e.reports[0].assertSubstrings("A is equal to B"))
                assertThat(e.reports[1].assertSubstrings("B is equal to C"))
            }
        }
    }

    class DummyComparable(private val value: Int) : Comparable<DummyComparable> {
        override fun compareTo(other: DummyComparable) = value.compareTo(other.value)
    }

    @Test
    fun assertAllTestForCoverage() {
        assertAll {
            that(null).isNull()
            that(Any()).isNotSameAs(Any())
            that(DummyComparable(10)).isGreaterThan(DummyComparable(9))
            that(true).isTrue()
            that(100.toByte()).isEqualTo(100.toByte())
            that(100.toShort()).isEqualTo(100.toShort())
            that(100).isEqualTo(100)
            that(100L).isEqualTo(100L)
            that(100.0f).isEqualTo(100.0f)
            that(100.0).isEqualTo(100.0)
            that("Hello").isEqualTo("Hello")
            that(listOf(1, 2, 3)).isEqualTo(listOf(1, 2, 3))
            that(mapOf(1 to 2, 3 to 4)).isEqualTo(mapOf(1 to 2, 3 to 4))
            that(sequenceOf(1, 2, 3)).containsAllIn(1, 2, 3)
            that(arrayOf(1, 2, 3)).isEqualTo(arrayOf(1, 2, 3))
            that(booleanArrayOf(true, false)).isEqualTo(booleanArrayOf(true, false))
            that(byteArrayOf(1, 2, 3)).isEqualTo(byteArrayOf(1, 2, 3))
            that(charArrayOf('a', 'b', 'c')).isEqualTo(charArrayOf('a', 'b', 'c'))
            that(shortArrayOf(1, 2, 3)).isEqualTo(shortArrayOf(1, 2, 3))
            that(intArrayOf(1, 2, 3)).isEqualTo(intArrayOf(1, 2, 3))
            that(longArrayOf(1, 2, 3)).isEqualTo(longArrayOf(1, 2, 3))
            that(floatArrayOf(1.0f, 2.0f, 3.0f)).isEqualTo(floatArrayOf(1.0f, 2.0f, 3.0f))
            that(doubleArrayOf(1.0, 2.0, 3.0)).isEqualTo(doubleArrayOf(1.0, 2.0, 3.0))
        }
    }
}
