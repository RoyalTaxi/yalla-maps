# Changelog

All notable changes to this project will be documented in this file.

## [1.0.13] - 2026-02-20

### Fixed
- Updated map fallback point to `MapConstants.BOBUR_SQUARE` (`40.761746, 72.351894`) across Google and MapLibre providers (lite/extended).
- Used Bobur Square as the hard fallback when no cached last location is available.
- Preserved cached last-location-first startup behavior while replacing the final fallback coordinate.

## [1.0.11] - 2026-02-17

### Fixed
- Changed no-location map fallback target from `UZBEKISTAN_CENTER` to `ANDIJAN_CENTER` in both providers (Google/MapLibre, lite/extended), so denied-permission startup no longer opens at Uzbekistan center.
- Added explicit `MapConstants.ANDIJAN_CENTER` (`40.7821, 72.3442`) for a stable no-permission fallback location.

## [1.0.10] - 2026-02-17

### Fixed
- Preserved active camera padding during provider handoff in `SwitchingMapController` and reapplied it before restored camera movement, preventing misplaced centers after map switching.
- Applied padding updates only on the currently active provider while still propagating desired padding to both providers, removing inactive-provider side effects during map switches.
- Cleared stale programmatic recenter state on map rebind/idle in both providers to prevent old targets from overriding fresh padding on recenter.
- Normalized padding equality checks to semantic value comparisons (instead of object equality) across controllers and Android Google camera sync, preventing duplicate padding reapplication and wrong placements.
- Reduced unnecessary iOS Google padding resets by only mutating `view.padding` when effective inset values actually change.

## [1.0.9] - 2026-02-17

### Fixed
- Added explicit map theme propagation (`ThemeKind.Light`, `ThemeKind.Dark`, `ThemeKind.System`) through `MapDependencies` so provider theming follows app preference, not only system UI mode.
- Updated Google Maps rendering to apply explicit theme selection on Android (`LIGHT`, `DARK`, `FOLLOW_SYSTEM`) and iOS (`Light`, `Dark`, `Unspecified`) instead of hard-wiring system dark detection.
- Updated MapLibre theme resolution to honor explicit app theme and only fall back to system dark mode when `ThemeKind.System` is selected.

## [1.0.8] - 2026-02-16

### Added
- Added optional `startMarkerLabel` and `endMarkerLabel` to `ExtendedMap.Content` and forwarded them through `SwitchingMapProvider`.
- Enabled both Google and MapLibre extended maps to render marker badge labels from the new API when `showMarkerLabels` is enabled.

## [1.0.7] - 2026-02-15

### Fixed
- Re-released the recenter reliability fix from `1.0.6` with no behavioral changes.
- Ensured complete multi-platform artifact publication after a partial upload conflict on the previous patch version.

## [1.0.6] - 2026-02-15

### Fixed
- Fixed missed recenter after padding updates when a programmatic camera target is active and the camera is already idle.
- Ensured queued recenter executes immediately in both providers (`GoogleMapController`, `LibreMapController`) instead of waiting for a future idle callback that may never fire.
- Restored consistent recenter behavior when entering Taxi flow without destination across Google Maps and MapLibre providers.

## [1.0.5] - 2026-02-16

### Fixed
- Hardened map-provider handoff in `SwitchingMapController` so transient camera/marker emissions from the newly-mounted provider cannot overwrite preserved runtime state.
- Re-applied preserved camera and marker state after provider readiness to keep focus/location flows stable when switching (`MapLibre -> Google` and vice versa).
- Prevented false downstream sheet transitions caused by temporary provider-reset states during runtime map switching.

## [1.0.4] - 2026-02-15

### Fixed
- Stabilized runtime map-provider switching (`MapLibre -> Google`) by preserving live marker/camera state in `SwitchingMapController` during provider handoff.
- Prevented temporary fallback to `MarkerState.INITIAL`/default camera from overriding valid state after provider change.
- Removed false downstream state transitions in consumers that depend on marker/location continuity (for example, being stuck on outside flow until app restart).
