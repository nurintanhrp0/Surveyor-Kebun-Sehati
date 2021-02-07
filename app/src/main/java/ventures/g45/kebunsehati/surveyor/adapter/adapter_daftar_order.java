package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_orderan_sales;

public class adapter_daftar_order extends ArrayAdapter<modal_orderan_sales> {
    private List<modal_orderan_sales> daftarMenu;
    private Context context;
    int layout;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    Integer id_menu =0;

    public adapter_daftar_order(@NonNull Context context, int layout, List<modal_orderan_sales> daftarMenu) {
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

            holder.tanggal = v.findViewById(R.id.txtTempo);
            holder.total = v.findViewById(R.id.txtTotal);
            holder.status = v.findViewById(R.id.icoStatus);
            holder.pengiriman = v.findViewById(R.id.txtTanggal);
            holder.blockTanggal = v.findViewById(R.id.blockTanggal);
            holder.tglOrder = v.findViewById(R.id.txtTglOrder);

            v.setTag(holder);
        }
        else{
            holder=(MenuHolder) v.getTag();


        }

        modal_orderan_sales menu = daftarMenu.get(position);
        holder.tanggal.setText(menu.getQty());
        holder.total.setText("Rp " + decimalFormat.format(Integer.valueOf(menu.getTotal())));
        holder.blockTanggal.setVisibility(View.VISIBLE);
        holder.tglOrder.setVisibility(View.VISIBLE);
        holder.tglOrder.setText(menu.getTglOrder());
        holder.pengiriman.setText(menu.getTanggal());
        if (menu.getStatus_order().equals("diterima")){
            holder.status.setImageDrawable(v.getResources().getDrawable(R.drawable.lunas));
            //holder.tanggal.setTextColor(v.getResources().getColor(android.R.color.holo_green_light));

        }else {
            holder.status.setImageDrawable(v.getResources().getDrawable(R.drawable.belum_lunas));
            //holder.tanggal.setTextColor(v.getResources().getColor(android.R.color.holo_red_dark));
        }
        return v;
    }

    static class MenuHolder{
        TextView tanggal, total, pengiriman, tglOrder;
        ImageView status;
        LinearLayout blockTanggal;
    }
}
