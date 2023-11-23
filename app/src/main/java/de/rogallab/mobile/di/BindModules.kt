package de.rogallab.mobile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rogallab.mobile.data.repositories.PeopleRepositoryImpl
import de.rogallab.mobile.domain.IPeopleRepository
import javax.inject.Singleton

// @Binds Shothand for binding an interface type

@Module
@InstallIn(SingletonComponent::class)
interface IBindSingletonModules {
   @Binds
   @Singleton
   fun bindPeopleRepository(
      peopleRepositoryImpl: PeopleRepositoryImpl
   ): IPeopleRepository
}