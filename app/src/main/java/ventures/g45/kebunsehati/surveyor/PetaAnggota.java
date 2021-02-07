package ventures.g45.kebunsehati.surveyor;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.adapter.adapter_orderan_sales;
import ventures.g45.kebunsehati.surveyor.modal.modal_alamat_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;

public class PetaAnggota extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String noHp, defaultUrl, urlLokasiHoreka;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    double nowlat=0, nowlng=0;
    Marker marker;
    private List<modal_alamat_horeka> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peta_anggota);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlLokasiHoreka = defaultUrl + "getHorekaLokasi.html";

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }else {
            getLocation();
        }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(nowlat, nowlng)).title("Lokasi Anda").icon(bitmapDescriptorFromVector(this, R.drawable.ic_my_location)));
        marker.setTag(-1);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMarkerClick(Marker marker) {
                final int position = (int)(marker.getTag());
                Log.d("postion", String.valueOf( list.get(position).getKoordinat()));
                if (position !=  -1){
                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(PetaAnggota.this);
                    builder1.setMessage("Alamat : " + list.get(position).getAlamat() + System.lineSeparator()
                            + System.lineSeparator()+ "Apakah anda akan melakukan order untuk member ini?");
                    builder1.setTitle(list.get(position).getNama());
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Ya",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(getApplicationContext(), Checkin.class);
                                    intent.putExtra("koordinat", list.get(position).getKoordinat());
                                    intent.putExtra("id_horeka", list.get(position).getId_horeka());
                                    intent.putExtra("id_alamat", list.get(position).getId());
                                    startActivity(intent);
                                }
                            });
                    builder1.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {
                    marker.showInfoWindow();
                }
                return false;
            }
        });

        new GetHoreka().execute();
    }

    public void getLocation(){
        LocationTrack locationTrack = new LocationTrack(PetaAnggota.this);
        if(locationTrack.canGetLocation()) {
            nowlat = locationTrack.getLatitude();
            nowlng = locationTrack.getLongitude();
        }
        else{
            locationTrack.showSettingsAlert();
        }
        mapFragment.getMapAsync(this);
    }

    private class GetHoreka extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlLokasiHoreka, "POST", params);

            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(PetaAnggota.this);
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
                    Toast.makeText(PetaAnggota.this, "Tidak dapat memuat data", Toast.LENGTH_SHORT).show();
                }else {
                    if (result.has("data")){
                        list = new ArrayList<>();
                        JSONArray daftar = new JSONArray(result.getString("data"));
                        if (daftar.length()>0){
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include((new LatLng(nowlat, nowlng)));

                            for (int i = 0; i<daftar.length(); i++){
                                JSONObject lokasi = daftar.getJSONObject(i);

                                modal_alamat_horeka alamat = new modal_alamat_horeka();
                                alamat.setId(lokasi.getString("id_alamat"));
                                alamat.setId_horeka(lokasi.getString("id_horeka"));
                                alamat.setKoordinat(lokasi.getString("lat") + "," + lokasi.getString("lng"));
                                alamat.setNama(lokasi.getString("nama"));
                                alamat.setAlamat(lokasi.getString("alamat"));
                                list.add(alamat);

                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lokasi.getDouble("lat"), lokasi.getDouble("lng"))).title(lokasi.getString("nama")).icon(bitmapDescriptorFromVector(PetaAnggota.this, R.drawable.ic_pin)));
                                marker.setTag(i);

                                builder.include(new LatLng(lokasi.getDouble("lat"), lokasi.getDouble("lng")));

                            }

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

                        }
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}