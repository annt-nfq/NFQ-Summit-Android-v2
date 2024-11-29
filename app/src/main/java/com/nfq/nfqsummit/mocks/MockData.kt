package com.nfq.nfqsummit.mocks

import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.model.Translation
import com.nfq.data.domain.model.TranslationAudio
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import java.time.LocalDate
import java.time.LocalDateTime

val mockBlog = Blog(
    id = 1,
    title = "Title 1",
    description = "Description 1",
    iconUrl = "",
    contentUrl = "https://example.com",
    attractionId = 1,
    largeImageUrl = "",
    isFavorite = false,
    isRecommended = false
)

val mockFavoriteAndRecommendedBlog = Blog(
    id = 2,
    title = "Title 2",
    description = "Description 2",
    iconUrl = "",
    contentUrl = "contentUrl",
    attractionId = 1,
    largeImageUrl = "",
    isFavorite = true,
    isRecommended = true
)

val mockFavoriteBlog = Blog(
    id = 3,
    title = "Title 3",
    description = "Description 3",
    iconUrl = "",
    contentUrl = "contentUrl",
    attractionId = 1,
    largeImageUrl = "",
    isFavorite = true,
    isRecommended = false
)

val mockRecommendedBlog = Blog(
    id = 4,
    title = "Title 4",
    description = "Description 4",
    iconUrl = "",
    contentUrl = "contentUrl",
    attractionId = 1,
    largeImageUrl = "",
    isFavorite = false,
    isRecommended = true
)

val mockEventDay1 = SummitEvent(
    id = "1",
    name = "Event 1",
    start = LocalDateTime.of(2024, 1, 6, 10, 0),
    end = LocalDateTime.of(2024, 1, 6, 11, 0),
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
    0.0, 0.0,
    "",
    "Bangkok, Thailand",
    iconUrl = "",
    speakerName = "Speaker Name",
    speakerPosition = "Speaker Position",
    tag = "\uD83D\uDCBC Summit"
)

val mockEventDay2H1 = SummitEvent(
    id = "2",
    name = "Event name",
    start = LocalDateTime.of(2024, 1, 2, 11, 0),
    end = LocalDateTime.of(2024, 1, 2, 12, 0),
    tag = "\uD83D\uDCBC Summit"
)

val mockEventDay2H2 = SummitEvent(
    id = "2",
    name = "Event name",
    start = LocalDateTime.of(2024, 1, 2, 12, 0),
    end = LocalDateTime.of(2024, 1, 2, 13, 0),
    tag = "\uD83D\uDCBC Summit"
)

val mockSpeakerEvent = SummitEvent(
    "2",
    "Event name",
    LocalDate.of(2024, 1, 1).atTime(10, 0),
    LocalDate.of(2024, 1, 1).atTime(11, 0),
    iconUrl = "",
    ordering = 1,
    speakerName = "Speaker One",
    tag = "\uD83D\uDCBC Summit"
)

val mockSpeakerEvent2 = SummitEvent(
    "2",
    "Event name",
    LocalDate.of(2024, 1, 1).atTime(12, 0),
    LocalDate.of(2024, 1, 1).atTime(12, 30),
    iconUrl = "",
    ordering = 1,
    speakerName = "Speaker Two",
    tag = "\uD83D\uDCBC Summit"
)

val mockAttraction = Attraction(
    id = 1,
    title = "Attraction Title",
    icon = ""
)

val mockTranslationAudio = TranslationAudio(
    id = 1,
    title = "Audio name",
    audioUrl = "https://example.com/audio1.mp3"
)

val mockTranslation = Translation(
    id = 1,
    title = "Title",
    audios = listOf(
        mockTranslationAudio,
        mockTranslationAudio
    )
)

val mockUpcomingEvents = listOf(
    UpcomingEventUIModel(
        id = "1",
        name = "Pre-Summit Check-in",
        date = "10\nJun",
        imageUrl = "",
        isFavorite = false,
        startAndEndTime = "10:00 - 12:00",
        tag = "\uD83D\uDCBC Summit"
    ),
    UpcomingEventUIModel(
        id = "2",
        name = "Lunch",
        date = "10\nJun",
        imageUrl = "",
        isFavorite = false,
        startAndEndTime = "12:00 - 13:00",
        tag = "\uD83D\uDCBC Summit"
    )
)

val mockSavedEvents = listOf(
    SavedEventUIModel(
        id = "1",
        imageUrl = "",
        name = "E-Commerce Conference - Thai Market",
        date = "Wed, Jun 28 •17:00",
        tag = "\uD83D\uDCBC Summit"
    ),
    SavedEventUIModel(
        id = "2",
        imageUrl = "",
        name = "E-Commerce Conference ",
        date = "Wed, Jun 28 •17:00",
        tag = "\uD83D\uDCBC Summit"
    ),
    SavedEventUIModel(
        id = "3",
        imageUrl = "",
        name = " Thai Market",
        date = "Wed, Jun 28 •17:00",
        tag = "\uD83D\uDCBC Summit"
    )
)
