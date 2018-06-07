package com.yecon.filemanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;


/**
 * Created by chenchu on 14-12-17.
 */
/*
public abstract class AbsFileItemAdapter<T extends ListViewItemHolder> extends ArrayAdapter<FileInfo> {

        private FileItemGestureDetector.OnListItemTouchListener mOnListItemTouchListener;

        private int mLayout;

        public AbsFileItemAdapter(Context context, int layout,FileItemGestureDetector.OnListItemTouchListener listener,List<FileInfo> data) {
            super(context,layout,data);
            mOnListItemTouchListener = listener;
            mLayout = layout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            T holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                holder = (T)inflater.inflate(mLayout,null);
                holder.setOnListItemTouchListener(mOnListItemTouchListener);
            } else {
                holder = (T) convertView;
            }

            FileInfo info = getItem(position);
            if (info.isSelected){
                holder.setBackgroundColor(Color.GRAY);
            } else {
                holder.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.setTag(info);
            setView(holder,info);
            return holder;
        }

    public abstract void setView(T holder,FileInfo info);

}
*/

public abstract class AbsFileItemAdapter<T extends ListViewItemHolder> extends BaseAdapter implements Filterable {
    private static final String Tag = "absfileitemadpater";

    private FileItemGestureDetector.OnListItemTouchListener mOnListItemTouchListener;

    private int mLayout;

    private Context mContext;

    private LayoutInflater mInflater;

    private List<FileInfo> mObjects;

    private List<FileInfo> mOriginalValues;

    private FileFilter mFilter;

    public List<FileInfo> getInfos() {
        return mObjects;
    }

    public void setInfos(List<FileInfo> infos) {
        if (mOriginalValues == null) {
            mOriginalValues = mObjects;
        }
       mObjects = infos;
       notifyDataSetChanged();
    }

    private class FileFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                    mOriginalValues = mObjects;
                }

            if (constraint == null || constraint.length() == 0) {
                results.values = mOriginalValues;
                results.count = mOriginalValues.size();
            } else {
                String constraintString = constraint.toString().toLowerCase();

                List<FileInfo> values = new ArrayList<FileInfo>();

                final int count = mOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    final FileInfo info = mOriginalValues.get(i);
                    final String value = info.fileName;
                    //Log.d(Tag, "value is "+ value);
                    final String valueText = value.toLowerCase();

                    if (valueText.endsWith(constraintString)) {
                        values.add(info);
                    }
                }

                results.values = values;
                results.count = values.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mObjects = (List<FileInfo>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    public AbsFileItemAdapter(Context context, int layout,FileItemGestureDetector.OnListItemTouchListener listener,List<FileInfo> data) {
        super();
        mOnListItemTouchListener = listener;
        mLayout = layout;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mObjects = data;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
            return position;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new FileFilter();
        }
        return mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T holder;
        if (convertView == null) {
            holder = (T)mInflater.inflate(mLayout,null);
            holder.setOnListItemTouchListener(mOnListItemTouchListener);
        } else {
            holder = (T) convertView;
        }

        FileInfo info = (FileInfo) getItem(position);
        if (info.isSelected){
            //holder.setBackgroundColor(Color.BLUE);
            holder.setBackgroundResource(R.drawable.list_p);
        } else {
            //holder.setBackgroundColor(Color.TRANSPARENT);
        	holder.setBackgroundResource(R.drawable.list_n);
        }
        holder.setTag(info);
        setView(holder,info);
        return holder;
    }

    public abstract void setView(T holder,FileInfo info);

}
