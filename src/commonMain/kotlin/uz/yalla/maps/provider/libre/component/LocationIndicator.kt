package uz.yalla.maps.provider.libre.component

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import io.github.dellisd.spatialk.geojson.dsl.featureCollection
import io.github.dellisd.spatialk.geojson.dsl.point
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import uz.yalla.core.geo.GeoPoint
import uz.yalla.resources.Res
import uz.yalla.resources.ic_user_location
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

private const val ACCURACY_LAYER_SIZE = 256f
private const val DEFAULT_ACCURACY_METERS = 50.0
private val ACCURACY_COLOR = Color(0xFF562DF8)

@Composable
fun LocationIndicator(
    location: GeoPoint?,
    cameraState: CameraState,
    accuracyMeters: Double = DEFAULT_ACCURACY_METERS
) {
    location ?: return

    val source = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            featureCollection {
                feature(geometry = point(longitude = location.lng, latitude = location.lat))
            }
        )
    )

    AccuracyLayer(
        source = source,
        accuracyMeters = accuracyMeters,
        latitude = location.lat,
        cameraState = cameraState
    )

    SymbolLayer(
        id = "user-location",
        source = source,
        iconImage = image(painterResource(Res.drawable.ic_user_location)),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true)
    )
}

@Composable
private fun AccuracyLayer(
    source: org.maplibre.compose.sources.GeoJsonSource,
    accuracyMeters: Double,
    latitude: Double,
    cameraState: CameraState
) {
    val iconSize = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        snapshotFlow { cameraState.position.zoom }.collectLatest { zoom ->
            val metersPerPixel = 156543.03392 * cos(latitude * PI / 180.0) / 2.0.pow(zoom)
            val radiusPixels = accuracyMeters / metersPerPixel
            val targetSize = (radiusPixels / (ACCURACY_LAYER_SIZE / 2)).toFloat().coerceIn(0.1f, 20f)
            iconSize.snapTo(targetSize)
        }
    }

    SymbolLayer(
        id = "user-location-accuracy",
        source = source,
        iconImage = image(AccuracyPainter),
        iconSize = const(iconSize.value),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true)
    )
}

private object AccuracyPainter : Painter() {
    override val intrinsicSize = Size(ACCURACY_LAYER_SIZE, ACCURACY_LAYER_SIZE)

    override fun DrawScope.onDraw() {
        val center = Offset(ACCURACY_LAYER_SIZE / 2, ACCURACY_LAYER_SIZE / 2)
        val radius = ACCURACY_LAYER_SIZE / 2

        drawCircle(
            brush = Brush.radialGradient(
                radius = radius,
                center = center,
                colors = listOf(ACCURACY_COLOR.copy(alpha = 0.1f), ACCURACY_COLOR.copy(alpha = 0.2f))
            ),
            radius = radius,
            center = center
        )
    }
}
