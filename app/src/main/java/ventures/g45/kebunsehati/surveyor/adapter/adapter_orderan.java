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
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan;

public class adapter_orderan extends ArrayAdapter<modal_orderan> {
    private List<modal_orderan> daftarProduk;
    private Context context;
    int layout;

    public adapter_orderan (Context ctx, int layout, List<modal_orderan> daftar){
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

            holder.nama = v.findViewById(R.id.txtNama);
            holder.alamat = v.findViewById(R.id.txtAlamat);
            holder.item= v.findViewById(R.id.txtItem);
            holder.status = v.findViewById(R.id.txtStatus);

            v.setTag(holder);
        }
        else{
            holder=(ProdukHolder) v.getTag();
        }

        modal_orderan modalOrderan = daftarProduk.get(position);
        holder.nama.setText("Nama : " + modalOrderan.getNama());
        holder.alamat.setText("Alamat : " + modalOrderan.getAlama());
        holder.item.setText("Jumlah Item : " + modalOrderan.getJumlahitem());
        holder.status.setText("Status : " + modalOrderan.getStatus());

        return v;
    }

    static class ProdukHolder{
        TextView nama, alamat, item, status;
    }
}

