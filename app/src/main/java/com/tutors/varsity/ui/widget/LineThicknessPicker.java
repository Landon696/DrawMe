package com.tutors.varsity.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tutors.varsity.App;
import com.tutors.varsity.R;
import com.tutors.varsity.ui.event.ColorPicked;
import com.tutors.varsity.util.otto.ApplicationBus;

/**
 * Created by Landon on 5/21/15.
 */
public class LineThicknessPicker extends DialogFragment {

    private static String BUNDLE_LINE_COLOR = "blc";
    private Integer[] mSizes = {
            2,
            4,
            6,
            8,
            10,
            12,
            14,
            18,
            22
    };
    private int mLineColor;

    public static LineThicknessPicker newInstance(int lineColor) {

        LineThicknessPicker thicknessPicker = new LineThicknessPicker();
        Bundle arg = new Bundle();
        arg.putInt(BUNDLE_LINE_COLOR, lineColor);
        thicknessPicker.setArguments(arg);

        return thicknessPicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mLineColor = getArguments().getInt(BUNDLE_LINE_COLOR);
        final Dialog dialog = new Dialog(getActivity(), R.style.Popup);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.dialog_thickness_picker, null);
        dialog.setContentView(dialogView);

        ListView listView = (ListView) dialogView.findViewById(R.id.thickness_listview);
        listView.setAdapter(new LineThicknessAdapter(App.getInstance(), mSizes));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int colorId = mSizes[position];
                ApplicationBus.getInstance().post(new ColorPicked(colorId));
                dismiss();
            }
        });


        return dialog;
    }

    public class LineThicknessAdapter extends BaseAdapter {
        private Context mContext;
        private Integer[] mSizes;
        LayoutInflater mLayoutInflater;

        public LineThicknessAdapter(Context c, Integer[] sizes) {
            mContext = c;
            mSizes = sizes;
            mLayoutInflater = LayoutInflater.from(c);
        }

        public int getCount() {
            return mSizes.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = mLayoutInflater.inflate(R.layout.list_item_line_thickness, null);

            TextView label=(TextView)convertView.findViewById(R.id.size);
            View lineThickness = convertView.findViewById(R.id.line_thickness);
            lineThickness.setBackgroundColor(getResources().getColor(mLineColor));
            ViewGroup.LayoutParams lp = lineThickness.getLayoutParams();
            lp.height = mSizes[position];

            label.setText(mSizes[position] + " pt");

            return convertView;
        }
    }
}
