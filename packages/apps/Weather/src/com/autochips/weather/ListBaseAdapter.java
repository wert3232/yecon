package com.autochips.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListBaseAdapter<T, V> extends BaseAdapter {
    protected Context context;

    private LayoutInflater mInflater;

    private int layoutResource;

    protected List<T> datalist;

    public void setData(List<T> datalist) {
        this.datalist = datalist;
    }

    public List<T> getData() {
        return datalist;
    }

    public ListBaseAdapter() {

    }

    public ListBaseAdapter(Context context, int layoutResource) {
        super();

        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;

        datalist = new ArrayList<T>();
    }

    @Override
    public int getCount() {

        return datalist.size();
    }

    @Override
    public T getItem(int paramInt) {

        return datalist.get(paramInt);
    }

    @Override
    public long getItemId(int paramInt) {

        return paramInt;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        V holder = null;

        T record = getItem(position);
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(layoutResource, null);
            holder = getViewHolder();
            bindView(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (V) convertView.getTag();
        }

        setViewContent(holder, record, convertView, position);
        view = convertView;
        return view;
    }

    public abstract V getViewHolder();

    public abstract void bindView(V viewHolder, View convertView);

    public abstract void setViewContent(V viewHolder, T record,
            View convertView, int position);

}
