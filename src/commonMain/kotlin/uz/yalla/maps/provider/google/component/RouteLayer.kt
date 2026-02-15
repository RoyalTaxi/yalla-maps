package uz.yalla.maps.provider.google.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Polyline
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.provider.google.toLatLng

private val RouteWidth = 4.dp
private val RouteColor = Color(0xFF562DF8)

@Composable
fun RouteLayer(route: List<GeoPoint>) {
    if (route.size < 2) return

    val widthPx = with(LocalDensity.current) { RouteWidth.toPx() }

    Polyline(
        points = route.map { it.toLatLng() },
        color = RouteColor,
        width = widthPx,
        jointType = JointType.Round,
        startCap = Cap.Round,
        endCap = Cap.Round
    )
}
