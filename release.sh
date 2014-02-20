jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore knoda-release.keystore Knoda/build/apk/Knoda-release-unsigned.apk Knoda
jarsigner -verify Knoda/build/apk/Knoda-release-unsigned.apk
zipalign -v 4 Knoda/build/apk/Knoda-release-unsigned.apk knoda-release.apk
