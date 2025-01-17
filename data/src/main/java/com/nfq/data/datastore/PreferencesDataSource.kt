package com.nfq.data.datastore

import androidx.datastore.core.DataStore
import com.nfq.data.datastore.model.AppConfigResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PreferencesDataSource @Inject constructor(
    private val appConfigDataStore: DataStore<AppConfigResponse>,
) {
    val appConfigDataFlow: Flow<AppConfigResponse>
        get() = appConfigDataStore.data

    suspend fun updateOnboardingStatus(isCompletedOnboarding: Boolean) {
        appConfigDataStore.updateData { it.copy(isCompletedOnboarding = isCompletedOnboarding) }
    }

    suspend fun updateNotificationSetting(isShownNotificationPermissionDialog: Boolean) {
        appConfigDataStore.updateData { it.copy(isShownNotificationPermissionDialog = isShownNotificationPermissionDialog) }
    }
}