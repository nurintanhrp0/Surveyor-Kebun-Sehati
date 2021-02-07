package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_orderan_sales;

public class Checkout extends AppCompatActivity {

    TextView txtTitle, txtTitleAlamat, txtNamaPenerima, txtAddress, txtTitleJadwal, txtTitlePembayaran, txtJarak, txttitleNilaiBeban;
    TextView txtTitleRekap, txtTitleTotalBelanja, txtNilaiTotalBelanja, txtAlamat, txtInfoPembayaran;
    TextView txtTitleTotalOngkir, txtNilaiTotalOngkir, txtTitleTotalTagihan, txtNilaiTotalTagihan, txtCatatan, txtNilaibeban;
    TextView txtTanggalPengiriman, txtTanggal;
    TextView txtInfo1, txtInfo2,txtSelengkapnya1, txtSelengkapnya2;
    ImageView icoBack;
    Button btnEditAlamat, btnBeli, btnEditTanggal;
    RadioButton jadwalPagi, jadwalSore, bayarCOD;
    String defaultUrl, urlGetDataShipping, no_hp, token, alamat, idAlamat, urlSimpanOrder, tglKirim, ketPembayaran, tujuan, jarak, atas_nama, urlimagepembayaran, dataUrl, metode_pembayaran;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    Intent intent;
    float factor;
    RadioGroup blockPilihanJadwal, blockPilihanPembayaran;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    String myFormat = "dd MMM yyyy", sign, limit, koordinat_alamat, urlGetAlamat, catatan, beban_order, biaya_jarak, noHp;
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    Integer ongkir, total;
    int idPembayaran=0;
    ProgressBar pBar;
    AlertDialog alertDialog;
    LinearLayout blockOutlet;
    RelativeLayout blockOngkir, blockBebanBiaya;
    String sNama,sKota;
    Integer id_horeka, id_temp;
    DatePickerDialog dialog;
    adapter_orderan_sales adapterOrderan;
    private List<modal_orderan_sales> list;
    ListView barisanCart;
    Integer values = 0, cek = 0, qty_cart, cek_status =0;
    LinearLayout blockTier;
    String id_produk, satuanBarang, urlGetTier, urlCekStock, urlPlacingOrder, urlRemoveOrder, urlCekdiskon;
    RadioButton pembayarannya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetDataShipping = defaultUrl + "getcheckouthoreka.html";
        urlSimpanOrder = defaultUrl + "saveorderhoreka.html";
        urlCekStock = defaultUrl + "getcekstock.html";
        urlPlacingOrder = defaultUrl + "placingorderhoreka.html";
        urlRemoveOrder = defaultUrl + "removetemphoreka.html";
        urlGetAlamat = defaultUrl + "getCabangHoreka.html";
        urlCekdiskon = defaultUrl + "getcekdiskon.html";

        decimalFormat = new DecimalFormat("#,###.##");
        factor = getResources().getDisplayMetrics().density;

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        id_horeka = sharedPreferences.getInt("id_horeka", 0);
        idPembayaran = sharedPreferences.getInt("id_pembayaran", 10);
        noHp = sharedPreferences.getString("noHp", "");

