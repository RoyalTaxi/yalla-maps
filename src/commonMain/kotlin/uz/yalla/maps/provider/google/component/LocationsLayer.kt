package uz.yalla.maps.provider.google.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Marker
import uz.yalla.maps.compose.rememberComposeBitmapDescriptor
import uz.yalla.maps.compose.rememberUpdatedMarkerState
import uz.yalla.maps.provider.google.toLatLng

private object MarkerColors {
    val Start = Color(0xFF562DF8)
    val Point = Color(0xFFAEAEB2)
    val Finish = Color(0xFFFF3B30)
    val BadgeBackground = Color(0xFF1C1C1E)
    val BadgeText = Color.White
    val MarkerFill = Color.White
}

private object MarkerDimens {
    val MarkerSize = 22.dp
    val BorderWidth = 6.dp
    val BadgeHeight = 28.dp
    val BadgePadding = 12.dp
}

private enum class LocationType { START, POINT, FINISH }

@Composable
fun LocationsLayer(
    arrival: Int?,
    duration: Int?,
    locations: List<GeoPoint>,
    startLabel: String? = null,
    endLabel: String? = null
) {
    if (locations.size < 2) return

    locations.forEachIndexed { index, location ->
        val type = when (index) {
            0 -> LocationType.START
            locations.lastIndex -> LocationType.FINISH
            else -> LocationType.POINT
        }

        val badgeText = when (index) {
            0 -> startLabel
            locations.lastIndex -> endLabel
            else -> null
        }

        key("location-$index") {
            LocationMarker(
                location = location,
                type = type,
                badgeText = badgeText
            )
        }
    }
}

@Composable
private fun LocationMarker(
    location: GeoPoint,
    type: LocationType,
    badgeText: String?
) {
    val icon = rememberComposeBitmapDescriptor(type, badgeText.orEmpty()) {
        LocationMarkerContent(type = type, badgeText = badgeText)
    }

    Marker(
        state = rememberUpdatedMarkerState(position = location.toLatLng()),
        icon = icon,
        anchor = Offset(0.5f, 0.5f)
    )
}

@Composable
private fun LocationMarkerContent(
    type: LocationType,
    badgeText: String?
) {
    val borderColor = when (type) {
        LocationType.START -> MarkerColors.Start
        LocationType.POINT -> MarkerColors.Point
        LocationType.FINISH -> MarkerColors.Finish
    }
    val density = LocalDensity.current
    val borderWidthPx = with(density) { MarkerDimens.BorderWidth.toPx() }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .height(MarkerDimens.BadgeHeight)
                    .background(
                        color = MarkerColors.BadgeBackground,
                        shape = RoundedCornerShape(MarkerDimens.BadgeHeight / 2)
                    )
                    .padding(horizontal = MarkerDimens.BadgePadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    style = TextStyle(
                        color = MarkerColors.BadgeText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Canvas(
            modifier = Modifier.size(MarkerDimens.MarkerSize)
        ) {
            val radius = size.minDimension / 2f
            drawCircle(color = borderColor, radius = radius)
            val innerRadius = (radius - borderWidthPx).coerceAtLeast(0f)
            if (innerRadius > 0f) {
                drawCircle(color = MarkerColors.MarkerFill, radius = innerRadius)
            }
        }

        if (badgeText != null) {
            Spacer(modifier = Modifier.height(MarkerDimens.BadgeHeight))
        }
    }
}
