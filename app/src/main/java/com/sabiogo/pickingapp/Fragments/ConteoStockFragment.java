package com.sabiogo.pickingapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sabiogo.pickingapp.R;
import com.sabiogo.pickingapp.Adapters.StockAdapter;

import java.util.List;

import data_access.StockDAO;
import entities.ItemStock;

/**
 * Created by Dani on 27/11/2017.
 */

public class ConteoStockFragment extends Fragment {

    private StockAdapter stockAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_slide_conteo_stock, container, false);

        ListView lv_articulos = (ListView)fragmentLayout.findViewById(R.id.lv_itemsStock);

        List<ItemStock> listadoItemsStock = StockDAO.getStockList(getContext());
        stockAdapter = new StockAdapter(getActivity(), R.layout.listview_conteo_row, listadoItemsStock);
        lv_articulos.setAdapter(stockAdapter);

        return fragmentLayout;
    }

    public static ConteoStockFragment newInstance() {
        ConteoStockFragment fragment = new ConteoStockFragment();
        return fragment;
    }

}
