# kairo-darb

DARB ("dense-ish albeit readable binary") encodes boolean lists into compact hex strings while keeping them human-readable. Designed for JWT permission lists.

## Key files
- `src/main/kotlin/kairo/darb/DarbEncoder.kt` -- `encode(List<Boolean>)` and `decode(String)` functions

## Patterns and conventions
- Format: `"23.2CB08E"` where prefix is the list length and body is hex-encoded 4-bit chunks
- Each hex character represents 4 booleans (e.g. `C` = `1100`)
- The prefix disambiguates trailing bits in the last chunk

## Foot-guns
- Changing `chunkSize` from 4 would break the encoding format
- Decode throws `IllegalArgumentException` for malformed input
