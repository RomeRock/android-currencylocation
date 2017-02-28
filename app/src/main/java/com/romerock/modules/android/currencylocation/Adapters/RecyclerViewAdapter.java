package com.romerock.modules.android.currencylocation.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.modules.android.currencylocation.Interfaces.ItemClickInterface;
import com.romerock.modules.android.currencylocation.Model.ItemSettings;
import com.romerock.modules.android.currencylocation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ebricko on 15/12/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
    private final ItemClickInterface itemClickListener;
    private List<ItemSettings> ItemSettings;
    private int position;

    public RecyclerViewAdapter(List<com.romerock.modules.android.currencylocation.Model.ItemSettings> ItemSettings, @NonNull ItemClickInterface listener) {
        this.ItemSettings = ItemSettings;
        this.itemClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // - get data from your ItemSettings at this position
        // - replace the contents of the view with that ItemSettings

        final String[]split= ItemSettings.get(position).getTitle().split("#");
        viewHolder.txtViewTitle.setText(split[0]);
        if (ItemSettings.get(position).getImageUrl() != 0 && ItemSettings.get(position).getImageUrl() != -10)
            viewHolder.imgViewIcon.setImageResource(ItemSettings.get(position).getImageUrl());
        if (ItemSettings.get(position).getImageUrl() == -10)
            viewHolder.imgViewIcon.setVisibility(View.GONE);
        if (!ItemSettings.get(position).isStatusEnable()) {
            viewHolder.txtViewPreview.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtViewPreview.setVisibility(View.GONE);
        }
        if (ItemSettings.get(position).isSelected()) {
            viewHolder.item_rel.setBackgroundResource(R.drawable.border_item_selected);

        }else{
            viewHolder.item_rel.setBackgroundColor(0x00FF00);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(view, position, split[1]); // call the onClick in the OnItemClickListener
            }
        });
    }

    @Override
    public int getItemCount() {
        return ItemSettings.size();
    }

    @Override
    public void onClick(View view) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        public TextView txtViewPreview;
        public RelativeLayout item_rel;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            txtViewPreview = (TextView) itemLayoutView.findViewById(R.id.item_preview);
            item_rel=(RelativeLayout)itemLayoutView.findViewById(R.id.item_relative);
        }
    }

    public void setFilter(List<ItemSettings> countryModels) {
        ItemSettings = new ArrayList<>();
        ItemSettings.addAll(countryModels);
        notifyDataSetChanged();
    }
}