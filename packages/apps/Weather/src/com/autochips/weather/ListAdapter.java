package com.autochips.weather;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ListAdapter extends
        ListBaseAdapter<CountryRegion, ListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private Context mContext;

    public ListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mContext = context;
    }

    @Override
    public void bindView(ListAdapter.ViewHolder viewHolder, View convertView) {
        viewHolder.title = (TextView) convertView
                .findViewById(android.R.id.text1);
    }

    public final class ViewHolder {
        public TextView title;
    }

    @Override
    public ViewHolder getViewHolder() {
        return new ViewHolder();
    }

    @Override
    public void setViewContent(ViewHolder viewHolder, CountryRegion record,
            View convertView, int position) {
        String title = StringUtil.replaceSymbol(record.getTitle());
        viewHolder.title.setText(title);
    }
}
