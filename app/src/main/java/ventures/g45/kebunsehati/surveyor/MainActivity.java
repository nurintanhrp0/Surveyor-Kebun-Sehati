package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import  ventures.g45.kebunsehati.surveyor.modal.modal_orderan;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_orderan;
import  ventures.g45.kebunsehati.surveyor.modal.modal_penugasan;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_penugasan;
import  ventures.g45.kebunsehati.surveyor.modal.modal_horeka;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_horeka;
import  ventures.g45.kebunsehati.surveyor.modal.modal_alamat_horeka;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_horeka_alamat;

public class MainActivity extends AppCompatActivity {

    ImageView out;
    LinearLayout akun, survei, order, invoice, member, titipjual, baris1, baris2, baris3;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LocationTrack locationTrack;
    Double latitude, longtitude;
    TextView txtJam, txtTanggal;
    String formatJam = "HH : mm", formatTanggal = "EEEE, dd MMM yyyy";
    SimpleDateFormat fJam, fTanggal;
    Calendar calendar;
    Integer login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        login = sharedPreferences.getInt("login", 0);

        fJam = new SimpleDateFormat(formatJam, Locale.getDefault());
        fTanggal = new SimpleDateFormat(formatTanggal, Locale.getDefault());

        out = findViewById(R.id.logout);
        survei = findViewById(R.id.survei);
        order = findViewById(R.id.pesanan);
        invoice = findViewById(R.id.invoice);
        akun = findViewById(R.id.akun);
        member = findViewById(R.id.member);
        titipjual = findViewById(R.id.titipjual);
        txtJam = findViewById(R.id.txtJam);
        txtTanggal = findViewById(R.id.txtTanggal);
        baris1 = findViewById(R.id.baris1);
        baris2 = findViewById(R.id.baris2);
        baris3 = findViewById(R.id.baris3);

        //login = 0; surveyor
        //login = 1; sales
        //login = 2; both
        if (login == 0){
            baris1.setVisibility(View.GONE);
            baris2.setVisibility(View.GONE);
        }else if (login == 1){
            survei.setVisibility(View.GONE);
        }

//            start();
//            try {
//                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
//                }
//            } catch (Exception e){
//
//                e.printStackTrace();
//            }
        survei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DaftarAnggkringan.class);
                startActivity(intent);
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DaftarOrderan.class);
                startActivity(intent);
            }
        });

        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Daftar_invoice.class);
                startActivity(intent);
            }
        });

        akun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserAccount.class);
                startActivity(intent);
            }
        });

        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DaftarMember.class);
                startActivity(intent);
            }
        });

        titipjual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TitipJual.class);
                startActivity(intent);
            }
        });

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear().apply();
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });


        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                calendar = Calendar.getInstance();
                txtTanggal.setText(fTanggal.format(calendar.getTime()));
                txtJam.setText(fJam.format(calendar.getTime()));
                handler.postDelayed(this, delay);
            }
        }, delay);


    }

    Thread t = new Thread() {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(5000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLocation();

                        }
                    });
                }
            }
            catch (Exception e) {
            }
        }
    } ;

    public void start(){
        t.start();
    }

    public void stop(){
        t.interrupt();
    }

    public void getLocation(){
        locationTrack = new LocationTrack(MainActivity.this);
        if(locationTrack.canGetLocation()) {
            latitude = locationTrack.getLatitude();
            longtitude = locationTrack.getLongitude();
            Log.d("My Current location", "Lat : " + locationTrack.getLatitude() + " Long : " + locationTrack.getLongitude());
        }
        else{
            locationTrack.showSettingsAlert();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        System.exit(0);
    }

    /*private class updatePosisi extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("latitude", latitude.toString()));
            params.add(new BasicNameValuePair("longtitude", longtitude.toString()));

            JSONObject json = jsonParser.makeHttpRequest(urlUpdatePosisi, "POST", params);

            return json;
        }

//        protected void onPostExecute(JSONObject result) {
//
//            if ((pDialog != null) && pDialog.isShowing())
//                pDialog.dismiss();
//            pDialog = null;
//
//            try {
//                if (result.getInt("error") == 1) {
//                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
//                } else {
//
//                }
//            }
//            catch(JSONException e){
//                e.printStackTrace();
//            }
//
//        }
    }*/
}
