package com.chucknorris;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final AsyncHttpClient client = new AsyncHttpClient();

    //This would usually be something else, but for now to test locally
    final String baseUri = "http://10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating header text
        TextView headerTextView = (TextView) findViewById(R.id.header);
        String headerText = "Old Chuck lives here! " + getEmojiByUnicode(0x1F920);
        headerTextView.setText(headerText);

        //Creating the laughing emoji view
        TextView laughTextView = (TextView) findViewById(R.id.laugh);
        String laughEmoji = getEmojiByUnicode(0x1F602);
        laughTextView.setText(laughEmoji);

        //creating the dropdown menu with joke categories
        final Spinner spin = (Spinner) findViewById(R.id.categories);
        spin.setOnItemSelectedListener(this);

        client.get(baseUri + "/get-categories", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String[] categories = response.toString().substring(
                        2, response.toString().length() - 1).split("\",\"");
                try {
                    //Creating the ArrayAdapter instance having the categories list
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            categories
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    spin.setAdapter(adapter);

                } catch (Exception e) {
                    // TODO: handle errors, should be logged
                    e.printStackTrace();
                }
            }
        });

    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(final AdapterView<?> arg0, View arg1, final int position, long id) {

        final EditText joke = (EditText) findViewById(R.id.joke_text);
        String selectedValue = arg0.getSelectedItem().toString();
        String uri = baseUri + "/get-joke?category=" + selectedValue;

        client.get(uri, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.println(response.get("value").toString());
                    joke.setText(response.get("value").toString());
                } catch (Exception e) {
                    // TODO: handle errors, should be logged
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO: what happens when nothing is selected;
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

}