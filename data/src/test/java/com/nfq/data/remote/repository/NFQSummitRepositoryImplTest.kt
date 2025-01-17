package com.nfq.data.remote.repository

import androidx.datastore.core.DataStore
import arrow.core.Either
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.datastore.PreferencesDataSource
import com.nfq.data.datastore.model.AppConfigResponse
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.datasource.NFQSummitDataSource
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.remote.model.response.EventDayResponse
import com.nfq.data.toLocalDateTimeInMillis
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NFQSummitRepositoryImplTest {
    private lateinit var repository: NFQSummitRepositoryImpl
    private lateinit var mockDataStore: DataStore<AppConfigResponse>
    private lateinit var mockDataSource: NFQSummitDataSource
    private lateinit var preferencesDataSource: PreferencesDataSource
    private lateinit var mockEventDao: EventDao
    private lateinit var mockUserDao: UserDao

    @Before
    fun setUp() {
        mockDataStore = mockk()
        mockDataSource = mockk()
        preferencesDataSource = PreferencesDataSource(mockDataStore)
        mockEventDao = mockk()
        mockUserDao = mockk()

        repository = NFQSummitRepositoryImpl(
            mockDataSource,
            preferencesDataSource,
            mockEventDao,
            mockUserDao
        )
    }

    @Test
    fun `events flow returns all events from dao`() = runTest {
        val mockEvents = listOf(mockk<EventEntity>(), mockk<EventEntity>())
        every { mockEventDao.getUpcomingEvents(currentTime = any()) } returns flowOf(
            mockEvents
        )

        val result = repository.upcomingEvents.first()

        assertEquals(mockEvents, result)
    }

    @Test
    fun `savedEvents flow returns favorite events from dao`() = runTest {
        val mockFavoriteEvents = listOf(mockk<EventEntity>(), mockk<EventEntity>())
        every { mockEventDao.getFavoriteEvents() } returns flowOf(mockFavoriteEvents)

        val result = repository.savedEvents.first()

        assertEquals(mockFavoriteEvents, result)
    }

    @Test
    fun `authenticateWithAttendeeCode successful`() = runTest {
        val attendeeCode = "TEST123"
        val mockAuthResponse = mockk<AttendeeResponse>()

        coEvery { mockDataSource.authenticateWithAttendeeCode(attendeeCode) } returns Either.Right(
            mockAuthResponse
        )
        coEvery { mockUserDao.insertUser(any()) } returns Unit

        val result = repository.authenticateWithAttendeeCode(attendeeCode)

        assertTrue(result.isRight())
        coVerify { mockUserDao.insertUser(any()) }
    }

    @Test
    fun `authenticateWithAttendeeCode failed`() = runTest {
        val attendeeCode = "TEST123"
        val mockError = DataException.Api("Authentication failed")

        coEvery {
            mockDataSource.authenticateWithAttendeeCode(attendeeCode)
        } returns Either.Left(mockError)

        val result = repository.authenticateWithAttendeeCode(attendeeCode)

        assertTrue(result.isLeft())
        coVerify(exactly = 0) { mockUserDao.insertUser(any()) }
    }

    @Test
    fun `fetchEventActivities updates events with favorites`() = runTest {
        val latestEvents = EventActivityResponseMock.eventsList
        val favoriteEvents = EventActivityResponseMock.favoriteEvents

        coEvery { mockDataSource.getEventActivities() } returns Either.Right(latestEvents)
        coEvery { mockEventDao.getFavoriteEvents() } returns flowOf(favoriteEvents)
        coEvery { mockEventDao.insertEvents(any()) } returns Unit

        val result = repository.fetchEventActivities()

        assertTrue(result.isRight())
        coVerify {
            mockEventDao.insertEvents(match { events ->
                events.any { it.id == "1" && it.isFavorite }
            })
        }
    }

    @Test
    fun `getEventActivityByID returns event details`() = runTest {
        val eventId = "1"
        val mockEventEntity = EventActivityResponseMock.mockEventEntity

        coEvery { mockEventDao.getEventById(eventId) } returns mockEventEntity

        val result = repository.getEventActivityByID(eventId)

        assertTrue(result.isRight())
        result.map { details ->
            assertEquals(eventId, details.id)
            assertEquals("Test Event", details.name)
        }
    }

    @Test
    fun `updateFavorite successfully updates event favorite status`() = runTest {
        val eventId = "1"
        coEvery {
            mockEventDao.updateFavorite(
                eventId = eventId,
                updatedAt = any(),
                isFavorite = true
            )
        } returns Unit

        val result = repository.updateFavorite(eventId, true)

        assertTrue(result.isRight())
        coVerify { mockEventDao.updateFavorite(eventId, updatedAt = any(), isFavorite = true) }
    }

    @Test
    fun `updateOnboardingStatus updates preferences`() = runTest {
        coEvery { mockDataStore.updateData(any()) } returns AppConfigResponse(isCompletedOnboarding = true, isShownNotificationPermissionDialog = false)

        repository.updateOnboardingStatus(true)

        coVerify { mockDataStore.updateData(any()) }
    }
}

