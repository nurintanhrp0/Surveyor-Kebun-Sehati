package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserAccount extends AppCompatActivity {

    TextView txtNama, txtAlamat, txtNohp, txtENoktp;
    Button btnLogout;
    Intent intent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Integer id_horeka;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String defaultUrl, dataUrl, urlGetProfil, sPin, urlUpdatePwd, noHp;
    ImageView icoBack;
    LinearLayout home, order, invoice, akun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetProfil = defaultUrl + "getKurir.html";

        txtNama = findViewById(R.id.txtNama);
        txtAlamat = findViewById(R.id.txtPIC);
        txtNohp = findViewById(R.id.txtNohp);
        txtENoktp = findViewById(R.id.txtEmail);
        btnLogout = findViewById(R.id.btnLogOut);
        icoBack = findViewById(R.id.icoBack);
        home = findViewById(R.id.home);
        order = findViewById(R.id.order);
        invoice = findViewById(R.id.invoice);
        akun = findViewById(R.id.akun);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear().apply();
                intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

        new GetProfil().execute();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private class GetProfil extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetProfil, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserAccount.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if ((pDialog != null) && (pDialog.isShowing()))
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }else {
                        txtNama.setText(result.getString("nama"));
                        txtAlamat.setText(result.getString("alamat"));
                        txtNohp.setText(result.getString("no_hp"));
                        txtENoktp.setText(result.getString("noktp"));
                    }
                }else {
                    //Toast.makeText(getApplicationContext(), "Ups! Menu yang kamu pilih belum tersedia di outlet ini.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}