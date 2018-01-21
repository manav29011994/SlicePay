package com.slicepay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.slicepay.models.AddToAdapter;
import com.slicepay.models.OnBottomReachedListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by manav on 20/1/18.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    List<AddToAdapter> addToAdapters;
    Context context;
    OnBottomReachedListener onBottomReachedListener;


    public GridAdapter(Context context,ArrayList<AddToAdapter>addToAdapters)
    {
        this.context=context;
        this.addToAdapters=addToAdapters;

    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        GridViewHolder gridViewHolder = new GridViewHolder(v);
        return gridViewHolder;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        if (position == addToAdapters.size() - 1){
            onBottomReachedListener.onBottomReached(position);

        }
        holder.name.setText(addToAdapters.get(position).getName());
        Picasso.with(context).load(addToAdapters.get(position).getUrl()).transform(new RoundedCornersTransformation(10,10)).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return addToAdapters.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        public GridViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public void setFilter(ArrayList<AddToAdapter> al) {
        addToAdapters=new ArrayList<>();
        addToAdapters.addAll(al);
        notifyDataSetChanged();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }
}
