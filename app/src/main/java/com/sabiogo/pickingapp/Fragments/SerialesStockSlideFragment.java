package com.sabiogo.pickingapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabiogo.pickingapp.R;

/**
 * Created by Dani on 27/11/2017.
 */

public class SerialesStockSlideFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_seriales_stock, container, false);

        return rootView;
    }
}