object EventActivityResponseMock {
    private val event1 = EventActivityResponse(
        id = 1,
        name = "NFQ Tech Conference",
        isFavorite = false,
        images = listOf(
            "https://example.com/event1-image1.jpg",
            "https://example.com/event1-image2.jpg"
        ),
        location = "Tech Innovation Center, San Francisco",
        timeStart = "2024-07-15T10:00:00",
        timeEnd = "2024-07-15T12:00:00",
        description = "A cutting-edge technology conference exploring the latest innovations in software development, AI, and digital transformation.",
        latitude = 37.7749,
        longitude = -122.4194,
        isTimeLate = false,
        gatherTime = "2024-07-15T09:45:00",
        gatherLocation = "Main Lobby",
        leavingTime = "2024-07-15T12:15:00",
        leavingLocation = "Tech Innovation Center Exit",
        adminNotes = "Ensure all participants have their badges",
        order = 1,
        category = null,
        genre = null,
        eventDay = EventDayResponse(
            id = 1,
            name = "Day 1",
            dateStart = "2024-07-15"
        ),
        qrCodeUrl = "https://example.com/qr/event1",
        code = "TECH2024",
        title = "NFQ Tech Conference 2024",
        quantity = 200,
        isMain = true,
        speaker = null
    )

    private val event2 = EventActivityResponse(
        id = 2,
        name = "Networking Lunch",
        isFavorite = false,
        images = listOf(
            "https://example.com/event2-image1.jpg"
        ),
        location = "Conference Dining Hall",
        timeStart = "2024-07-15T12:30:00",
        timeEnd = "2024-07-15T14:00:00",
        description = "Collaborative networking lunch for conference attendees to connect and share insights.",
        latitude = 37.7750,
        longitude = -122.4195,
        isTimeLate = false,
        gatherTime = "2024-07-15T12:25:00",
        gatherLocation = "Dining Hall Entrance",
        leavingTime = "2024-07-15T14:05:00",
        leavingLocation = "Dining Hall Exit",
        adminNotes = "Dietary restrictions to be considered",
        order = 2,
        category = null,
        genre = null,
        eventDay = EventDayResponse(
            id = 1,
            name = "Day 1",
            dateStart = "2024-07-15"
        ),
        qrCodeUrl = "https://example.com/qr/event2",
        code = "LUNCH2024",
        title = "Networking Lunch Session",
        quantity = 150,
        isMain = false,
        speaker = null
    )

    val favoriteEvents = listOf(
        EventEntity(
            id = "1",
            name = "Favorite Event",
            isFavorite = true,
            timeStart = 0L,
            description = "",
            location = "",
            latitude = 0.0,
            longitude = 0.0,
            images = emptyList(),
            isTimeLate = false,
            gatherTime = "",
            gatherLocation = "",
            leavingTime = "",
            leavingLocation = "",
            adminNotes = "",
            order = 0,
            category = null,
            genre = null,
            eventDay = null,
            qrCodeUrl = "",
            code = "",
            title = "",
            quantity = 0,
            isMain = false,
            timeEnd = 0L,
            speaker = null,
            updatedAt = 0L
        )
    )
    val mockEventEntity = EventEntity(
        id = "1",
        name = "Test Event",
        isFavorite = false,
        timeStart = "2023-01-01T10:00:00".toLocalDateTimeInMillis(),
        description = "Event description",
        location = "Test Location",
        latitude = 0.0,
        longitude = 0.0,
        images = listOf("image1.jpg"),
        isTimeLate = false,
        gatherTime = "",
        gatherLocation = "",
        leavingTime = "",
        leavingLocation = "",
        adminNotes = "",
        order = 0,
        category = null,
        genre = null,
        eventDay = null,
        qrCodeUrl = "",
        code = "",
        title = "",
        quantity = 0,
        isMain = false,
        timeEnd = 0L,
        speaker = null,
        updatedAt = 0L
    )
    val eventsList = listOf(event1, event2)
}