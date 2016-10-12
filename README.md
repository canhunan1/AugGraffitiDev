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

- ```GoogleMapActivity.java``` - enables Google Map, and database interaction for "C" tag unveiling.

During the activity creation, the Google Map is initiated by ```initMap()``` method. In this method, a ```MapFragment``` object is instantiated through ```findFragmentById()``` method with an argument ```R.id.mapFragment```. Then, the Google Map starts by calling ```getMapAsync()``` method. 

```
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ...
            initMap();
        }
        ...
```

```
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }
```

Once the Google Map is started and ready for next operation, the ```onMapReady``` callback function is invoked, a ```GoogleMap``` argument is paased to this method. In ```onMapReay```, and new ```GoogleApiClient``` is ```build()``` and this Api is used to connect to the Google Map server by calling ```connect()``` method. 

```
 public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleApiClinet = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClinet.connect();
        mGoogleMap.setOnMarkerClickListener(this);
    }

```

Once the server is connected, a ```onConnected()``` callback function is invoked and the map location details can be specified in this funciton, such as in the ```setPriority()``` function, you can specifed the accuracy of Google Map, here we're using high accuracy ```PRIORITY_HIGH_ACCURACY``` and ```ACCESS_FINE_LOCATION```.

```
public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ...
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.v(tag,"not get permission");
                Toast.makeText(this,"Please allow permission to access your location in the setting", Toast.LENGTH_LONG).show();
                return;
            }
            Log.v(tag,"get permission");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClinet,mLocationRequest, this);
    }
```

Once you moved and the location changed, the ```onLocationChanged(Location location)``` callback function is invoked. And the current location is passed in as ```Location``` type argument. The curent latitude and longitude are stored in ```lat``` and ```lng``` respectively with the method ```getLatitude()``` or ```getLongitude()```. The Map screen automatically follows your movement because of calling of methode ```animateCamera()``` which has an argument of ```CameraUpdate``` type. In the same time, the current location data is send to database in a ```HashMap``` format, where the keys and values are all ```String``` type. The ```HashMap``` object ```params``` contains 3 key, vaule pairs. The first key is```"email"``` which has a value retrived from the Intent sent from ```MainActivity```, the retriving function is ```intent.getStringExtra(MainActivity.EXTRA_MESSAGE)```. The second and third key is ```loc_long``` and ```loc_lat``` repectively, their values are retrived from ```location```.  The ```params``` ```HashMap``` is post to database server in a similar way discussed in previous paragrah. First, a ```RequestQueue``` is instantiated through method ```newRequestQueue``` in ```Volley```. The content that wshould be posted is in ```StringRequest``` object called ```stringRequest``` which is initiated through calling a function ```postTagNearByRequest``` which has two argumments, a ```HashMap``` and a ```url```. Then, this request is send to database server with ```add()``` method. 

```
public void onLocationChanged(Location location) {//when the location is changed
       ...
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            
            ...
            
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 19);//use cameraupate to focus the screen to the current position
                mGoogleMap.animateCamera(update);
            
            ...
            
                RequestQueue queue = Volley.newRequestQueue(this);
                //post request to get the tags nearby
                String url = "http://roblkw.com/msa/neartags.php";
                final Map<String, String> params = new HashMap<String, String>();
                if(personEmail==null) {
                    Intent intent = getIntent();
                    personEmail = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
                }
                params.put("email", personEmail);
                params.put("loc_long", lng.toString());
                params.put("loc_lat", lat.toString());
                // Request a string response from the provided URL.
                StringRequest stringRequest = postTagNearByRequest(params, url);
                queue.add(stringRequest);
            }
        }
    }
```

Once the request is posted, the response from database is collected through callback function ```onResponse```, frome where, the "C" tag data is recieved, analyized and shown on Map screen through ``` setCollectMarker```method. 


```
private StringRequest postTagNearByRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //The format of the response is "tagId,Latitude,Longitude"
                        String[] tagLoc = response.trim().split("[,]+");
                        //First we assume the format of the response never changed and the response we get is always correct
                        //then we can get the numb int numTag = 0;
                        int numTag = tagLoc.length%3==0?tagLoc.length/3:-1;
                        if(tagList == null){
                            tagList = new LinkedList<Tag>();
                        }
                        int i = numTag;
                        while(--i >=0) {//we have tags near by
                            //  double lat = Double.valueOf(tagLoc[numTag * 3 + 1]);
                            // LatLng ll = new LatLng(Double.valueOf(tagLoc[numTag * 3 + 1]), Double.valueOf(tagLoc[numTag * 3 + 2]));
                            tagList.add(new Tag(Integer.valueOf(tagLoc[i * 3]), new LatLng(Double.valueOf(tagLoc[i * 3 + 2]), Double.valueOf(tagLoc[i * 3 + 1]))));
                            setCollectMarker(tagList.get(numTag - i- 1).ll);
                        }
                        
                        ...
    
    }
```

```
private Marker setCollectMarker(LatLng ll){
        MarkerOptions optionsCollect = new MarkerOptions()
                .title("Collect")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.c))
                .position(ll)
                .snippet("Clicking me to collect a tag");
        return mGoogleMapActivity.addMarker(optionsCollect);
    }
```

- ```CameraPreview.java``` - defines how camera is opened. 
This ```CameraPreview``` class extends ```SurfaceView``` and implements ```SurfaceHolder``` interface








