package com.ls.bitcoinapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ls.bitcoinapp.R;
import com.ls.bitcoinapp.model.CoinModel;

import java.util.ArrayList;

public class AdapterCoin extends RecyclerView.Adapter<AdapterCoin.CoinHolder> {

    private ArrayList<CoinModel> list;
    private OnSwitchAlarmListener listener;

    public AdapterCoin(OnSwitchAlarmListener listener) {
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    public void setList(ArrayList<CoinModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CoinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CoinHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_coin, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CoinHolder holder, int position) {
        CoinModel c = list.get(position);
        holder.txtName.setText(c.getTypeCoin().toString());
        holder.imgSwitch.setImageResource(c.isAlarm() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        if (c.getPercent() != null) {
            String percent = Double.parseDouble(c.getPercent()) < 0 ? c.getPercent() : "+" + c.getPercent();
            holder.txtPercent.setText(percent + "%");
        } else {
            holder.txtPercent.setText("0%");
        }
        holder.txtPrice.setText(c.getPriceShow());
        holder.imgSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSwitchAlarmListener(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CoinHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtPercent;
        private TextView txtPrice;
        private ImageView imgSwitch;

        public CoinHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtItemCoin);
            txtPercent = itemView.findViewById(R.id.txtItemPercent);
            txtPrice = itemView.findViewById(R.id.txtItemPrice);
            imgSwitch = itemView.findViewById(R.id.imgSwitch);
        }
    }

    public interface OnSwitchAlarmListener {
        void onSwitchAlarmListener(CoinModel coinModel);
    }
}
