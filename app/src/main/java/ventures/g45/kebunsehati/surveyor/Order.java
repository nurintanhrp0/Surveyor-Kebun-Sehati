package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import ventures.g45.kebunsehati.surveyor.modal.modal_kategori;
import ventures.g45.kebunsehati.surveyor.modal.modal_produk;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_kategori;
import ventures.g45.kebunsehati.surveyor.adapter.adapter_produk;

public class Order extends AppCompatActivity {

    private ArrayList<modal_kategori> list_kategori;
    adapter_kategori adapterKategori;
    private List<modal_produk> list;
    adapter_produk adapterProduk;
    static ListView list_produk;
    RecyclerView blocKategori;
    static Integer[] posisi;
    Integer position = 0, dari=1, click =0, values=0, qty_cart, idPembayaran;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String id_horeka, imgUrl, id_produk, satuanBarang,  catatan, noHp, id_alamat;
    String defaultUrl, urlGetProduk, dataUrl, urlPlacingOrder, urlRemoveOrder, urlCekStock, urlCekdiskon;
    Button btnCheckout;
    TextView txtNilaiTotal, inpSearch;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetProduk = defaultUrl + "getdaftarproduksales.html";
        urlPlacingOrder = defaultUrl + "placingorderhoreka.html";
        urlRemoveOrder = defaultUrl + "removetemphoreka.html";
        urlCekStock = defaultUrl + "getcekstock.html";
        urlCekdiskon = defaultUrl + "getcekdiskon.html";

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");
        idPembayaran = sharedPreferences.getInt("id_pembayaran", 10);

        Intent intent = getIntent();
        id_horeka = intent.getStringExtra("id_horeka");
        id_alamat = intent.getStringExtra("id_alamat");

        blocKategori = findViewById(R.id.blockKatgeori);
        list_produk = findViewById(R.id.list_produk);
        btnCheckout =  findViewById(R.id.btnCheckout);
        txtNilaiTotal =  findViewById(R.id.txtNilaiTotal);
        inpSearch = findViewById(R.id.inpSearch);
        inpSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        inpSearch.setSingleLine(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        blocKategori.setLayoutManager(layoutManager);
        list_kategori = new ArrayList<>();
        list = new ArrayList<>();

        list_produk.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                modal_produk modalMenu = (modal_produk) view.getItemAtPosition(position);
                Integer id = Integer.valueOf(modalMenu.getId_kategori());
                Log.d("id", id.toString());
                for (int i = 0; i < list_kategori.size(); i++) {
                    if (i == id) {
                        adapterKategori.notifyItemChanged(i, "ganti");
                        if (i != dari) {
                            adapterKategori.notifyItemChanged(dari, "tidak");
                        }
                        layoutManager.scrollToPositionWithOffset(i, 0);
                        dari = i;
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                position = firstVisibleItem;

            }
        });

