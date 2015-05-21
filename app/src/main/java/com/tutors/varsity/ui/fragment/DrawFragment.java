package com.tutors.varsity.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

        initViews(v);

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
                emailFriend(getActivity(),R.id.drawing_container);
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

    private void initViews(View v) {

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

    private void emailFriend(Activity activity, int resourceId) {
        takeScreenshotAndEmail(activity, resourceId);
    }

    private void takeScreenshotAndEmail(Activity activity, int resourceId) {
        long iterator= new DateTime().getMillis() / 1000L; //unix time

        String mPath = Environment.getExternalStorageDirectory().toString() + "/DrawMe/";
        View v1 = activity.getWindow().getDecorView().findViewById(resourceId);
        v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());
        v1.setDrawingCacheEnabled(true);

        final Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        File imageFile = new File(mPath);
        if (!imageFile.exists()) {
            imageFile.mkdirs();
        }
        imageFile = new File(imageFile+"/"+iterator+"_screenshot.png");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            emailPhoto(imageFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void emailPhoto(String path) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DrawMe Photo");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Checkout this cool photo I drew.");

        File file = new File(path);

        if (!file.exists() || !file.canRead()) {
            return;
        }

        Uri uri = Uri.fromFile(file);

        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }
}
