package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_angkringan;

public class adapter_angkringan extends ArrayAdapter<modal_angkringan> {
    private List<modal_angkringan> daftarProduk;
    private Context context;
    int layout;


    public adapter_angkringan(Context ctx, int layout, List<modal_angkringan> daftar){
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

            holder.nama = v.findViewById(R.id.txtNamaPenjual);
            holder.alamat = v.findViewById(R.id.txtAlamat);
            holder.juragan= v.findViewById(R.id.txtNamaJuragan);

            v.setTag(holder);
        }
        else{
            holder=(ProdukHolder) v.getTag();
        }

        modal_angkringan modalAngkringan = daftarProduk.get(position);
        holder.nama.setText(modalAngkringan.getNama_penjual());
        holder.alamat.setText(modalAngkringan.getAlamat());
        holder.juragan.setText("Juragan : " + modalAngkringan.getNama_juragan());

        return v;
    }

    static class ProdukHolder{
        TextView nama, alamat, juragan;

    }
}

