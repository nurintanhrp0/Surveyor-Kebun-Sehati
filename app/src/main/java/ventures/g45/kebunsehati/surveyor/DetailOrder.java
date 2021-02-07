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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.adapter.adapter_orderan_sales;
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;

public class DetailOrder extends AppCompatActivity {

    TextView txtStatusorder, txtIdorder, txtTanggal, txtAlamat, txtTotal, txtInvoice, txtReorder, txtNamaCabang, txtDiskon, txtPajak, txtOngkir, txtBebanOrder, txtSubtotal, tvDiskon, tvPajak, tvOngkir, tvBebanorder, txtNamaHoreka;
    ImageView icoBack;
    ListView barisanCart;
    Button btnTerima;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String defaultUrl, dataUrl, urlGetOrder, id_order, statusOrder, urlUbahStatus, urlSimpanOrder, id_invoice, id_horeka;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    adapter_orderan_sales adapterOrderan;
    private List<modal_orderan_sales> list;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        final Intent intent = getIntent();
        id_order = intent.getStringExtra("id_order");
        id_horeka = intent.getStringExtra("id_horeka");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetOrder = defaultUrl + "getDetailOrderHoreka.html";

        txtStatusorder = findViewById(R.id.txtStatusOrder);
        txtIdorder = findViewById(R.id.txtIdOrder);
        txtTanggal = findViewById(R.id.txtTanggal);
        txtAlamat = findViewById(R.id.txtAlamat);
        barisanCart = findViewById(R.id.barisanCart);
        btnTerima = findViewById(R.id.btnTerima);
        icoBack = findViewById(R.id.icoBack);
        txtTotal = findViewById(R.id.txtTotal);
        txtInvoice = findViewById(R.id.txtLink);
        txtReorder = findViewById(R.id.reorder);
        txtNamaCabang = findViewById(R.id.txtNamaCabang);
        txtDiskon = findViewById(R.id.txtDiskon);
        txtPajak = findViewById(R.id.txtPajak);
        txtOngkir = findViewById(R.id.txtOngkir);
        txtBebanOrder = findViewById(R.id.txtBebanOrder);
        txtSubtotal = findViewById(R.id.txtSubTotal);
        tvDiskon = findViewById(R.id.tvDiskon);
        tvPajak = findViewById(R.id.tvPajak);
        tvOngkir = findViewById(R.id.tvOngkir);
        tvBebanorder = findViewById(R.id.tvBebanOrder);
        txtNamaHoreka = findViewById(R.id.txtNamaHoreka);
        list = new ArrayList<>();

        txtInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), Detail_invoice.class);
                intent.putExtra("id_invoice", id_invoice);
                intent.putExtra("id_horeka", id_horeka);
                startActivity(intent);
            }
        });

        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        new GetDaftarOrder().execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), DaftarOrderan.class);
        startActivity(intent);
    }

    private class GetDaftarOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("id_order", id_order));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetOrder, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailOrder.this);
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
                        txtStatusorder.setText(result.getString("status_order"));
                        txtIdorder.setText(id_order);
                        txtTanggal.setText(result.getString("tanggal"));
                        txtNamaHoreka.setText(result.getString("nama_horeka"));
                        if (result.getString("nama_cabang").equals("null") || result.getString("nama_cabang").equals("")){
                            txtNamaCabang.setVisibility(View.GONE);
                        }
                        txtNamaCabang.setText(result.getString("nama_cabang"));
                        txtAlamat.setText(result.getString("alamat"));
                        statusOrder = result.getString("status_order");
                        txtTotal.setText("Rp " + decimalFormat.format(result.getInt("total")));
                        txtDiskon.setText("- Rp " + decimalFormat.format(result.getInt("diskontotal")));
                        tvPajak.setText("Pajak " + result.getInt("besar_pajak") + "%");
                        txtPajak.setText("Rp " + decimalFormat.format(result.getInt("pajak")));
                        txtOngkir.setText("Rp "+ decimalFormat.format(result.getInt("ongkir")));
                        txtBebanOrder.setText("Rp " + decimalFormat.format(result.getInt("beban_order")));
                        txtSubtotal.setText("Rp " + decimalFormat.format(result.getInt("subtotal")));
                        if (result.getInt("diskontotal") == 0){
                            txtDiskon.setVisibility(View.GONE);
                            tvDiskon.setVisibility(View.GONE);
                        }
                        if (result.getInt("pajak") == 0){
                            txtPajak.setVisibility(View.GONE);
                            tvPajak.setVisibility(View.GONE);
                        }
                        if (result.getInt("ongkir") == 0){
                            txtOngkir.setVisibility(View.GONE);
                            tvOngkir.setVisibility(View.GONE);
                        }
                        if (result.getInt("beban_order") == 0){
                            txtBebanOrder.setVisibility(View.GONE);
                            tvBebanorder.setVisibility(View.GONE);
                        }
                        id_invoice = result.getString("id_invoice");
                        if (id_invoice.equals("null")){
                            txtInvoice.setVisibility(View.GONE);
                        }

                      /*  if (statusOrder.equals("Diterima")){
                            btnTerima.setText("Order Kembali");
                            btnTerima.setVisibility(View.VISIBLE);
                            btnTerima.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                        } else {
                            if ((statusOrder.equals("Sedang Disiapkan")) || (statusOrder.equals("Dikirim"))) {
                                btnTerima.setText("Sudah menerima pesanan anda?");
                                btnTerima.setVisibility(View.VISIBLE);
                            } else {
                                btnTerima.setVisibility(View.INVISIBLE);
                            }
                        }*/

                        JSONArray daftar = new JSONArray(result.getString("data"));
                        if (daftar.length()>0){
                            for (int i = 0; i<daftar.length(); i++){
                                JSONObject produk = daftar.getJSONObject(i);

                                modal_orderan_sales modalOrderan = new modal_orderan_sales();
                                modalOrderan.setNama(produk.getString("nama"));
                                modalOrderan.setQty(produk.getString("qty"));
                                modalOrderan.setHarga(produk.getString("harga_satuan"));
                                modalOrderan.setTotal(produk.getString("totalproduk"));
                                modalOrderan.setSatuan(produk.getString("satuan"));
                                modalOrderan.setCatatan(produk.getString("catatan"));
                                modalOrderan.setHarga_normal(produk.getString("harga_normal"));
                                modalOrderan.setDari(0);
                                list.add(modalOrderan);
                                adapterOrderan = new adapter_orderan_sales(DetailOrder.this, R.layout.cv_orderan_sales, list);
                                barisanCart.setAdapter(adapterOrderan);
                                Helper.getListViewSize(barisanCart);

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
