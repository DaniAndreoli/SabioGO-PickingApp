package com.sabiogo.pickingapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sabiogo.pickingapp.Activities.StockActivity;
import com.sabiogo.pickingapp.R;
import java.util.List;
import data_access.SerialDAO;
import entities.ItemStock;
import entities.Serial;

/**
 * Created by Federico on 28/10/2017.
 */

public class SerialesAdapter extends ArrayAdapter<Serial>{

    private Activity activity;
    private static LayoutInflater inflater = null;
    private List<Serial> listaSeriales;
    private ViewPager mPager;

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

    public ViewPager getmPager() {
        return mPager;
    }

    public void setmPager(ViewPager mPager) {
        this.mPager = mPager;
    }

    public static class ViewHolder {
        public TextView serial;
        public Button btnEliminarSerial;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.listview_row, null);
                holder = new ViewHolder();

                holder.serial = (TextView) vi.findViewById(R.id.tv_descripcionItem);
                holder.btnEliminarSerial = (Button) vi.findViewById(R.id.btn_eliminarSerial);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.serial.setText(listaSeriales.get(position).getNumero());
            holder.btnEliminarSerial.setVisibility(View.VISIBLE);

            holder.btnEliminarSerial.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Serial serial = listaSeriales.get(position);
                    SerialDAO.borrarSerial(getContext(), serial.getNumero(), serial.getTipoComprobante(), serial.getCodigoArticulo());
                    listaSeriales = SerialDAO.getSerialList(getContext(), "Stock");

                    Toast.makeText(getContext(), "Producto Eliminado!", Toast.LENGTH_LONG).show();

                    //notifyDataSetChanged();
                    ((StockActivity) activity).actualizarPager();
                }
            });

        } catch (Exception e) {
            throw e;
        }
        return vi;
    }
}