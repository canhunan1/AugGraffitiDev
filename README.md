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

![alt tag](https://cloud.githubusercontent.com/assets/21367763/18692829/e482ac4c-7f51-11e6-8ffd-627f12f5cba6.JPG) Login screen

Step 2: Use your "Google Account" to login by clikcing "Sigin in with Google" button. If the login succeeds, you will be directed to Map screen. In this screen, a "P" tag located in the center represnets your current location. This "P" button also allows you to place a tag on current location (this function is currently unavailable and will be implemented in next step). By tapping the "SIGN OUT" button on top of the screen, you will be directed back to Login screen.   

![alt tag](https://cloud.githubusercontent.com/assets/21367763/18692831/e67f0f54-7f51-11e6-817e-e51446127084.JPG) Map screen

Step 3: Walking around ASU campus and find tags! Tags within 50m of your current location will appear on your Map screen. This is shown in picture below in which the "C" are tags placed by other Users. Further steps regarding how to place tags and collect tags will be added here in next step.

![alt tag](https://cloud.githubusercontent.com/assets/21367763/18692833/e7e49de6-7f51-11e6-9b28-32f08f4c4463.JPG) Map screen with tags

## App Details
This app is developed in Android Studio. The entire codes is composed of two major parts - .xml and .java file. The .xml files define the layouts of user interface while the .java files form the backbone of this application and enable the functionalies.

The .xml files are stored in ```app\src\main\res\layout``` folder:

- ```activity_main.xml``` - decribes the Login screen, in which the "Sign in with Google" is defined as below
```
<com.google.android.gms.common.SignInButton
    android:id="@+id/login"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom">
</com.google.android.gms.common.SignInButton>

```

- ```activity_google_map.xml``` - decribes the Map screen, in which the "Googe Map" is contained in a ```fragment``` view
```
<fragment
   android:layout_width="match_parent"
   android:layout_height="422dp"
   android:name="com.google.android.gms.maps.MapFragment"
   android:id="@+id/mapFragment"
   android:layout_alignParentTop="true"
   android:layout_weight="1.21" />
```

The .java files are stored in ```app\src\main\java\com\example\jianan\auggraffiti``` folder:

- ```MainActivity.java``` - enables Google Sign-In, database interactions and GoogleMapActivity initiation.

Once the app icon is tapped, the app is created by calling ```onCreate``` callback function. During the creation period, it create a  ```GoogleSignInOptions``` object ```signInOptions``` which configures sign-in to request users basic sign-in information, here we request user's email for sign-in by calling ```.requestEmail()``` methods. In the same time, a ``` GoogleApiClient``` object is instantiated as ```googleApiClient``` to access Google's Sign-In API, the options are specified in ```signInOptions``` argument. Once the Sign-In button is clicked, the ```onClick`` callback function is invoked and it starts the Google Sign-In activity.
```
protected void onCreate(Bundle savedInstanceState) {
        ...
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        ...

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, REQUEST_CODE);
            }
        });
    }
```

The Google Sign-In results are rerived by ```onActivityResult``` callback function. The Sign-In results are stored in ```result``` which is a ```GoogleSignInResult``` object. If Sign-In succeeds (```result.isSuccess()```), the sign-in person's personal email is returned through ```getEmail()``` method.  In the next step, the personal email is post to database. In this process,  a request queue is first initiated by calling a ```Volley``` method ```newRequestQueue()``` with an argument ```FragmentActivity``` type. Then a ```StringRequest``` object ```stringRequest``` is added to the queue to talk to database. The ```StringRequest``` is instantiated by calling a ```postStringRequest``` function. 

```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            //to make sure continue to the next screen if the sign failed for unknown reason
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                personEmail = account.getEmail();

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://roblkw.com/msa/login.php";
                final Map<String, String> params = new HashMap<String, String>();
                params.put("email", personEmail);
                
                // Request a string response from the provided URL.
                StringRequest stringRequest = postStringRequest(params, url);
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
            else{
                Status status= result.getStatus();
                Toast.makeText(this,status.toString(), Toast.LENGTH_LONG).show();
            }

        }
    }
```

In ```postStringRequest```  function, it post a ```<"email",personalEmail>``` hashmap onto the specified ```url``` and the response is retrived by a ```onResponse``` callback function. If the database login succeeds, it replies a ```String 0```. Once ```"0"``` is received, a ```sendMessage()``` method is invoked. This method starts an ```Intent``` to open the ```GoogleMapActivity.class``` activity and it also passess a ```String``` argument named ```personalEmail``` to that activity for furtuer use.

```
 private StringRequest postStringRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("0")) // when get 0, log in successfully
                            sendMessage(personEmail);
                    }
                    ...
    ```
    
    ```
        public void sendMessage(String message) {
        Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        }
    ```