        list_produk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                modal_produk modalProduk = (modal_produk) parent.getItemAtPosition(position);
                if (modalProduk.getStock().equals("0")){
                    Toast.makeText(Order.this, "Maaf, stock untuk produk ini habis!", Toast.LENGTH_SHORT).show();
                }else {
                    if (click == 0) {
                        click = 1;
                        AddToCartDialog(String.valueOf(modalProduk.getId_produk()), modalProduk.getNama(), modalProduk.getUkuran(), modalProduk.getSatuan(), modalProduk.getThumbnail(), String.valueOf(modalProduk.getHarga()), modalProduk.getQty_temp(), modalProduk.getCatatan_temp(), modalProduk.getKelipatan_order(), modalProduk.getMinimal_order(), modalProduk.getStock());
                    }
                }

            }
        });

        inpSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductSearching();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CekDiskon().execute();
            }
        });

        new GetDaftarMenu().execute();
    }

    public static void focusOnView(final Integer id, final Integer dari){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    list_produk.smoothScrollToPositionFromTop(posisi[id], 0);
                }catch (Exception e){}

            }
        });
    }

    private class GetDaftarMenu extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_outlet", "1"));
            params.add(new BasicNameValuePair("id_horeka", id_horeka.toString()));
            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetProduk, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Order.this);
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
                        if (result.getInt("totalCart") != 0 ){
                            btnCheckout.setText("Checkout (" + result.getInt("totalCart") + ")");
                            txtNilaiTotal.setText("Rp " + decimalFormat.format(result.getInt("totalHarga")));
                        }else {
                            btnCheckout.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
                            btnCheckout.setEnabled(false);
                        }

                        Integer id = 0, baris =0, scroll = -1;
                        JSONArray kategoriMenu = new JSONArray(result.getString("kategori"));
                        if (kategoriMenu.length() > 0) {
                            for (int i = 0; i < kategoriMenu.length(); i++) {

                                final JSONObject menu = kategoriMenu.getJSONObject(i);

                                final Integer id_kat = menu.getInt("id_kategori");

                                modal_kategori modalKaegori = new modal_kategori();
                                modalKaegori.setNama(menu.getString("nama"));
                                modalKaegori.setThumbnail(menu.getString("thumbnail"));
                                modalKaegori.setId_kategori(menu.getInt("id_kategori"));

                                list_kategori.add(modalKaegori);
                                adapterKategori = new adapter_kategori(list_kategori, Order.this);
                                blocKategori.setAdapter(adapterKategori);


                            }
                        }

                        JSONArray daftar = new JSONArray(result.getString("produk"));
                        posisi = new Integer[daftar.length()];
                        if (daftar.length() > 0) {
                            for (int i = 0; i < daftar.length(); i++) {
                                JSONObject produk = daftar.getJSONObject(i);

                                if (!produk.getString("harga").equals("null")) {
                                    imgUrl = defaultUrl + "uploads/thumbnail/" + produk.getString("thumbnail");
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

                                    if (id != produk.getInt("id_kategori")) {
                                        posisi[produk.getInt("id_kategori")] = baris;
                                        id = produk.getInt("id_kategori");
                                        scroll = scroll + 1;
                                        modalProduk.setId_kategori(scroll);
                                    } else {
                                        modalProduk.setId_kategori(scroll);
                                    }

                                    baris = baris + 1;

                                    list.add(modalProduk);
                                    adapterProduk = new adapter_produk(Order.this, R.layout.cv_produk, list);
                                    list_produk.setAdapter(adapterProduk);
                                    adapterProduk.notifyDataSetChanged();


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

    public void AddToCartDialog(final String id, String nama, final String ukuran, final String satuan, String thumbnail, final String harga, final Integer qty_temp, String catatan_temp, final String kelipatan_order, final String minimal_order, String stock) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Order.this, R.style.AddToCart);
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
                new RemoveOrder().execute(id, id_horeka.toString(), harga);
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
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Order.this, R.style.DialogPutih);
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
        }else {
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

    private class CekStock extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Order.this);
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
            params.add(new BasicNameValuePair("cek", "0"));

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
                        Toast.makeText(Order.this, "Maaf, jumlah quantity orderan Anda lebih dari stock yang tersedia!", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(Order.this, "Maaf, stock produk ini telah habis!", Toast.LENGTH_SHORT).show();
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

            pDialog = new ProgressDialog(Order.this);
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
            params.add(new BasicNameValuePair("id_temp", "0"));
            params.add(new BasicNameValuePair("id_pembayaran", idPembayaran.toString()));

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
                        Intent intent = new Intent(Order.this, Order.class);
                        intent.putExtra("id_horeka", id_horeka);
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

            pDialog = new ProgressDialog(Order.this);
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
            params.add(new BasicNameValuePair("harga","0"));
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
                        Toast.makeText(Order.this, "Berhasil menghapus orderan!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Order.this, Order.class);
                        intent.putExtra("id_horeka", id_horeka);
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

    private class CekDiskon extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Order.this);
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
                        editor.putInt("id_horeka", Integer.parseInt(id_horeka)).apply();
                        intent.putExtra("id_alamat", Integer.parseInt(id_alamat));
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

    public void ProductSearching(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Order.this, R.style.Searching);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_searching, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        TextView txtTitleSearching = dialogView.findViewById(R.id.txtTitleSearching);
        final EditText inpSearching = dialogView.findViewById(R.id.inpSearching);
        ImageView icoBack = dialogView.findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        inpSearching.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(Order.this, SearchingResult.class);
                    editor.putInt("id_horeka", Integer.parseInt(id_horeka)).apply();
                    intent.putExtra("kata", inpSearching.getText().toString());
                    intent.putExtra("id_alamat", id_alamat);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}