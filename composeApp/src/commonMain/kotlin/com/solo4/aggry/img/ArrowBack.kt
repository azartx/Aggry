package com.solo4.aggry.img

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val arrowBack: ImageVector
    get() {
        if (_arrowBack != null) return _arrowBack!!

        _arrowBack = ImageVector.Builder(
            name = "arrowBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveToRelative(313f, -440f)
                lineToRelative(224f, 224f)
                lineToRelative(-57f, 56f)
                lineToRelative(-320f, -320f)
                lineToRelative(320f, -320f)
                lineToRelative(57f, 56f)
                lineToRelative(-224f, 224f)
                horizontalLineToRelative(487f)
                verticalLineToRelative(80f)
                horizontalLineTo(313f)
                close()
            }
        }.build()

        return _arrowBack!!
    }

private var _arrowBack: ImageVector? = null