package ru.kpfu.itis.paramonov.combinatorika.presentation.model

enum class Formula {

    PLACEMENTS, PERMUTATIONS, COMBINATIONS, URN_SCHEME;

    override fun toString(): String {
        return name.lowercase()
            .replace("_", " ")
            .replaceFirstChar { c -> c.uppercase() }
    }

}