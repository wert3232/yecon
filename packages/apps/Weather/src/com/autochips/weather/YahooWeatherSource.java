package com.autochips.weather;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

public class YahooWeatherSource implements WeatherSource {

    private Context mContext;

    private Dao mDao;

    private CountryRegion curCity;

    private Weather todayWeather;

    private Weather tomorrowWeather;

    private Weather currentWeather;
    private String curLocalWoeid;
    private String curLocalCityname;
    private static String[] conditionItems = null;

    private static String[] conditionImageItems = null;

    //private final String baseUrl = "http://xml.weather.yahoo.com/forecastrss?u=c&w=";
   //"http://weather.yahooapis.com/forecastrss?u=c&w=";
  //  
    public YahooWeatherSource(Context context) {
        mContext = context;
        mDao = DBFactory.getInstance(context);
        conditionImageItems = context.getResources().getStringArray(
                R.array.yahoo_condition_image);
    }

    @Override
    public Weather getCurrentWeather() {
        return currentWeather;
    }

    @Override
    public Weather getTodayWeather() {
        return todayWeather;
    }

    @Override
    public Weather getTomorrowWeather() {
        return tomorrowWeather;
    }

    @Override
    public Weather getNewestWeather() {
        try {        
	            if(WeatherProvider.mbCheckCurCity)
	            {
	            	//Location lo = locationManager.getInstance().getNewLocation(mContext);	            	
	            	//queryCityCode(lo,getCityHttpTaskListener());
	            	
	            	Location lo = locationManager.getInstance().getNewLocation(mContext);
	            	if(lo!=null)
	            		queryCityCode(lo,getCityHttpTaskListener());
	            	else
	            	{
	            		curCity = mDao.getCurrentCity();
	            		if (curCity != null) {
	            			queryWeather(curCity.getWoeid(),
	                            getHttpTaskListener(curCity.getWoeid()));
	            		}
	            	}
	            }
	            else
	            {
	            	curCity = mDao.getCurrentCity();
	        		if (curCity != null) {
	        			queryWeather(curCity.getWoeid(),
	                        getHttpTaskListener(curCity.getWoeid()));
	        		}
	            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void queryWeather(String woeid, HttpTaskListener listener) {
        /*HttpInternetTask task = new HttpInternetTask();
        String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid="+woeid+"%20and%20u=%22c%22&format=xml&env=store://datatables.org/alltableswithkeys";        				
        task.request(baseUrl, listener);*/
    }
    public void queryCityCode(Location loacal, HttpTaskListener listener) {
       /* HttpInternetTask task = new HttpInternetTask();
        String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20admin2%20from%20geo.places(1)%20where%20text%3D%22("+ 
        		loacal.getLatitude()+"%2C"+loacal.getLongitude()+")%22";   
        //String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20admin2%20from%20geo.places(1)%20where%20text%3D%22(22.57%2C113.9)%22";  
        task.request(baseUrl, listener);*/
    }
    @Override
    public void initData() {
    }

    @Override
	public void parseCityData(String xml) throws Exception {

        StringReader stringReader = new StringReader(xml);
        InputSource inputSource = new InputSource(stringReader);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document dom = documentBuilder.parse(inputSource);
        Element root = dom.getDocumentElement();
        NodeList channels = root.getElementsByTagName("admin2");

        Element channelNode = (Element) channels.item(0);
       // if ("woeid".equals(channelNode.getNodeName())) {  
            
        curLocalCityname =	channelNode.getFirstChild().getNodeValue();
       // }       
        curLocalWoeid  = channelNode.getAttribute("woeid");
	}

	@Override
    public void parseWeatherData(String xml) throws Exception {
        todayWeather = null;
        tomorrowWeather = null;
        currentWeather = null;
        StringReader stringReader = new StringReader(xml);
        InputSource inputSource = new InputSource(stringReader);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document dom = documentBuilder.parse(inputSource);
        Element root = dom.getDocumentElement();
        NodeList channels = root.getElementsByTagName("channel");

        Element channelNode = (Element) channels.item(0);
        NodeList itemNode = channelNode.getElementsByTagName("item");

        NodeList condition = ((Element) itemNode.item(0)).getElementsByTagName("yweather:condition");
        Element cNode = ((Element) condition.item(0));

        currentWeather = new Weather();
        currentWeather.condition = cNode.getAttribute("text");
        currentWeather.setCurrentTempC(cNode.getAttribute("temp"));
        currentWeather.code = Integer.valueOf(cNode.getAttribute("code"));

        NodeList forecast = ((Element) itemNode.item(0))
                .getElementsByTagName("yweather:forecast");

        for (int i = 0; i < forecast.getLength(); i++) {
            Element node = ((Element) forecast.item(i));
            if (i == 0 || i == 1) {
                Weather w = new Weather();
                w.setHighTempC(node.getAttribute("high"));
                w.setLowTempC (node.getAttribute("low"));
                w.condition = node.getAttribute("text");
                w.code = Integer.valueOf(node.getAttribute("code"));

                if (i == 0) {
                    todayWeather = w;
                } else {
                    tomorrowWeather = w;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public String getConditionByCode(String c) {
        try {
            int code = Integer.valueOf(c);
            String condition = conditionItems[code];
            return condition;
        } catch (Exception e) {
        }
        return mContext.getText(R.string.unknown).toString();
    }

    @Override
    public Drawable getDrawableByCode(String c) {
        Resources resource = mContext.getResources();
        int code = 0;
        try {
            code = Integer.valueOf(c);
            int resid = resource.getIdentifier(conditionImageItems[code],
                    "drawable", mContext.getPackageName());
            Drawable drawable = resource.getDrawable(resid);
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource.getDrawable(R.drawable.cloudy);
    }

    @Override
    public void refreshConditionList() {
        conditionItems = mContext.getResources().getStringArray(
                R.array.yahoo_condition);
    }
    
    private Toast mToast;

    @SuppressLint("ShowToast")
    public Toast getToast() {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
        }
        return mToast;
    }

    private HttpTaskListener getHttpTaskListener(final String woeid) {
        HttpTaskListener httpTaskListener = new HttpTaskListener() {

            @Override
            public void onSuccess(String response) {
                try {
                    parseWeatherData(response);
                    Weather cur = getCurrentWeather();
                    Weather today = getTodayWeather();
                    Weather tomorrow = getTomorrowWeather();
                    if (tomorrow != null) {
                        WeatherCache cache = new WeatherCache(cur, today,
                                tomorrow, String.valueOf(System
                                        .currentTimeMillis()));
                        cache.setWoeid(woeid);
                        mDao.saveOrUpdateWeatherCache(cache);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.sendUpdateWeatherBroadcast(mContext, mDao);
                }
            }

            @Override
            public void onException(int resultCode) {
                switch (resultCode) {
                case HttpInternetTask.CODE_CONNECT_FAILE:
                    //getToast().setText(mContext.getString(R.string.error));
                    //getToast().show();
                    break;
                case HttpInternetTask.CODE_CONNECT_TIMEOUT:
                    getToast().setText(mContext.getString(R.string.timeout));
                    getToast().show();
                    break;
                case HttpInternetTask.CODE_INVALID_DATA:
                    getToast().setText(mContext.getString(R.string.invalid_data));
                    getToast().show();
                    break;
                default:
                    break;
                }
                Utils.sendUpdateCityBroadcast(mContext, mDao);
                Utils.sendUpdateWeatherBroadcast(mContext, mDao);
            }
        };
        return httpTaskListener;
    }
    private HttpTaskListener getCityHttpTaskListener() {
        HttpTaskListener httpTaskListener = new HttpTaskListener() {

            @Override
            public void onSuccess(String response) {
                try {
                    parseCityData(response);                                  
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                	if(curLocalCityname!= null)
                	{
                		CountryRegion cr = mDao.getCurrentCityByCityName(curLocalCityname, curLocalWoeid);
                		cr.setWoeid(curLocalWoeid);
                		mDao.saveOrUpdateCurrentCity(cr);
                	}
                	if (curLocalWoeid != null) {
                        queryWeather(curLocalWoeid,
                                getHttpTaskListener(curLocalWoeid));
                    }
                }
            }

            @Override
            public void onException(int resultCode) {
                switch (resultCode) {
                case HttpInternetTask.CODE_CONNECT_FAILE:
                    //getToast().setText(mContext.getString(R.string.error));
                    //getToast().show();
                    break;
                case HttpInternetTask.CODE_CONNECT_TIMEOUT:
                    getToast().setText(mContext.getString(R.string.timeout));
                    getToast().show();
                    break;
                case HttpInternetTask.CODE_INVALID_DATA:
                    getToast().setText(mContext.getString(R.string.invalid_data));
                    getToast().show();
                    break;
                default:
                    break;
                }
               // Utils.sendUpdateWeatherBroadcast(mContext, mDao);
            }
        };
        return httpTaskListener;
    }
}
