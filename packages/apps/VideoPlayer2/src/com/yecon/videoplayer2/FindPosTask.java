package com.yecon.videoplayer2;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class FindPosTask<T1, T2> extends AsyncTask<FindPosTask.Input<T1, T2>, Integer, Integer> {
	private CompareCallback<T1, T2> compareCallback;
	FindPosTask(CompareCallback<T1, T2> compareCallback){
		this.compareCallback = compareCallback;
	}
	public static class Input<T1, T2>{
		List<T1>list = new ArrayList<T1>();
		T2 bean;
		public List<T1> getList() {
			return list;
		}
		public void setList(List<T1> list) {
			this.list.addAll(list);
		}
		public T2 getBean() {
			return bean;
		}
		public void setBean(T2 bean) {
			this.bean = bean;
		}
		
	}
	public interface CompareCallback<T1, T2>{
		void onStart();
		boolean equal(T1 t1, T2 bean);
		void onFinished(int pos);
	}
	@Override
	protected Integer doInBackground(Input<T1, T2>... arg0) {
		// TODO Auto-generated method stub
		Input<T1, T2> input = (Input<T1, T2>)arg0[0];
		if(input!=null){
			if(input.list!=null){
				for(int i=0;i<input.list.size();i++){
					if(compareCallback.equal(input.list.get(i), input.bean)){
						return i;
					}
				}
			}
		}
		return -1;
	}
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		compareCallback.onFinished(result);
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		compareCallback.onStart();
	}
	
	
	
}
