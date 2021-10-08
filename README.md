# Android Image Browser

Android image browser for [Pixabay](https://pixabay.com/) made with Android SDK. 

## Features

### Layout Modes

Provides list or grid layout modes.

### Remote Configuration

Some configurations can be controlled remotely. Currently it's hosted on the GitHub pages of this repository.

### Recent Search Suggestion

Suggests keywords depending on recent search history.

## Build

This project should be built with [Android Studio](https://developer.android.com/studio).

### Dependencies

- [Fuel](https://github.com/kittinunf/fuel)
- [Coil](https://github.com/coil-kt/coil)
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin)

### Secrets

This project uses [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin). Some keys should be provided in `local.properties`.

```
PIXABAY_API_KEY=YourPixabayApiKeyHere
REMOTE_CONFIG_URL=https://nagachiang.github.io/android-image-browser/config.json
```

## Future Works

- Full [MVVM architecture](https://github.com/android/architecture-samples)
- Full image: View full image after clicking the item
- Endless scrolling: Load images in the next page while scrolling
- More information: e.g. uploader, tags, etc.
- Support other image sources