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
import entities.Serial;

/**
 * Created by Federico on 28/10/2017.
 */

public class SerialesAdapter extends ArrayAdapter<Serial>{

    private Activity activity;
    private static LayoutInflater inflater = null;
    private List<Serial> listaSeriales;

    public SerialesAdapter(Activity activity, int textViewResourceId,List<Serial> lsSeriales) {
        super(activity, textViewResourceId, lsSeriales);
        try {
            this.activity = activity;
            this.listaSeriales = lsSeriales;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
            throw e;
        }
    }

    public int getCount() {
        return listaSeriales.size();
    }

    public ItemStock getItem(ItemStock position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView serial;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.listview_conteo_row, null);
                holder = new ViewHolder();

                holder.serial = (TextView) vi.findViewById(R.id.tv_descripcionItem);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.serial.setText(listaSeriales.get(position).getSerial());
        } catch (Exception e) {
            throw e;
        }
        return vi;
    }
}