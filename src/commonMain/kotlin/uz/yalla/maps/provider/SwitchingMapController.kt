package uz.yalla.maps.provider

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import uz.yalla.core.contract.MapPreferences
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.MapKind
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.provider.google.GoogleMapController
import uz.yalla.maps.provider.libre.LibreMapController
import kotlin.time.Duration.Companion.seconds

class SwitchingMapController(
    mapPreferences: MapPreferences,
    val googleController: GoogleMapController = GoogleMapController(),
    val libreController: LibreMapController = LibreMapController()
) : MapController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var activeController: MapController = googleController
    private var collectorJob: Job? = null
    private var handoffJob: Job? = null
    private var suppressStateSync: Boolean = false

    private val _cameraPosition = MutableStateFlow(CameraPosition.DEFAULT)
    override val cameraPosition = _cameraPosition.asStateFlow()

    private val _markerState = MutableStateFlow(MarkerState.INITIAL)
    override val markerState = _markerState.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    override val isReady = _isReady.asStateFlow()

    init {
        scope.launch {
            mapPreferences.mapKind.collectLatest { type ->
                val nextController = when (type) {
                    MapKind.Google -> googleController
                    MapKind.Libre -> libreController
                }
                seedNextController(nextController)
                activeController = nextController
                syncFromActive()
                collectFromActive()
            }
        }
    }

    override suspend fun moveTo(point: GeoPoint, zoom: Float) {
        activeController.moveTo(point, zoom)
    }

    override suspend fun animateTo(point: GeoPoint, zoom: Float, durationMs: Int) {
        activeController.animateTo(point, zoom, durationMs)
    }

    override suspend fun animateToWithBearing(point: GeoPoint, bearing: Float, zoom: Float, durationMs: Int) {
        activeController.animateToWithBearing(point, bearing, zoom, durationMs)
    }

    override suspend fun fitBounds(points: List<GeoPoint>, padding: PaddingValues, animate: Boolean) {
        activeController.fitBounds(points, padding, animate)
    }

    override suspend fun zoomIn() = activeController.zoomIn()

    override suspend fun zoomOut() = activeController.zoomOut()

    override suspend fun setZoom(zoom: Float) = activeController.setZoom(zoom)

    override fun setDesiredPadding(padding: PaddingValues) {
        googleController.setDesiredPadding(padding)
        libreController.setDesiredPadding(padding)
    }

    override suspend fun updatePadding(padding: PaddingValues) {
        googleController.updatePadding(padding)
        libreController.updatePadding(padding)
    }

    override fun updateMarkerState(state: MarkerState) = activeController.updateMarkerState(state)

    override fun setMarkerPosition(point: GeoPoint) = activeController.setMarkerPosition(point)

    override fun clearMarker() = activeController.clearMarker()

    override fun onMapReady() = activeController.onMapReady()

    override fun reset() {
        handoffJob?.cancel()
        suppressStateSync = false
        googleController.reset()
        libreController.reset()
        _isReady.value = false
        _markerState.value = MarkerState.INITIAL
        _cameraPosition.value = CameraPosition.DEFAULT
    }

    private fun collectFromActive() {
        collectorJob?.cancel()
        collectorJob = scope.launch {
            launch { activeController.cameraPosition.collectLatest(::updateCameraFromController) }
            launch { activeController.markerState.collectLatest(::updateMarkerFromController) }
            launch { activeController.isReady.collectLatest { _isReady.value = it } }
        }
    }

    private fun syncFromActive() {
        updateCameraFromController(activeController.cameraPosition.value)
        updateMarkerFromController(activeController.markerState.value)
        _isReady.value = activeController.isReady.value
    }

    private fun seedNextController(nextController: MapController) {
        handoffJob?.cancel()

        val preservedCamera = _cameraPosition.value
        val currentMarker = _markerState.value
        val hasPreservedState = preservedCamera != CameraPosition.DEFAULT || currentMarker != MarkerState.INITIAL
        suppressStateSync = hasPreservedState

        if (currentMarker != MarkerState.INITIAL) {
            nextController.updateMarkerState(currentMarker)
        }

        if (!hasPreservedState) return

        handoffJob = scope.launch {
            // Wait until the next provider map is ready before applying preserved camera/marker.
            withTimeoutOrNull(5.seconds) {
                nextController.isReady
                    .filter { it }
                    .first()
            }

            if (preservedCamera != CameraPosition.DEFAULT) {
                nextController.moveTo(preservedCamera.target, preservedCamera.zoom)
            }
            if (currentMarker != MarkerState.INITIAL) {
                nextController.updateMarkerState(currentMarker.copy(isMoving = false))
            }

            suppressStateSync = false
            syncFromActive()
        }
    }

    private fun updateCameraFromController(cameraPosition: CameraPosition) {
        if (suppressStateSync) return
        val current = _cameraPosition.value
        if (cameraPosition == CameraPosition.DEFAULT && current != CameraPosition.DEFAULT) return
        _cameraPosition.value = cameraPosition
    }

    private fun updateMarkerFromController(markerState: MarkerState) {
        if (suppressStateSync) return
        val current = _markerState.value
        if (markerState == MarkerState.INITIAL && current != MarkerState.INITIAL) return
        _markerState.value = markerState
    }
}
