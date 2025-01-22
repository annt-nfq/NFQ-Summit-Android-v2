package com.nfq.nfqsummit.screens.dashboard.tabs.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.CountryEnum
import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository
) : ViewModel() {
    private val loadingFlow = MutableStateFlow(false)

    val uiState = combine(
        exploreRepository.exploreBlogs(CountryEnum.THAILAND),
        exploreRepository.exploreBlogs(CountryEnum.VIETNAM),
        loadingFlow
    ) { thailand, vietnam, isLoading ->
        ExploreUIState(
            exploreThailand = thailand.map { it.toExploreItem(CountryEnum.THAILAND) },
            exploreVietnam = vietnam.map { it.toExploreItem(CountryEnum.VIETNAM) },
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = ExploreUIState(),
        started = SharingStarted.WhileSubscribed(5000)
    )

    private fun Blog.toExploreItem(countryEnum: CountryEnum) = ExploreItem(
        imageUrl = iconUrl,
        title = title,
        destination = when {
            "Transportation" in title -> {
                if (countryEnum == CountryEnum.THAILAND) "${AppDestination.Transportations.route}/${id}"
                else "${AppDestination.Blogs.route}/${id}"
            }

            "Payment" in title -> {
                "${AppDestination.Blogs.route}/${id}"
            }

            "Attraction" in title -> {
                AppDestination.Attractions.route
            }

            "Survival Kit" in title -> {
                if (countryEnum == CountryEnum.THAILAND) AppDestination.Survival.route
                else "${AppDestination.Blogs.route}/${id}"
            }

            else -> throw IllegalArgumentException("Unknown destination")
        }
    )

    init {
        fetchAttractions()
        fetchBlogs()
        configCountry(CountryEnum.VIETNAM)
    }

    private fun configCountry(countryEnum: CountryEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            exploreRepository.configCountry(countryEnum)
        }
    }

    private fun fetchAttractions() {
        viewModelScope.launch(Dispatchers.IO) {
            updateLoadingStateIfNeeded()
            exploreRepository
                .fetchAttractions()
                .onRight { loadingFlow.value = false }
                .onLeft {
                    loadingFlow.value = false
                    UserMessageManager.showMessage(it)
                }
        }
    }

    private fun fetchBlogs() {
        viewModelScope.launch(Dispatchers.IO) {
            exploreRepository
                .fetchBlogs()
                .onLeft {
                    UserMessageManager.showMessage(it)
                }
        }
    }

    private suspend fun updateLoadingStateIfNeeded() {
        val events = exploreRepository.exploreBlogs(CountryEnum.THAILAND).firstOrNull()
        loadingFlow.value = events.isNullOrEmpty()
    }

}

data class ExploreUIState(
    val exploreThailand: List<ExploreItem> = listOf(),
    val exploreVietnam: List<ExploreItem> = listOf(),
    val isLoading: Boolean = false
)