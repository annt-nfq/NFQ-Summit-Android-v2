package com.nfq.nfqsummit.screens.survival

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.Translation
import com.nfq.data.domain.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurvivalViewModel @Inject constructor(
    private val translationRepository: TranslationRepository
) : ViewModel() {

    var translations by mutableStateOf<List<Translation>?>(null)
        private set

    fun getTranslations() {
        viewModelScope.launch {
            when (val response = translationRepository.getAllTranslations()) {
                is Response.Success -> {
                    translations = response.data
                }

                is Response.Loading -> {
                }

                is Response.Failure -> {
                }
            }
        }
    }
}