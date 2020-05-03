package com.example.covid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.simplearcloader.SimpleArcLoader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;




public class MainActivity extends AppCompatActivity  {

    TextView active,cured,death,migrated,ConnError, ConnServerError ;
    TableLayout tl;
    SimpleArcLoader arcProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcProgressBar = (SimpleArcLoader) findViewById(R.id.pbProgess2);
        ConnError = (TextView) findViewById(R.id.connError);
        ConnServerError = (TextView) findViewById(R.id.connServerError);
        tl = (TableLayout)findViewById(R.id.coronoTableLayout);
        active = (TextView) findViewById(R.id.active);
        cured = (TextView) findViewById(R.id.cured);
        death = (TextView) findViewById(R.id.deaths);
        migrated = (TextView) findViewById(R.id.migrated);

        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            tl.setVisibility(View.GONE);
            ConnError.setVisibility(View.GONE);
            arcProgressBar.setVisibility(View.VISIBLE);
            new INDIA().execute();

        }else{
            checkNetworkConnection();
            tl.setVisibility(View.GONE);
            ConnError.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Check Your Internet Connection!!!", Toast.LENGTH_SHORT).show();
        }
        final Button BtnRefresh = findViewById(R.id.btnRefresh);
        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected() == true) {
                    tl.setVisibility(View.GONE);
                    ConnError.setVisibility(View.GONE);
                    ConnServerError.setVisibility(View.GONE);
                    arcProgressBar.setVisibility(View.VISIBLE);
                    active.setText("");
                    cured.setText("");
                    death.setText("");
                    migrated.setText("");
                    new INDIA().execute();


                }else{
                    checkNetworkConnection();
                    tl.setVisibility(View.GONE);
                    ConnError.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Check Your Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    class INDIA extends AsyncTask<Void, ArrayList,ArrayList>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Void... voids) {

            ArrayList<String> arrayList = new ArrayList<>();

            try {
                String url = "http://www.mohfw.gov.in/";

                Document document = Jsoup.connect(url).get();
                System.out.println("document"+document);


                Elements elements = document.getElementsByClass("site-stats-count");
                System.out.println("elements"+elements);

                Elements itemElements = elements.select("ul li");

                for(Element elem : itemElements){
                    System.out.println(elem.select("strong").text());
                    if (elem.select("strong").text()!=""){
                        arrayList.add(elem.select("strong").text());
                    }
                }

                System.out.println("arraylist"+arrayList);
            }catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                System.out.println("error on fetching");
                System.out.println(e);
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            System.out.println("Execution Completed" + arrayList );
            if(arrayList.isEmpty()){
                // SET VALUES :
                active.setText("");
                cured.setText("");
                death.setText("");
                migrated.setText("");
                arcProgressBar.setVisibility(View.GONE);
                tl.setVisibility(View.GONE);
                ConnServerError.setVisibility(View.VISIBLE);
            }
            else{
                // SET VALUES :
                active.setText(""+arrayList.get(0));
                cured.setText(""+arrayList.get(1));
                death.setText(""+arrayList.get(2));
                migrated.setText(""+arrayList.get(3));
                arcProgressBar.setVisibility(View.GONE);
                tl.setVisibility(View.VISIBLE);

            }


            super.onPostExecute(arrayList);

        }

    }
    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
    }



}
