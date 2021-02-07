package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.adapter.adapter_horeka;
import ventures.g45.kebunsehati.surveyor.modal.modal_produktitip;
import ventures.g45.kebunsehati.surveyor.modal.modal_horeka;

public class DaftarTitip extends AppCompatActivity {

    ImageView icoback;
    ListView list_titipjual;
    TextView txtNodata;
    String noHp, defaultUrl, urlGetDaftar, id_horeka, urlUpdateTitipJual;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private List<modal_produktitip> list2;
    private List<modal_horeka> list;
    adapter_horeka adapterHoreka;
    Intent intent;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_titip);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        intent = getIntent();
        id_horeka = intent.getStringExtra("id_horeka");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetDaftar = defaultUrl + "getdaftartitip.html";
        urlUpdateTitipJual = defaultUrl + "getupdatetitip.html";

        icoback = findViewById(R.id.icoBack);
        list_titipjual = findViewById(R.id.list_produk);
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
                modal_produktitip modal = list2.get(position);
                AddtDialog(modal.getId_produk(), modal.getNama_produk(), Integer.valueOf(modal.getJumlah_titip()), Integer.valueOf(modal.getHarga()), modal.getWaktu_titip());
                Log.d("harga", modal.getHarga());
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
            params.add(new BasicNameValuePair("id_horeka", id_horeka));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetDaftar, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DaftarTitip.this);
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
                            list2 = new ArrayList<>();
                            JSONArray data = new JSONArray(result.getString("data"));
                            if (data.length() > 0) {

                                for (int j = 0; j < data.length(); j++) {

                                    final JSONObject daftar = data.getJSONObject(j);

                                    modal_horeka modal = new modal_horeka();
                                    modal.setNama(daftar.getString("nama_produk"));

                                    modal_produktitip produk = new modal_produktitip();
                                    produk.setId_produk(daftar.getString("id_produk"));
                                    produk.setNama_produk(daftar.getString("nama_produk"));
                                    produk.setHarga(daftar.getString("harga"));
                                    produk.setJumlah_titip(daftar.getString("jumlah_titip"));
                                    produk.setWaktu_titip(daftar.getString("waktu_titip"));
                                    list2.add(produk);

                                    list.add(modal);
                                    adapterHoreka = new adapter_horeka(DaftarTitip.this, R.layout.cv_penugasan, list);
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

    public void AddtDialog(final String id_produk, final String nama, final Integer jumlahTitip, final Integer harga, final String waktu_titip) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(DaftarTitip.this, R.style.DialogPutih);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_edit_titipjual, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        final TextView txtNama, txtHarga, txtJumlahTitip, txtSetoran;
        final EditText txtJumlahLaku;
        final RadioButton rdYa, rdTidak;
        txtNama = dialogView.findViewById(R.id.txtNama);
        txtHarga = dialogView.findViewById(R.id.txtHargaBarang);
        Button simpan = dialogView.findViewById(R.id.btnSimpan);
        ImageView close = dialogView.findViewById(R.id.btnClose);
        txtJumlahTitip = dialogView.findViewById(R.id.txJumlahTitip);
        txtSetoran = dialogView.findViewById(R.id.txtSetoran);
        txtJumlahLaku = dialogView.findViewById(R.id.txtJumlahLaku);
        rdYa = dialogView.findViewById(R.id.rdYa);
        rdTidak = dialogView.findViewById(R.id.rdTidak);

        txtNama.setText(nama);
        Log.d("harga2", harga.toString());
        txtHarga.setText("Rp " + decimalFormat.format(harga));
        txtJumlahTitip.setText(decimalFormat.format(jumlahTitip) + "");
        txtSetoran.setText(decimalFormat.format(0));

        txtJumlahLaku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    Integer hitung = Integer.valueOf(s.toString()) * harga;
                    txtSetoran.setText(decimalFormat.format(hitung));
                }else {
                    txtSetoran.setText(decimalFormat.format(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer update;
                if (rdYa.isChecked()){
                    update = 1;
                }else {
                    update = 0;
                }
                alertDialog.dismiss();
                new UpdateTitipJual().execute(id_produk, String.valueOf((jumlahTitip - Integer.valueOf(txtJumlahLaku.getText().toString()))), update.toString(), waktu_titip, harga.toString());
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private class UpdateTitipJual extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DaftarTitip.this);
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
                    Toast.makeText(DaftarTitip.this, "Gagal Melakukan Perubahan!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(DaftarTitip.this, "Berhasil Menyimpan Perubahan!", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), DaftarTitip.class);
                    intent.putExtra("id_horeka", id_horeka);
                    startActivity(intent);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("id_produk", strings[0]));
            params.add(new BasicNameValuePair("jumlah_laku", strings[1]));
            params.add(new BasicNameValuePair("update", strings[2]));
            params.add(new BasicNameValuePair("waktu_titip", strings[3]));
            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("harga", strings[4]));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlUpdateTitipJual, "POST", params);
            return jsonObject;
        }
    }

}