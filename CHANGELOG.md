# Changelog

All notable changes to this project will be documented in this file.

## [1.0.4] - 2026-02-15

### Fixed
- Stabilized runtime map-provider switching (`MapLibre -> Google`) by preserving live marker/camera state in `SwitchingMapController` during provider handoff.
- Prevented temporary fallback to `MarkerState.INITIAL`/default camera from overriding valid state after provider change.
- Removed false downstream state transitions in consumers that depend on marker/location continuity (for example, being stuck on outside flow until app restart).

