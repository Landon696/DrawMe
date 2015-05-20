package com.tutors.varsity.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.tutors.varsity.R;

/**
 * Created by Landon on 5/18/15.
 */
public class DrawFragment extends Fragment implements View.OnClickListener {

    ImageButton mPencil;
    ImageButton mEraser;
    View mColorSwatch;

    public DrawFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_draw, container, false);

        intiViews(v);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //default to pencil view
        makeToolbarButtonActive(R.id.pencil);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pencil:
                makeToolbarButtonActive(view.getId());
                break;

            case R.id.eraser:
                makeToolbarButtonActive(view.getId());
                break;

            case R.id.color_swatch:
                break;
        }
    }

    private void intiViews(View v) {

        mPencil = (ImageButton) v.findViewById(R.id.pencil);
        mEraser = (ImageButton) v.findViewById(R.id.eraser);
        mColorSwatch = v.findViewById(R.id.color_swatch);

        mPencil.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mColorSwatch.setOnClickListener(this);
    }

    private void makeToolbarButtonActive(int rId) {
        ((GradientDrawable)mPencil.getBackground()).setStroke(2, getResources().getColor(R.color.black));
        ((GradientDrawable)mEraser.getBackground()).setStroke(2, getResources().getColor(R.color.black));

        GradientDrawable gd = (GradientDrawable)(getActivity().findViewById(rId)).getBackground();
        gd.setStroke(2,getResources().getColor(R.color.white));

    }
}
