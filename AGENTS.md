# Falling Kitten Android project

## Build and test

Use JDK 17 and Android SDK 36. Configure `JAVA_HOME` and `ANDROID_HOME` for your local installation, then run:

```sh
bash ./gradlew --offline testReleaseUnitTest lintRelease bundleRelease
```

## Validation status

This branch is a draft source candidate for version 1.3 (version code 4). Host tests and Android build checks have passed, but the candidate has not been validated on a native device, signed, or submitted to Google Play. Do not treat build success as release or gameplay acceptance evidence.
