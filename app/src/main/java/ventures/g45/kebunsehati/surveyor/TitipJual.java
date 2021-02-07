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

import ventures.g45.kebunsehati.surveyor.adapter.adapter_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_alamat_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_penugasan;

public class TitipJual extends AppCompatActivity {

    ImageView icoback;
    ListView list_titipjual;
    TextView txtNodata;
    String noHp, defaultUrl, urlGetDaftar;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private List<modal_horeka> list;
    adapter_horeka adapterHoreka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titip_jual);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetDaftar = defaultUrl + "gettitipjual.html";

        icoback = findViewById(R.id.icoBack);
        list_titipjual = findViewById(R.id.list_titipjual);
        txtNodata = findViewById(R.id.txtNoData);

        icoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_titipjual.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modal_horeka modalHoreka = (modal_horeka) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), DaftarTitip.class);
                intent.putExtra("id_horeka", modalHoreka.getId_horeka());
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
            pDialog = new ProgressDialog(TitipJual.this);
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
                        if (result.has("data")) {
                            txtNodata.setVisibility(View.GONE);
                            list = new ArrayList<>();
                            JSONArray data = new JSONArray(result.getString("data"));
                            if (data.length() > 0) {

                                for (int j = 0; j < data.length(); j++) {

                                    final JSONObject daftar = data.getJSONObject(j);

                                    modal_horeka modalHoreka = new modal_horeka();
                                    modalHoreka.setId_horeka(daftar.getString("id"));
                                    modalHoreka.setNama(daftar.getString("nama"));

                                    list.add(modalHoreka);
                                    adapterHoreka = new adapter_horeka(TitipJual.this, R.layout.cv_penugasan, list);
                                    list_titipjual.setAdapter(adapterHoreka);

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