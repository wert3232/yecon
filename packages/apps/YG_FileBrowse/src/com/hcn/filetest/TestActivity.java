package com.hcn.filetest;

import com.yecon.filemanager.FileListFragment;
import com.yecon.filemanager.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

/**
 * Created by chenchu on 15-2-27.
 */
public class TestActivity extends Activity implements View.OnLongClickListener{
    FileTestUtil mUtil = null;
    private Button mTestButton;
    private ListView mChecksumList;
    private TextView mResultInfos;

    private String src = null;

    private FileTestUtil getTestUtil() {
        if (mUtil == null) {
            mUtil = new FileTestUtil();
        }
        return mUtil;
    }
    
    
    //just test popupmenu
    @Override
    public boolean onLongClick(View v) {
    	// TODO Auto-generated method stub
    	PopupMenu menu = new PopupMenu(TestActivity.this, v);
    	menu.getMenuInflater().inflate(R.menu.menu_sort,menu.getMenu());
    	menu.show();
    	return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTestButton = (Button)findViewById(R.id.button_test);
        mTestButton.setOnLongClickListener(TestActivity.this);
        mChecksumList = (ListView)findViewById(R.id.list_view_checksum);
        mResultInfos = (TextView) findViewById(R.id.textview_info);
        Intent intent = getIntent();
        src = intent.getCharSequenceExtra(FileListFragment.Tag).toString();
        getActionBar().setTitle(src);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int MSG_COPY_DONE = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int i = msg.what;
            if (i == MSG_COPY_DONE) {
                updateUI();
            }
        };
    };

    private void updateUI(){
        mTestButton.setText(getString(R.string.test));
        mTestButton.setClickable(true);
        mResultInfos.setText(mUtil.eLog);
        mChecksumList.setAdapter(new ArrayAdapter<String>(this, R.layout.checksum_item,mUtil.getChecksumList()));
    }

    public void onStart(View v) {
        mTestButton.setText("copying");
        mTestButton.setClickable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getTestUtil();
                mUtil.reset();
                String dest = mUtil.getParentPath(src);
                mUtil.test(100,src,dest,true);
                mHandler.sendEmptyMessage(MSG_COPY_DONE);
            }
        }).start();
    }
}
