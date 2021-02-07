package ventures.g45.kebunsehati.surveyor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.adapter.adapter_angkringan;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_horeka;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_horeka_alamat;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_penugasan;
import ventures.g45.kebunsehati.surveyor.modal.modal_alamat_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_penugasan;

public class DaftarMember extends AppCompatActivity {

    ImageView icoback, icoMap, icoAdd;
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
    private List<modal_horeka> list3;
    private List<modal_alamat_horeka> list4;
    adapter_horeka adapterHoreka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_member);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetDaftar = defaultUrl + "getorderankurir.html";

        icoback = findViewById(R.id.icoBack);
        list_penugasan = findViewById(R.id.list_penugasan);
        txtNodata = findViewById(R.id.txtNoData);
        icoAdd = findViewById(R.id.tambahMember);
        icoMap = findViewById(R.id.map);
        list =new ArrayList<>();

        list3 = new ArrayList<>();
        list4 = new ArrayList<>();

        icoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), RegisterMember.class);
                startActivity(intent);
            }
        });

        icoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), PetaAnggota.class);
                startActivity(intent);
            }
        });

        icoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_penugasan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(DaftarMember.this, R.style.DialogPutih);
                LayoutInflater inflater = LayoutInflater.from(DaftarMember.this);
                View dialogView = inflater.inflate(R.layout.popup_alamat, null);
                dialog.setView(dialogView);
                final AlertDialog alertDialog = dialog.create();
                ListView listView = dialogView.findViewById(R.id.list_alamat);
                final ArrayList<modal_alamat_horeka> list_alamat = new ArrayList<>();
                adapter_horeka_alamat adapter;

                modal_horeka modalHoreka = (modal_horeka) parent.getItemAtPosition(position);
                String id_horeka = modalHoreka.getId_horeka();

                for (int i=0; i<list4.size(); i++){
                    if (list4.get(i).getId_horeka().equals(id_horeka)){
                        modal_alamat_horeka modalAlamatHoreka = new modal_alamat_horeka();
                        modalAlamatHoreka.setId_horeka(list4.get(i).getId_horeka());
                        modalAlamatHoreka.setId(list4.get(i).getId());
                        modalAlamatHoreka.setAlamat(list4.get(i).getAlamat());
                        modalAlamatHoreka.setKoordinat(list4.get(i).getKoordinat());
                        list_alamat.add(modalAlamatHoreka);
                    }
                }
                adapter = new adapter_horeka_alamat(DaftarMember.this, R.layout.cv_penugasan, list_alamat);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        finish();
                        Intent intent = new Intent(dialog.getContext(), Checkin.class);
                        intent.putExtra("koordinat", list_alamat.get(position).getKoordinat());
                        intent.putExtra("id_horeka", list_alamat.get(position).getId_horeka());
                        intent.putExtra("id_alamat", list_alamat.get(position).getId());
                        startActivity(intent);
                    }
                });
                alertDialog.show();

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
            pDialog = new ProgressDialog(DaftarMember.this);
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
                        if (result.has("horeka")) {
                            txtNodata.setVisibility(View.GONE);
                            JSONArray data = new JSONArray(result.getString("horeka"));
                            if (data.length() > 0) {

                                for (int j = 0; j < data.length(); j++) {

                                    final JSONObject daftar = data.getJSONObject(j);

                                    modal_horeka modalHoreka = new modal_horeka();
                                    modalHoreka.setId_horeka(daftar.getString("id"));
                                    modalHoreka.setNama(daftar.getString("nama"));

                                    JSONArray data2 = new JSONArray(daftar.getString("alamat"));
                                    if (data2.length() > 0 || daftar.has("alamat")){
                                        for (int k = 0; k<data2.length(); k++){
                                            JSONObject alamat = data2.getJSONObject(k);

                                            modal_alamat_horeka modalAlamatHoreka = new modal_alamat_horeka();
                                            modalAlamatHoreka.setId(alamat.getString("id"));
                                            modalAlamatHoreka.setId_horeka(daftar.getString("id"));
                                            modalAlamatHoreka.setAlamat(alamat.getString("alamat"));
                                            modalAlamatHoreka.setKoordinat(alamat.getString("koordinat"));
                                            list4.add(modalAlamatHoreka);
                                        }
                                    }

                                    list3.add(modalHoreka);
                                    adapterHoreka = new adapter_horeka(DaftarMember.this, R.layout.cv_penugasan, list3);
                                    list_penugasan.setAdapter(adapterHoreka);

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
