package com.sabiogo.pickingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabiogo.pickingapp.PickeoStock.OnListFragmentInteractionListener;
import com.sabiogo.pickingapp.dummy.DummyContent.DummyItem;

import java.util.List;

import entities.ItemStock;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemStockRecyclerViewAdapter extends RecyclerView.Adapter<MyItemStockRecyclerViewAdapter.ViewHolder> {

    private final List<ItemStock> mValues;
    //private final OnListFragmentInteractionListener mListener;

    //public MyItemStockRecyclerViewAdapter(List<ItemStock> items, OnListFragmentInteractionListener listener) {
    public MyItemStockRecyclerViewAdapter(List<ItemStock> items) {
            mValues = items;
        //mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itemstock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.codArt.setText(Integer.toString(mValues.get(position).getCodigoArticulo()));
        holder.cantidad.setText(Float.toString(mValues.get(position).getCantidad()));

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView codArt;
        public final TextView cantidad;
        public ItemStock mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            codArt = (TextView) view.findViewById(R.id.tv_codArtStock);
            cantidad = (TextView) view.findViewById(R.id.tv_cantidadStock);
        }

        @Override
    public String toString() {
            return "";
        //return super.toString() + " '" + mContentView.getText() + "'";
    }
}
}
