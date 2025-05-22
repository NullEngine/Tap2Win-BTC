package com.codekroy.tap2winbtc.adapter;






import android.annotation.SuppressLint;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.codekroy.tap2winbtc.R;
import com.codekroy.tap2winbtc.model.fetch_model;

public class History_Adapter extends ArrayAdapter {
    ArrayList<fetch_model> arrayList;
    Context context;
    TextView email,status,amount;
    View divider_view;
    int COM;
    public History_Adapter(Context context, ArrayList<fetch_model> arrayList,int COM) {
        super(context,R.layout.history_child);
        this.arrayList=arrayList;
        this.context=context;
        this.COM=COM;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;

    }
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater.from(context));

            @SuppressLint("ViewHolder")
            View rowView=inflater.inflate(R.layout.history_child, parent,false);
            email=rowView.findViewById(R.id.email);
            status=rowView.findViewById(R.id.status);
            divider_view=rowView.findViewById(R.id.view);
            amount=rowView.findViewById(R.id.amount);
            if (position==0){
                divider_view.setVisibility(View.VISIBLE);
            }
            amount.setText(arrayList.get(position).getPoints() +" Pts");
            if (COM==1){
                @SuppressLint("SimpleDateFormat") SimpleDateFormat server_formet=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat need_format=new SimpleDateFormat("dd MMM yyyy 'at' HH:mm");
                try {
                    Date date=server_formet.parse(arrayList.get(position).gettime());
                    email.setText(arrayList.get(position).getEmail());
                    if (arrayList.get(position).getStatus()==0){
                        status.setText("Status:  Under Review\nAmount: BTC("+formatDoubleValue(
                                arrayList.get(position).getbtc())+")" +
                                "\nTime: "+need_format.format(Objects.requireNonNull(date)));
                    } else if (arrayList.get(position).getStatus()==1) {
                        status.setText("Status:  Approved\nAmount:  BTC("+formatDoubleValue(
                                arrayList.get(position).getbtc())+")" +
                                "\nTime: "+need_format.format(Objects.requireNonNull(date)));
                    } else {
                        status.setText("Status:  IW\nAmount:  BTC("+formatDoubleValue(
                                arrayList.get(position).getbtc())+")" +
                                "\nTime: "+need_format.format(Objects.requireNonNull(date)));

                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            } else if (COM==2){
                status.setVisibility(View.GONE);
                email.setText(arrayList.get(position).getEmail().replaceAll("(^[^@]{5}|(?!^)\\G)[^@]", "$1*"));
            }
        return rowView;

    }

    public static String formatDoubleValue(double value) {
        if (value % 1 == 0) {
            return new DecimalFormat("0.00").format(value);
        } else {
            String formatted = new DecimalFormat("0.000000000000").format(value);
            int nonZeroIndex = -1;
            for (int i = formatted.indexOf('.') + 1; i < formatted.length(); i++) {
                if (formatted.charAt(i) != '0') {
                    nonZeroIndex = i;
                    break;
                }
            }
            if (nonZeroIndex == -1) return formatted;

            int endIndex = Math.min(nonZeroIndex + 9, formatted.length());
            return formatted.substring(0, endIndex);
        }
    }

}
