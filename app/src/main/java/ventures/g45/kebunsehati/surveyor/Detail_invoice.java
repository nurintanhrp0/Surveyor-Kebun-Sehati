package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.adapter.adapter_orderan_sales;
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;

public class Detail_invoice extends AppCompatActivity implements TransactionFinishedCallback {

    ImageView icback;
    TextView txtNilaitotal, txtNilaiTempo, txtNama, txtNohp, txtEmail, txtAlamat, txtSubtotal, txtPajak, tvPajak, lainnya, txtNamaAlamat, txtDiskon, txtBebanOrder, txtOngkir, txtTitle, txtJatuhTempo, txtNoInvoice;
    Button btnKonfirmasi;
    ListView list_produk, list_produk2;
    Integer idPembayaran = 0, total;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    adapter_orderan_sales adapterOrderan;
    private List<modal_orderan_sales> list1;
    private List<modal_orderan_sales> list2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sNama, defaultUrl, dataUrl, urlGetInvoice, id_invoice, id_horeka, urlDoPembayarab, email;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    LinearLayout blockPilihanPembayaran;
    String tujuan, atas_nama, urlimagepembayaran, metode_pembayaran, sPembayaran, sMetode,idMidtrans, id_order;
    RelativeLayout blockPembayaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_invoice);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetInvoice = defaultUrl + "getdetailinvoicehoreka.html";
        urlDoPembayarab = defaultUrl + "dopembayaran.html";

        Intent intent = getIntent();
        id_invoice = intent.getStringExtra("id_invoice");
        id_horeka = intent.getStringExtra("id_horeka");

        icback= findViewById(R.id.icoBack);
        txtNilaitotal = findViewById(R.id.txtNilaiTotal);
        txtNilaiTempo = findViewById(R.id.txtNilaiTempo);
        txtNama = findViewById(R.id.txtNama);
        txtNohp = findViewById(R.id.txtNohp);
        txtEmail = findViewById(R.id.txtEmail);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtSubtotal = findViewById(R.id.txtSubTotal);
        txtPajak = findViewById(R.id.txtPajak);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);
        tvPajak = findViewById(R.id.tvPajak);
        list_produk = findViewById(R.id.list_produk);
        blockPilihanPembayaran = findViewById(R.id.blockPilihanPembayaran);
        blockPembayaran = findViewById(R.id.blockPembayaran);
        lainnya = findViewById(R.id.lainnya);
        list_produk2 = findViewById(R.id.list_produk2);
        txtNamaAlamat = findViewById(R.id.txtNamaAlamat);
        txtDiskon = findViewById(R.id.txtDiskon);
        txtBebanOrder = findViewById(R.id.txtBebanOrder);
        txtOngkir = findViewById(R.id.txtOngkir);
        txtTitle = findViewById(R.id.txtTitle);
        txtJatuhTempo = findViewById(R.id.txtJatuhTempo);
        txtNoInvoice = findViewById(R.id.txtNoInvoice);

        icback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lainnya.getText().toString().equals("Lainnya")){
                    list_produk2.setVisibility(View.VISIBLE);
                    list_produk.setVisibility(View.GONE);
                    lainnya.setText("Lebih Sedikit");
                }else {
                    list_produk2.setVisibility(View.GONE);
                    list_produk.setVisibility(View.VISIBLE);
                    lainnya.setText("Lainnya");
                }
            }
        });

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*Intent intent = new Intent(getApplicationContext(), InformasiPembayaran.class);
                    intent.putExtra("id_invoice", id_invoice);
                    intent.putExtra("tujuan", tujuan);
                    intent.putExtra("atas_nama", atas_nama);
                    intent.putExtra("urlimage", urlimagepembayaran);
                    intent.putExtra("total", total);
                    intent.putExtra("metode", metode_pembayaran);
                    intent.putExtra("class", "0");
                    startActivity(intent);*/
                if (idPembayaran == 0){
                        new SavePembayaran().execute();
                }else{
                    UserDetail userDetail = LocalDataHandler.readObject("user_details", UserDetail.class);
                    if (userDetail == null) {
                        userDetail = new UserDetail();
                        userDetail.setEmail(email);
                        LocalDataHandler.saveObject("user_details", userDetail);
                    }
                    TransactionRequest request = new TransactionRequest(id_order, total);
                    MidtransSDK.getInstance().setTransactionRequest(request);
                    //MidtransSDK.getInstance().setTransactionRequest(transactionRequest("115",2000, 1, "John"));
                    MidtransSDK.getInstance().startPaymentUiFlow(Detail_invoice.this);
                }
            }
        });

        new GetDaftarInvoice().execute();
        makePayment();
    }

    private class GetDaftarInvoice extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("id_invoice", id_invoice));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetInvoice, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Detail_invoice.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            if ((pDialog != null) && (pDialog.isShowing()))
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }else {
                        txtNilaitotal.setText("Rp " + decimalFormat.format(result.getInt("total")));
                        total = result.getInt("total");
                        email = result.getString("email");
                        id_order = result.getString("id_order");
                        txtNoInvoice.setText(id_invoice);
                        txtNilaiTempo.setText(result.getString("due_date"));
                        txtNama.setText(result.getString("nama"));
                        txtNohp.setText(result.getString("no_hp"));
                        txtEmail.setText(result.getString("email"));
                        //txtAlamat.setText(result.getString("alamat"));
                        txtSubtotal.setText("Rp " + decimalFormat.format(result.getInt("subtotal")));
                        txtPajak.setText("Rp " + decimalFormat.format(result.getInt("nilai_pajak")));
                        tvPajak.setText("Pajak (" + result.getString("pajak") + "%)");
                        /*if (result.getString("nama_cabang").equals("null")){
                            txtNamaAlamat.setVisibility(View.GONE);
                        }else {
                            txtNamaAlamat.setText(result.getString("nama_cabang"));
                        }*/
                        txtDiskon.setText("- Rp " + decimalFormat.format(result.getInt("diskon")));
                        txtOngkir.setText("Rp " + decimalFormat.format(result.getInt("ongkir")));
                        txtBebanOrder.setText("Rp " + decimalFormat.format(result.getInt("beban_order")));
                        btnKonfirmasi.setText("Terima Pembayaran " + "(Rp " + decimalFormat.format(result.getInt("total")) + ")");

                        if(result.getString("status").equals("lunas")){
                            btnKonfirmasi.setText("Sudah Titip Sales " + "(Rp " + decimalFormat.format(result.getInt("total")) + ")");
                            btnKonfirmasi.setEnabled(false);
                            btnKonfirmasi.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                            txtTitle.setText("Detail Invoice (Lunas)");
                            txtJatuhTempo.setText("Tanggal Pembayaran");
                        }

                        idPembayaran = result.getInt("id");
                        if(idPembayaran > 0){
                            btnKonfirmasi.setText("Sudah Titip Sales " + "(Rp " + decimalFormat.format(result.getInt("total")) + ")");
                            btnKonfirmasi.setEnabled(false);
                            btnKonfirmasi.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        }

                        JSONArray pembayaranArray = new JSONArray(result.getString("pembayaran"));
                        if (pembayaranArray.length() > 0) {
                            for (int i = 0; i < pembayaranArray.length(); i++) {

                                 final JSONObject pembayaran = pembayaranArray.getJSONObject(i);
                                final String no = pembayaran.getString("tujuan");
                                final String thumbnail2 = pembayaran.getString("thumbnail");
                                final String an = pembayaran.getString("atas_nama");
                                final String metode = pembayaran.getString("metode");

                                final RadioButton pembayarannya = new RadioButton(Detail_invoice.this);
                                if (pembayaran.getString("tujuan").isEmpty()) {
                                    pembayarannya.setText(pembayaran.getString("metode"));
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        pembayarannya.setText(Html.fromHtml(pembayaran.getString("metode")));
                                    } else {
                                        pembayarannya.setText(Html.fromHtml(pembayaran.getString("metode")));
                                    }
                                }
                                pembayarannya.setTextSize(14);
                                pembayarannya.setId(pembayaran.getInt("id"));
                                if (idPembayaran == pembayarannya.getId()) {
                                    pembayarannya.setChecked(true);
                                    pembayarannya.setChecked(true);
                                    tujuan = no;
                                    idPembayaran = pembayarannya.getId();
                                    atas_nama = an;
                                    urlimagepembayaran = thumbnail2;
                                    metode_pembayaran =metode;
                                }

                                pembayarannya.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tujuan = no;
                                        idPembayaran = pembayarannya.getId();
                                        atas_nama = an;
                                        urlimagepembayaran = thumbnail2;
                                        metode_pembayaran =metode;
                                        try {
                                            if(!result.getString("status").equals("lunas")) {

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                blockPilihanPembayaran.addView(pembayarannya);
                            }
                        }

                        list1 = new ArrayList<>();
                        JSONArray daftar = new JSONArray(result.getString("data"));
                        if (daftar.length()>0){
                            for (int i = 0; i<1; i++){
                                JSONObject produk = daftar.getJSONObject(i);

                                modal_orderan_sales modalOrderan = new modal_orderan_sales();
                                modalOrderan.setNama(produk.getString("nama"));
                                modalOrderan.setQty(produk.getString("qty"));
                                modalOrderan.setHarga(produk.getString("harga_satuan"));
                                modalOrderan.setTotal(produk.getString("totalproduk"));
                                modalOrderan.setSatuan(produk.getString("satuan"));
                                modalOrderan.setCatatan(produk.getString("catatan"));
                                modalOrderan.setDari(0);
                                modalOrderan.setHarga_normal(produk.getString("harga_normal"));
                                list1.add(modalOrderan);
                                adapterOrderan = new adapter_orderan_sales(Detail_invoice.this, R.layout.cv_orderan_sales, list1);
                                list_produk.setAdapter(adapterOrderan);

                            }

                        }

                        list2 = new ArrayList<>();
                        JSONArray daftar2 = new JSONArray(result.getString("data"));
                        if (daftar.length()>1){
                            for (int i = 0; i<daftar2.length(); i++){
                                JSONObject produk = daftar2.getJSONObject(i);

                                modal_orderan_sales modalOrderan = new modal_orderan_sales();
                                modalOrderan.setNama(produk.getString("nama"));
                                modalOrderan.setQty(produk.getString("qty"));
                                modalOrderan.setHarga(produk.getString("harga_satuan"));
                                modalOrderan.setTotal(produk.getString("totalproduk"));
                                modalOrderan.setSatuan(produk.getString("satuan"));
                                modalOrderan.setCatatan(produk.getString("catatan"));
                                modalOrderan.setHarga_normal(produk.getString("harga_normal"));
                                modalOrderan.setDari(0);
                                list2.add(modalOrderan);
                                adapterOrderan = new adapter_orderan_sales(Detail_invoice.this, R.layout.cv_orderan_sales, list2);
                                list_produk2.setAdapter(adapterOrderan);
                                Helper.getListViewSize(list_produk2);

                            }

                        }else {
                            lainnya.setVisibility(View.GONE);
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

    private class SavePembayaran extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_invoice", id_invoice));
            params.add(new BasicNameValuePair("id_pembayaran", idPembayaran.toString()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlDoPembayarab, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Detail_invoice.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            if ((pDialog != null) && (pDialog.isShowing()))
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }else {
                        finish();
                        Toast.makeText(Detail_invoice.this, "Pembayaran diterima, silahkan tunggu konfirmasi dari admin", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(getApplicationContext(), Detail_invoice.class);
                        intent.putExtra("id_invoice", id_invoice);
                        intent.putExtra("id_horeka", id_horeka);
                        startActivity(intent);
                    }
                }else {
                    //Toast.makeText(getApplicationContext(), "Ups! Menu yang kamu pilih belum tersedia di outlet ini.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void makePayment(){
        Log.d("build", "build");
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        String url = "https://ks.g45lab.xyz/api/";

        UIKitCustomSetting setting = MidtransSDK.getInstance().getUIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        setting.setShowEmailInCcForm(false);

        SdkUIFlowBuilder.init()
                .setContext(this)
                .setMerchantBaseUrl(url)
                .setClientKey("SB-Mid-client-KHy6N_pxH6I0d_uF")
                .setTransactionFinishedCallback(Detail_invoice.this)
                .enableLog(true)
                .setColorTheme(new CustomColorTheme("#777777","#f77474" , "#3f0d0d"))
                .setUIkitCustomSetting(setting)
                .buildSDK();

    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if(result.getResponse() != null){
            sPembayaran = "cancel midtrans";
            switch (result.getStatus()){
                case TransactionResult.STATUS_SUCCESS:
                    //Toast.makeText(this, "Transaction Sukses " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    sPembayaran = "lunas";
                    break;
                case TransactionResult.STATUS_PENDING:
                    sPembayaran = "waiting midtrans";
                    Toast.makeText(this, "Transaction Pending " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    sPembayaran = "cancel midtrans";
                    Toast.makeText(this, "Transaction Failed" + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
            }
            result.getResponse().getValidationMessages();
        }else if(result.isTransactionCanceled()){
            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_LONG).show();
        }else{
            if(result.getStatus().equalsIgnoreCase((TransactionResult.STATUS_INVALID))){
                Toast.makeText(this, "Transaction Invalid" + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Something Wrong", Toast.LENGTH_LONG).show();
            }
        }
        sMetode = result.getResponse().getPaymentType();
        idMidtrans = result.getResponse().getTransactionId();
        new SimpanOrder().execute();
    }

    private class SimpanOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Detail_invoice.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_invoice", id_invoice));
            params.add(new BasicNameValuePair("status_pembayaran", sPembayaran));
            params.add(new BasicNameValuePair("metode_pembayaran", sMetode));
            params.add(new BasicNameValuePair("id_midtrans", idMidtrans));

            JSONObject json = jsonParser.makeHttpRequest(urlDoPembayarab, "POST", params);

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
                        Toast.makeText(Detail_invoice.this, "Berhasil melakukan pembayaran!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Detail_invoice.this, Daftar_invoice.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}