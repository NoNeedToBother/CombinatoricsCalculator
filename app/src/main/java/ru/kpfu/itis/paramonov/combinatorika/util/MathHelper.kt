package ru.kpfu.itis.paramonov.combinatorika.util

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

class MathHelper @Inject constructor() {

    fun factorial(n : Int) : BigInteger {
        var res = (1).toBigInteger()
        if (n == 0 || n == 1) return res

        for (i in 2 .. n) {
            res = res.times(i.toBigInteger())
        }
        return res
    }

    fun pow(n : Int, k : Int) : BigInteger {
        var res = (1).toBigInteger()
        for (i in 1 .. k) {
            res = res.times(n.toBigInteger())
        }
        return res
    }

    fun placements(n : Int, k : Int, repetitions : Boolean) : BigInteger{
        return if (repetitions) pow(n, k)
        else factorial(n).divide(factorial(n - k))
    }

    fun permutations(n: Int, repetitions: Boolean, vararg nVars: Int) : BigInteger{
        var res = factorial(n)
        if (!repetitions) return res
        else {
            for(nVar in nVars) {
                res = res.divide(factorial(nVar))
            }
            return res
        }
    }

    private fun c(n : Int, k : Int) : BigInteger {
        return factorial(n).divide(factorial(k)).divide(factorial(n - k))
    }

    fun combinations(n : Int, k : Int, repetitions: Boolean) : BigInteger {
        return if (repetitions) c(n + k - 1, k)
        else c(n, k)
    }

    fun urnScheme(n : Int, m : Int, k : Int) : BigDecimal{
        return c(m, k).toBigDecimal().divide(c(n, k).toBigDecimal(), SCALE, RoundingMode.HALF_UP)
    }

    fun urnScheme(n : Int, m : Int, k : Int, r : Int) : BigDecimal {
        return c(m, r).times(c(n - m, k - r)).toBigDecimal().divide(c(n, k).toBigDecimal(), SCALE, RoundingMode.HALF_UP)
    }

    companion object {
        private const val SCALE = 5
    }
}