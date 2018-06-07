package com.autochips.weather;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class HttpInternetTask extends AsyncTask<String, Void, String> {
    public final static int CODE_CONNECT_FAILE = 0x0010;

    public final static int CODE_CONNECT_TIMEOUT = 0x0011;

    public final static int CODE_INVALID_DATA = 0x0012;

    public final static int CODE_NO_WARNING = 0x0013;

    private HttpTaskListener mListener;

    private int mReturnCode;

    public HttpInternetTask() {
        super();
    }

    public void request(String url, HttpTaskListener listener) {
        if (listener == null) {
            throw new RuntimeException("httpTaskListener not is null");
        }
        mListener = listener;
        execute(url);
    }

    public void cancel() {
        cancel(true);
    }

    @Override
    protected String doInBackground(String... args) {
        if (args.length == 0)
            throw new RuntimeException("argument data exception");
        mReturnCode = CODE_NO_WARNING;
        String decryptData = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        int timeout = Constants.CONNECTE_TIMEOUT;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        HttpConnectionParams.setSoTimeout(params, timeout);
        String url = args[0];
        HttpGet httpRequest = new HttpGet(url);
        HttpClient httpclient = new DefaultHttpClient(params);
        try {
            HttpResponse response = httpclient.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                decryptData = EntityUtils.toString(response.getEntity());
            } else {
                mReturnCode = CODE_INVALID_DATA;
            }
        } catch (ConnectTimeoutException e) {
            mReturnCode = CODE_CONNECT_TIMEOUT;
            e.printStackTrace();
        } catch (Exception e) {
            mReturnCode = CODE_CONNECT_FAILE;
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    baos = null;
                }
            }
            httpclient.getConnectionManager().shutdown();
        }
        return decryptData;
    }

    @Override
    protected void onPostExecute(String result) {
        if (isCancelled())
            return;
        if (result == null) {
            mListener.onException(mReturnCode);
        } else {
            mListener.onSuccess(result);
        }
        mListener = null;
    }

}
