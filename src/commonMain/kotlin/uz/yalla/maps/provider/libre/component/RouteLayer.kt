package uz.yalla.maps.provider.libre.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.dsl.featureCollection
import io.github.dellisd.spatialk.geojson.dsl.lineString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.value.LineCap
import org.maplibre.compose.expressions.value.LineJoin
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import uz.yalla.core.geo.GeoPoint

private object RouteConfig {
    const val LAYER_ID = "route-layer"
    val Width = 4.dp
    val Color = Color(0xFF562DF8)
}

@Composable
fun RouteLayer(route: List<GeoPoint>) {
    if (route.size < 2) return

    val source =
        rememberGeoJsonSource(
            data =
                GeoJsonData.Features(
                    featureCollection {
                        feature(
                            geometry =
                                lineString {
                                    route.forEach { point ->
                                        point(longitude = point.lng, latitude = point.lat)
                                    }
                                }
                        )
                    }
                )
        )

    LineLayer(
        id = RouteConfig.LAYER_ID,
        source = source,
        color = const(RouteConfig.Color),
        width = const(RouteConfig.Width),
        cap = const(LineCap.Round),
        join = const(LineJoin.Round)
    )
}
