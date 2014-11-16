package com.example.nav.listget;

import android.os.AsyncTask;
import android.util.Log;

import com.example.nav.listget.Interfaces.Display;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Created by Nav on 11/11/2014.
 */
public class Mongo {

    private final Display activity;

    private static final String BASE_URL = "https://api.mongolab.com/api/1//databases/sandbox/collections/";
    private static final String API_KEY = "&apiKey=bup2ZBWGDC-IlRrpRsjTtJqiM_QKSmKa";

    public Mongo( final Display a ){ activity = a; }

    public void get( String coll, int id )
    {
        String query = "{\"_id\":" + id + "}";

        try
        {
            String url = BASE_URL + coll + "?q=" + URLEncoder.encode( query, "UTF-8") +  API_KEY;
            Log.d( "URL", url );
            new GetTask( activity ).execute( new URI( url ) );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(final InputStream is)
            throws IOException {
        InputStreamReader isr;
        BufferedReader reader;
        final StringBuilder builder;
        String line;

        isr = new InputStreamReader(is);
        reader = new BufferedReader(isr);
        builder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }

    private class GetTask
            extends AsyncTask<URI, Void, String>   // params, progress, result
    {
        private final Display activity;

        public GetTask(final Display a)
        {
            activity = a;
        }

        @Override
        protected String doInBackground(final URI... uris)
        {
            InputStream inputStream;
            String      result;

            if(uris.length != 1)
            {
                throw new IllegalArgumentException("You must provide one uri only");
            }

            inputStream = null;

            try
            {
                final HttpClient httpclient;
                final HttpGet httpGet;
                final HttpResponse httpResponse;

                httpclient   = new DefaultHttpClient();
                httpGet      = new HttpGet(uris[0]);
                httpResponse = httpclient.execute(httpGet);
                inputStream  = httpResponse.getEntity().getContent();

                if(inputStream != null)
                {
                    result = convertStreamToString(inputStream);
                }
                else
                {
                    result = null;
                }

                return (result);
            }
            catch(final ClientProtocolException ex)
            {
                Log.d("InputStream", ex.getLocalizedMessage());
            }
            catch(final IOException ex)
            {
                Log.d("InputStream", ex.getLocalizedMessage());
            }

            return (null);
        }

        @Override
        protected void onPostExecute(final String result)
        {
            if (result != null)
            {
                activity.setDisplayList( result );
            }
        }

    }

}
