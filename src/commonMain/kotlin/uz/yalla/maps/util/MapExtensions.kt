package uz.yalla.maps.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.LayoutDirection
import io.github.dellisd.spatialk.geojson.BoundingBox
import io.github.dellisd.spatialk.geojson.Position
import uz.yalla.core.geo.GeoPoint
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// ============================================
// Position Conversions
// ============================================

fun GeoPoint.toPosition(): Position = Position(longitude = lng, latitude = lat)

fun Position.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

fun Pair<Double, Double>.toPosition(): Position = Position(latitude = first, longitude = second)

fun Pair<Double, Double>.toGeoPoint(): GeoPoint = GeoPoint(first, second)

fun Pair<Double, Double>.isValid(): Boolean = first != 0.0 && second != 0.0

fun GeoPoint.isValid(): Boolean = lat != 0.0 && lng != 0.0

// ============================================
// Bounding Box
// ============================================

fun List<GeoPoint>.toBoundingBox(): BoundingBox =
    when {
        isEmpty() -> BoundingBox(emptyList())
        else ->
            BoundingBox(
                west = minOf { it.lng },
                south = minOf { it.lat },
                east = maxOf { it.lng },
                north = maxOf { it.lat }
            )
    }

// ============================================
// Padding Operations
// ============================================

infix operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    PaddingValues(
        top = calculateTopPadding() + other.calculateTopPadding(),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
        start = calculateLeftPadding(LayoutDirection.Ltr) + other.calculateLeftPadding(LayoutDirection.Ltr),
        end = calculateRightPadding(LayoutDirection.Ltr) + other.calculateRightPadding(LayoutDirection.Ltr)
    )

// ============================================
// Geo Calculations
// ============================================

private const val EARTH_RADIUS_KM = 6371.0

fun haversineDistance(
    from: GeoPoint,
    to: GeoPoint
): Double =
    haversineDistance(
        lat1 = from.lat,
        lng1 = from.lng,
        lat2 = to.lat,
        lng2 = to.lng
    )

fun haversineDistance(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double
): Double {
    val dLat = (lat2 - lat1).toRadians()
    val dLng = (lng2 - lng1).toRadians()
    val a =
        sin(dLat / 2).pow(2) +
            cos(lat1.toRadians()) * cos(lat2.toRadians()) *
            sin(dLng / 2).pow(2)
    return EARTH_RADIUS_KM * 2 * atan2(sqrt(a), sqrt(1 - a))
}

fun normalizeHeading(heading: Float): Float {
    val normalized = heading % 360
    return if (normalized < 0) normalized + 360 else normalized
}

private fun Double.toRadians(): Double = this * (PI / 180.0)
