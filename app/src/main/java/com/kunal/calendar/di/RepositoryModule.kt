package com.kunal.calendar.di

import com.kunal.calendar.data.network.apis.EventsApi
import com.kunal.calendar.data.network.datastore.DatastoreManager
import com.kunal.calendar.data.repositories.TasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTasksRepository(
        api: EventsApi,
        datastoreManager: DatastoreManager
    ): TasksRepository {
        return TasksRepository(api, datastoreManager)
    }

}