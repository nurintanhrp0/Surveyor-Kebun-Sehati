package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_produk;

public class adapter_produk extends ArrayAdapter<modal_produk> {
    private List<modal_produk> daftarMenu;
    private Context context;
    int layout;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    Integer id_menu =0;

    public adapter_produk(@NonNull Context context, int layout, List<modal_produk> daftarMenu) {
        super(context, layout, daftarMenu);
        this.daftarMenu = daftarMenu;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=convertView;
        MenuHolder holder;

        if(v==null){
            LayoutInflater vi=((Activity)context).getLayoutInflater();
            v=vi.inflate(layout, parent,false);

            holder=new MenuHolder();

            holder.nama = v.findViewById(R.id.txtNamaProduk);
            holder.harga_jual = v.findViewById(R.id.txtHarga);
            holder.stock = v.findViewById(R.id.txtStock);
            holder.stock_habis = v.findViewById(R.id.txtStockHabis);
            holder.diskon = v.findViewById(R.id.txtDiskon);

            v.setTag(holder);
        }
        else{
            holder=(MenuHolder) v.getTag();


        }

        modal_produk menu = daftarMenu.get(position);
        holder.nama.setText(menu.getNama());
        holder.harga_jual.setText("Rp " + decimalFormat.format(menu.getHarga()) + "/ " + decimalFormat.format(Integer.valueOf(menu.getUkuran())) + " " + menu.getSatuan());
        holder.stock.setText(menu.getKeterangan_stock());
        holder.stock_habis.setText(menu.getStock_habis());

        if (menu.getDiskon() == 0){
            holder.diskon.setVisibility(View.GONE);
        }else {
            holder.stock.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            holder.diskon.setText("Rp " + decimalFormat.format(menu.getDiskon()) + "/ " + decimalFormat.format(Integer.valueOf(menu.getUkuran())) + " " + menu.getSatuan());
            holder.diskon.setVisibility(View.VISIBLE);
            holder.diskon.setPaintFlags(holder.diskon.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        //holder.txtBerat.setText(decimalFormat.format(Integer.valueOf(menu.getUkuran())) + "/" + menu.getSatuan());

       /* if (menu.getQty_temp() != 0){
            holder.blockCart.setVisibility(View.VISIBLE);
            holder.txtQty_temp.setText(menu.getQty_temp() + "");
        }*/

        return v;
    }

    static class MenuHolder{
        TextView nama, harga_jual, stock, stock_habis, diskon;
        TextView txtBerat, txtQty_temp;
        RelativeLayout blockCart;
    }
}
