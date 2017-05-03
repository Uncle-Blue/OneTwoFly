package com.uncle_blue.nasa_hackthon.onetwofly;

import android.Manifest.permission;
import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uncle_blue.nasa_hackthon.onetwofly.model.Airport;
import com.uncle_blue.nasa_hackthon.onetwofly.model.AqiStation;
import com.uncle_blue.nasa_hackthon.onetwofly.util.network.OtfApiHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LottieAnimationView mStartPageAnimView, loading_LAV;
    private GoogleMap mMap;
    private Toolbar toolbar;    //頂部按鈕條
    private DrawerLayout drawerLayout;  //左側選單
    private NavigationView navigationView;
    private FloatingActionMenu fam; //漂浮按鈕選單
    private LinearLayout loadingPage, inputPage;
    private View infoInputView;
    private PopupWindow infoInputPopupWindow;   // PopupWindow for adding some info to send to server
    private int height_44dp, height_32dp;
    private int width_44dp,  width_32dp;
    private BitmapDrawable bD_aqi_best, bD_aqi_better, bD_aqi_worse, bD_aqi_worst, bD_aqi_unknown, bD_airport_tower, bD_arrive;
    private Bitmap bM_aqi_best, bM_aqi_better, bM_aqi_worse, bM_aqi_worst, bM_aqi_unknown, bM_airport_tower, bM_arrive;

    private final String[] LOCATION_PERMS = {android.Manifest.permission.ACCESS_FINE_LOCATION};
    private final int INITIAL_REQUEST = 1337;
    private final int LOCATION_REQUEST = INITIAL_REQUEST + 1;

    private Location myCurrentLocation;
    private LatLng defultLatLng_Taiwan = new LatLng(23.5491, 119.8999);
    private LatLng myCurrentLatLng = defultLatLng_Taiwan;
    private GoogleApiClient myGoogleApiClient;
    private boolean gMapFirstStart = true;
    private int cameraHeight_aqi = AQI_LOCAL;
    private final static int AQI_LOCAL = 12;
    private final static int AQI_GLOBAL = 4;

    private final static int STATE_FRIGHT = 666;
    private final static int STATE_AQI_STATION = 667;
    private int currentState = STATE_FRIGHT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initView();
        initToolbar();
    }

    private void initView() {
        height_44dp = (int)convertDpToPixel(44, this);
        width_44dp = (int)convertDpToPixel(44, this);
        height_32dp = (int)convertDpToPixel(32, this);
        width_32dp = (int)convertDpToPixel(32, this);

        bD_aqi_best =(BitmapDrawable)getResources().getDrawable(R.drawable.aqi_best, getTheme());
        bD_aqi_better =(BitmapDrawable)getResources().getDrawable(R.drawable.aqi_better, getTheme());
        bD_aqi_worse =(BitmapDrawable)getResources().getDrawable(R.drawable.aqi_worse, getTheme());
        bD_aqi_worst =(BitmapDrawable)getResources().getDrawable(R.drawable.aqi_worst, getTheme());
        bD_aqi_unknown =(BitmapDrawable)getResources().getDrawable(R.drawable.aqi_unknow, getTheme());
        bD_airport_tower =(BitmapDrawable)getResources().getDrawable(R.drawable.airport_tower, getTheme());
        bD_arrive =(BitmapDrawable)getResources().getDrawable(R.drawable.arrival, getTheme());

        bM_aqi_best = Bitmap.createScaledBitmap(bD_aqi_best.getBitmap(), width_32dp, height_32dp, false);
        bM_aqi_better = Bitmap.createScaledBitmap(bD_aqi_better.getBitmap(), width_32dp, height_32dp, false);
        bM_aqi_worse = Bitmap.createScaledBitmap(bD_aqi_worse.getBitmap(), width_32dp, height_32dp, false);
        bM_aqi_worst = Bitmap.createScaledBitmap(bD_aqi_worst.getBitmap(), width_32dp, height_32dp, false);
        bM_aqi_unknown = Bitmap.createScaledBitmap(bD_aqi_unknown.getBitmap(), width_32dp, height_32dp, false);
        bM_airport_tower = Bitmap.createScaledBitmap(bD_airport_tower.getBitmap(), width_44dp, width_44dp, false);
        bM_arrive = Bitmap.createScaledBitmap(bD_arrive.getBitmap(), width_44dp, width_44dp, false);

        initToolbar();
        initDrawerLayout();
        initFloatingActionStuff();
        initInfoInputPopupWindow();
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //      toolbar.setTitle("Inforest");
        //      toolbar.setTitleMarginStart(250);
        // toolbar.setSubtitle("可以有副標喔~~");
        // toolbar.setLogo(R.drawable.ic_account_balance_black_24dp);
        BitmapDrawable bD = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_list_black_24dp, getTheme());
        Bitmap b = Bitmap.createScaledBitmap(bD.getBitmap(), width_44dp, width_44dp, false);
        toolbar.setNavigationIcon(new BitmapDrawable(getResources(), b));
    }

    private void initDrawerLayout() {
        //初始化左側選單與內容
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);



        mStartPageAnimView = (LottieAnimationView) findViewById(R.id.start_page_LAV);
        mStartPageAnimView.setSpeed(1.2f);
        mStartPageAnimView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mMap !=null) {
                    drawerLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        mStartPageAnimView.playAnimation();

        navigationView = (NavigationView) findViewById(R.id.navigationView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");;
        //取得當前手機系統時間
        Date currentDate = new Date(System.currentTimeMillis());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.titleTV)).setText(dateFormat.format(currentDate));

    }

    private void initFloatingActionStuff() {
        fam = (FloatingActionMenu) findViewById(R.id.fab_menu);
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        fam.setOnMenuButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fam.toggle(fam.isAnimated());
                fam.requestFocus();
            }
        });

        fab1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHeight_aqi = AQI_LOCAL;
                String message = "{'token': 'demo'," +
                        "'keyword': 'bangalore'}";
                String url = "https://api.waqi.info/map/bounds/?";

                myCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
                url = new StringBuffer(url)
                        .append("latlng=" + String.valueOf(myCurrentLocation.getLatitude() - 0.5))
                        .append("," + String.valueOf(myCurrentLocation.getLongitude() - 0.5))
                        .append("," + String.valueOf(myCurrentLocation.getLatitude() + 0.5))
                        .append("," + String.valueOf(myCurrentLocation.getLongitude() + 0.5))
                        .append("&token=4b33edc080ac635bd7259d9d5f9a1a9d8fa465c4")
                        .toString();

                showLoadingPage();
                sendGetRequest(url, OtfApiHelper.TYPE_AQI_STATION);
