# Changelog

All notable changes to this project will be documented in this file.

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
