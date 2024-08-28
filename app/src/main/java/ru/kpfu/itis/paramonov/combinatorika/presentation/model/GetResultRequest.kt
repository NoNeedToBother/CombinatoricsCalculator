package ru.kpfu.itis.paramonov.combinatorika.presentation.model

sealed interface GetResultRequest

data class PermutationsRequest(
    val n: Int, val nVars: List<Int>? = null, val allowRepetitions: Boolean
): GetResultRequest

data class PlacementsRequest(
    val n: Int, val k: Int, val allowRepetitions: Boolean
): GetResultRequest

data class CombinationsRequest(
    val n: Int, val k: Int, val allowRepetitions: Boolean
): GetResultRequest

data class UrnSchemeRequest(
    val n : Int, val m : Int, val k : Int, val r: Int? = null
): GetResultRequest