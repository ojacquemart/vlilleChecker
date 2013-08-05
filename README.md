# vlilleChecker  Android App

This repository contains the source code for the Vlille Checker Android app.

<a href="https://play.google.com/store/apps/details?id=com.vlille.checker" alt="Download from Google Play">
  <img src="http://www.android.com/images/brand/android_app_on_play_large.png">
</a>

This project is an unofficial android app for the Vlille service in Lille, France.

![image1](https://lh4.ggpht.com/1L90ewgpWg8bmlgvaakrPn9BHlWm92ksXWxgoGlcAeVSCgWaEkdOWUybIvRt5Puq7g=h230)&nbsp;![image2]
(https://lh3.ggpht.com/bI12FdIk17-sr8yyF6ZgYnccfCTjii7Ap-bgMDjIF9gJfIfS0rEgosA_xLXURjFZ_yoO=h230)&nbsp;![image3](https://lh4.ggpht.com/VfyOGdfC8TG8AvhFeeF8EMaAX3c7ctKi8Mb025Y8Lb0Zj7n9AMrkFLXCwOBwSrPyrXM=h230)&nbsp;![image4](https://lh5.ggpht.com/qoHa8hENdPBdNlunKBa7SvycxSQF_E3mMXNcZbmdJIuBgftIfE193i0Wh7KgzQTBnSs=h230)


## Versions

* 01/08/2013: v2.2
* 10/21/2012: v2.1
* 10/20/2012: v2.0
* 06/13/2012: v1.1
* 06/04/2012: v1.0

## Libraries

This project uses the following open source libraries:

* [ACRA - Application Crash Report for Android](http://code.google.com/p/acra/)
* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
* [PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh)
* [OpenStreetMapDroid](http://code.google.com/p/osmdroid/)
* [Apache - Commons Lang](http://commons.apache.org/lang/)
* [Apache - Commons Collections](http://commons.apache.org/collections/)
* [GPSEmulator to simulate GPS location](http://code.google.com/p/android-gps-emulator/)

## Installation

### Android Studio users

Import libraries/actionbarpulltorefresh-library-0.6.aar in your local .m2 repository, waiting for an aar version of the library
* mvn install:install-file -Dfile=%LOCAL_REPO_DIR%\libraries\actionbarpulltorefresh-library-0.6.aar -DgroupId=com.github.chrisbanes.actionbarpulltorefresh -DartifactId=library -Dversion=0.6 -Dpackaging=aar
* import the build.gradle

#### Eclipse users

* import the app/pom.xml and reference ActionBarSherlock as a library.
