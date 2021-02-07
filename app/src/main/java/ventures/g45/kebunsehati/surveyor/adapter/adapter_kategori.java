package ventures.g45.kebunsehati.surveyor.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ventures.g45.kebunsehati.surveyor.R;
import ventures.g45.kebunsehati.surveyor.modal.modal_kategori;
import ventures.g45.kebunsehati.surveyor.Order;

public class adapter_kategori extends RecyclerView.Adapter<adapter_kategori.ViewHolder> {
    public static Object ViewHolder;
    private static int clickedTextViewPos;
    private List<modal_kategori> data;
    private static Context context;
    int layout;
    Order listener;
    String dataUrl = "https://dataks.g45lab.xyz/";
    static Integer id_kategori;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static Integer selected;
    public static final String PAYLOAD_NAME = "PAYLOAD_NAME";
    public static final String PAYLOAD_KOSONG = "PAYLOAD_KOSONG";

    public adapter_kategori(ArrayList<modal_kategori> daftar, Context ctx) {
        data = daftar;
        context = ctx;
        listener = (Order) ctx;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cv_kategori,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.nama.setText(data.get(i).getNama());
        Picasso.get().load(dataUrl + "uploads/kategori/" + data.get(i).getThumbnail()).into(viewHolder.thumbnail);

        if (data.get(i).getId_kategori() == i){
            viewHolder.line.setVisibility(View.VISIBLE);
            viewHolder.nama.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

        if (clickedTextViewPos == i) {

            viewHolder.line.setVisibility(View.VISIBLE);
            viewHolder.nama.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            id_kategori = data.get(i).getId_kategori();
            Integer dari = 0;
            Order.focusOnView(id_kategori, dari);


        } else {
            viewHolder.line.setVisibility(View.GONE);
            viewHolder.nama.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final adapter_kategori.ViewHolder viewHolder, final int i, List<Object> playLoad) {
        viewHolder.nama.setText(data.get(i).getNama());
        Picasso.get().load(dataUrl + "uploads/kategori/" + data.get(i).getThumbnail()).into(viewHolder.thumbnail);


        if (data.get(i).getId_kategori() == i){
            viewHolder.line.setVisibility(View.VISIBLE);
            viewHolder.nama.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

        if (!playLoad.isEmpty()) {
            for (final Object payload : playLoad) {
                String pay = (String) payload;
                if (pay.equals("ganti")){
                    viewHolder.line.setVisibility(View.VISIBLE);
                     viewHolder.nama.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
                else {
                    viewHolder.line.setVisibility(View.GONE);
                   viewHolder.nama.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                }
            }

        }else {
            if (clickedTextViewPos == i) {
                viewHolder.line.setVisibility(View.VISIBLE);
                viewHolder.nama.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                id_kategori = data.get(i).getId_kategori();
                Integer dari = 0;
                Order.focusOnView(id_kategori, dari);
            } else {
                viewHolder.line.setVisibility(View.GONE);
                viewHolder.nama.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cvMain;
        ImageView thumbnail;
        TextView nama;
        View line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            nama = (TextView) itemView.findViewById(R.id.nama);
            cvMain = (CardView) itemView.findViewById(R.id.cvMain);
            line = itemView.findViewById(R.id.line);

            cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = 0;
                    String play = "";
                    clickedTextViewPos = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
