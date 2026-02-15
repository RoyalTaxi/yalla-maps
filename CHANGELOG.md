# Changelog

All notable changes to this project will be documented in this file.

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
