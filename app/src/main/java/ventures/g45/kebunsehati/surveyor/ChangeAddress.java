package ventures.g45.kebunsehati.surveyor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChangeAddress extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Boolean mRequestingLocationUpdates;
    SettingsClient mSettingsClient;
    LocationSettingsRequest mLocationSettingsRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private Location mCurrentLocation;
    Geocoder geocoder;
    String mLastUpdateTime, originAdd, kota, city, token, no_hp, defaultUrl, urlSimpanAlamat, curLocation, idAlamat, sKepemilikan, sJuragan;
    InputMethodManager imm;
    Double originLat, originLng;
    List<Address> addresses;
    RelativeLayout markerOrigin;
    TextView txtTitleAlamat, txtAlamat;
    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    Button btnInputAlamat, btnSimpanAlamat;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    final String[] sAlamat = new String[1];
    Integer totalBelanja, dari, id_horeka;
    double latitude, longtitude;
    Integer id_alamat, id_kelurahan;
    String detailAlamat, nama, notelp, jenis, noJuragan, titip,inpTitip, inpBantuan, sBantuan1, sBantuan2, sBantuan3, sBantuan4, sBantuan, sNamaKelompok;
    LatLng placeLatLng;
    EditText txtNamaCabang;
    LinearLayout blockButton;
    String[] bantuan = {sBantuan1, sBantuan2, sBantuan3, sBantuan4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlSimpanAlamat = defaultUrl + "doSaveAlamatHoreka.html";

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longtitude = intent.getDoubleExtra("longtitude", 0);
        detailAlamat = intent.getStringExtra("alamat");
        nama = intent.getStringExtra("nama");
        notelp = intent.getStringExtra("notelp");
        sKepemilikan = intent.getStringExtra("kepemilikan");
        sJuragan = intent.getStringExtra("juragan");
        dari = intent.getIntExtra("dari", 0);
        jenis = intent.getStringExtra("jenis");
        noJuragan = intent.getStringExtra("no_juragan");
        titip = intent.getStringExtra("terima_titip");
        for (int i =0; i<4; i++){
            bantuan[i]= intent.getStringExtra("jenis_bantuan"+i);
        }
        inpTitip = intent.getStringExtra("inpTitip");
        inpBantuan =intent.getStringExtra("inpBantuan");
        id_kelurahan = intent.getIntExtra("id_kelurahan", 0);
        sBantuan = intent.getStringExtra("banyak_bantuan");
        sNamaKelompok = intent.getStringExtra("nama_kelompok");

        txtTitleAlamat = (TextView) findViewById(R.id.txtTitleAlamat);
        txtAlamat = (TextView) findViewById(R.id.txtAlamat);
        btnInputAlamat = (Button) findViewById(R.id.btnInputAlamat);
        btnSimpanAlamat = (Button) findViewById(R.id.btnSimpanAlamat);
        blockButton = findViewById(R.id.blockButton);

        if (latitude != 0) {
            placeLatLng = new LatLng(latitude, longtitude);
        }
        if (detailAlamat != null){
            txtAlamat.setText(detailAlamat);
        }

//        if (dari == 1){
//            blockButton.setVisibility(View.GONE);
//        }

        btnSimpanAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   SimpanAlamat();
            }
        });

        btnInputAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchAlamat();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerOrigin = (RelativeLayout)findViewById(R.id.markerOrigin);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                    mCurrentLocation = locationResult.getLastLocation();

                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


                if (latitude == 0){
                    updateLocationUI();
                }else {
                    updateLocationUII();
                }
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            getLocation();

    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception var2) {
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    LatLng latLng = mMap.getCameraPosition().target;
                    Geocoder geocoder = new Geocoder(ChangeAddress.this);

                    try {
                        List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (addressList != null && addressList.size() > 0) {
                            String locality = addressList.get(0).getAddressLine(0);
                            String country = addressList.get(0).getCountryName();
                            city = addressList.get(0).getSubAdminArea();
                            if (!locality.isEmpty() && !country.isEmpty()) {
                                originLat = latLng.latitude;
                                originLng = latLng.longitude;
                                kota = city;
                                //if (dari != 1) {
                                    txtAlamat.setText(addressList.get(0).getAddressLine(0));
                                //}
                                curLocation = addressList.get(0).getAddressLine(0);
                                mCurrentLocation.setLatitude(latLng.latitude);
                                mCurrentLocation.setLongitude(latLng.longitude);
                                addresses = addressList;
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


    }

    public void getLocation(){
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {

            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                mRequestingLocationUpdates = true;
                startLocationUpdates();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    openSettings();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }

        }).check();
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                if (latitude == 0){
                    updateLocationUI();
                }else {
                    updateLocationUII();
                }
//                if(dari == 1){
//                    updateLocationUII();
//                }else {
//                    updateLocationUI();
//                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(ChangeAddress.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.i("error", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                        Log.e("error", errorMessage);

                        Toast.makeText(ChangeAddress.this, errorMessage, Toast.LENGTH_LONG).show();
                }

                if (latitude == 0){
                    updateLocationUI();
                }else {
                    updateLocationUII();
                }
//                if(dari == 1){
//                    updateLocationUII();
//                }else {
//                    updateLocationUI();
//                }
            }
        });
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            double latitude, longitude;
            geocoder = new Geocoder(this, Locale.getDefault());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                String[] alamat = address.split(",");
                String titleAlamat = addresses.get(0).getFeatureName();
                city = addresses.get(0).getSubAdminArea();
                originLat = latitude;
                originLng = longitude;
                originAdd = address;
                kota = city;
                curLocation = address;
               // if (dari != 1) {
                    txtAlamat.setText(address);
                //}
                stopLocationUpdates();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateLocationUII() {
        double latitude, longitude;
            geocoder = new Geocoder(this, Locale.getDefault());
            latitude = placeLatLng.latitude;
            longitude = placeLatLng.longitude;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude,1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0);
            String[] alamat = address.split(",");
            String titleAlamat = addresses.get(0).getFeatureName();
            city = addresses.get(0).getSubAdminArea();
            originLat = latitude;
            originLng = longitude;
            originAdd = address;
            kota = city;
            curLocation = address;
            //if (dari != 1) {
                txtAlamat.setText(address);
            //}
            stopLocationUpdates();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        LatLng origin;
                        if (latitude == 0) {
                            origin = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                        }else {
                            origin = new LatLng(placeLatLng.latitude, placeLatLng.longitude);
                        }

