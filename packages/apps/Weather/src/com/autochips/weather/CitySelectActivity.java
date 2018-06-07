package com.autochips.weather;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CitySelectActivity extends Activity {

    private ListView mListView;
    private Dao dao;
    private ListAdapter mAdapter;
    private ArrayList<CountryRegion> localPath;
    private TextView navTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_select);
        localPath = new ArrayList<CountryRegion>();

        navTextView = (TextView) findViewById(R.id.city_nav);
        navTextView.setText("/");
        mListView = (ListView) findViewById(R.id.list);
        dao = DBFactory.getInstance(this);
        mAdapter = new ListAdapter(this);
        mListView.setAdapter(mAdapter);

        CountryRegion region = new CountryRegion();
        addLocalPath(region);
        notifyListDataSetChanged(region);
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            CountryRegion region = mAdapter.getData().get(position);
            addLocalPath(region);
            notifyListDataSetChanged(region);
        }

    };

    private void notifyListDataSetChanged(CountryRegion region) {
        List<CountryRegion> list;
        switch (region.getRegionLevel()) {
        case CountryRegion.REGION_LEVEL_ROOT:
            mAdapter.setData(dao.getCountryList());
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            break;
        case CountryRegion.REGION_LEVEL_COUNTRY:
            list = dao.getStateList(region.getCountryNameEn(),
                    region.getCountryCode());
            if (list.size() > 0) {
                mAdapter.setData(dao.getStateList(region.getCountryNameEn(),
                        region.getCountryCode()));
            } else {
                mAdapter.setData(dao.getCityList(region.getCountryNameEn(), ""));
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            break;
        case CountryRegion.REGION_LEVEL_STATE:
            mAdapter.setData(dao.getCityList(localPath.get(1)
                    .getCountryNameEn(), region.getStateNameEn()));
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            break;
        case CountryRegion.REGION_LEVEL_CITY:
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putParcelableArrayListExtra("listParce", localPath);
            startActivity(intent);
            finish();
            break;
        }

    }

    private void refreshLocalPath(TextView tv) {
        String path = "";
        int i = 0;
        for (CountryRegion cr : localPath) {
            if (i > 0)
                path += "/" + StringUtil.replaceSymbol(cr.getTitle());
            i++;
        }
        tv.setText(path);
    }

    private void addLocalPath(CountryRegion region) {
        switch (region.getRegionLevel()) {
        case CountryRegion.REGION_LEVEL_ROOT:
            localPath.clear();
            localPath.add(region);
            break;
        case CountryRegion.REGION_LEVEL_COUNTRY:
            localPath.add(1, region);
            break;
        case CountryRegion.REGION_LEVEL_STATE:
            localPath.add(2, region);
            break;
        case CountryRegion.REGION_LEVEL_CITY:
            localPath.add(localPath.size() >= 4 ? 3 : localPath.size(), region);
            break;
        }
        refreshLocalPath(navTextView);
    }

    private void backToUpLevel() {
        notifyListDataSetChanged(localPath.get(localPath.size() - 2));
        localPath.remove(localPath.size() - 1);
        refreshLocalPath(navTextView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && localPath.size() > 1) {
            backToUpLevel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
