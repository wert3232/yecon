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
import android.widget.Toast;

public class BaiduWeatherSource implements WeatherSource {

    private Context mContext;

    private Dao mDao;

    private CountryRegion curCity;

    private Weather todayWeather;

    private Weather tomorrowWeather;

    private Weather currentWeather;

    private static String[] conditionItems = null;

    private static String[] conditionImageItems = null;

    
    public BaiduWeatherSource(Context context) {
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
            curCity = mDao.getCurrentCity();
            if (curCity != null) {
                queryWeather(curCity.getCityName(),
                        getHttpTaskListener(curCity.getCityName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
   /* String httpUrl = "https://api.heweather.com/x3/weather?cityid=����ID&key=XXXXXXXXX";
    String jsonResult = request(httpUrl);
   
    public static String request(String httpUrl) {
    BufferedReader reader = null;String result = null;StringBuffer sbf = new StringBuffer();
    try {
    URL url = new URL(httpUrl);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.connect();
    InputStream is = connection.getInputStream();
    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    String strRead = null;
    while ((strRead = reader.readLine()) != null) {
    sbf.append(strRead); sbf.append("\r\n");
    }
    reader.close();
    result = sbf.toString();
    } catch (Exception e) { e.printStackTrace(); }
    return result;
    }*/
    @Override
    public void queryWeather(String woeid, HttpTaskListener listener) {
    	//FIXME:yfzhang
        /*HttpInternetTask task = new HttpInternetTask();
        String baseUrl = "http://apis.baidu.com/heweather/weather/free?city="+woeid;  						
        
        task.request(baseUrl, listener);*/
    	//yfzhang
    }

    @Override
    public void initData() {
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
        currentWeather.setCurrentTempF(cNode.getAttribute("temp"));
        currentWeather.code = Integer.valueOf(cNode.getAttribute("code"));

        NodeList forecast = ((Element) itemNode.item(0))
                .getElementsByTagName("yweather:forecast");

        for (int i = 0; i < forecast.getLength(); i++) {
            Element node = ((Element) forecast.item(i));
            if (i == 0 || i == 1) {
                Weather w = new Weather();
                w.setHighTempF(node.getAttribute("high"));
                w.setLowTempF (node.getAttribute("low"));
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
                Utils.sendUpdateWeatherBroadcast(mContext, mDao);
            }
        };
        return httpTaskListener;
    }

	@Override
	public void parseCityData(String response) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
