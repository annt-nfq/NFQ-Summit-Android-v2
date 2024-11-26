package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val blogRepository: BlogRepository
) : ViewModel() {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private var _upcomingEvents = MutableStateFlow<Response<List<UpcomingEventUIModel>>>(Response.Loading)
    var upcomingEvents : StateFlow<Response<List<UpcomingEventUIModel>>> = _upcomingEvents.asStateFlow()

    init {
        getUpcomingEvents()
    }

    private fun getUpcomingEvents() = viewModelScope.launch {
        _upcomingEvents.value = when (val response = eventRepository.getAllEvents()) {
            is Response.Success -> {
                val events = response.data?.sortedBy { it.start }?.take(3)?.map {
                    UpcomingEventUIModel(
                        id = it.id,
                        name = it.name,
                        imageUrl = it.iconUrl.orEmpty(),
                        date = it.start.format(DateTimeFormatter.ofPattern("dd\nMMM")),
                        startAndEndTime = "${it.start.format(dateTimeFormatter)} - ${it.end.format(dateTimeFormatter)}",
                        isFavorite = false,
                        tag = "\uD83D\uDCBC Summit"
                    )
                }.orEmpty()
                Response.Success(events)
            }
            is Response.Failure -> Response.Failure(response.e)
            Response.Loading -> Response.Loading
        }
    }

    val recommendedBlogs = blogRepository.getRecommendedBlogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Response.Loading
        )

    val favoriteBlogs = blogRepository.getFavoriteBlogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Response.Loading
        )

    fun markAsFavorite(favorite: Boolean, blog: Blog) {
        viewModelScope.launch {
            blogRepository.markBlogAsFavorite(blog, favorite)
        }
    }
}