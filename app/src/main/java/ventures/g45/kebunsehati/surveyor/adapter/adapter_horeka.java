package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_horeka;

public class adapter_horeka extends ArrayAdapter<modal_horeka> {
    private List<modal_horeka> daftarProduk;
    private Context context;
    int layout;
    //String dataUrl = "https://dataks.g45lab.xyz/";
    String dataUrl = "https://datalive.kebunsehati.id/";

    public adapter_horeka(Context ctx, int layout, List<modal_horeka> daftar){
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

        final modal_horeka modalHoreka = daftarProduk.get(position);
        holder.nama.setText(modalHoreka.getNama());
        holder.thumbnail.setVisibility(View.GONE);

        return v;
    }

    static class ProdukHolder{
        TextView nama;
        ImageView thumbnail;
    }
}

