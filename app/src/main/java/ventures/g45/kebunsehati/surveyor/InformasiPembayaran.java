package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class InformasiPembayaran extends AppCompatActivity {

    TextView txtTitle, keterangan, txtTotal, total, txtNorek, norek;
    Button btnKonfirmasi;
    ImageView icBack, share1, share, thumbnail;
    Typeface MontserratRegular;
    ProgressDialog pDialog;
    String defaultUrl, urlGetPembayaran,sKeterangan, no_tujuan, atas_nama, urlimage, dataUrl, id_invoice, pembayaran, class_name, idAlamat, id_horeka;
    JSONParser jsonParser = new JSONParser();
    Integer hargaTotal;
    DecimalFormat format;
    LinearLayout blockNorek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi_pembayaran);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetPembayaran = defaultUrl + "getubahstatus.html";

        final Intent intent = getIntent();
        id_invoice = intent.getStringExtra("id_invoice");
        urlimage = intent.getStringExtra("urlimage");
        hargaTotal = intent.getIntExtra("total", 0);
        no_tujuan = intent.getStringExtra("tujuan");
        atas_nama = intent.getStringExtra("atas_nama");
        pembayaran = intent.getStringExtra("metode");
        class_name = intent.getStringExtra("class");
        id_horeka = intent.getStringExtra("id_horeka");
        idAlamat = intent.getStringExtra("id_alamat");
        Log.d("class", class_name);

        format = new DecimalFormat("#,###.##");

        txtTitle = findViewById(R.id.txtTitle);
        keterangan = findViewById(R.id.txtAN);
        keterangan.setText(atas_nama);
        icBack = findViewById(R.id.icoBack);
        btnKonfirmasi = findViewById(R.id.btnBKonfirmasi);
        txtTotal = findViewById(R.id.txtTotal);
        total = findViewById(R.id.total);
        total.setText("Rp " + format.format(hargaTotal));
        txtNorek = findViewById(R.id.txtNorek);
        txtNorek.setTypeface(MontserratRegular);
        norek = findViewById(R.id.norek);
        norek.setText(Html.fromHtml((no_tujuan)));
        share1 = findViewById(R.id.btnShare1);
        share = findViewById(R.id.btnShare);
        blockNorek = findViewById(R.id.blockNorek);
        thumbnail = findViewById(R.id.thumbnail);
        Picasso.get().load(dataUrl + "uploads/pembayaran/" + urlimage).into(thumbnail);

        if (no_tujuan.equals("")){
            blockNorek.setVisibility(View.GONE);
        }

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void shareNorek(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(no_tujuan);
        Toast.makeText(this, "Disalin ke clipboard", Toast.LENGTH_SHORT).show();

    }

    public void shareTotal(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(hargaTotal + "");
        Toast.makeText(this, "Disalin ke clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (class_name.equals("0")){
            super.onBackPressed();
        }else {
            Intent intent = new Intent(InformasiPembayaran.this, Order.class);
            intent.putExtra("id_horeka", id_horeka.toString());
            intent.putExtra("id_alamat", idAlamat);
            startActivity(intent);
        }

    }
}