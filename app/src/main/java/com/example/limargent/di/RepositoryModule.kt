package com.example.limargent.di

import com.example.limargent.Data.Categorie.CategoryRepositoryImpl
import com.example.limargent.Domain.Categorie.CategoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Permet de faire le lien entre l'interface et l'implémentation genre CategorieRepositoryImple et CategorieRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
}