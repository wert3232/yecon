package com.yecon.ipodplayer.fragment;
 

import com.yecon.ipodplayer.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ForumFragment extends Fragment {
//	OnBackListener mListener;

//	public interface OnBackListener {
//		public void backEvent();
//	}
	
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnBackListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
//        }
//    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View parentView = inflater.inflate(R.layout.fragment_friend, container, false);
	
//		//鐠囧墽鈻肩�澶嬪笓
//		ImageView cource_time= (ImageView)parentView.findViewById(R.id.cource_time);
//		cource_time.setOnClickListener(mListener);
//		//闁惧搫顕遍崨锟�//		 TextView  instructor_num0=(TextView)parentView.findViewById(R.id.instructor_num0);
//		 instructor_num0.setOnClickListener(mListener);
		
		return parentView;
		// return super.onCreateView(, container, savedInstanceState);
	}
	
	 private View.OnClickListener mListener = new View.OnClickListener() {  
	        public void onClick(View v) { 
	        	 int viewId = v.getId(); 
	            switch(viewId){
	          
	            	default:
	            		break;
	            
	            }	 
	                	
	        }
			
		};
	
	
	
}
