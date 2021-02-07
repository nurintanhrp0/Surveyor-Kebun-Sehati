package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.adapter.adapter_penugasan;
import ventures.g45.kebunsehati.surveyor.modal.modal_angkringan;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_angkringan;
import ventures.g45.kebunsehati.surveyor.modal.modal_penugasan;

public class DaftarAnggkringan extends AppCompatActivity {

    ImageView icoback;
    ListView list_penugasan;
    TextView txtNodata, txtTarget, txtDone;
    private List<modal_penugasan> list;
    adapter_angkringan adapter;
    String noHp;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String defaultUrl, urlGetDaftar;
    adapter_penugasan adapterPenugasan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_anggkringan);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetDaftar = defaultUrl + "getorderankurir.html";

        icoback = findViewById(R.id.icoBack);
        list_penugasan = findViewById(R.id.list_penugasan);
        txtNodata = findViewById(R.id.txtNoData);
        txtTarget = findViewById(R.id.txtTarget);
        txtDone = findViewById(R.id.txtDone);
        list =new ArrayList<>();

        icoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_penugasan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modal_penugasan modalPenugasan = (modal_penugasan) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), Angkringan.class);
                editor.putInt("id_kelurahan", Integer.valueOf(modalPenugasan.getId())).apply();
                intent.putExtra("id_kelurahan", Integer.valueOf(modalPenugasan.getId()));
                startActivity(intent);

            }
        });

        new GetDaftar().execute();
    }

    
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private class GetDaftar extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetDaftar, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DaftarAnggkringan.this);
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
                        if (result.has("penugasan")) {
                            txtNodata.setVisibility(View.GONE);
                            txtTarget.setText("Target : " + result.getString("target"));
                            txtDone.setText("Target yang Sudah Dicapai : " + result.getString("done"));
                            editor.putInt("id_penugasan", result.getInt("id_penugasan")).apply();
                            JSONArray data = new JSONArray(result.getString("penugasan"));
                            if (data.length() > 0) {

                                for (int j = 0; j < data.length(); j++) {

                                    final JSONObject daftar = data.getJSONObject(j);

                                    modal_penugasan modalPenugasan = new modal_penugasan();
                                    modalPenugasan.setId(daftar.getString("id_kelurahan"));
                                    modalPenugasan.setNama(daftar.getString("nama"));
                                    modalPenugasan.setThumbnail(daftar.getString("thumbnail"));

                                    list.add(modalPenugasan);
                                    adapterPenugasan = new adapter_penugasan(DaftarAnggkringan.this, R.layout.cv_penugasan, list);
                                    list_penugasan.setAdapter(adapterPenugasan);

                                }
                            }

                        }


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
