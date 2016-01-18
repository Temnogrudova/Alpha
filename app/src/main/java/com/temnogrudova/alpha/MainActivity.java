package com.temnogrudova.alpha;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    public static String LOG_TAG = "my_log";
    RecyclerView twitterMessages;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        twitterMessages = (RecyclerView)findViewById(R.id.myList);
        twitterMessages.setLayoutManager(new LinearLayoutManager(this));
        twitterMessages.setHasFixedSize(false);
        new ParseTask().execute();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://api.app.net/posts/stream/global");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;

            ArrayList<Item> items = new ArrayList<Item>();
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray data = dataJsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject itemData = data.getJSONObject(i);

                    String itemText=itemData.getString("text");

                    JSONObject user = itemData.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar_image");
                    String itemAvatarUrl = avatar.getString("url");

                    String itemName = user.getString("name");

                    String itemTime = itemData.getString("created_at");
                    try {
                        Date d = parse(itemTime);
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        itemTime= df.format(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    items.add(new Item(itemAvatarUrl, itemName, itemText, itemTime));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                items.add(new Item("", "", "",""));
            }
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(items);
            twitterMessages.setAdapter(recyclerAdapter);

            // MessagesAdapter boxAdapter = new MessagesAdapter(context, R.layout.pattern,items);
           // twitterMessages.setAdapter(boxAdapter);
        }
    }
    public static Date parse( String input ) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        //this is zero time so we need to add that TZ indicator for
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }

        return df.parse( input );

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
