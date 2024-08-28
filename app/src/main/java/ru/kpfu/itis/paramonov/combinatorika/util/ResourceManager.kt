package ru.kpfu.itis.paramonov.combinatorika.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceManagerImpl @Inject constructor(
    @ApplicationContext private val ctx: Context
): ResourceManager {
    override fun getString(@StringRes stringId: Int): String = ctx.resources.getString(stringId)

    override fun getString(@StringRes stringId: Int, vararg args: Any?): String {
        return ctx.resources.getString(stringId, *args)
    }
}

interface ResourceManager {
    fun getString(stringId: Int): String

    fun getString(stringId: Int, vararg args: Any?): String
}