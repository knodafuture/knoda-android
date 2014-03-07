rm knoda-release.apk
./gradlew verifyRelease incrementVersionCode clean assembleRelease
gradle_status=$?
if [ $gradle_status == 0 ]; then
    jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore knoda-release.keystore Knoda/build/apk/Knoda-release-unsigned.apk Knoda
    jarsigner -verify Knoda/build/apk/Knoda-release-unsigned.apk
    /Applications/Android\ Studio.app/sdk/tools/zipalign -v 4 Knoda/build/apk/Knoda-release-unsigned.apk knoda-release.apk
else
	echo "I refuse to build your release.  It appears that your configuration is pointing at test instead of prod.  Override me if you wish."
fi

