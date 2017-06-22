package ysolution.ys_mahmoud;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String[] sections;
    Toast toast;
    Spinner spinner;
    RecyclerView itemsRecyclerView;
    List<Item> itemList;
    RecyclerAdapter adapter;
    TextView noInternet;
    ProgressBar loadingProgress;
    public final String CONNECTION_ERROR = "no internet connection";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingProgress = (ProgressBar)findViewById(R.id.pb_loading);
        noInternet = (TextView)findViewById(R.id.tv_noInternet);

        itemsRecyclerView = (RecyclerView) findViewById(R.id.rv_items);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        spinner = (Spinner) findViewById(R.id.s_sections);
        toast = new Toast(this);

        sections = getResources().getStringArray(R.array.sections);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                sections);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (isOnline())
                    getData(MainActivity.this, spinner.getSelectedItemPosition());
                else
                    toast.makeText(MainActivity.this, "you are offline", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(final AdapterView<?> adapterView, View view, final int i, long l) {
        toast.cancel();
        itemList = getList(adapterView.getContext(), sections[i]);
        if(itemList == null  &&  isOnline()) {
            getData(adapterView.getContext(), i);
        }
        else {
            if(itemList == null) {
                 itemList = new ArrayList<>();
                toast.makeText(adapterView.getContext(), CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
                noInternet.setVisibility(View.VISIBLE);
            }
            else {
                noInternet.setVisibility(View.INVISIBLE);
            }
        }
        adapter = new RecyclerAdapter(itemList, adapterView.getContext());
        itemsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    public void getData(final Context context, final int i) {
        itemList = new ArrayList<>();
        loadingProgress.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.INVISIBLE);
        Ion.with(context)
                .load(getString(R.string.baseUrl) + sections[i] + getString(R.string.type) + getString(R.string.key))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result != null) {
                            JsonArray jsonArray = result.getAsJsonArray("results");
                            for (int i = 0; i < jsonArray.size(); i++) {
                                String url = null;
                                if (jsonArray.get(i).getAsJsonObject()
                                        .getAsJsonArray("multimedia").size() != 0) {
                                    url = jsonArray.get(i).getAsJsonObject()
                                            .getAsJsonArray("multimedia")
                                            .get(0).getAsJsonObject()
                                            .get("url").toString();
                                }
                                itemList.add(new Item(
                                        jsonArray.get(i).getAsJsonObject().get("title").toString(),
                                        jsonArray.get(i).getAsJsonObject().get("published_date").toString(),
                                        url
                                ));
                            }
                            adapter = new RecyclerAdapter(itemList, context);
                            itemsRecyclerView.setAdapter(adapter);
                            saveList(itemList, context, sections[i]);

                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                        else {
                            toast.makeText(context, "you are offline", Toast.LENGTH_SHORT).show();
                            loadingProgress.setVisibility(View.INVISIBLE);
                            noInternet.setVisibility(View.VISIBLE);
                        }
                        Log.v("size", itemList.size() + "");
                    }
                });
    }
    public void saveList(List<Item> list, Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }
    public List<Item> getList(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, "");
        Type type = new TypeToken< List < Item >>() {}.getType();
        return gson.fromJson(json, type);
    }
}