        intent = getIntent();
        tglKirim = intent.getStringExtra("tglKirim");
        idAlamat = String.valueOf(intent.getIntExtra("id_alamat", 0));

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtAlamat = (TextView) findViewById(R.id.txtAlamat);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        btnEditAlamat = (Button) findViewById(R.id.btnEditAlamat);
        txtTitleJadwal = (TextView) findViewById(R.id.txtTitleJadwal);
        txtTitlePembayaran = (TextView) findViewById(R.id.txtTitlePembayaran);
        txtTitleRekap = (TextView) findViewById(R.id.txtTitleRekap);
        txtTitleTotalBelanja = (TextView) findViewById(R.id.txtTitleTotalBelanja);
        txtNilaiTotalBelanja = (TextView) findViewById(R.id.txtNilaiTotalBelanja);
        btnBeli = (Button) findViewById(R.id.btnBeli);
        txtTitleTotalOngkir = (TextView) findViewById(R.id.txtTitleTotalOngkir);
        txtNilaiTotalOngkir = (TextView) findViewById(R.id.txtNilaiTotalOngkir);
        txtTitleTotalTagihan = (TextView) findViewById(R.id.txtTitleTotalTagihan);
        txtNilaiTotalTagihan = (TextView) findViewById(R.id.txtNilaiTotalTagihan);
        blockPilihanJadwal = (RadioGroup) findViewById(R.id.blockPilihanJadwal);
        blockPilihanPembayaran = (RadioGroup) findViewById(R.id.blockPilihanPembayaran);
        txtTanggalPengiriman = (TextView) findViewById(R.id.txtTanggalPengiriman);
        txtTanggal = (TextView) findViewById(R.id.txtTanggal);
        txtJarak = findViewById(R.id.txtJarak);
        txtCatatan = findViewById(R.id.txtCatatan);
        txtInfo1 = findViewById(R.id.txtInfo1);
        txtInfo2 =  findViewById(R.id.txtInfo2);
        txtSelengkapnya1 =  findViewById(R.id.txtLihatSelengkapnya1);
        txtSelengkapnya2 = findViewById(R.id.txtLihatSelengkapnya2);
        txtNilaibeban =  findViewById(R.id.txtNilaiTotalBeban);
        txttitleNilaiBeban =  findViewById(R.id.txtTitleTotalBeban);
        blockBebanBiaya = findViewById(R.id.blockTotalBeban);
        blockOngkir = findViewById(R.id.blockTotalOngkir);
        barisanCart = findViewById(R.id.barisanCart);
        txtInfoPembayaran = findViewById(R.id.txtInfoPembayaran);
        list = new ArrayList<>();

        myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.DATE, 1);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dialog =  new DatePickerDialog(Checkout.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        if (tglKirim == null) {
            tglKirim = sdf.format(myCalendar.getTime());
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(myCalendar.getTimeInMillis());

        }
        txtTanggal.setText(tglKirim);

        btnEditTanggal = (Button) findViewById(R.id.btnEditTanggal);
        btnEditTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

            }
        });

        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cek_status = 1;
                new CekStock().execute();
            }
        });

        barisanCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modal_orderan_sales modalOrderan = (modal_orderan_sales) parent.getItemAtPosition(position);
                AddToCartDialog(modalOrderan.getId(), modalOrderan.getNama(), modalOrderan.getQty(), modalOrderan.getSatuan(), modalOrderan.getThumbnail(), modalOrderan.getHarga(), Integer.valueOf(modalOrderan.getQty()), modalOrderan.getCatatan(), modalOrderan.getKelipatan_order(), modalOrderan.getMinimal_order(), modalOrderan.getStock(), modalOrderan.getId_temp(), modalOrderan.getHarga_temp());

            }
        });

        btnEditAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Checkout.this, R.style.DialogPutih);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_pilih_outlet, null);
                dialog.setView(dialogView);
                blockOutlet = dialogView.findViewById(R.id.blockOutlet);
                TextView tambahAlamat = dialogView.findViewById(R.id.tambahAlamat);
                pBar = dialogView.findViewById(R.id.pBar);
                alertDialog = dialog.create();

                tambahAlamat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(), ChangeAddress.class);
                        intent.putExtra("dari", 3);
                        startActivity(intent);
                    }
                });

                new GetOutlet().execute();

                alertDialog.show();
            }
        });

        new GetDataShipping().execute();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(Checkout.this, Order.class);
        intent.putExtra("id_horeka", id_horeka.toString());
        intent.putExtra("id_alamat", idAlamat);
        startActivity(intent);
    }

    private void updateLabel() {
        tglKirim = sdf.format(myCalendar.getTime());
        txtTanggal.setText(tglKirim);

    }


    private class GetOutlet extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetAlamat, "POST", params);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            pBar.setVisibility(View.GONE);

            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }
                    else {
                        JSONArray daftar = new JSONArray(result.getString("data"));
                        if (daftar.length() > 0){

                            for (int i=0; i < daftar.length(); i++){

                                final JSONObject outlet = daftar.getJSONObject(i);

                                final Integer id = outlet.getInt("id");

                                LinearLayout linearLayout= new LinearLayout(Checkout.this);
                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                linearLayout.setId(View.generateViewId());

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                                layoutParams.setMargins((int) (20 * factor), 0, (int) (15 * factor), (int) (15 * factor));
                                linearLayout.setLayoutParams(layoutParams);

                                ViewGroup.LayoutParams layoutJarak = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView jarak= new TextView(Checkout.this);
                                jarak.setLayoutParams(layoutJarak);
                                jarak.setPadding((int) (10 * factor), (int) (5 * factor), (int) (8 * factor), 0);
                                jarak.setTextSize(14);
                                jarak.setAllCaps(true);
                                jarak.setText(outlet.getString("jarak")+ " KM");
                                jarak.setTextColor(getResources().getColor(android.R.color.black));
                                jarak.setMaxLines(1);
                                jarak.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayout.addView(jarak);

                                if (!outlet.getString("nama").equals("")) {

                                    ViewGroup.LayoutParams layoutCatatan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView catatan = new TextView(Checkout.this);
                                    catatan.setLayoutParams(layoutCatatan);
                                    catatan.setPadding((int) (10 * factor), (int) (5 * factor), (int) (8 * factor), 0);
                                    catatan.setTextSize(14);
                                    catatan.setAllCaps(true);
                                    catatan.setText(outlet.getString("nama"));
                                    catatan.setMaxLines(1);
                                    catatan.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayout.addView(catatan);
                                }

                                ViewGroup.LayoutParams layoutAlamat= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView alamat = new TextView(Checkout.this);
                                alamat.setLayoutParams(layoutAlamat);
                                alamat.setPadding((int) (10 * factor), (int) (5 * factor), (int) (8 * factor), (int) (8 * factor));
                                alamat.setTextSize(14);
                                alamat.setText(outlet.getString("alamat"));
                                alamat.setMaxLines(3);
                                alamat.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayout.addView(alamat);

                                linearLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), Checkout.class);
                                        intent.putExtra("id_alamat", id);
                                        startActivity(intent);
                                    }
                                });

                                blockOutlet.addView(linearLayout);
                            }


                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class GetDataShipping extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Checkout.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("tglKirim", tglKirim));
            params.add(new BasicNameValuePair("id_alamat", idAlamat));
            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlGetDataShipping, "POST", params);

            return json;

        }

        protected void onPostExecute(final JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {
                        total = result.getInt("total");
                        if (result.getString("alamat").equals("null")){
                            Intent intent = new Intent(getApplicationContext(), ChangeAddress.class);
                            intent.putExtra("dari", 3);
                            startActivity(intent);
                        }

                        idAlamat = result.getString("id_alamat");
                        if(result.getString("nama_cabang").equals("null")){
                            txtCatatan.setVisibility(View.GONE);
                        }else {
                            txtCatatan.setText(result.getString("nama_cabang"));
                        }
                        txtAddress.setText(result.getString("alamat"));
                        txtJarak.setText(result.getString("jarak") + " KM");
                        txtNilaiTotalBelanja.setText(decimalFormat.format(total));
                        ongkir = result.getInt("ongkir");
                        limit = result.getString("limit");
                        sign = result.getString("sign");
                        jarak =  result.getString("jarak");
                        beban_order = result.getString("biaya_beban");
                        biaya_jarak =  result.getString("biaya_jarak");

                        if (jarak.equals("0")){
                            txtJarak.setVisibility(View.GONE);
                        }

                        if (ongkir != 0){
                            txtNilaiTotalOngkir.setText(decimalFormat.format(ongkir) + "");
                            txtSelengkapnya1.setVisibility(View.VISIBLE);
                            txtInfo1.setVisibility(View.VISIBLE);
                            txtInfo1.setText("Lokasi pengiriman diluar area pengiriman kami. Anda akan dikenakan ongkos kirim sebesar Rp " + decimalFormat.format(ongkir));

                        }else {
                            blockOngkir.setVisibility(View.GONE);
                        }
                        if (!beban_order.equals("0")){
                            txtSelengkapnya2.setVisibility(View.VISIBLE);
                            txtInfo2.setVisibility(View.VISIBLE);
                            txtInfo2.setText("Total belanja Anda kurang dari nilai minimal belanja (Rp " + decimalFormat.format(Integer.valueOf(limit)) + "). Anda akan dikenakan biaya beban minimal belanja sebesar Rp " + decimalFormat.format(Integer.valueOf(beban_order)));

                            txtNilaibeban.setText(decimalFormat.format(Integer.valueOf(beban_order)) + "");
                        }else {
                            blockBebanBiaya.setVisibility(View.GONE);
                        }

                        total = total + ongkir + Integer.valueOf(beban_order);
                        txtNilaiTotalTagihan.setText(decimalFormat.format(total));

                        final String info = result.getString("info_pembayaran");
                        JSONArray pembayaranArray = new JSONArray(result.getString("pembayaran"));
                        if (pembayaranArray.length() > 0) {
                            for (int i = 0; i < pembayaranArray.length(); i++) {

                                final JSONObject pembayaran = pembayaranArray.getJSONObject(i);
                                final String metode = pembayaran.getString("metode");
                                final Integer id = pembayaran.getInt("id");

                                pembayarannya = new RadioButton(Checkout.this);
                                pembayarannya.setTextSize(14);
                                pembayarannya.setText(pembayaran.getString("metode"));
                                pembayarannya.setId(id);

                                if (idPembayaran == pembayarannya.getId()) {
                                    pembayarannya.setChecked(true);
                                    idPembayaran = id;
                                }

                                final int finalI = i;
                                pembayarannya.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        idPembayaran = id;
//                                        if (finalI == 1){
//                                            TextView textView = new TextView(Checkout.this);
//                                            textView.setText(info);
//                                            textView.setTextColor(getResources().getColor(android.R.color.black));
//                                            textView.setPadding((int) (5 * factor), 0, 0, 0);
//                                            blockPilihanPembayaran.addView(textView);
//                                            /*Log.d("p","klik");
//                                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Checkout.this, R.style.DialogPutih);
//                                            LayoutInflater inflater = getLayoutInflater();
//                                            View dialogView = inflater.inflate(R.layout.popup_warning, null);
//                                            dialog.setView(dialogView);
//                                            Button icOk = dialogView.findViewById(R.id.btnOk);
//                                            Button icBatal = dialogView.findViewById(R.id.btnBatal);
//                                            TextView isi = dialogView.findViewById(R.id.isi);
//                                            final androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
//                                                icBatal.setVisibility(View.VISIBLE);
//                                                isi.setText("Jika memilih metode pembayaran tempo, maka anda tidak mendapatkan harga diskon");
//                                                icOk.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        tujuan = no;
//                                                        atas_nama = an;
//                                                        urlimagepembayaran = thumbnail2;
//                                                        metode_pembayaran =metode;
//                                                        idPembayaran = id;
//                                                        editor.putInt("id_pembayaran", idPembayaran).apply();
//                                                        Log.d("id", String.valueOf(idPembayaran));
//                                                        new CekDiskon().execute();
//                                                    }
//                                                });
//                                                icBatal.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        alertDialog.dismiss();
//                                                    }
//                                                });
//
//                                            alertDialog.show();*/
//                                        }else {
                                           /* tujuan = no;
                                            atas_nama = an;
                                            urlimagepembayaran = thumbnail2;
                                            metode_pembayaran =metode;

                                            editor.putInt("id_pembayaran", idPembayaran).apply();
                                            Log.d("id", String.valueOf(idPembayaran));
                                            new CekDiskon().execute();*/
                                        }
                                    //}
                                });

                                blockPilihanPembayaran.addView(pembayarannya);

                                if(i == 1){
                                    TextView label = new TextView(Checkout.this);
                                    label.setText(info);
                                    label.setTextSize(14);
                                    label.setPadding((int) (5 * factor), 0, 0, 0);
                                    blockPilihanPembayaran.addView(label);
                                }
                            }
                        }

                        JSONArray jadwalArray = new JSONArray(result.getString("jadwal"));
                        Integer unable = 0;
                        if (jadwalArray.length() > 0) {
                            for (int i = 0; i < jadwalArray.length(); i++) {

                                final JSONObject jadwal = jadwalArray.getJSONObject(i);

                                final RadioButton jadwalnya = new RadioButton(Checkout.this);
                                jadwalnya.setId(jadwal.getInt("id"));

                                if (jadwal.getString("keterangan").isEmpty()) {
                                    jadwalnya.setText(jadwal.getString("jadwal"));
                                } else {
                                    unable = jadwal.getInt("id");
                                    jadwalnya.setText(jadwal.getString("jadwal") + "\n" + "( "+ jadwal.getString("keterangan") + " )");
                                }

                                if (i == 0) {
                                    jadwalnya.setChecked(true);
                                    if (jadwalnya.getId() == unable){
                                        btnBeli.setEnabled(false);
                                        btnBeli.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                    }else {
                                        btnBeli.setEnabled(true);
                                        btnBeli.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                                    }
                                }

                                jadwalnya.setTextSize(14);

                                final Integer finalUnable = unable;
                                jadwalnya.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (jadwalnya.getId() == finalUnable){
                                            btnBeli.setEnabled(false);
                                            btnBeli.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                        }else {
                                            btnBeli.setEnabled(true);
                                            btnBeli.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                                        }
                                    }
                                });

                                blockPilihanJadwal.addView(jadwalnya);
                            }
                        }

                        if (result.has("data")) {
                            JSONArray daftar = new JSONArray(result.getString("data"));
                            if (daftar.length() > 0) {
                                for (int i = 0; i < daftar.length(); i++) {
                                    JSONObject produk = daftar.getJSONObject(i);

                                    modal_orderan_sales modalOrderan = new modal_orderan_sales();
                                    modalOrderan.setNama(produk.getString("nama"));
                                    modalOrderan.setQty(produk.getString("qty"));
                                    modalOrderan.setHarga(produk.getString("harga_satuan"));
                                    modalOrderan.setTotal(produk.getString("totalproduk"));
                                    modalOrderan.setSatuan(produk.getString("satuan"));
                                    modalOrderan.setId(produk.getString("id"));
                                    modalOrderan.setCatatan(produk.getString("catatan"));
                                    modalOrderan.setMinimal_order(produk.getString("minimal_order"));
                                    modalOrderan.setKelipatan_order(produk.getString("kelipatan_order"));
                                    modalOrderan.setStock(produk.getString("stock"));
                                    modalOrderan.setThumbnail(produk.getString("thumbnail"));
                                    modalOrderan.setDari(1);
                                    modalOrderan.setHarga_normal(produk.getString("harga_normal"));
                                    modalOrderan.setId_temp(produk.getInt("id_temp"));
                                    modalOrderan.setHarga_temp(produk.getString("harga_temp"));
                                    list.add(modalOrderan);
                                    adapterOrderan = new adapter_orderan_sales(Checkout.this, R.layout.cv_orderan_sales, list);
                                    barisanCart.setAdapter(adapterOrderan);
                                    Helper.getListViewSize(barisanCart);
                                }

                            }
                        }else {
                            editor.remove("id_pembayaran").apply();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void AddToCartDialog(final String id, String nama, final String ukuran, final String satuan, String thumbnail, final String harga, final Integer qty_temp, String catatan_temp, final String kelipatan_order, final String minimal_order, String stock, Integer idtemp, final String harga_temp) {
        id_temp = idtemp;
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Checkout.this, R.style.AddToCart);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_add_to_cart, null);
        dialog.setView(dialogView);
        final android.app.AlertDialog alertDialog = dialog.create();
        TextView txtTambah = dialogView.findViewById(R.id.txtTambah);
        TextView txtNamaProduk = dialogView.findViewById(R.id.txtNamaProduk);
        TextView txtBeratProduk = dialogView.findViewById(R.id.txtBeratProduk);
        TextView txtHargaProduk = dialogView.findViewById(R.id.txtHargaProduk);
        TextView txtTitleQty = dialogView.findViewById(R.id.txtTitleQty);
        ImageView imgProduk = dialogView.findViewById(R.id.imgProduk);
        final EditText inpCatatan = dialogView.findViewById(R.id.inpCatatan);
        TextView txtSatuan = dialogView.findViewById(R.id.txtSatuan);
        txtSatuan.setText(satuan);
        final EditText jumlah = dialogView.findViewById(R.id.display);
        jumlah.setImeOptions(EditorInfo.IME_ACTION_DONE);
        jumlah.setSingleLine(true);
        TextView increment = dialogView.findViewById(R.id.increment);
        TextView decrement = dialogView.findViewById(R.id.decrement);
        values = Integer.parseInt(ukuran);
        final Integer unit = Integer.parseInt(kelipatan_order);
        final Integer downrange = 0;
        final Integer uprange = Integer.parseInt(stock);
        id_produk = id;
        satuanBarang = satuan;
        catatan = catatan_temp;
        jumlah.setText(values+"");
        final Button btnBeliPopup = dialogView.findViewById(R.id.btnBeliPopup);
        final ImageView hapus = dialogView.findViewById(R.id.hapus);
        blockTier = dialogView.findViewById(R.id.blockTier);

        txtNamaProduk.setText(nama);
        txtBeratProduk.setText(decimalFormat.format(Integer.valueOf(ukuran)) + " " + satuan);
        txtHargaProduk.setText("Rp " + decimalFormat.format(Integer.valueOf(harga)));
        Picasso.get().load(dataUrl + "uploads/thumbnail/" + thumbnail).into(imgProduk);

        if (qty_temp != 0){
            jumlah.setText(qty_temp + "");
            inpCatatan.setText(catatan_temp);
            hapus.setVisibility(View.VISIBLE);
        }

        jumlah.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                }
                return false;
            }
        });

        increment.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                values = Integer.parseInt(jumlah.getText().toString());
                if (values >= downrange && values < uprange)
                    values += unit;
                jumlah.setText(String.valueOf(values));
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                values = Integer.parseInt(jumlah.getText().toString());
                if (values > downrange)
                    values -= unit;
                jumlah.setText(String.valueOf(values));
            }
        });


        ImageView icoClose = dialogView.findViewById(R.id.icoClose);
        icoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        btnBeliPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                catatan = inpCatatan.getText().toString();
                qty_cart = Integer.valueOf(jumlah.getText().toString());
                if (qty_cart % Integer.valueOf(kelipatan_order)==0) {
                    if (qty_cart >= Integer.valueOf(minimal_order)) {
                        cek_status =0;
                        new CekStock().execute(id, String.valueOf(jumlah.getText()));
                    } else {
                        Integer i = 1;
                        showwarning(i, kelipatan_order, minimal_order, satuan);
                    }
                }else {
                    Integer i = 0;
                    showwarning(i, kelipatan_order, minimal_order, satuan);
                }

            }
        });

        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                new RemoveOrder().execute(id, id_horeka.toString(),harga_temp);
            }
        });

        alertDialog.show();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception var2) {
        }

    }

    public void showwarning(Integer i, String kelipatan_order, String minimal_order, String satuanBarang){
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Checkout.this, R.style.DialogPutih);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_warning, null);
        dialog.setView(dialogView);
        Button icOk = dialogView.findViewById(R.id.btnOk);
        Button icBatal = dialogView.findViewById(R.id.btnBatal);
        TextView isi = dialogView.findViewById(R.id.isi);
        final androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
        if (i == 0) {
            isi.setText("Kelipatan jumlah pembelian untuk produk ini adalah " + kelipatan_order + " " + satuanBarang);
            icOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }else if (i ==10){
            icBatal.setVisibility(View.VISIBLE);
            isi.setText("Jika memilih metode pembayaran tempo, maka anda tidak mendapatkan harga diskon");
            icOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CekDiskon().execute();
                }
            });
            icBatal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
        else {
            isi.setText("Jumlah pembelian Anda kurang dari minimal order yaitu " + minimal_order + " " + satuanBarang);
            icOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }


        alertDialog.show();
    }

    private class CekDiskon extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Checkout.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("id_pembayaran", String.valueOf(idPembayaran)));

            JSONObject json = jsonParser.makeHttpRequest(urlCekdiskon, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1){

                    }else {
                        Intent intent = new Intent(getApplicationContext(), Checkout.class);
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

    private class CekStock extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Checkout.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            if (cek_status == 0) {
                params.add(new BasicNameValuePair("id", args[0]));
                params.add(new BasicNameValuePair("qty", args[1]));
            }else {
                for (int i= 0; i<list.size(); i++){
                    modal_orderan_sales modalOrderan = list.get(i);
                    params.add(new BasicNameValuePair("id"+i, modalOrderan.getId()));
                    params.add(new BasicNameValuePair("qty"+i, modalOrderan.getQty()));
                }
                params.add(new BasicNameValuePair("banyak", String.valueOf(list.size())));
            }
            params.add(new BasicNameValuePair("cek", cek_status.toString()));

            JSONObject json = jsonParser.makeHttpRequest(urlCekStock, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("hasil") == 2) {
                        if(cek_status == 0) {
                            new PlacingOrder().execute(id_produk, id_horeka.toString(), qty_cart.toString(), satuanBarang, catatan, qty_cart.toString());
                        }else {
                            new SimpanOrder().execute();
                        }
                    }else if (result.getInt("hasil") == 1){
                        Toast.makeText(Checkout.this, "Maaf, jumlah quantity orderan Anda lebih dari stock yang tersedia!", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(Checkout.this, "Maaf, stock produk ini telah habis!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class PlacingOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Checkout.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("qty", args[2]));
            params.add(new BasicNameValuePair("satuan", args[3]));
            if (args[4].isEmpty()){
                args[4] = "";
            }
            params.add(new BasicNameValuePair("keterangan", args[4]));
            params.add(new BasicNameValuePair("ukuran",args[5]));
            params.add(new BasicNameValuePair("id_temp", id_temp.toString()));
            params.add(new BasicNameValuePair("id_pembayaran", String.valueOf(idPembayaran)));

            JSONObject json = jsonParser.makeHttpRequest(urlPlacingOrder, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Maaf, akun Anda belum aktif", Toast.LENGTH_LONG).show();
                    }else if (result.getInt("error") == 10){
                        Toast.makeText(Checkout.this, "Maaf, jumlah pembelian Anda lebih dari stok diskons", Toast.LENGTH_SHORT).show();
                    } else{
                        intent = new Intent(Checkout.this, Checkout.class);
                        intent.putExtra("tglKirim", tglKirim);
                        intent.putExtra("id_alamat", Integer.parseInt(idAlamat));
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

    private class SimpanOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Checkout.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("tglKirim", tglKirim));
            params.add(new BasicNameValuePair("ongkir", ongkir.toString()));
            params.add(new BasicNameValuePair("id_alamat", idAlamat));
            params.add(new BasicNameValuePair("beban_order", beban_order));
            params.add(new BasicNameValuePair("id_pembayaran", String.valueOf(idPembayaran)));

            JSONObject json = jsonParser.makeHttpRequest(urlSimpanOrder, "POST", params);

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
                        Toast.makeText(Checkout.this, "Berhasil Order!", Toast.LENGTH_SHORT).show();
                       /*if (idPembayaran != 0 && idPembayaran != 15) {
                            intent = new Intent(getApplicationContext(), InformasiPembayaran.class);
                            intent.putExtra("id_invoice", result.getString("id_order"));
                            intent.putExtra("tujuan", tujuan);
                            intent.putExtra("atas_nama", atas_nama);
                            intent.putExtra("urlimage", urlimagepembayaran);
                            intent.putExtra("total", total);
                            intent.putExtra("metode", metode_pembayaran);
                            intent.putExtra("class", "1");
                            intent.putExtra("id_horeka", id_horeka.toString());
                            intent.putExtra("id_alamat", idAlamat);
                            startActivity(intent);
                        }else {*/
                           intent = new Intent(Checkout.this, DetailOrder.class);
                           intent.putExtra("id_order", result.getString("id_order"));
                           intent.putExtra("id_horeka", id_horeka.toString());
                           startActivity(intent);
                        //}
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class RemoveOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Checkout.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("id_horeka", args[1]));
            params.add(new BasicNameValuePair("harga", args[2]));

            JSONObject json = jsonParser.makeHttpRequest(urlRemoveOrder, "POST", params);

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
                        Toast.makeText(Checkout.this, "Berhasil menghapus orderan!", Toast.LENGTH_SHORT).show();
                        intent = new Intent(Checkout.this, Checkout.class);
                        intent.putExtra("tglKirim", tglKirim);
                        intent.putExtra("id_alamat", Integer.parseInt(idAlamat));
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

}