package com.yecon.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import net.micode.fileexplorer.Util;

/**
 * Created by chenchu on 15-3-21.
 */
public class FileNeatSearchActivity extends ExpandableListActivity {
    private static final String Tag = "file neatsearchactivity";

    List<String> mGroup;
    List<List<String>> mChildren;

    public static final int FIlE_SEARCH_RESULT_ZERO = 331;

    //TODO:if canceled and lifecycle and progressbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String query = getIntent().getStringExtra(MainActivity.Tag);
        setTitle(query);
        new FileSearcher().execute(new String[]{query});
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String path = mChildren.get(groupPosition).get(childPosition);
        Intent intent = new Intent();
        intent.putExtra(FileOperationService.FILE_ACTION_SEARCH,path);
        setResult(Activity.RESULT_OK,intent);
        finish();
        return true;
    }

    private final class FileSearcher extends AsyncTask<String, Void, Boolean> {


        private boolean searchEach(String mountPoint, String name) {
            File file = new File(mountPoint);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult,file,name);
            } else {
                return false;
            }
            return true;
        }
        Map<String, List<String>> mResult = new HashMap<String, List<String>>();
        private ProgressDialog pd = null;
        @Override
        protected void onPreExecute() {
            if (mResult != null) {
                mResult.clear();
            }
            super.onPreExecute();
        }

        //TODO
        @Override
        protected Boolean doInBackground(String... params) {
            String name = params[0];
            File internal = new File(Util.getSdDirectory());
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, internal, name);
            }
            File sd1 = new File(FileStorageStateListener.pathSD1);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, sd1, name);
            }
            File sd2 = new File(FileStorageStateListener.pathSD2);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, sd2, name);
            }
            
            File usb1 = new File(FileStorageStateListener.pathUSB1);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, usb1, name);
            }
            File usb2 = new File(FileStorageStateListener.pathUSB2);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, usb2, name);
            }
            File usb3 = new File(FileStorageStateListener.pathUSB3);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, usb3, name);
            }
            File usb4 = new File(FileStorageStateListener.pathUSB4);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, usb4, name);
            }
            File usb5 = new File(FileStorageStateListener.pathUSB5);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, usb5, name);
            }
           
            
            File usb = new File(FileStorageStateListener.USB_ROOT_PATH);
            if (!isCancelled()) {
                FileOperator.searchFile(mResult, usb, name);
            }
            
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mResult.size() == 0) {
                Log.d(Tag, "search size is 0");
                setResult(FileNeatSearchActivity.FIlE_SEARCH_RESULT_ZERO);
                finish();
            } else {
                Set<String> keySet = mResult.keySet();
                mGroup = new ArrayList<String>(keySet.size());
                mChildren = new ArrayList<List<String>>(keySet.size());
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String name = iterator.next();
                    mGroup.add(name);
                    List<String> list = mResult.get(name);
                    mChildren.add(list);
                    int size = list.size();
                    for (int i = 0; i < size; ++i) {
                        Log.d(Tag, "{" + name + " : " + list.get(i) + "}");
                    }
                }
                setListAdapter(new MyExpandableListAdapter(FileNeatSearchActivity.this,mGroup,mChildren));
            }
        }
    }

    private class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        private List<String> mGroup;
        private List<List<String>> mChildren;

        MyExpandableListAdapter(Context context, List<String> group, List<List<String>> children) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            mGroup = group;
            mChildren = children;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChildren.get(groupPosition).size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mChildren.get(groupPosition).get(childPosition);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.text_view4,parent,false);
            }
            ((TextView) convertView).setText(mGroup.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(android.R.layout.simple_expandable_list_item_1,parent,false);
            }
            ((TextView) convertView).setText(mChildren.get(groupPosition).get(childPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    };
}