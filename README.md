# AugGraffitiDev
## Author
By Jianan Yang and Wenhao Chen
## Summary
AugGraffiti is a mobile application on the Android for users to create their own Graffiti on there mobile device. 

## Install
Download AugGraffitiDev\MyApplication\app\app-release.apk to your phone and make sure you allow permission to use your location.
##Developement
To develope, you need to confingure the build.gradle (app) firstly. You need to change directory ```storeFile file('D:/asu/asu/EEE598/AugGraffitiDev/keystore.jks')``` to your local directory
```
debug {
            keyAlias 'AugGraffiti'
            keyPassword '940205'
            storeFile file('D:/asu/asu/EEE598/AugGraffitiDev/keystore.jks')
            storePassword '940205'
        }
```
## Usage
A complete AugGraffitiDev consists 5 different screens, i.e. Login, Map, Place, Collect and Gallery. As of now, users can only interact with Login and Map screen. The other three screens will be developed and added to this app in next step.

Step 1: To open the app, click the icon named "MyApplication". Once it is opened succeffully, the Login screen pops up.

![alt tag](https://cloud.githubusercontent.com/assets/21367763/18692829/e482ac4c-7f51-11e6-8ffd-627f12f5cba6.JPG)

Step 2: Use your "Google Account" to login by clikcing "Sigin in with Google" button. If the login succeeds, you will be directed to Map screen. In this screen, a "P" tag located in the center represnets your current location. This "P" button also allows you to place a tag on current location (this function is currently unavailable and will be implemented in next step). By tapping the "SIGN OUT" button on top of the screen, you will be directed back to Login screen.   

![alt tag](https://cloud.githubusercontent.com/assets/21367763/18692831/e67f0f54-7f51-11e6-817e-e51446127084.JPG)

Step 3: Walking around ASU campus and find tags! Tags within 50m of your current location will appear on your Map screen. This is shown in picture below in which the "C" are tags placed by other Users.

![alt tag](https://cloud.githubusercontent.com/assets/21367763/18692833/e7e49de6-7f51-11e6-9b28-32f08f4c4463.JPG)

