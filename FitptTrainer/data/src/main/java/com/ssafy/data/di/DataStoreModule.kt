package com.ssafy.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ssafy.data.datasource.TrainerDataStoreSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(USER_PREFERENCES)
            }
        )

    @Provides
    @Singleton
    fun provideTrainerDataSource(
        dataStore: DataStore<Preferences>
    ): TrainerDataStoreSource {
        return TrainerDataStoreSource(dataStore)
    }

    //@Singleton
    //@Provides
    //fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}