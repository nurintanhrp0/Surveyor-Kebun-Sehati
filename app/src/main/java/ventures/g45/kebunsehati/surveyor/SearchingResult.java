package ventures.g45.kebunsehati.surveyor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.modal.modal_produk;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_produk;

public class SearchingResult extends AppCompatActivity {

    TextView txtTitleNoResult, txtTitleSpesial;
    ImageView icoBack;
    LinearLayout barisanSpesial, blockTier;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    float factor;
    String kategori, noHp, defaultUrl, urlSearching, dataUrl, kataKunci, catatan, satuanBarang, id_produk, urlPlacingOrder, urlRemoveOrder, urlGetTier, urlCekStock, id_alamat;
    Intent intent;
    Integer id_horeka, click = 0, values, qty_cart, id;
    List<modal_produk> list;
    adapter_produk adapterProduk;
    ListView list_produk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_result);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        id_horeka = sharedPreferences.getInt("id_horeka", 0);
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlSearching = defaultUrl + "doSearch.html";
        urlPlacingOrder = defaultUrl + "placingorderhoreka.html";
        urlRemoveOrder = defaultUrl + "removetemphoreka.html";
        urlGetTier = defaultUrl + "getTierHoreka.html";
        urlCekStock = defaultUrl + "getcekstock.html";

        factor = getResources().getDisplayMetrics().density;
        decimalFormat = new DecimalFormat("#,###.##");

        intent = getIntent();
        kataKunci = intent.getStringExtra("kata");
        id = intent.getIntExtra("id", 0);
        id_alamat = intent.getStringExtra("id_alamat");

        txtTitleNoResult = (TextView) findViewById(R.id.txtTitleNoResult);
        icoBack = (ImageView) findViewById(R.id.icoBack);
        list_produk = findViewById(R.id.list_produk);
        list = new ArrayList<>();
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        list_produk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                modal_produk modalProduk = (modal_produk) parent.getItemAtPosition(position);
                if (modalProduk.getStock().equals("0")){
                    Toast.makeText(SearchingResult.this, "Maaf, stock untuk produk ini habis!", Toast.LENGTH_SHORT).show();
                }else {
                    if (click == 0) {
                        click = 1;
                        AddToCartDialog(String.valueOf(modalProduk.getId_produk()), modalProduk.getNama(), modalProduk.getUkuran(), modalProduk.getSatuan(), modalProduk.getThumbnail(), String.valueOf(modalProduk.getHarga()), modalProduk.getQty_temp(), modalProduk.getCatatan_temp(), modalProduk.getKelipatan_order(), modalProduk.getMinimal_order(), modalProduk.getStock());
                    }
                }

            }
        });


        new Pencarian().execute(kataKunci);
    }

    private class Pencarian extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SearchingResult.this);
            pDialog.setMessage("Mengambil data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("kata", args[0]));
            params.add(new BasicNameValuePair("id", id.toString()));

            JSONObject json = jsonParser.makeHttpRequest(urlSearching, "POST", params);

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
                        if (result.getInt("cari") == 0) {
                            txtTitleNoResult.setVisibility(View.VISIBLE);
                        } else {
                            txtTitleNoResult.setVisibility(View.GONE);
                        }
                        if (result.has("produk")){
                            JSONArray daftar = new JSONArray(result.getString("produk"));
                            if (daftar.length() > 0) {
                                for (int i = 0; i < daftar.length(); i++) {
                                    JSONObject produk = daftar.getJSONObject(i);

                                    if (!produk.getString("harga").equals("null")) {
                                        modal_produk modalProduk = new modal_produk();
                                        modalProduk.setNama(produk.getString("nama_produk"));
                                        if (produk.getString("harga").equals("null")) {
                                            modalProduk.setHarga(0);
                                        } else {
                                            modalProduk.setHarga(produk.getInt("harga"));
                                        }
                                        modalProduk.setStock(produk.getString("stock"));
                                        modalProduk.setId_produk(produk.getInt("id_produk"));
                                        modalProduk.setUkuran(produk.getString("qty"));
                                        modalProduk.setSatuan(produk.getString("nama_satuan"));
                                        modalProduk.setThumbnail(produk.getString("thumbnail"));
                                        modalProduk.setQty_temp(produk.getInt("qty_temp"));
                                        modalProduk.setCatatan_temp(produk.getString("catatan_temp"));
                                        modalProduk.setKelipatan_order(produk.getString("kelipatan_order"));
                                        modalProduk.setMinimal_order(produk.getString("minimal_order"));
                                        modalProduk.setKeterangan_stock(produk.getString("keterangan_stock"));
                                        modalProduk.setStock_habis(produk.getString("stock_habis"));
                                        modalProduk.setDiskon(produk.getInt("diskon"));

                                        list.add(modalProduk);
                                        adapterProduk = new adapter_produk(SearchingResult.this, R.layout.cv_produk, list);
                                        list_produk.setAdapter(adapterProduk);
                                        adapterProduk.notifyDataSetChanged();

                                    }
                                }
                            }
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

    public void AddToCartDialog(final String id, String nama, final String ukuran, final String satuan, String thumbnail, final String harga, final Integer qty_temp, String catatan_temp, final String kelipatan_order, final String minimal_order, String stock) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SearchingResult.this, R.style.AddToCart);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_add_to_cart, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
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

                click =0;
                alertDialog.dismiss();
            }
        });


        btnBeliPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click =0;
                alertDialog.dismiss();
                catatan = inpCatatan.getText().toString();
                qty_cart = Integer.valueOf(jumlah.getText().toString());
                if (qty_cart % Integer.valueOf(kelipatan_order)==0) {
                    if (qty_cart >= Integer.valueOf(minimal_order)) {
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
                click =0;
                alertDialog.dismiss();
                new RemoveOrder().execute(id, id_horeka.toString(), String.valueOf((Integer.valueOf(harga)/qty_temp)));
            }
        });


        new GetTier().execute(id);

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
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(SearchingResult.this, R.style.DialogPutih);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_warning, null);
        dialog.setView(dialogView);
        Button icOk = dialogView.findViewById(R.id.btnOk);
        TextView isi = dialogView.findViewById(R.id.isi);
        final androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
        if (i == 0){
            isi.setText("Kelipatan jumlah pembelian untuk produk ini adalah " + kelipatan_order + " "+ satuanBarang);
            icOk.setOnClickListener(new View.OnClickListener() {
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

    private class GetTier extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SearchingResult.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));

            JSONObject json = jsonParser.makeHttpRequest(urlGetTier, "POST", params);

            return json;

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        protected void onPostExecute(final JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {

                        int k = 0;
                        JSONArray daftarTier = new JSONArray(result.getString("tier"));
                        TableLayout table = new TableLayout(SearchingResult.this);
                        table.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        table.setShrinkAllColumns(true);
                        table.setStretchAllColumns(true);
                        GradientDrawable gd=new GradientDrawable();
                        gd.setStroke(2, Color.BLACK);
                        //table.setBackground(gd);
                        if (daftarTier.length() > 1){
                            for (int i = -1 ; i<result.getInt("total"); i++){
                                JSONObject tier = null;
                                if (i != -1) {
                                    tier = daftarTier.getJSONObject(i);
                                }
                                TableRow row = new TableRow(SearchingResult.this);
                                row.setBackground(gd);
                                row.setPadding((int) (5 * factor), (int) (8 * factor), (int) (5 * factor), (int) (8 *factor));
                                //kolom
                                for (int j = 1; j<4; j++){
                                    TextView tv = new TextView(SearchingResult.this);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    }
                                    if (i == -1){
                                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                                        if (j == 2) {
                                            tv.setText("Harga");
                                        } else if (j == 3){
                                            tv.setText("Action");
                                        } else {
                                            tv.setText("Qty");
                                        }
                                    }else {
                                        if (j == 2) {
                                            tv.setText(decimalFormat.format(tier.getInt("harga")));
                                        } else if(j == 1) {
                                            tv.setText(tier.getInt("qty") + "");
                                        } else {
                                            tv.setText("Beli");
                                            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                            tv.setTextColor(getResources().getColor(android.R.color.white));
                                            tv.setPadding((int) (0 * factor), (int) (5 * factor), (int) (0 * factor), (int) (5 *factor));
                                        }
                                    }
                                    row.addView(tv);

                                    final JSONObject finalTier1 = tier;
                                    row.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Integer qty = null;
                                            try {
                                                qty_cart = finalTier1.getInt("qty");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new CekStock().execute(id_produk, qty_cart.toString());


                                        }
                                    });
                                }
                                table.addView(row);

                            }
                            blockTier.addView(table);
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

    private class PlacingOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SearchingResult.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("qty", args[2]));
            params.add(new BasicNameValuePair("satuan", args[3]));
            if (args[4].isEmpty()){
                args[4] = "";
            }
            params.add(new BasicNameValuePair("keterangan", args[4]));
            params.add(new BasicNameValuePair("ukuran",args[5]));
            params.add(new BasicNameValuePair("id_temp", "0"));
            params.add(new BasicNameValuePair("noHp", noHp));

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
                    } else {
                        intent = new Intent(SearchingResult.this, Order.class);
                        intent.putExtra("id_horeka", id_horeka.toString());
                        intent.putExtra("id_alamat", id_alamat);
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

    private class RemoveOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SearchingResult.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("id_horeka", args[1]));
            params.add(new BasicNameValuePair("harga", "0"));
            params.add(new BasicNameValuePair("noHp", noHp));

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
                        Toast.makeText(SearchingResult.this, "Berhasil menghapus orderan!", Toast.LENGTH_SHORT).show();
                        intent = new Intent(SearchingResult.this, Order.class);
                        intent.putExtra("id_horeka", id_horeka.toString());
                        intent.putExtra("id_alamat", id_alamat);
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

            pDialog = new ProgressDialog(SearchingResult.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("qty", args[1]));

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
                        new PlacingOrder().execute(id_produk, id_horeka.toString(), qty_cart.toString(), satuanBarang, catatan, qty_cart.toString());
                    }else if (result.getInt("hasil") == 1){
                        Toast.makeText(SearchingResult.this, "Maaf, jumlah quantity orderan Anda lebih dari stock yang tersedia!", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(SearchingResult.this, "Maaf, stock produk ini telah habis!", Toast.LENGTH_SHORT).show();
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