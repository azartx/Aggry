package com.solo4.aggry.img

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val VectorIcons.PushPin: ImageVector
    get() {
        if (_Push_pin != null) return _Push_pin!!

        _Push_pin = ImageVector.Builder(
            name = "push_pin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color.Transparent)
                ) {
                    moveTo(0f, 0f)
                    horizontalLineTo(24f)
                    verticalLineTo(24f)
                    horizontalLineTo(0f)
                    verticalLineTo(0f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(14f, 4f)
                    verticalLineToRelative(5f)
                    curveToRelative(0f, 1.12f, 0.37f, 2.16f, 1f, 3f)
                    horizontalLineTo(9f)
                    curveToRelative(0.65f, -0.86f, 1f, -1.9f, 1f, -3f)
                    verticalLineTo(4f)
                    horizontalLineTo(14f)
                    moveTo(17f, 2f)
                    horizontalLineTo(7f)
                    curveTo(6.45f, 2f, 6f, 2.45f, 6f, 3f)
                    curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                    curveToRelative(0f, 0f, 0f, 0f, 0f, 0f)
                    lineToRelative(1f, 0f)
                    verticalLineToRelative(5f)
                    curveToRelative(0f, 1.66f, -1.34f, 3f, -3f, 3f)
                    verticalLineToRelative(2f)
                    horizontalLineToRelative(5.97f)
                    verticalLineToRelative(7f)
                    lineToRelative(1f, 1f)
                    lineToRelative(1f, -1f)
                    verticalLineToRelative(-7f)
                    horizontalLineTo(19f)
                    verticalLineToRelative(-2f)
                    curveToRelative(0f, 0f, 0f, 0f, 0f, 0f)
                    curveToRelative(-1.66f, 0f, -3f, -1.34f, -3f, -3f)
                    verticalLineTo(4f)
                    lineToRelative(1f, 0f)
                    curveToRelative(0f, 0f, 0f, 0f, 0f, 0f)
                    curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                    curveTo(18f, 2.45f, 17.55f, 2f, 17f, 2f)
                    lineTo(17f, 2f)
                    close()
                }
            }
        }.build()

        return _Push_pin!!
    }

val VectorIcons.PushPinFilled: ImageVector
    get() {
        if (_Push_pin_filled != null) return _Push_pin_filled!!

        _Push_pin_filled = ImageVector.Builder(
            name = "push_pin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color.Transparent)
                ) {
                    moveTo(0f, 0f)
                    horizontalLineTo(24f)
                    verticalLineTo(24f)
                    horizontalLineTo(0f)
                    verticalLineTo(0f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(16f, 9f)
                    verticalLineTo(4f)
                    lineToRelative(1f, 0f)
                    curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                    verticalLineToRelative(0f)
                    curveToRelative(0f, -0.55f, -0.45f, -1f, -1f, -1f)
                    horizontalLineTo(7f)
                    curveTo(6.45f, 2f, 6f, 2.45f, 6f, 3f)
                    verticalLineToRelative(0f)
                    curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                    lineToRelative(1f, 0f)
                    verticalLineToRelative(5f)
                    curveToRelative(0f, 1.66f, -1.34f, 3f, -3f, 3f)
                    horizontalLineToRelative(0f)
                    verticalLineToRelative(2f)
                    horizontalLineToRelative(5.97f)
                    verticalLineToRelative(7f)
                    lineToRelative(1f, 1f)
                    lineToRelative(1f, -1f)
                    verticalLineToRelative(-7f)
                    horizontalLineTo(19f)
                    verticalLineToRelative(-2f)
                    horizontalLineToRelative(0f)
                    curveTo(17.34f, 12f, 16f, 10.66f, 16f, 9f)
                    close()
                }
            }
        }.build()

        return _Push_pin_filled!!
    }

private var _Push_pin_filled: ImageVector? = null

private var _Push_pin: ImageVector? = null