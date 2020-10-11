package com.project.googlemaps2020.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.googlemaps2020.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @ActivityRetainedScoped
    @Provides
    fun provideMainRepository(
        auth: FirebaseAuth,
        storageRef: StorageReference,
        firestoreRef: FirebaseFirestore
    ) = MainRepository(auth, storageRef, firestoreRef)

    @ActivityRetainedScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)
}