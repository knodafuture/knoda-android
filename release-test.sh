rm knoda-test-release.apk
./gradlew clean assembleRelease
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore knoda-release.keystore Knoda/build/apk/Knoda-test-release-unsigned.apk Knoda
jarsigner -verify Knoda/build/apk/Knoda-test-release-unsigned.apk
/Applications/Android\ Studio.app/sdk/tools/zipalign -v 4 Knoda/build/apk/Knoda-release-unsigned.apk knoda-test-release.apk