//                String message = "{'message':'On click button's id is " + v.getId() + "'}";
//                sendGetRequest(OtfApiHelper.OTF_SERVER, message);

                fam.close(fam.isAnimated());
            }
        });
        fab2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHeight_aqi = AQI_GLOBAL;
                String url = "https://api.waqi.info/map/bounds/?latlng=-90,-180,90,180&token=4b33edc080ac635bd7259d9d5f9a1a9d8fa465c4";
                showLoadingPage();
                sendGetRequest(url, OtfApiHelper.TYPE_AQI_STATION);

                fam.close(fam.isAnimated());
            }
        });
        fab3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                infoInputPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

                fam.close(fam.isAnimated());
            }
        });
    }

    private void initInfoInputPopupWindow(){
        infoInputView = getLayoutInflater().inflate(R.layout.popup_window_info_input, null);
        inputPage = (LinearLayout) infoInputView.findViewById(R.id.inputPage);
        loadingPage = (LinearLayout) infoInputView.findViewById(R.id.loadingPage);
        loading_LAV = (LottieAnimationView) infoInputView.findViewById(R.id.loading_LAV);

        final EditText editText = ((EditText) infoInputView.findViewById(R.id.pW_flightNumET));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.clearFocus();
        ((Button) infoInputView.findViewById(R.id.pW_cancelBtn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                infoInputPopupWindow.dismiss();
            }
        });
        ((Button) infoInputView.findViewById(R.id.pW_SubmitBtn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingPage();
                String flightNumber = editText.getText().toString().trim();
//                editText.setText("");
                String parameter = "{'flight':'" + flightNumber + "'}";
//                        String url = "http://140.115.202.15/OneTwoFly/web/OneTwoFly/public/";
                String url = "http://140.115.202.15/OneTwoFly/web/OneTwoFly/public/flight?flight=";
                sendGetRequest(url + flightNumber, OtfApiHelper.TYPE_FLIGHT).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        hideLoadingPage();
                        return null;
                    }
                },Task.UI_THREAD_EXECUTOR);
//                        sendPostRequest(url,parameter);

