package com.tutors.varsity.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.tutors.varsity.R;
import com.tutors.varsity.model.DrawMePath;
import com.tutors.varsity.ui.event.ColorPicked;
import com.tutors.varsity.ui.event.LineThicknessPicked;
import com.tutors.varsity.ui.event.PlaybackFinished;
import com.tutors.varsity.ui.widget.ColorPicker;
import com.tutors.varsity.ui.widget.DrawingCanvas;
import com.tutors.varsity.ui.widget.LineThicknessPicker;
import com.tutors.varsity.util.BitmapHelper;
import com.tutors.varsity.util.otto.ApplicationBus;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Landon on 5/18/15.
 */
public class DrawFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_GALLERY = 1;

    ImageButton mPencil;
    ImageButton mEraser;
    View mColorSwatch;
    FrameLayout mDrawingContainer;
    FrameLayout mDrawingCanvasContainer;
    DrawingCanvas mDrawingCanvas;
    int mPencilColor;
    ImageView mPhotoView;
    Bitmap mPhoto;
    int mLineThickness;
    int mDrawingCanvasState = DrawingCanvas.DRAWING;
    LinearLayout mToolbar;

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
    public void onDestroy() {
        super.onDestroy();
        if (mPhoto != null) {
            mPhoto.recycle();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pencil:
                makeToolbarButtonActive(view.getId());
                LineThicknessPicker.newInstance(mPencilColor).show(getFragmentManager(),"");
                break;

            case R.id.eraser:
                makeToolbarButtonActive(view.getId());
                mDrawingCanvas.erase();
                mPhotoView.setImageBitmap(null);
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
            case R.id.action_play:
                mDrawingCanvasState = DrawingCanvas.REPLAY_PLAY;
                mToolbar.setVisibility(View.GONE);
                getActivity().invalidateOptionsMenu();
                mDrawingCanvas.play();
                return true;
            case R.id.action_undo:
                mDrawingCanvas.undo();
                return true;
            case R.id.action_add_photo:
                loadPhotoGallery();
                return true;
            case R.id.action_email:
                emailFriend(getActivity(),R.id.drawing_container);
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        //Adding picture from photo gallery intent
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {

            //need device screen size to optimize bitmap load
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            try {
                final Uri imageUri = intent.getData();
                String picFullPath = "";

                if (imageUri != null) {

                    // now we get the path to the image file
                    Cursor cursor = getActivity().getContentResolver().query(imageUri,
                            new String[]{MediaStore.Images.Media.DATA},
                            null, null, null);
                    cursor.moveToFirst();
                    picFullPath = cursor.getString(0);

                    cursor.close();
                }

                mPhoto = BitmapHelper.optimize(picFullPath, width, height);
                boolean wideScreen = BitmapHelper.wideScreen(mPhoto.getWidth(), mPhoto.getHeight());

                //handle landscape pic
                if (mPhoto.getWidth() > mPhoto.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    mPhoto = Bitmap.createBitmap(mPhoto, 0, 0, mPhoto.getWidth(),
                            mPhoto.getHeight(), matrix, true);
                }

                if (wideScreen) {
                    mPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                else {
                    mPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);
                }

                mPhotoView.setImageBitmap(mPhoto);

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> drawnPathsString = new ArrayList<>();

        LinkedList<DrawMePath> drawnPaths = mDrawingCanvas.getPaths();
        for (DrawMePath p : drawnPaths) {
            drawnPathsString.add(new Gson().toJson(p));
        }
        outState.putStringArrayList("drawSteps", drawnPathsString);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem undo = menu.findItem(R.id.action_undo);
        MenuItem picture = menu.findItem(R.id.action_add_photo);
        MenuItem email = menu.findItem(R.id.action_email);
        MenuItem mediaPlay = menu.findItem(R.id.action_play);

        if (mDrawingCanvasState == DrawingCanvas.DRAWING) {
            undo.setVisible(true);
            picture.setVisible(true);
            email.setVisible(true);
        }
        else if (mDrawingCanvasState == DrawingCanvas.REPLAY_PLAY) {
            undo.setVisible(false);
            picture.setVisible(false);
            email.setVisible(false);
            mediaPlay.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Subscribe
    public void onColorPicked(ColorPicked event) {

        mColorSwatch.setBackgroundColor(getResources().getColor(event.getColorId()));
        mPencilColor = event.getColorId();
        mDrawingCanvas.setPencilColor(mPencilColor);
    }

    @Subscribe
    public void onLineStrokePicked(LineThicknessPicked event) {
        mLineThickness = event.getLineThickness();
        mDrawingCanvas.setStroke(mLineThickness);
    }

    @Subscribe
    public void onPlaybackFinished(PlaybackFinished event) {
        mDrawingCanvasState = DrawingCanvas.DRAWING;
        getActivity().invalidateOptionsMenu();
        mToolbar.setVisibility(View.VISIBLE);
    }

    private void initViews(View v) {

        mToolbar = (LinearLayout) v.findViewById(R.id.toolbar);
        mPhotoView = (ImageView) v.findViewById(R.id.photo);
        mDrawingContainer = (FrameLayout) v.findViewById(R.id.drawing_container);
        mDrawingCanvasContainer = (FrameLayout) v.findViewById(R.id.drawing_canvas_container);
        mPencil = (ImageButton) v.findViewById(R.id.pencil);
        mEraser = (ImageButton) v.findViewById(R.id.eraser);
        mColorSwatch = v.findViewById(R.id.color_swatch);

        mPencil.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mColorSwatch.setOnClickListener(this);

        mPencilColor = R.color.blue;
        mDrawingCanvas = new DrawingCanvas(getActivity());
        mDrawingCanvas.setPencilColor(mPencilColor);
        mDrawingCanvasContainer.addView(mDrawingCanvas);
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

    private void loadPhotoGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }
}
