package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterMember extends AppCompatActivity {

    EditText inpNama, inpPic, inpNohp, inpEmail, inpNowa;
    TextView inpAlamat;
    Spinner inpKota;
    Button btnDaftar;
    Intent intent;
    String[] id_kota, nama_kota;
    JSONParser jsonParser = new JSONParser();
    String defaultUrl, urlGetKota, sKota, sId_kota, sNama, sAlamat, sPIC, sNohp, sNowa, sEmail, urlSaveMember, alamat, koordinat, cabang, noHp;
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_member);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetKota = defaultUrl + "getKota.html";
        urlSaveMember = defaultUrl + "simpanMemberHoreka.html";

        intent = getIntent();
        alamat =  intent.getStringExtra("alamat");
        koordinat =  intent.getStringExtra("koordinat");
        cabang = intent.getStringExtra("nama_cabang");

        inpNama =  findViewById(R.id.inpNama);
        inpAlamat =  findViewById(R.id.inpAlamat);
        inpPic = findViewById(R.id.inpPIC);
        inpNohp = findViewById(R.id.inpNohp);
        inpEmail = findViewById(R.id.inpEmail);
        inpNowa = findViewById(R.id.inpNowa);
        inpKota = findViewById(R.id.inpKota);
        btnDaftar = findViewById(R.id.btnregis);

        if (alamat != null){
            inpAlamat.setText(alamat);
        }

        inpAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ChangeAddress.class);
                finish();
                intent.putExtra("dari", 1);
                startActivity(intent);
            }
        });

        inpKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));

                sKota = ((TextView) parent.getChildAt(0)).getText().toString();
                if (position != 0) {
                    sId_kota = id_kota[position];

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sId_kota.equals("0")){
                    Toast.makeText(RegisterMember.this, "Silahkan pilih kota terlebih dahulu", Toast.LENGTH_SHORT).show();
                }else {
                    sNama = inpNama.getText().toString();
                    sAlamat = inpAlamat.getText().toString();
                    sPIC = inpPic.getText().toString();
                    sNohp = inpNohp.getText().toString();
                    sNowa = inpNowa.getText().toString();
                    sEmail = inpEmail.getText().toString();
                    if (!sNama.isEmpty() && !sAlamat.isEmpty() && !sPIC.isEmpty() && !sNohp.isEmpty() && !sNowa.isEmpty()) {
                        if (sEmail.isEmpty()) {
                            new SaveMember().execute();
                        } else {
                            if (isEmailValid(sEmail)) {
                                new SaveMember().execute();
                            } else {
                                Toast.makeText(getApplicationContext(), "Email yang anda isikan tidak valid", Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Mohon untuk mengisi semua data!", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        new GetKota().execute();
    }

    private class GetKota extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterMember.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                JSONArray daftar = new JSONArray(result.getString("data"));
                id_kota =  new String[daftar.length() + 1];
                nama_kota = new String[daftar.length() + 1];
                id_kota[0] = "0";
                nama_kota[0]="-Pilih Kota-";
                if (daftar.length()>0){
                    for (int i =0; i<daftar.length(); i++){
                        JSONObject kota = daftar.getJSONObject(i);

                        id_kota[i + 1] = kota.getString("id_kota");
                        nama_kota[i + 1] = kota.getString("nama_kota");
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterMember.this, android.R.layout.simple_spinner_item, nama_kota);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inpKota.setAdapter(adapter);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("param", "0"));
            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetKota, "POST", params);
            return jsonObject;
        }
    }

    public static boolean isEmailValid(String email) {
        String domainChars = "a-z0-9\\-";
        String atomChars = "a-z0-9\\Q!#$%&'*+-/=?^_`{|}~\\E";
        String emailRegex = "^" + dot(atomChars) + "@" + dot(domainChars) + "$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static String dot(String chars) {
        return "[" + chars + "]+(?:\\.[" + chars + "]+)*";
    }

    private class SaveMember extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterMember.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result.getInt("error") == 1){
                    Toast.makeText(RegisterMember.this, "Nomor Handphone atau Nama ini telah terdaftar sebagai akun horeka!", Toast.LENGTH_SHORT).show();
                }else {
                    showwarning();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("nama", sNama));
            params.add(new BasicNameValuePair("id_kota", sId_kota));
            params.add(new BasicNameValuePair("alamat", sAlamat));
            params.add(new BasicNameValuePair("pic", sPIC));
            params.add(new BasicNameValuePair("no_hp", sNohp));
            params.add(new BasicNameValuePair("noWa", sNowa));
            params.add(new BasicNameValuePair("email", sEmail));
            params.add(new BasicNameValuePair("koordinat", koordinat));
            params.add(new BasicNameValuePair("cabang", cabang));
            params.add(new BasicNameValuePair("noHp", noHp));
            JSONObject jsonObject = jsonParser.makeHttpRequest(urlSaveMember, "POST", params);
            return jsonObject;
        }
    }

    public void showwarning(){
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(RegisterMember.this, R.style.DialogPutih);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_warning, null);
        dialog.setView(dialogView);
        Button icOk = dialogView.findViewById(R.id.btnOk);
        TextView isi = dialogView.findViewById(R.id.isi);
        final androidx.appcompat.app.AlertDialog alertDialog = dialog.create();

        icOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
                intent = new Intent(getApplicationContext(), DaftarMember.class);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}