package com.sabiogo.pickingapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sabiogo.pickingapp.R;

import java.util.List;
import entities.ItemStock;

/**
 * Created by Federico on 28/10/2017.
 */

public class StockAdapter extends ArrayAdapter<ItemStock>{

    private Activity activity;
    private static LayoutInflater inflater = null;
    private List<ItemStock> listaItemStocks;

    public StockAdapter(Activity activity, int textViewResourceId,List<ItemStock> lsItemStocks) {
        super(activity, textViewResourceId, lsItemStocks);
        try {
            this.activity = activity;
            this.listaItemStocks = lsItemStocks;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
            throw e;
        }
    }

    public int getCount() {
        return listaItemStocks.size();
    }

    public ItemStock getItem(ItemStock position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView codigoArticulo;
        public TextView cantidad;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.listview_row, null);
                holder = new ViewHolder();

                holder.codigoArticulo = (TextView) vi.findViewById(R.id.tv_descripcionItem);
                holder.cantidad = (TextView) vi.findViewById(R.id.tv_cantidadItem);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.codigoArticulo.setText(listaItemStocks.get(position).getCodigoArticulo());
            holder.cantidad.setText(Float.toString(listaItemStocks.get(position).getCantidad()));

            notifyDataSetChanged();

        } catch (Exception e) {
            throw e;
        }
        return vi;
    }
}