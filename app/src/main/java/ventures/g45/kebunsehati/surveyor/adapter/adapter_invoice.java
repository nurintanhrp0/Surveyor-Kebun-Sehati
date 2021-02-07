package ventures.g45.kebunsehati.surveyor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_invoice;

public class adapter_invoice extends ArrayAdapter<modal_invoice> {
    private List<modal_invoice> daftarMenu;
    private Context context;
    int layout;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    Integer id_menu =0;

    public adapter_invoice(@NonNull Context context, int layout, List<modal_invoice> daftarMenu) {
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

            holder.status = v.findViewById(R.id.icoStatus);
            holder.due_date = v.findViewById(R.id.txtTempo);
            holder.total = v.findViewById(R.id.txtTotal);
            holder.tglOrder = v.findViewById(R.id.txtTglOrder);

            v.setTag(holder);
        }
        else{
            holder=(MenuHolder) v.getTag();


        }

        modal_invoice menu = daftarMenu.get(position);
        holder.total.setText("Rp " + decimalFormat.format(menu.getTotal()));
        holder.tglOrder.setText(menu.getId_invoice());
        if (!menu.getStatus().equals("lunas")){
            holder.due_date.setText("Jatuh tempo : " + menu.getDue_date());
            holder.due_date.setTextColor(v.getResources().getColor(android.R.color.holo_red_dark));
            holder.status.setImageDrawable(v.getResources().getDrawable(R.drawable.belum_lunas));

        }else {
            holder.due_date.setText("Tanggal Pembayaran : " + menu.getDue_date());
            holder.due_date.setTextColor(v.getResources().getColor(android.R.color.holo_green_light));
            holder.status.setImageDrawable(v.getResources().getDrawable(R.drawable.lunas));
        }


        return v;
    }

    static class MenuHolder{
        TextView due_date, total, tglOrder;
        ImageView status;
    }
}
