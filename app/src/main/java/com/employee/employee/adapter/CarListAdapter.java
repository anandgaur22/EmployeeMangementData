package com.employee.employee.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.employee.employee.R;
import com.employee.employee.interfaces.ICallback;
import com.employee.employee.model.CarListModel;
import com.employee.employee.utils.AppGlobalValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarListViewHolder> {

    private ICallback iCallback;
    private List<CarListModel> carListModels;
    private List<CarListModel> filterlist;
    private Context context;


    public CarListAdapter(List carListModels, Context context, ICallback iCallback) {
        this.carListModels = carListModels;
        this.context = context;
        this.iCallback = iCallback;
        this.filterlist = new ArrayList<CarListModel>();
        this.filterlist.addAll(carListModels);
    }

    @NonNull
    @Override
    public CarListAdapter.CarListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_layout, parent, false);
        CarListAdapter.CarListViewHolder carlist = new CarListAdapter.CarListViewHolder(view);
        AppGlobalValidation.setScaleAnimation(view);
        return carlist;
    }

    @Override
    public void onBindViewHolder(@NonNull CarListViewHolder holder, final int position) {



         holder.car_no_txtView.setText(carListModels.get(position).getCar_no());
         holder.customer_nme_txtView.setText(carListModels.get(position).getCustomer_name());


        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iCallback.onItemClick(position);
            }
        });

    }




    @Override
    public int getItemCount() {
        return carListModels.size();
    }


    public class CarListViewHolder extends RecyclerView.ViewHolder {

        CardView card_view;
        TextView car_no_txtView,customer_nme_txtView;

        public CarListViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view);
            car_no_txtView = view.findViewById(R.id.car_no_txtView);
            customer_nme_txtView = view.findViewById(R.id.customer_nme_txtView);

        }
    }

    //ToDo Filter Class
    public void filter(CharSequence charText) {
        //charText = charText.toLowerCase(Locale.getDefault());
        carListModels.clear();
        if (charText.length() == 0) {
            carListModels.addAll(filterlist);
        } else {
            for (CarListModel list : filterlist) {
                if (list.getCar_no().toLowerCase(Locale.getDefault()).contains(charText)) {
                    carListModels.add(list);
                }
            }
        }
        notifyDataSetChanged();
    }
}
