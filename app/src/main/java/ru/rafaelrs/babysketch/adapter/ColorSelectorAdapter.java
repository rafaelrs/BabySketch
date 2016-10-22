package ru.rafaelrs.babysketch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import ru.rafaelrs.babysketch.R;

/**
 * Created with Android Studio
 * User: rafaelrs
 * Date: 21.10.16
 * To change this template use File | Settings | File Templates.
 */

public class ColorSelectorAdapter extends ArrayAdapter<Integer> {

    private Integer[] mColorsList;

    private Context mContext;

    public ColorSelectorAdapter(Context context, int resource, Integer[] colorsList) {
        super(context, resource);

        this.mContext = context;
        this.mColorsList = colorsList;
    }

    @Override
    public int getCount() {
        return mColorsList == null ? 0 : mColorsList.length;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.adapter_color_selector, null);
        }
        convertView.findViewById(R.id.selector_field).setBackgroundColor(mColorsList[position]);
        return (convertView);
    }

    public Integer getItem(int position) {
        return mColorsList[position];
    }
}
