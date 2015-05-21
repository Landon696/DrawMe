package com.tutors.varsity.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.squareup.otto.Subscribe;
import com.tutors.varsity.R;
import com.tutors.varsity.ui.event.ColorPicked;
import com.tutors.varsity.ui.widget.ColorPicker;
import com.tutors.varsity.ui.widget.DrawingCanvas;
import com.tutors.varsity.util.otto.ApplicationBus;

/**
 * Created by Landon on 5/18/15.
 */
public class DrawFragment extends Fragment implements View.OnClickListener {

    ImageButton mPencil;
    ImageButton mEraser;
    View mColorSwatch;
    DrawingCanvas mDrawingCanvas;
    FrameLayout mContainer;
    int mPencilColor;

    public DrawFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onResume() {
        super.onResume();
        ApplicationBus.getInstance().register(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        ApplicationBus.getInstance().unregister(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pencil:
                makeToolbarButtonActive(view.getId());
                break;

            case R.id.eraser:
                makeToolbarButtonActive(view.getId());
                mDrawingCanvas.erase();
                break;

            case R.id.color_swatch:
                ColorPicker.newInstance().show(getFragmentManager(), "");
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                mDrawingCanvas.undo();
                return true;
            case R.id.action_email:
                return true;
            default:
                break;
        }

        return false;
    }

    @Subscribe
    public void onColorPicked(ColorPicked event) {

        mColorSwatch.setBackgroundColor(getResources().getColor(event.getColorId()));
        mPencilColor = event.getColorId();
        mDrawingCanvas.setPencilColor(mPencilColor);
    }

    private void intiViews(View v) {

        mContainer = (FrameLayout) v.findViewById(R.id.drawing_container);
        mPencil = (ImageButton) v.findViewById(R.id.pencil);
        mEraser = (ImageButton) v.findViewById(R.id.eraser);
        mColorSwatch = v.findViewById(R.id.color_swatch);

        mPencil.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mColorSwatch.setOnClickListener(this);

        mPencilColor = R.color.blue;
        mDrawingCanvas = new DrawingCanvas(getActivity());
        mDrawingCanvas.setPencilColor(mPencilColor);
        mContainer.addView(mDrawingCanvas);
    }

    private void makeToolbarButtonActive(int rId) {
        ((GradientDrawable)mPencil.getBackground()).setStroke(2, getResources().getColor(R.color.black));
        ((GradientDrawable)mEraser.getBackground()).setStroke(2, getResources().getColor(R.color.black));

        GradientDrawable gd = (GradientDrawable)(getActivity().findViewById(rId)).getBackground();
        gd.setStroke(2,getResources().getColor(R.color.white));

    }
}
