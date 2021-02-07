package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.util.List;

import ventures.g45.kebunsehati.surveyor.Angkringan;
import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_penugasan;

public class adapter_penugasan extends ArrayAdapter<modal_penugasan> {
    private List<modal_penugasan> daftarProduk;
    private Context context;
    int layout;
    //String dataUrl = "https://dataks.g45lab.xyz/";
    String dataUrl = "https://datalive.kebunsehati.id/";

    public adapter_penugasan(Context ctx, int layout, List<modal_penugasan> daftar){
        super(ctx, layout, daftar);

        this.context=ctx;
        this.layout=layout;
        this.daftarProduk=daftar;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        ProdukHolder holder;

        if(v==null){
            LayoutInflater vi=((Activity)context).getLayoutInflater();
            v=vi.inflate(layout, parent,false);

            holder=new ProdukHolder();

            holder.nama = v.findViewById(R.id.txtKelurahan);
            holder.thumbnail = v.findViewById(R.id.thumbnail);

            v.setTag(holder);
        }
        else{
            holder=(ProdukHolder) v.getTag();
        }

        final modal_penugasan modalPenugasan = daftarProduk.get(position);
        holder.nama.setText(modalPenugasan.getNama());
        Picasso.get().load(dataUrl + "uploads/kelurahaan/" + modalPenugasan.getThumbnail()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext(), R.style.Theme_AppCompat_Light_NoActionBar_FullScreen);
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.popup_gambar, null);
                dialog.setView(dialogView);
                final AlertDialog alertDialog = dialog.create();
                ImageView thumbnail = dialogView.findViewById(R.id.thumbnail2);
                Picasso.get().load(dataUrl + "uploads/kelurahaan/" + modalPenugasan.getThumbnail()).into(thumbnail);

                alertDialog.show();
            }
        });

        return v;
    }

    static class ProdukHolder{
        TextView nama;
        ImageView thumbnail;
    }
}

