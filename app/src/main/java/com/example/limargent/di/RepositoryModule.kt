package com.example.limargent.di

import com.example.limargent.Data.Categorie.CategoryProvider
import com.example.limargent.Data.Movements.MovementsProvider
import com.example.limargent.Data.Objectifs.ObjectifProvider
import com.example.limargent.Data.Preference.PreferenceProvider
import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.Categorie.CategoryRepository
import com.example.limargent.Domain.Mouvements.MovementsRepository
import com.example.limargent.Domain.Objectif.ObjectifRepository
import com.example.limargent.Domain.Preference.PreferenceRepository
import com.example.limargent.Domain.Users.UserRepository
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