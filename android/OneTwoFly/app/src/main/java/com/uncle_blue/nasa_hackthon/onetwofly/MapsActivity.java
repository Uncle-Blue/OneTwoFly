package com.uncle_blue.nasa_hackthon.onetwofly;

import android.*;
import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uncle_blue.nasa_hackthon.onetwofly.util.network.OtfApiHelper;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import okhttp3.Response;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Toolbar toolbar;    //頂部按鈕條
    private DrawerLayout drawerLayout;  //左側選單
    private FloatingActionMenu fam; //漂浮按鈕選單

    private final String[] LOCATION_PERMS = {android.Manifest.permission.ACCESS_FINE_LOCATION};
    private final int INITIAL_REQUEST = 1337;
    private final int LOCATION_REQUEST = INITIAL_REQUEST + 1;

    private Location myCurrentLocation;
    private LatLng defultLatLng_Taiwan = new LatLng(23.5491, 119.8999);
    private LatLng myCurrentLatLng = defultLatLng_Taiwan;
    private GoogleApiClient myGoogleApiClient;
    private boolean gMapFirstStart = true;

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
        initToolbar();
        initDrawerLayout();
        initFloatingActionStuff();
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //      toolbar.setTitle("Inforest");
        //      toolbar.setTitleMarginStart(250);
        // toolbar.setSubtitle("可以有副標喔~~");
        // toolbar.setLogo(R.drawable.ic_account_balance_black_24dp);
        toolbar.setNavigationIcon(R.drawable.ic_list_black_24dp);
    }

    private void initDrawerLayout() {
        //初始化左側選單與內容
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void initFloatingActionStuff() {
        fam = (FloatingActionMenu) findViewById(R.id.fab_menu);
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        fam.setOnMenuButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "menu open!", Toast.LENGTH_SHORT).show();
                fam.toggle(fam.isAnimated());
                fam.requestFocus();
            }
        });

        fab1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "{'message':'On click button's id is " + v.getId() + "'}";
                sendToServer(OtfApiHelper.OTF_SERVER, message);

                Toast.makeText(MapsActivity.this, "fab1 click!", Toast.LENGTH_SHORT).show();
                fam.close(fam.isAnimated());
            }
        });
        fab2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "fab2 click!", Toast.LENGTH_SHORT).show();
                fam.close(fam.isAnimated());
            }
        });
        fab3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Your Flight");
                builder.setMessage("Please input the flight number below.");

                // Set up the input
                final EditText input = new EditText(MapsActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                Toast.makeText(MapsActivity.this, "fab3 click!", Toast.LENGTH_SHORT).show();
                fam.close(fam.isAnimated());
            }
        });
    }

    private Task<Void> sendToServer(final String url, final String message){
        return Task.callInBackground(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return OtfApiHelper.POST(url, message);
            }
        }).continueWith(new Continuation<Response, Void>() {
            @Override
            public Void then(Task<Response> task) throws Exception {
                if(task.isFaulted() || task.isCancelled()){
                    Toast.makeText(MapsActivity.this,
                            "Error code: " + String.valueOf(task.getResult().code())
                                    + "\n" + task.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                }
                fam.open(fam.isAnimated());
                Toast.makeText(MapsActivity.this, task.getResult().message(), Toast.LENGTH_SHORT).show();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
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
                Toast.makeText(this, "無法取得目前所在位置", Toast.LENGTH_SHORT).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defultLatLng_Taiwan, 6));
            } else if (gMapFirstStart) {
                myCurrentLatLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLatLng, 16));
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
                        myCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
                        if (gMapFirstStart) {
                            if (myCurrentLocation == null) {
                                Toast.makeText(this, "無法取得目前所在位置", Toast.LENGTH_SHORT).show();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defultLatLng_Taiwan, 6));
                            } else if (gMapFirstStart) {
                                myCurrentLatLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLatLng, 16));
                                gMapFirstStart = false;
                            }
                        }
                    }
                } else if (grantResults.length > 0) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("MapsActivity:", "permission denied");
                    Toast.makeText(this, "無法取得權限，請重新啟動！", Toast.LENGTH_SHORT).show();
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
}
