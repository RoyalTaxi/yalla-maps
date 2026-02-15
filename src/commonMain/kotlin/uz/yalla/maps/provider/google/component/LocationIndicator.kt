package uz.yalla.maps.provider.google.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Circle
import uz.yalla.maps.compose.Marker
import uz.yalla.maps.compose.rememberBitmapDescriptor
import uz.yalla.maps.compose.rememberUpdatedMarkerState
import uz.yalla.maps.provider.google.toLatLng
import uz.yalla.resources.Res
import uz.yalla.resources.ic_user_location

private val ACCURACY_COLOR = Color(0xFF562DF8)
private const val DEFAULT_ACCURACY_METERS = 50.0

@Composable
fun LocationIndicator(
    location: GeoPoint?,
    accuracyMeters: Double = DEFAULT_ACCURACY_METERS
) {
    location ?: return

    Circle(
        center = location.toLatLng(),
        radius = accuracyMeters,
        fillColor = ACCURACY_COLOR.copy(alpha = 0.2f),
        strokeColor = ACCURACY_COLOR.copy(alpha = 0.4f),
        strokeWidth = 1f
    )

    val icon = rememberBitmapDescriptor(Res.drawable.ic_user_location)
    Marker(
        state = rememberUpdatedMarkerState(position = location.toLatLng()),
        icon = icon,
        anchor = Offset(0.5f, 0.5f)
    )
}
