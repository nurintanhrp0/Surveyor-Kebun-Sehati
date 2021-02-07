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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_daftar_order;

public class DaftarOrderan extends AppCompatActivity {

    ImageView icoBack;
    ListView list_orderan;
    adapter_daftar_order adapterDaftarOrder;
    private List<modal_orderan_sales> list;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sNama, defaultUrl, dataUrl, urlGetOrder, id_order, noHp;
    Integer id_horeka, angminle;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    TextView txtNoData;
    LinearLayout home, order, invoice, akun;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_orderan);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetOrder = defaultUrl + "getDaftarOrderHoreka.html";

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        icoBack = findViewById(R.id.icoBack);
        txtNoData = findViewById(R.id.txtNoData);
        list_orderan = findViewById(R.id.list_orderan);
        home = findViewById(R.id.home);
        order = findViewById(R.id.order);
        invoice = findViewById(R.id.invoice);
        akun = findViewById(R.id.akun);
        list = new ArrayList<>();

        list_orderan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modal_orderan_sales modalOrderan = (modal_orderan_sales) parent.getItemAtPosition(position);
                Intent intent =  new Intent(getApplicationContext(), DetailOrder.class);
                intent.putExtra("id_order", modalOrderan.getId());
                intent.putExtra("id_horeka", modalOrderan.getId_horeka());
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

        new GetDaftarOrder().execute();
    }

    private class GetDaftarOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetOrder, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DaftarOrderan.this);
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
                        if(result.has("data")) {
                            txtNoData.setVisibility(View.GONE);
                            list_orderan.setVisibility(View.VISIBLE);
                            JSONArray daftar = new JSONArray(result.getString("data"));
                            if (daftar.length() > 0) {
                                for (int i = 0; i < daftar.length(); i++) {
                                    JSONObject orderan = daftar.getJSONObject(i);

                                    modal_orderan_sales modalOrderan = new modal_orderan_sales();
                                    modalOrderan.setId(orderan.getString("id"));
                                    modalOrderan.setId_horeka(orderan.getString("id_horeka"));
                                    modalOrderan.setTotal(orderan.getString("total"));
                                    modalOrderan.setQty(orderan.getString("qty"));
                                    modalOrderan.setStatus_order(orderan.getString("status_order"));
                                    modalOrderan.setTanggal(orderan.getString("tanggal"));
                                    modalOrderan.setTglOrder(orderan.getString("tglorder"));
                                    list.add(modalOrderan);
                                    adapterDaftarOrder = new adapter_daftar_order(DaftarOrderan.this, R.layout.cv_invoice, list);
                                    list_orderan.setAdapter(adapterDaftarOrder);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}