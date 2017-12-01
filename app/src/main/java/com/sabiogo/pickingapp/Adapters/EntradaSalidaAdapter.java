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

import entities.Item;

/**
 * Created by Federico on 28/10/2017.
 */

public class EntradaSalidaAdapter extends ArrayAdapter<Item>{

    private Activity activity;
    private static LayoutInflater inflater = null;
    private List<Item> listaItemsComprobante;

    public EntradaSalidaAdapter(Activity activity, int textViewResourceId,List<Item> lsItems) {
        super(activity, textViewResourceId, lsItems);
        try {
            this.activity = activity;
            this.listaItemsComprobante = lsItems;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
            throw e;
        }
    }

    public int getCount() {
        return listaItemsComprobante.size();
    }

    public Item getItem(Item position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView descripcionArticulo;
        public TextView cantidad;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.listview_conteo_row, null);
                holder = new ViewHolder();

                holder.descripcionArticulo = (TextView) vi.findViewById(R.id.tv_descripcionItem);
                holder.cantidad = (TextView) vi.findViewById(R.id.tv_cantidadItem);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.descripcionArticulo.setText(listaItemsComprobante.get(position).getDescripcion());
            holder.cantidad.setText(Double.toString(listaItemsComprobante.get(position).getSaldo()));
        } catch (Exception e) {
            throw e;
        }
        return vi;
    }
}