//                        float latitude, longtitude;
//                        latitude = Float.parseFloat(inputText.split(",")[0]);
//                        longtitude = Float.parseFloat(inputText.split(",")[1]);
//                        String url = OtfApiHelper.getAqiStationRequestUrl(latitude, longtitude);
//                        sendGetRequest(url, "hihi");
            }
        });

        //最後一個參數false表示沒有取得焦點
        infoInputPopupWindow = new PopupWindow(infoInputView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        infoInputPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoInputPopupWindow.setTouchable(true);
        infoInputPopupWindow.update();

    }

    private boolean addAirportMarker(Airport airport){
        if(airport != null){
            mMap.clear();
            String airportMessage = "Lng. : " + airport.getLongitude()
                    + "\nLat. : " + airport.getLatitude()
                    + "\nLocal RH : " + airport.getHumidity() + "%"
                    + "\nLocal Temp. : " + airport.getTemperature() + " 'C";

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(airport.getLatitude(), airport.getLongitude()))
                    .title(String.valueOf(airport.getAirportName()))
                    .icon(BitmapDescriptorFactory.fromBitmap(bM_arrive))
                    .snippet(String.valueOf(airportMessage))
            ).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(airport.getLatitude(), airport.getLongitude()), 16), 1500, null);
        }

        return false;
    }

    private boolean addAqiMarkers(ArrayList<AqiStation> aqiStations){
        if(aqiStations != null && aqiStations.size() > 0){
            mMap.clear();
            for( AqiStation aqiStation : aqiStations) {
                final Bitmap aqiIcon;
                final int aqi = aqiStation.getAqi();
                aqiIcon = decideAqiIcon(aqi);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(aqiStation.getLatitude(), aqiStation.getLongitude()))
                        .title(String.valueOf(aqiStation.getUid()))
                        .icon(BitmapDescriptorFactory.fromBitmap(aqiIcon))
                        .snippet(String.valueOf(aqi))
                );
            }
            myCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
            myCurrentLatLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLatLng, cameraHeight_aqi), 1500, null);
            return true;
        }

        return false;
    }

    private Bitmap decideAqiIcon(int aqi){
        final Bitmap aqiIcon;

        if (aqi >= 0 && aqi < 75) {
            aqiIcon = bM_aqi_best;
        } else if (aqi >= 75 && aqi < 150) {
            aqiIcon = bM_aqi_better;
        } else if (aqi >= 150 && aqi < 225) {
            aqiIcon = bM_aqi_worse;
        } else if (aqi >= 225) {
            aqiIcon = bM_aqi_worst;
        } else {
            aqiIcon = bM_aqi_unknown;
        }

        return aqiIcon;
    }

    private Task<Void> sendGetRequest(final String url, final int type){
        return Task.callInBackground(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return OtfApiHelper.GET(url);
            }
        }).continueWith(new Continuation<Response, Void>() {
            @Override
            public Void then(Task<Response> task) throws Exception {
                if(task.isFaulted() || task.isCancelled()){
                    return null;
                }

                if(task.getResult() != null){
                    hideLoadingPage();
                    String resultBody = null;
                    try {
                        resultBody = task.getResult().body().string();
                    }catch (Exception e){
                        if(task.getResult().code() == 500){
                            Toast.makeText(MapsActivity.this, "Please check your flight number.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MapsActivity.this, "Oops! something error...", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }
                    switch (type){
                        case OtfApiHelper.TYPE_FLIGHT :
                            currentState = STATE_FRIGHT;

                            Airport airport
                                    = OtfApiHelper.parseAirportJsonData(resultBody);
                            addAirportMarker(airport);
                            break;
                        case OtfApiHelper.TYPE_AQI_STATION :
                            currentState = STATE_AQI_STATION;

                            ArrayList<AqiStation> aqiStations
                                    = OtfApiHelper.parseAqiStationJsonData(resultBody);
                            addAqiMarkers(aqiStations);
                            break;
                    }
                }
                infoInputPopupWindow.dismiss();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private Task<Void> sendPostRequest(final String url, final String message){
        return Task.callInBackground(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return OtfApiHelper.POST(url, message);
            }
        }).continueWith(new Continuation<Response, Void>() {
            @Override
            public Void then(Task<Response> task) throws Exception {
                if(task.isFaulted() || task.isCancelled()){
                    return null;
                }

                if(task.getResult() != null){
                    ArrayList<AqiStation> aqiStations
                            = OtfApiHelper.parseAqiStationJsonData(task.getResult().body().string());
                    addAqiMarkers(aqiStations);
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private void showLoadingPage(){
        loadingPage.setVisibility(View.VISIBLE);
        inputPage.setVisibility(View.INVISIBLE);

        loading_LAV.playAnimation();
    }

    private void hideLoadingPage(){
        loadingPage.setVisibility(View.INVISIBLE);
        inputPage.setVisibility(View.VISIBLE);

        if(loading_LAV.isAnimating()) {
            loading_LAV.cancelAnimation();
        }
    }

    /////////////////////////////////////////////////////////
    //----------------- library framework -----------------//
    /////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "內容籌備中", Toast.LENGTH_SHORT).show();
            return true;
        }else
        */

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be
     * used. This is where we can add markers or lines, add listeners or move the camera. In this
     * case, we just add a marker near Sydney, Australia. If Google Play services is not installed on
     * the device, the user will be prompted to install it inside the SupportMapFragment. This method
     * will only be triggered once the user has installed Google Play services and returned to the
     * app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(!mStartPageAnimView.isAnimating()) {
            drawerLayout.setVisibility(View.VISIBLE);
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
        } else {
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();

                    return false;//false則除了執行自訂的行為外，還會執行預設的事件(顯示資訊視窗、移動鏡頭)
                }
            });

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.gm_info_window_layout, null);
                    CircleImageView infoWindowCIV = (CircleImageView) view.findViewById(R.id.infoWindowCIV);
                    TextView titleTv = (TextView) view.findViewById(R.id.titleTV);
                    TextView contentTV= (TextView) view.findViewById(R.id.contentTV);
                    if(currentState == STATE_AQI_STATION){
                        Bitmap aqiIcon = decideAqiIcon(Integer.parseInt(marker.getSnippet()));
                        infoWindowCIV.setImageBitmap(aqiIcon);

                        titleTv.setText("Station " + marker.getTitle());
                        if("-1".equals(marker.getSnippet())) {
                            contentTV.setText("Losing index :(");
                        }else {
                            contentTV.setText("AQI index : " + marker.getSnippet());
                        }
                    } else if (currentState == STATE_FRIGHT){
                        infoWindowCIV.setImageBitmap(bM_airport_tower);

                        titleTv.setText("Dest. >>" + marker.getTitle());
                        contentTV.setText(marker.getSnippet());
                    }
                    return view;
                }
            });
        }
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        myGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {  //屬於GoogleApiClient.ConnectionCallbacks
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, LOCATION_PERMS, LOCATION_REQUEST);
            return;
        }

        myCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
        if (gMapFirstStart) {
            if (myCurrentLocation == null) {
                Toast.makeText(this, "Can't get the local Lng. and Lat.", Toast.LENGTH_SHORT).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defultLatLng_Taiwan, 6));
            } else if (gMapFirstStart) {
                myCurrentLatLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLatLng, 16), 1500, null);
                gMapFirstStart = false;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {  //屬於GoogleApiClient.ConnectionCallbacks
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {    //屬於GoogleApiClient.OnConnectionFailedListener
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    Log.d("MapsActivity:", "permission granted");
                    if (mMap != null) {
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                marker.showInfoWindow();

                                return true;//false則除了執行自訂的行為外，還會執行預設的事件(顯示資訊視窗、移動鏡頭)
                            }
                        });

                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View view = getLayoutInflater().inflate(R.layout.gm_info_window_layout, null);
                                CircleImageView infoWindowCIV = (CircleImageView) view.findViewById(R.id.infoWindowCIV);
                                TextView titleTv = (TextView) view.findViewById(R.id.titleTV);
                                TextView contentTV= (TextView) view.findViewById(R.id.contentTV);
                                if(currentState == STATE_AQI_STATION){
                                    Bitmap aqiIcon = decideAqiIcon(Integer.parseInt(marker.getSnippet()));
                                    Drawable aqiBitmapDrawable = new BitmapDrawable(getResources(), aqiIcon);
                                    infoWindowCIV.setImageBitmap(aqiIcon);
                                }
                                titleTv.setText(marker.getTitle());
                                contentTV.setText(marker.getSnippet());
                                return view;
                            }
                        });

                        myCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
                        if (gMapFirstStart) {
                            if (myCurrentLocation == null) {
                                Toast.makeText(this, "Can't get the local Lng. and Lat.", Toast.LENGTH_SHORT).show();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defultLatLng_Taiwan, 6));
                            } else if (gMapFirstStart) {
                                myCurrentLatLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLatLng, 16), 1500, null);
                                gMapFirstStart = false;
                            }
                        }
                    }
                } else if (grantResults.length > 0) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("MapsActivity:", "permission denied");
                    Toast.makeText(this, "Permission deny! \n Application turn off.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (fam.isOpened()) {
                    fam.close(fam.isAnimated());
                }
                break;
        }
        return super.dispatchTouchEvent(e);
    }


    /**
     * Covert dp to px
     * @param dp
     * @param context
     * @return pixel
     */
    public static float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * Covert px to dp
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context){
        float dp = px / getDensity(context);
        return dp;
    }
    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}
