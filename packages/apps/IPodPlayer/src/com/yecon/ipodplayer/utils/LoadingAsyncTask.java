package com.yecon.ipodplayer.utils;

import android.content.Context;
import android.os.AsyncTask;

public class LoadingAsyncTask extends AsyncTask<Integer, Integer, String>{
	public LoadingAsyncTask(){

	}

	public LoadingAsyncTask(Context mContext){

	}
	@Override
	protected String doInBackground(Integer... params) {
		
		

		return params[0].intValue()+"";
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
	}






}
