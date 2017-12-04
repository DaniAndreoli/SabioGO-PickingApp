package com.sabiogo.pickingapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.sabiogo.pickingapp.Adapters.SerialesAdapter;
import com.sabiogo.pickingapp.R;
import java.util.List;
import data_access.SerialDAO;
import entities.Serial;

/**
 * Created by Dani on 27/11/2017.
 */

public class SerialesEntradaSalidaFragment extends Fragment {

    private SerialesAdapter serialesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_slide_seriales_entradasalida, container, false);

        ListView lv_seriales = (ListView)fragmentLayout.findViewById(R.id.lv_serialesEntradaSalida);

        List<Serial> listadoSerialesEntradaSalida = SerialDAO.getSerialesLeidos(getContext(), "Entrada/Salida");
        serialesAdapter = new SerialesAdapter(getActivity(), R.layout.listview_row, listadoSerialesEntradaSalida);
        lv_seriales.setAdapter(serialesAdapter);

        return fragmentLayout;
    }

    public static SerialesEntradaSalidaFragment newInstance() {
        SerialesEntradaSalidaFragment fragment = new SerialesEntradaSalidaFragment();
        return fragment;
    }

}
