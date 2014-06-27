rm knoda-release.apk
./gradlew clean assembleRelease
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore knoda-release.keystore Knoda/build/outputs/apk/Knoda-release-unsigned.apk Knoda
jarsigner -verify Knoda/build/outputs/apk/Knoda-release-unsigned.apk
/Applications/Android\ Studio.app/sdk/build-tools/19.1.0/zipalign -v 4 Knoda/build/outputs/apk/Knoda-release-unsigned.apk knoda-release.apk
