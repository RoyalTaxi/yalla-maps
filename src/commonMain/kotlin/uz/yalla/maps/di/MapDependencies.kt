package uz.yalla.maps.di

import uz.yalla.core.contract.LastLocationProvider
import uz.yalla.core.contract.LocationProvider
import uz.yalla.core.contract.MapPreferences

interface MapDependencies {
    val mapPreferences: MapPreferences
    val locationProvider: LocationProvider
    val lastLocationProvider: LastLocationProvider?
}
