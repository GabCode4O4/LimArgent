package com.example.limargent.di

import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.MovementsService
import com.example.limargent.Application.UserService
import com.example.limargent.Domain.Categorie.CategoryRepository
import com.example.limargent.Domain.Mouvements.MovementsRepository
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
        impl: CategoryService
    ): CategoryRepository
    
    @Binds
    @Singleton
    abstract fun bindMovementRepository(
        impl: MovementsService
    ): MovementsRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserService
    ): UserRepository
}