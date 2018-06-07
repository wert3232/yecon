package com.yecon.ipodplayer.fragment;

 

import java.util.ArrayList;

import com.yecon.ipodplayer.R;
 

 
 


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class FragmentTabAcitivity extends FragmentActivity{
	//瀹氫箟涓�釜 aidl鐨勯�淇￠�閬�
 
	static Context mContext;
	//杩欎釜鏄笁涓猼abhost鐨刬ndex
   final int iLocalityFragment=0;
   final int iFriednFragment=1;
   final int iForumFragment=2;
	
	// 瀹氫箟FragmentTabHost瀵硅薄
	private FragmentTabHost mTabHost;
	private RadioGroup mTabRg;
	private ViewPager mViewPage;
	TabsAdapter mTabsAdapter;
	//瀹氫箟涓変釜绐楀彛鐨剈i
	private final Class[] fragments = { LocalityFragment.class, FriendFragment.class,ForumFragment.class };
//瀹氫箟澶栭儴鐨刟idl璋冪敤
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		initView();
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	private void initView() {

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager());
		mViewPage = (ViewPager) findViewById(R.id.pager);
		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		//鎬诲叡鍔犲叆杩檚ane鎺т欢
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPage, mTabRg);
		// 寰楀埌fragment鐨勪釜锟�
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			// 涓烘瘡姣忎釜Tab鎸夐挳璁剧疆鍥炬爣銆佹枃瀛楀拰鍐呭
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			// 灏員ab鎸夐挳娣诲姞杩汿ab閫夐」鍗′腑
			mTabHost.addTab(tabSpec, fragments[i], null);

			mTabsAdapter.addTab(mTabHost.newTabSpec(i + "")
					.setIndicator(i + ""), fragments[i], null);
		}

		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_locality:
					mTabHost.setCurrentTab(iLocalityFragment);
					break;
				case R.id.tab_friend:
					mTabHost.setCurrentTab(iFriednFragment);

					break;
				case R.id.tab_forum:

					mTabHost.setCurrentTab(iForumFragment);
					break;
			

				default:
					break;
				}
			}
		});
		// mTabHost.setCurrentTab(0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	protected void onDestroy(){
		
		super.onDestroy();
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements
			TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final RadioGroup mTabRg;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost,
				ViewPager pager, RadioGroup tabRg) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabRg = tabRg;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
			((RadioButton) mTabRg.getChildAt(position)).setChecked(true);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}


}
