package com.autochips.weather;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DaoImpl implements Dao {
    private DBOpenHelper openHelper;

    public DaoImpl(Context context) {
        String dbName = DBOpenHelper.DBNAME;
        Utils.copyDatabaseFile(context, false, dbName);
        openHelper = new DBOpenHelper(context, dbName);
    }

    @Override
    public List<CountryRegion> getCountryList() {
        List<CountryRegion> list = new ArrayList<CountryRegion>();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select country_name,country_name_en,country_code");
        sql.append(" from country_region");
        sql.append(" group by country_name,country_name_en,country_code");
        if (Constants.IS_EN) {
            sql.append(" order by country_name_en asc");
        } else {
            sql.append(" order by pinyin asc");
        }

        try {
            cursor = db.rawQuery(sql.toString(), new String[] {});
            while (cursor.moveToNext()) {
                CountryRegion bean = new CountryRegion();
                PojoUtil.loadBean(bean, cursor);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close();
        }
        return list;
    }

    @Override
    public List<CountryRegion> getStateList(String countryNameEn,
            String countryCode) {
        List<CountryRegion> list = new ArrayList<CountryRegion>();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select state_name,state_name_en,state_code");
        sql.append(" from country_region");
        sql.append(" where state_name <> ? and country_name_en = ? and country_code = ?");
        sql.append(" group by state_name,state_name_en,state_code");
        if (Constants.IS_EN) {
            sql.append(" order by state_name_en asc");
        } else {
            sql.append(" order by state_pinyin asc");
        }

        try {
            cursor = db.rawQuery(sql.toString(), new String[] { "",
                    countryNameEn, countryCode });
            while (cursor.moveToNext()) {
                CountryRegion bean = new CountryRegion();
                PojoUtil.loadBean(bean, cursor);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close();
        }
        return list;
    }

    @Override
    public List<CountryRegion> getCityList(String countryNameEn,
            String stateNameEn) {
        List<CountryRegion> list = new ArrayList<CountryRegion>();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select _id,woeid,city_name,city_name_en,city_code");
        sql.append(" from country_region");
        sql.append(" where country_name_en = ? and state_name_en = ?");
        if (Constants.IS_EN) {
            sql.append(" order by city_name_en asc");
        } else {
            sql.append(" order by city_pinyin asc");
        }

        try {
            cursor = db.rawQuery(sql.toString(), new String[] { countryNameEn,
                    stateNameEn });
            while (cursor.moveToNext()) {
                CountryRegion bean = new CountryRegion();
                PojoUtil.loadBean(bean, cursor);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close();
        }
        return list;
    }
    public CountryRegion getCurrentCityByCityName(String cityNameEn,
            String curwoied) {
    	CountryRegion bean = null;
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select _id,woeid,city_name,city_name_en,city_code");
        sql.append(" from country_region");
        sql.append(" where city_name_en = ?");
        try {
            cursor = db.rawQuery(sql.toString(), new String[] { cityNameEn });
            while (cursor.moveToNext()) {
            	bean = new CountryRegion();
                PojoUtil.loadBean(bean, cursor);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close();
        }
        return bean;
    }
    @Override
    public CountryRegion getCurrentCity() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        CountryRegion bean = null;
        Cursor cursor = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select * from current_city");
        try {
            cursor = db.rawQuery(sql.toString(), new String[] {});
            while (cursor.moveToNext()) {
                bean = new CountryRegion();
                PojoUtil.loadBean(bean, cursor);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close();
        }
        return bean;
    }

    @Override
    public void saveOrUpdateCurrentCity(CountryRegion cr) {
        CountryRegion bean = getCurrentCity();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            if (bean == null) {
                ContentValues values = getContentValues(cr);
                db.insert("current_city", null, values);
            } else {
                ContentValues values = getContentValues(cr);
                db.update("current_city", values, "_id=?",
                        new String[] { String.valueOf(bean.getId()) });
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            // db.close();
        }
    }

    @Override
    public WeatherCache getWeatherCache() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        WeatherCache bean = null;
        Cursor cursor = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select * from weather_cache");
        try {
            cursor = db.rawQuery(sql.toString(), new String[] {});
            while (cursor.moveToNext()) {
                bean = new WeatherCache();
                PojoUtil.loadBean(bean, cursor);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close();
        }
        return bean;
    }

    @Override
    public void saveOrUpdateWeatherCache(WeatherCache cache) {
        WeatherCache bean = getWeatherCache();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            if (bean == null) {
                ContentValues values = getContentValues(cache);
                db.insert("weather_cache", null, values);
            } else {
                ContentValues values = getContentValues(cache);
                db.update("weather_cache", values, "_id=?",
                        new String[] { String.valueOf(bean.getId()) });
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            // db.close();
        }
    }

    private ContentValues getContentValues(CountryRegion cr) {
        ContentValues values = new ContentValues();
        values.put("country_name", cr.getCountryName());
        values.put("country_name_en", cr.getCountryNameEn());
        values.put("country_code", cr.getCountryCode());
        values.put("state_name", cr.getStateName());
        values.put("state_name_en", cr.getStateNameEn());
        values.put("state_code", cr.getStateCode());
        values.put("city_name", cr.getCityName());
        values.put("city_name_en", cr.getCityNameEn());
        values.put("city_code", cr.getCityCode());
        values.put("woeid", cr.getWoeid());
        return values;
    }

    private ContentValues getContentValues(WeatherCache cache) {
        ContentValues values = new ContentValues();
        values.put("firstday_condition", cache.getFirstdayCondition());
        values.put("firstday_high", cache.getFirstdayHigh());
        values.put("firstday_low", cache.getFirstdayLow());
        values.put("firstday_code", cache.getFirstdayCode());
        values.put("secday_condition", cache.getSecdayCondition());
        values.put("secday_high", cache.getSecdayHigh());
        values.put("secday_low", cache.getSecdayLow());
        values.put("secday_code", cache.getSecdayCode());
        values.put("update_time", cache.getUpdateTime());
        values.put("source", cache.getSource());
        values.put("current_temp", cache.getCurrentTemp());
        values.put("current_condition", cache.getCurrentCondition());
        values.put("current_code", cache.getCurrentCode());
        values.put("woeid", cache.getWoeid());
        return values;
    }

    final static class PojoUtil {

        public static void loadBean(Object bean, Cursor cursor)
                throws Exception {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                int index = cursor.getColumnIndex(field.getName());
                if (index > -1) {
                    if ("java.lang.String".equals(type.getName())) {
                        field.set(bean, cursor.getString(index));
                    } else if ("int".equals(type.getName())) {
                        field.setInt(bean, cursor.getInt(index));
                    }
                }
            }
        }
    }
}
