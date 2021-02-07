package ventures.g45.kebunsehati.surveyor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Angkringan extends AppCompatActivity {

    EditText inpNama, inpNotelp, inpNamajuragan, inpNoJuaragan, inpNamaKelompok;
    TextView inpAlamat, txtFoto, txtLabel3, txtLabel4, txtLabel2;
    Spinner sp_kepemilikan, sp_jenis;
    Button btnpilihfoto, btnsimpan;
    RadioGroup blockJenis, blockBantuan;
    RadioButton rdYa, rdTidak;
    CheckBox rdSupply, rdPemsaran, rdModal, rdLain;
    String sNama, sNotelp, sAlamat, koordinat, sJuragan, sKepemilikan, sJenis, sNoJuaraga, sTitip, sBantuan, inpTitip, inpBantuan, sBantuan1, sBantuan2, sBantuan3, sBantuan4, sNamaKelompok, berhasil;
    List<Bitmap> bitmaps = new ArrayList<>();
    String lat, lang, noHp, id_angkringan;
    Integer jlhfoto=0, ke, id_kelurahan, id_kategori, id_penugasan;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String defaultUrl, urlUploadFoto, urlSimpanData, urlGetData;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] kepemilikan = {"Pribadi", "Kelompok/Perusahaan"};
    String[] jenis;
    String[] titip = {"ya", "tidak"};
    String[] bantuan = {sBantuan1, sBantuan2, sBantuan3, sBantuan4};
    RadioButton[] rdTitip;
    CheckBox[] rdbantuan;
    ArrayAdapter<String> adapter, adapter2;
    LinearLayout pos, blockFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angkringan);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");
        id_penugasan = sharedPreferences.getInt("id_penugasan", 0);
        id_kelurahan = sharedPreferences.getInt("id_kelurahan", 0);
        Log.d("id_kelurahan", id_kelurahan.toString());

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlUploadFoto = defaultUrl + "douploadangkringan.html";
        urlSimpanData = defaultUrl + "dosaveangkringan.html";
        urlGetData = defaultUrl + "getangminle.html";

        final Intent intent = getIntent();
        sNama = intent.getStringExtra("nama");
        sNotelp = intent.getStringExtra("notelp");
        sAlamat = intent.getStringExtra("alamat");
        koordinat = intent.getStringExtra("koordinat");
        sJuragan = intent.getStringExtra("juragan");
        sKepemilikan = intent.getStringExtra("kepemilikan");
        sJenis = intent.getStringExtra("jenis");
        sNoJuaraga = intent.getStringExtra("no_juragan");
        sTitip = intent.getStringExtra("terima_titip");
        for (int i =0; i<4; i++){
            bantuan[i]= intent.getStringExtra("jenis_bantuan"+i);
        }
        inpTitip = intent.getStringExtra("inpTitip");
        inpBantuan =intent.getStringExtra("inpBantuan");
        id_kelurahan = intent.getIntExtra("id_kelurahan", 0);
        sBantuan = intent.getStringExtra("banyak_bantuan");
        sNamaKelompok = intent.getStringExtra("nama_kelompok");

        inpNama = findViewById(R.id.txtNamaPenjual);
        inpNotelp = findViewById(R.id.txtNohp);
        inpAlamat = findViewById(R.id.txtAlamat);
        btnpilihfoto = findViewById(R.id.pilihFoto);
        btnsimpan = findViewById(R.id.btnSimpan);
        txtFoto = findViewById(R.id.txtFoto);
        inpNamajuragan = findViewById(R.id.txtNamaJuragan);
        sp_kepemilikan = findViewById(R.id.sp_kepemilikan);
        pos =findViewById(R.id.pos);
        sp_jenis = findViewById(R.id.sp_jenis);
        inpNoJuaragan = findViewById(R.id.txtNohpjuaragan);
        blockJenis = findViewById(R.id.blockTitipanMakanan);
        rdYa = findViewById(R.id.rdYa);
        rdTidak = findViewById(R.id.rdTidak);
        rdSupply = findViewById(R.id.rdsupply);
        rdPemsaran = findViewById(R.id.rdPemasaran);
        rdModal = findViewById(R.id.rdModal);
        rdLain = findViewById(R.id.rdlain);
        blockFoto = findViewById(R.id.blockFoto);
        txtLabel3 = findViewById(R.id.txtLabel3);
        txtLabel4 = findViewById(R.id.txtLabel4);
        txtLabel2 = findViewById(R.id.txtLabel2);
        inpNamaKelompok = findViewById(R.id.txtNamaKelompok);

        rdTitip = new RadioButton[]{rdYa, rdTidak};
        rdbantuan = new CheckBox[]{rdSupply, rdPemsaran, rdModal, rdLain};

        adapter = new ArrayAdapter<String>(Angkringan.this, android.R.layout.simple_spinner_item, kepemilikan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_kepemilikan.setAdapter(adapter);

        if (sNama != null){
            inpNama.setText(sNama);
        }
        if (sAlamat != null){
            inpAlamat.setText(sAlamat);
        }
        if (sNotelp != null){
            inpNotelp.setText(sNotelp);
        }
        if (sJuragan != null){
            inpNamajuragan.setText(sJuragan);
        }
        if (sKepemilikan != null){
            sp_kepemilikan.setSelection(adapter.getPosition(sKepemilikan));
            if(sKepemilikan.equals("Kelompok/Perusahaan")){
                inpNamajuragan.setVisibility(View.VISIBLE);
                inpNoJuaragan.setVisibility(View.VISIBLE);
                txtLabel3.setVisibility(View.VISIBLE);
                txtLabel4.setVisibility(View.VISIBLE);
                txtLabel2.setVisibility(View.VISIBLE);
                inpNamaKelompok.setVisibility(View.VISIBLE);
            }else {
                inpNamajuragan.setVisibility(View.GONE);
                inpNoJuaragan.setVisibility(View.GONE);
                txtLabel3.setVisibility(View.GONE);
                txtLabel4.setVisibility(View.GONE);
                txtLabel2.setVisibility(View.GONE);
                inpNamaKelompok.setVisibility(View.GONE);
            }
        }

        if (sTitip != null){
            for (int i = 0; i<titip.length; i++){
                if (sTitip.equals(titip[i])){
                    rdTitip[i].setChecked(true);
                }
            }
        }
        for (int i =0; i<4; i++){
            if (bantuan[i] != null){
                  rdbantuan[i].setChecked(true);
            }
        }
        if (sNoJuaraga != null){
            inpNoJuaragan.setText(sNoJuaraga);
        }

        if (sNamaKelompok != null){
            inpNamaKelompok.setText(sNamaKelompok);
        }

        sp_kepemilikan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));

                sKepemilikan = ((TextView) parent.getChildAt(0)).getText().toString();

                if(sKepemilikan.equals("Kelompok/Perusahaan")){
                    inpNamajuragan.setVisibility(View.VISIBLE);
                    inpNoJuaragan.setVisibility(View.VISIBLE);
                    txtLabel3.setVisibility(View.VISIBLE);
                    txtLabel4.setVisibility(View.VISIBLE);
                    txtLabel2.setVisibility(View.VISIBLE);
                    inpNamaKelompok.setVisibility(View.VISIBLE);
                }else {
                    inpNamajuragan.setVisibility(View.GONE);
                    inpNoJuaragan.setVisibility(View.GONE);
                    txtLabel3.setVisibility(View.GONE);
                    txtLabel4.setVisibility(View.GONE);
                    txtLabel2.setVisibility(View.GONE);
                    inpNamaKelompok.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));

                sJenis = ((TextView) parent.getChildAt(0)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DaftarAnggkringan.class);
                startActivity(intent);
            }
        });

        inpAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sNama = inpNama.getText().toString();
                sNotelp = inpNotelp.getText().toString();
                sAlamat = inpAlamat.getText().toString();
                sJuragan = inpNamajuragan.getText().toString();
                sNoJuaraga = inpNoJuaragan.getText().toString();
                sNamaKelompok = inpNamaKelompok.getText().toString();
                Intent intent = new Intent(getApplicationContext(), ChangeAddress.class);
                intent.putExtra("nama", sNama);
                intent.putExtra("notelp", sNotelp);
                intent.putExtra("alamat", sAlamat);
                intent.putExtra("juragan", sJuragan);
                intent.putExtra("kepemilikan", sKepemilikan);
                intent.putExtra("no_juragan", sNoJuaraga);
                intent.putExtra("jenis", sJenis);
                intent.putExtra("terima_titip", sTitip);
                for (int i =0; i <4 ; i++) {
                    intent.putExtra("jenis_bantuan"+i, bantuan[i]);
                }
                intent.putExtra("inpTitip", inpTitip);
                intent.putExtra("inpBantuan", inpBantuan);
                intent.putExtra("no_juragan", sNoJuaraga);
                intent.putExtra("id_kelurahan", id_kelurahan);
                intent.putExtra("banyak_bantuan", sBantuan);
                intent.putExtra("nama_kelompok", sNamaKelompok);
                if (koordinat != null){
                    String[] koor = koordinat.split(",");
                    lat = koor[0];
                    lang = koor[1];
                }else {
                    lat = "0";
                    lang = "0";
                }
                intent.putExtra("latitude", Double.valueOf(lat));
                intent.putExtra("longtitude", Double.valueOf(lang));
                startActivity(intent);
            }
        });

        btnpilihfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Angkringan.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //ActivityCompat.requestPermissions(Angkringan.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    Dexter.withActivity(Angkringan.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {

                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }

                    }).check();
                }else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });

        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sNama = inpNama.getText().toString();
                sNotelp = inpNotelp.getText().toString();
                sAlamat = inpAlamat.getText().toString();
                sJuragan = inpNamajuragan.getText().toString();
                sNoJuaraga = inpNoJuaragan.getText().toString();
                sNamaKelompok = inpNamaKelompok.getText().toString();
                if (sKepemilikan.equals("Kelompok/Perusahaan")) {
                    if (!sNama.isEmpty() || !sAlamat.isEmpty() || sKepemilikan != null || !sNotelp.isEmpty() || !sNoJuaraga.isEmpty() || !sBantuan.isEmpty() || !sTitip.isEmpty() || !sJuragan.isEmpty() || !sNamaKelompok.isEmpty()) {
                      //  if (jlhfoto >= 4) {
                            new SimpanData().execute();
                      //  } else {
                       //     Toast.makeText(Angkringan.this, "Jumlah Foto Minimal 4!", Toast.LENGTH_SHORT).show();
                       // }
                    } else {
                        Toast.makeText(Angkringan.this, "Isi Semua Data terlebeh Dahulu!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (!sNama.isEmpty() || !sAlamat.isEmpty() || sKepemilikan != null || !sNotelp.isEmpty() || !sBantuan.isEmpty() || !sTitip.isEmpty()) {
                        //if (jlhfoto >= 4) {
                            new SimpanData().execute();
                      //  } else {
                         //   Toast.makeText(Angkringan.this, "Jumlah Foto Minimal 4!", Toast.LENGTH_SHORT).show();
                       // }
                    } else {
                        Toast.makeText(Angkringan.this, "Isi Semua Data terlebeh Dahulu!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        rdYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sTitip = "ya";
                dialogInputan(1);
            }
        });

        blockJenis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rdYa:
                        sTitip = "ya";
                        break;
                    case R.id.rdTidak:
                        sTitip = "tidak";
                        inpTitip = null;
                        break;
                }
            }
        });

    /*    blockBantuan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rdsupply:
                        sBantuan = "supply barang";
                        inpBantuan = null;
                        break;
                    case R.id.rdPemasaran:
                        sBantuan = "pemasaran";
                        inpBantuan = null;
                        break;
                    case R.id.rdModal:
                        sBantuan = "tambah modal";
                        inpBantuan = null;
                        break;
                    case R.id.rdlain:
                        sBantuan = "lain-lain";
                        dialogInputan(2);
                        break;
                }
            }
        }); */

        new GetData().execute();

    }

    public void dialogInputan(final Integer id){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Angkringan.this, R.style.DialogPutih);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_input, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        Button btnPin = (Button) dialogView.findViewById(R.id.btnSimpan);
        final EditText inputan = (EditText) dialogView.findViewById(R.id.inputan);
        if (id == 1) {
            if (inpTitip != null) {
                inputan.setText(inpTitip);
            }
        }else {
            if (inpBantuan != null) {
                inputan.setText(inpBantuan);
            }
        }
        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputan.getText().length() > 0){
                    if (id == 1){
                        inpTitip = inputan.getText().toString();
                    }else {
                        inpBantuan = inputan.getText().toString();
                    }
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(Angkringan.this, "Isi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clipData = data.getClipData();

                if (clipData != null){
                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri uri = clipData.getItemAt(i).getUri();
                        try {
                            InputStream is = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            bitmaps.add(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }

                    }
                }else {
                    Uri uri = data.getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }

                txtFoto.setText("Foto -  Already Uploads " + bitmaps.size());
                jlhfoto = bitmaps.size();
            }
        }
    }

    public void alamat(View view) {
        sNama = inpNama.getText().toString();
        sNotelp = inpNotelp.getText().toString();
        sAlamat = inpAlamat.getText().toString();
        sJuragan = inpNamajuragan.getText().toString();
        sNoJuaraga = inpNoJuaragan.getText().toString();
        Intent intent = new Intent(getApplicationContext(), ChangeAddress.class);
        intent.putExtra("nama", sNama);
        intent.putExtra("notelp", sNotelp);
        intent.putExtra("alamat", sAlamat);
        intent.putExtra("juragan", sJuragan);
        intent.putExtra("kepemilikan", sKepemilikan);
        intent.putExtra("no_juragan", sNoJuaraga);
        intent.putExtra("jenis", sJenis);
        intent.putExtra("terima_titip", sTitip);
        intent.putExtra("jenis_bantuan", sBantuan);
        intent.putExtra("inpTitip", inpTitip);
        intent.putExtra("inpBantuan", inpBantuan);
        intent.putExtra("no_juragan", sNoJuaraga);
        intent.putExtra("id_kelurahan", id_kelurahan);
        if (koordinat != null){
            String[] koor = koordinat.split(",");
            lat = koor[0];
            lang = koor[1];
        }else {
            lat = "0";
            lang = "0";
        }
        intent.putExtra("latitude", Double.valueOf(lat));
        intent.putExtra("longtitude", Double.valueOf(lang));
        startActivity(intent);
    }

    public void blockBantuan(View view) {
        switch (view.getId()){
            case R.id.rdsupply:
                if (rdSupply.isChecked()){
                    sBantuan1 = "supply barang";
                }else {
                    sBantuan1 = null;
                }
                bantuan[0]=sBantuan1;
                sBantuan = sBantuan1;
                inpBantuan = null;
                break;
            case R.id.rdPemasaran:
                if (rdPemsaran.isChecked()) {
                    sBantuan2 = "pemasaran";
                }else {
                    sBantuan2 = null;
                }
                sBantuan = sBantuan2;
                bantuan[1]=sBantuan2;
                inpBantuan = null;
                break;
            case R.id.rdModal:
                if (rdModal.isChecked()) {
                    sBantuan3 = "tambah modal";
                }else {
                    sBantuan3 = null;
                }
                bantuan[2]=sBantuan3;
                sBantuan = sBantuan3;
                inpBantuan = null;
                break;
            case R.id.rdlain:
                if (rdLain.isChecked()){
                    sBantuan4 = "lain-lain";
                    dialogInputan(2);
                }else {
                    sBantuan4 = null;
                    inpBantuan = null;
                }
                bantuan[3]=sBantuan4;
                sBantuan = sBantuan4;
                break;
        }

    }

    private class GetData extends AsyncTask<String, Void, JSONObject> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Angkringan.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            JSONObject json = jsonParser.makeHttpRequest(urlGetData, "POST", params);

            return json;
        }


        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {
                        JSONArray daftar = new JSONArray(result.getString("data"));
                        jenis = new String[daftar.length()];
                        if (daftar.length() > 0) {
                            for (int i = 0; i < daftar.length(); i++) {
                                JSONObject angkringan = daftar.getJSONObject(i);

                                jenis[i] = angkringan.getString("nama");

                            }

                        }

                        adapter2 = new ArrayAdapter<String>(Angkringan.this, android.R.layout.simple_spinner_item, jenis);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_jenis.setAdapter(adapter2);

                        if (sJenis != null){
                            sp_jenis.setSelection(adapter2.getPosition(sJenis));
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString (Bitmap bitmap){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();

            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            //Log.d("encode", encodedImage);

            return encodedImage;
    }

    private class SendFoto extends AsyncTask<String, Void, JSONObject> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected JSONObject doInBackground(String... args) {


            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", id_angkringan));
            for (int i = 0; i < bitmaps.size(); i++){
                String image =imageToString(bitmaps.get(i));
                params.add(new BasicNameValuePair("image"+i, image));
            }

            params.add(new BasicNameValuePair("nama", inpNama.getText().toString()));
            params.add(new BasicNameValuePair("banyak", String.valueOf(bitmaps.size())));

            JSONObject json = jsonParser.makeHttpRequest(urlUploadFoto, "POST", params);

            return json;
        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {
                       //if (ke == bitmaps.size()) {
                        if (berhasil.equals("tidak")) {
                           showwarnig(2);
                        }else {
                            showwarnig(1);
                        }
                      // }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class SimpanData extends AsyncTask<String, Void, JSONObject> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Angkringan.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("nama", sNama));
            params.add(new BasicNameValuePair("notelp", sNotelp));
            params.add(new BasicNameValuePair("alamat", sAlamat));
            params.add(new BasicNameValuePair("koordinat", koordinat));
            params.add(new BasicNameValuePair("juragan", sJuragan));
            params.add(new BasicNameValuePair("kepemilikan", sKepemilikan));
            params.add(new BasicNameValuePair("jenis", sJenis));
            params.add(new BasicNameValuePair("no_juragan", sNoJuaraga));
            params.add(new BasicNameValuePair("terima_titip", sTitip));
            for (int i =0; i<4; i++) {
                params.add(new BasicNameValuePair("jenis_bantuan"+i, bantuan[i]));
            }
            params.add(new BasicNameValuePair("titip", inpTitip));
            params.add(new BasicNameValuePair("bantuan", inpBantuan));
            params.add(new BasicNameValuePair("id_kelurahan", id_kelurahan.toString()));
            params.add(new BasicNameValuePair("id_penugasan", id_penugasan.toString()));
            params.add(new BasicNameValuePair("nama_kelompok", sNamaKelompok));

            JSONObject json = jsonParser.makeHttpRequest(urlSimpanData, "POST", params);

            return json;
        }


        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    }else  if (result.getInt("error") == 2){
                        showwarnig(3);
                    }
                    else {
                        berhasil = result.getString("berhasil");
                        id_angkringan =  result.getString("id");
                        if (bitmaps.size() == 0){
                            if (berhasil.equals("tidak")) {
                               showwarnig(2);
                            }else {
                                showwarnig(1);
                            }
                        }else {
                            // for (int i = 0; i < bitmaps.size(); i++){
                            //    String image =imageToString(bitmaps.get(i));
                            //      ke = i+1;
                            new SendFoto().execute();
                        }
                       // }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void showwarnig(Integer id){
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(Angkringan.this);
        if (id == 1) {
            builder1.setMessage("Berhasil Menyimpan Data!");
        } else if (id==2){
            builder1.setMessage("Berhasil Menyimpan Data Tapi Alamat di Luar Kelurahan yang Dipilih!");
        }else {
            builder1.setMessage("Angminle ini sudah didata!");
        }
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    }
                });


        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
