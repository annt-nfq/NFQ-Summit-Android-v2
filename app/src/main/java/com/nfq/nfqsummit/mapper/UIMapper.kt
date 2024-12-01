package com.nfq.nfqsummit.mapper

import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import java.time.format.DateTimeFormatter

fun List<SummitEvent>.toSavedEventUIModels(): List<SavedEventUIModel> {
    return this.map { it.toSavedEventUIModel() }
}

private fun SummitEvent.toSavedEventUIModel(): SavedEventUIModel {
    return SavedEventUIModel(
        id = this.id,
        imageUrl = this.coverPhotoUrl.orEmpty(),
        name = this.name,
        date = this.start.format(DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm")),
        tag = "\uD83D\uDCBC Summit"
    )
}

fun List<SummitEvent>.toUpcomingEventUIModels(): List<UpcomingEventUIModel> {
    return this.map { it.toUpcomingEventUIModel() }
}

private fun SummitEvent.toUpcomingEventUIModel(): UpcomingEventUIModel {
    return UpcomingEventUIModel(
        id = this.id,
        name = this.name,
        imageUrl = this.coverPhotoUrl.orEmpty(),
        date = this.start.format(DateTimeFormatter.ofPattern("dd\nMMM")),
        startAndEndTime = "${this.start.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
            this.end.format(DateTimeFormatter.ofPattern("HH:mm"))
        }",
        isFavorite = this.isFavorite,
        tag = "\uD83D\uDCBC Summit"
    )
}