package com.project.googlemaps2020.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.googlemaps2020.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        auth: FirebaseAuth,
        storageRef: StorageReference,
        firestoreRef: FirebaseFirestore
    ) = MainRepository(auth, storageRef, firestoreRef)
}