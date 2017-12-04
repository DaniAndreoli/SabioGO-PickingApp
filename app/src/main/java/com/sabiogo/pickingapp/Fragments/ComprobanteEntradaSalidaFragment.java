package com.sabiogo.pickingapp.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sabiogo.pickingapp.Adapters.EntradaSalidaAdapter;
import com.sabiogo.pickingapp.R;
import com.sabiogo.pickingapp.Adapters.StockAdapter;
import java.util.List;

import data_access.ComprobanteDAO;
import data_access.StockDAO;
import entities.Comprobante;
import entities.Item;
import entities.ItemStock;

/**
 * Created by Dani on 27/11/2017.
 */

public class ComprobanteEntradaSalidaFragment extends Fragment {

    private EntradaSalidaAdapter entradaSalidaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_slide_comprobante_entradasalida, container, false);

        ListView lv_articulos = (ListView)fragmentLayout.findViewById(R.id.lv_itemsComprobante);

        Comprobante comp = ComprobanteDAO.getComprobanteUsuario(getContext(), "pistola");
        entradaSalidaAdapter = new EntradaSalidaAdapter(getActivity(), R.layout.listview_row, comp.getItems());
        lv_articulos.setAdapter(entradaSalidaAdapter);

        return fragmentLayout;
    }

    public static ComprobanteEntradaSalidaFragment newInstance() {
        ComprobanteEntradaSalidaFragment fragment = new ComprobanteEntradaSalidaFragment();
        return fragment;
    }

}
