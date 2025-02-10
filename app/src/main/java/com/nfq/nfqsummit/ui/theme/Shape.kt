package com.nfq.nfqsummit.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val Shapes.bottomSheetLarge: Shape
    get() = RoundedCornerShape(
        topStart = CornerSize(32.dp),
        topEnd = CornerSize(32.dp),
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )

val Shapes.bottomSheetMedium: Shape
    get() = RoundedCornerShape(
        topStart = CornerSize(24.dp),
        topEnd = CornerSize(24.dp),
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )
