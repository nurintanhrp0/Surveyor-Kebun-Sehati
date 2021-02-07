package ventures.g45.kebunsehati.surveyor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Checkin extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationTrack locationTrack;
    private LatLngBounds fokus;
    double nowlat=0, nowlng=0;
    Button btnCheckin, btnCobaLagi;
    LinearLayout blockwarning;
    SupportMapFragment mapFragment;
    String deskoor, noHp, id_horeka, status, id_alamat;
    String defaultUrl, urlDoCheckin,urlRemoveTemp;
    Marker marker;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlDoCheckin = defaultUrl + "docheckin.html";
        urlRemoveTemp = defaultUrl + "removetemphoreka.html";

        Intent intent = getIntent();
        deskoor = intent.getStringExtra("koordinat");
        id_horeka = intent.getStringExtra("id_horeka");
        id_alamat = intent.getStringExtra("id_alamat");

        btnCheckin = findViewById(R.id.btnCheckin);
        blockwarning = findViewById(R.id.blockwarning);
        btnCobaLagi = findViewById(R.id.btnCobalagi);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoCheckin().execute();
            }
        });

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               getLocation();
           }
        });

        getLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to access your location", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getLocation(){
        locationTrack = new LocationTrack(Checkin.this);
        if(locationTrack.canGetLocation()) {
            nowlat = locationTrack.getLatitude();
            nowlng = locationTrack.getLongitude();
                   }
        else{
            locationTrack.showSettingsAlert();
        }
        mapFragment.getMapAsync(this);
    }

    public void updateUI(){
       String[] tujuan = deskoor.split(",");
       double lat, lng;
        Location loctujuan, locnow;
       lat = Double.parseDouble(tujuan[0]);
       lng = Double.parseDouble(tujuan[1]);
       loctujuan = new Location("");
       loctujuan.setLatitude(lat);
       loctujuan.setLongitude(lng);

       locnow = new Location("");
       locnow.setLatitude(nowlat);
       loctujuan.setLongitude(nowlng);

       float[] jarak = new float[1];
       Location.distanceBetween(nowlat, nowlng, lat, lng, jarak);
       if (jarak[0] >= 1000){
           blockwarning.setVisibility(View.VISIBLE);
           btnCobaLagi.setVisibility(View.VISIBLE);
           status = "waiting";
       }else {
           blockwarning.setVisibility(View.GONE);
           btnCobaLagi.setVisibility(View.GONE);
           status = "ok";
       }

        if (marker != null) {
            marker.remove();
        }else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Lokasi Horeka"));
        }
        Log.d("Lokasi", lat + " " + lng + " " + jarak[0]);
        Log.d("My Current location", "Lat : " + nowlat + " Long : " + nowlng);
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(nowlat, nowlng)).title("Lokasi Anda"));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include((new LatLng(lat, lng)));
        builder.include(new LatLng(nowlat, nowlng));
        final LatLngBounds bounds = builder.build();
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        } catch (IllegalStateException ise) {

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                }
            });
        }

        if (lat < nowlat) {
            fokus = new LatLngBounds(new LatLng(lat, lng), new LatLng(nowlat, nowlng));
        } else {
            fokus = new LatLngBounds(new LatLng(nowlat, nowlng), new LatLng(lat, lng));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fokus.getCenter(), 14));


    }

    private class DoCheckin extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("id_horeka", id_horeka));
            params.add(new BasicNameValuePair("koordinat", nowlat + " ," + nowlng));
            params.add(new BasicNameValuePair("status", status));

            JSONObject json = jsonParser.makeHttpRequest(urlDoCheckin, "POST", params);

            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Checkin.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result.getInt("error") == 1) {
                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(Checkin.this);
                    builder1.setMessage(result.getString("pesan"));
                    builder1.setCancelable(true);

                    if(result.getString("pesan").equals("Keranjang Anda tidak kosong. Silahkan hapus terlebih dahulu untuk melanjutkan.")) {
                        builder1.setPositiveButton(
                                "HAPUS",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new RemoveTemp().execute();
                                    }
                                });

                        builder1.setNegativeButton(
                                "BATAL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                    }else {
                        builder1.setNegativeButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                    }


                    androidx.appcompat.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Order.class);
                    intent.putExtra("id_horeka", id_horeka);
                    intent.putExtra("id_alamat", id_alamat);
                    startActivity(intent);
                }
            } catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    private class RemoveTemp extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("harga", "-5"));

            JSONObject json = jsonParser.makeHttpRequest(urlRemoveTemp, "POST", params);

            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Checkin.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result.getInt("error") == 1) {
                } else {
                    new DoCheckin().execute();
                }
            } catch(JSONException e){
                e.printStackTrace();
            }

        }
    }
}