//                        if (dari == 1){
//                            origin = new LatLng(placeLatLng.latitude, placeLatLng.longitude);
//
//                        }

//                        if (dari != 1) {
                            markerOrigin.setVisibility(View.VISIBLE);
//                        }else {
//                            mMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(origin.latitude, origin.longitude)));
//
//                        }
                        btnSimpanAlamat.setEnabled(true);
                        btnSimpanAlamat.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 18));
                    }
                });
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void SearchAlamat(){
        dialog = new AlertDialog.Builder(ChangeAddress.this, R.style.AddToCart);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.popup_search_address, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        TextView txtTitleInputAlamat = dialogView.findViewById(R.id.txtTitleInputAlamat);
        txtTitleInputAlamat.setTypeface(openSansBold);
        final EditText inpAlamat = dialogView.findViewById(R.id.inpAlamat);
        inpAlamat.setTypeface(openSansRegular);
        inpAlamat.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        inpAlamat.setSingleLine(true);
        Button btnSearchAddress = dialogView.findViewById(R.id.btnSearchAddress);
        btnSearchAddress.setTypeface(openSansBold);
        ImageView icoClose = dialogView.findViewById(R.id.icoClose);
        icoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sAlamat[0] = inpAlamat.getText().toString();
                if (sAlamat[0].isEmpty()) {
                    inpAlamat.setError("Silahkan isi alamat lengkap anda!");
                } else {
                    alertDialog.dismiss();
                    AddAlamat();
                }
            }
        });

        inpAlamat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sAlamat[0] = inpAlamat.getText().toString();
                    if (sAlamat[0].isEmpty()) {
                        inpAlamat.setError("Silahkan isi alamat lengkap anda!");
                    } else {
                        alertDialog.dismiss();
                        AddAlamat();
                    }
                }
                return false;
            }
        });

        alertDialog.show();
    }

    public void AddAlamat() {
        if (!sAlamat[0].isEmpty()) {
            geocoder = new Geocoder(ChangeAddress.this);
            try {
                List<Address> addressList = geocoder.getFromLocationName(sAlamat[0] + ", Yogyakarta", 1);
                if (addressList.size() > 0) {
                    originLat = addressList.get(0).getLatitude();
                    originLng = addressList.get(0).getLongitude();
                    curLocation = addressList.get(0).getAddressLine(0);
                    LatLng lokasi = new LatLng(originLat, originLng);

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 18));

                    txtAlamat.setText(curLocation);
                } else {
                    AlertDialog.Builder alertDialogBuilder;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        alertDialogBuilder = new AlertDialog.Builder(ChangeAddress.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        alertDialogBuilder = new AlertDialog.Builder(ChangeAddress.this);
                        alertDialogBuilder
                                .setMessage("Alamat anda tidak dapat ditampilkan di peta. Silahkan cek kembali atau pilih melalui peta.")
                                .setCancelable(true)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SimpanAlamat() {
        finish();
        if (dari == 0) {
            Intent intent = new Intent(getApplicationContext(), Angkringan.class);
            intent.putExtra("alamat", curLocation);
            intent.putExtra("koordinat", originLat.toString() + "," + originLng.toString());
            intent.putExtra("nama", nama);
            intent.putExtra("notelp", notelp);
            intent.putExtra("kepemilikan", sKepemilikan);
            intent.putExtra("juragan", sJuragan);
            intent.putExtra("jenis", jenis);
            intent.putExtra("no_juragan", noJuragan);
            intent.putExtra("terima_titip", titip);
            for (int i = 0; i < 4; i++) {
                intent.putExtra("jenis_bantuan" + (i), bantuan[i]);
            }
            intent.putExtra("inpTitip", inpTitip);
            intent.putExtra("inpBantuan", inpBantuan);
            intent.putExtra("no_juragan", noJuragan);
            intent.putExtra("id_kelurahan", id_kelurahan);
            intent.putExtra("banyak_bantuan", sBantuan);
            intent.putExtra("nama_kelompok", sNamaKelompok);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), RegisterMember.class);
            intent.putExtra("alamat", curLocation);
            intent.putExtra("koordinat", originLat.toString() + "," + originLng.toString());
            //intent.putExtra("nama_cabang", txtNamaCabang.getText().toString());
            startActivity(intent);
        }
    }

}
