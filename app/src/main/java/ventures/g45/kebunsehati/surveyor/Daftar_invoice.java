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

import ventures.g45.kebunsehati.surveyor.adapter.adapter_invoice;
import ventures.g45.kebunsehati.surveyor.modal.modal_invoice;

public class Daftar_invoice extends AppCompatActivity {

    ImageView icoBack;
    ListView list_invoice;
    adapter_invoice adapterInvoice;
    private List<modal_invoice> list;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sNama, defaultUrl, dataUrl, urlGetInvoice, id_invoice, noHp;
    Integer id_horeka, angminle;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    TextView txtNoData;
    Intent intent;
    LinearLayout home, order, invoice, akun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_invoice);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetInvoice = defaultUrl + "getinvoicehoreka.html";

        icoBack = findViewById(R.id.icoBack);
        txtNoData = findViewById(R.id.txtNoData);
        list_invoice = findViewById(R.id.list_invoice);
        home = findViewById(R.id.home);
        order = findViewById(R.id.order);
        invoice = findViewById(R.id.invoice);
        akun = findViewById(R.id.akun);
        list = new ArrayList<>();

        list_invoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modal_invoice modalInvoice = (modal_invoice) parent.getItemAtPosition(position);
               Intent intent =  new Intent(getApplicationContext(), Detail_invoice.class);
               intent.putExtra("id_invoice", modalInvoice.getId_invoice());
               intent.putExtra("id_horeka", modalInvoice.getId_horeka());
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


        new GetDaftarInvoice().execute();
    }

    private class GetDaftarInvoice extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetInvoice, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Daftar_invoice.this);
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
                            list_invoice.setVisibility(View.VISIBLE);
                            JSONArray daftar = new JSONArray(result.getString("data"));
                            if (daftar.length() > 0) {
                                for (int i = 0; i < daftar.length(); i++) {
                                    JSONObject invoice = daftar.getJSONObject(i);

                                    modal_invoice modalInvoice = new modal_invoice();
                                    modalInvoice.setId_invoice(invoice.getString("id_invoice"));
                                    modalInvoice.setId_horeka(invoice.getString("id_horeka"));
                                    modalInvoice.setTanggal(invoice.getString("tanggal"));
                                    modalInvoice.setDue_date(invoice.getString("due_date"));
                                    modalInvoice.setTotal(invoice.getInt("jumlah"));
                                    modalInvoice.setStatus(invoice.getString("status"));
                                    list.add(modalInvoice);
                                    adapterInvoice = new adapter_invoice(Daftar_invoice.this, R.layout.cv_invoice, list);
                                    list_invoice.setAdapter(adapterInvoice);

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