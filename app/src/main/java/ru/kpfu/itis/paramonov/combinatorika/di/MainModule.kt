package ru.kpfu.itis.paramonov.combinatorika.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kpfu.itis.paramonov.combinatorika.util.ResourceManager
import ru.kpfu.itis.paramonov.combinatorika.util.ResourceManagerImpl

@InstallIn(SingletonComponent::class)
@Module
interface MainModule {

    @Binds
    fun resourceManger(impl: ResourceManagerImpl): ResourceManager
}