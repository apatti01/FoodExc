package com.example.foodexc.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import com.example.foodexc.util.Constants.Companion.DEFAULT_CUISINE_TYPE
import com.example.foodexc.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodexc.util.Constants.Companion.PREFERENCES_CUISINE_TYPE
import com.example.foodexc.util.Constants.Companion.PREFERENCES_CUISINE_TYPE_ID
import com.example.foodexc.util.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.example.foodexc.util.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.example.foodexc.util.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val selectedMealType = preferencesKey<String>(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = preferencesKey<Int>(PREFERENCES_MEAL_TYPE_ID)
        val selectedCuisineType = preferencesKey<String>(PREFERENCES_CUISINE_TYPE)
        val selectedCuisineTypeId = preferencesKey<Int>(PREFERENCES_CUISINE_TYPE_ID)
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(name = PREFERENCES_NAME)

    suspend fun saveMealAndCuisineType(
        mealType: String,
        mealTypeId: Int,
        cuisineType: String,
        cuisineTypeId: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMealType] = mealType
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedCuisineType] = cuisineType
            preferences[PreferenceKeys.selectedCuisineTypeId] = cuisineTypeId
        }
    }

    val readMealAndCuisineType: Flow<MealAndCuisineType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        // if there is any value on the specific key we will return the string on the right (elvis operator)
        .map { preferences ->
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedCuisineType = preferences[PreferenceKeys.selectedCuisineType] ?: DEFAULT_CUISINE_TYPE
            val selectedCuisineTypeId = preferences[PreferenceKeys.selectedCuisineTypeId] ?: 0
            MealAndCuisineType(
                selectedMealType,
                selectedMealTypeId,
                selectedCuisineType,
                selectedCuisineTypeId
            )
        }

}

data class MealAndCuisineType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedCuisineType: String,
    val selectedCuisineTypeId: Int
)