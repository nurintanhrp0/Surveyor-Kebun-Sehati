package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;

public class adapter_orderan_sales extends ArrayAdapter<modal_orderan_sales> {
    private List<modal_orderan_sales> daftarMenu;
    private Context context;
    int layout;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    Integer id_menu =0;

    public adapter_orderan_sales(@NonNull Context context, int layout, List<modal_orderan_sales> daftarMenu) {
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

            holder.nama = v.findViewById(R.id.txtNama);
            holder.qty = v.findViewById(R.id.txtQty);
            holder.total = v.findViewById(R.id.txtTotal);
            holder.edit = v.findViewById(R.id.txtEdit);
            holder.catatan = v.findViewById(R.id.txtCatatan);
            holder.totalNormal = v.findViewById(R.id.txtTotalNormal);

            v.setTag(holder);
        }
        else{
            holder=(MenuHolder) v.getTag();


        }

        modal_orderan_sales menu = daftarMenu.get(position);
        holder.nama.setText(menu.getNama());
        holder.total.setText("Rp " + decimalFormat.format(Integer.valueOf(menu.getHarga())));
        holder.qty.setText(decimalFormat.format(Integer.valueOf(menu.getQty())) + " " + menu.getSatuan());
        if (!menu.getCatatan().equals("null") || !menu.getCatatan().equals("")){
            holder.catatan.setVisibility(View.VISIBLE);
            holder.catatan.setText(menu.getCatatan());
        }
        if (menu.getDari() == 1){
            holder.edit.setVisibility(View.VISIBLE);
        }

        if (!menu.getHarga_normal().equals("0")){
            holder.totalNormal.setVisibility(View.VISIBLE);
            holder.totalNormal.setText("Rp " + decimalFormat.format(Integer.valueOf(menu.getHarga_normal())));
            holder.totalNormal.setPaintFlags(holder.totalNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            holder.totalNormal.setVisibility(View.GONE);
        }

        return v;
    }

    static class MenuHolder{
        TextView nama, qty, total, edit, catatan, totalNormal;
    }
}
