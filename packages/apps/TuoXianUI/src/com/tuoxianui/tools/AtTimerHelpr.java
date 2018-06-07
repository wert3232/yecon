package com.tuoxianui.tools;



import android.util.Log;

public class AtTimerHelpr {
	private AtTimerHelprDoItCallBack callBack;
	private CountTimeThread mCountTimeThread;
	
	public AtTimerHelpr(){
	}
	public void setCallBack(AtTimerHelprDoItCallBack callBack){
		this.callBack = callBack;
	}
	public void reset(){
		if(mCountTimeThread != null){
			mCountTimeThread.reset();
		}
	}
	public  void start(int second){
		mCountTimeThread = new CountTimeThread(second);
	    mCountTimeThread.start();
    }
	public void stop(){
		if(mCountTimeThread != null){
			mCountTimeThread.setStop();
			mCountTimeThread.interrupt();
		}
	}
	
	private class CountTimeThread extends Thread{
	    private final long maxVisibleTime ;
	    private long startVisibleTime;
	    private boolean isStop = false;
	    /**
	     * 设置控件显示时间second单位是秒
	     * @param second
	     */
	    public CountTimeThread(int second){
	        maxVisibleTime = second * 1000;//换算为毫秒
	       
	        setDaemon(true);//设置为后台进程
	    }
	    @Override
	    public synchronized void start() {
	    	super.start();
	    	isStop = false;
	    }
	    public synchronized void setStop(){
	    	isStop = true;
	    }
	    /**
	     * 如果用户有操作，则重新开始计时隐藏时间
	     */
	    private synchronized void reset() {
             startVisibleTime = System.currentTimeMillis();
//             Log.e("resetreset", "reset: " + startVisibleTime);
        }
	   
	    @Override
	    public void run() { 
	        startVisibleTime = System.currentTimeMillis();//初始化开始时间
	       
	             while (true) {
	             //如果时间达到最大时间，则发送隐藏消息
	            	 if(isStop){
	            		 this.interrupt();
	            		 return;
                	}
	                if (startVisibleTime + maxVisibleTime < System.currentTimeMillis()){
	                	
	                	if(callBack != null){
	                		callBack.doIt();
	                	}
//	                	backHandler.sendEmptyMessage(0);
	                	Log.e("AtTimerHelpr","doIt");
	                   this.interrupt();
	                   return;
	                }
//	               Log.e("AtTimerHelpr", "startVisibleTime" + startVisibleTime + "   maxVisibleTime" +  System.currentTimeMillis());
	                try {
	                   Thread.sleep(100);
	                } catch (Exception e) {
	                    
	                }
	            }
	            
	      }
	}
	public interface AtTimerHelprDoItCallBack{
		public void doIt();
	}
}
