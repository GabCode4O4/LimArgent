package com.example.limargent.di

import com.example.limargent.data.categorie.CategoryProvider
import com.example.limargent.data.mouvements.MovementsProvider
import com.example.limargent.data.objectif.ObjectifProvider
import com.example.limargent.data.preference.PreferenceProvider
import com.example.limargent.data.user.UserProvider

import com.example.limargent.domain.categorie.CategoryRepository
import com.example.limargent.domain.mouvements.MovementsRepository
import com.example.limargent.domain.objectif.ObjectifRepository
import com.example.limargent.domain.preferences.PreferenceRepository
import com.example.limargent.domain.utilisateur.UserRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryProvider
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindMovementRepository(
        impl: MovementsProvider
    ): MovementsRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserProvider
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(
        impl: PreferenceProvider
    ): PreferenceRepository

    @Binds
    @Singleton
    abstract fun bindObjectifRepository(
        impl: ObjectifProvider
        ): ObjectifRepository
}
