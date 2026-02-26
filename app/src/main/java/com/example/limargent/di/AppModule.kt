package com.example.limargent.di

import com.example.limargent.Application.CategoryService
import com.example.limargent.Data.Categorie.CategoryProvider
import com.example.limargent.Data.Categorie.CategoryRepositoryImpl
import com.example.limargent.Domain.Categorie.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCategoryProvider(): CategoryProvider {
        return CategoryProvider()
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryProvider: CategoryProvider): CategoryRepository {
        return CategoryRepositoryImpl(categoryProvider)
    }

    @Provides
    @Singleton
    fun provideCategoryService(categoryRepository: CategoryRepository): CategoryService {
        return CategoryService(categoryRepository)
    }
}