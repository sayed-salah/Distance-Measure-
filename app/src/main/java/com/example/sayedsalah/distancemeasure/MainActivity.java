package com.example.sayedsalah.distancemeasure;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.FloatMath;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button btnstart = null;
    TextView displaytv = null;
    private LocationManager locationManager;
    private LocationListener locationListener; //listen for location changes

    private ArrayList<OneLocation> list = null;
   OneLocation firstlocation=null;
  // double  firstlitude,firstlongtude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnstart = (Button) findViewById(R.id.btn1);
        displaytv = (TextView) findViewById(R.id.dispalytv);
        list=new ArrayList<>();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                OneLocation onelocation=new OneLocation();
                onelocation.setLatitude(location.getLatitude());
                onelocation.setLongitude(location.getLongitude());
                list.add(onelocation);
                firstlocation= list.get(0);

              new  Getrout(firstlocation.getLatitude(),firstlocation.getLongitude(),location.getLatitude(),location.getLongitude()).execute();

               // meterDistanceBetweenPoints(list.indexOf(0));
               // displaytv.setText(list+" ");
                //displaytv.append("\n" + location.getLatitude() + " \n" + location.getLongitude());
            }



            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);

                return;
            }
        } else {

            configureButton();
        }


    }

//    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
//        float pk = (float) (180.f/Math.PI);
//
//        float a1 = lat_a / pk;
//        float a2 = lng_a / pk;
//        float b1 = lat_b / pk;
//        float b2 = lng_b / pk;
//
//        float t1 = (float) (Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2));
//        float t2 = (float) (Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2));
//        float t3 = (float) (Math.sin(a1)*Math.sin(b1));
//        double tt = Math.acos(t1 + t2 + t3);
//
//        return 6366000*tt;
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();

                return;


        }

    }

    private void configureButton() {
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TripDto dto = service.GetStartLatLong(TripIdA);

                locationManager.requestLocationUpdates("gps",0, 0, locationListener);
            }
        });


    }





    public class Getrout  extends AsyncTask<Void, Void, Void> {

        ArrayList<Route_details> routes_array = new ArrayList<>();;
      //  private OneLocation n1=null,n2=null;
        double l1,g1,l2,g2;

       // TextView displaytv = (TextView) findViewById(R.id.dispalytv);

        public Getrout(double l1,double g1,double l2,double g2) {
            this.l1=l1;
            this.g1=g1;
            this.l2=l2;
            this.g2=g2;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            routes_array.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            displaytv.setText(routes_array.get(0).duration.d + "," + "" + routes_array.get(0).distance.d);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {


                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                StringBuffer stringBuffer = new StringBuffer();
                String line, json;
                HttpsURLConnection httpsURLConnection = null;

                URL url = null;
                url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" +l1+","+g1+ "&destination=" +l2+","+g2+ "&key=AIzaSyC2N9QgsQTr-zc4S6LqC7TCjCXxVIarxyk");
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();

                inputStream = httpsURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null)
                    stringBuffer.append(line + "\n");
                json = stringBuffer.toString();
                JSONObject jsonData = new JSONObject(json);


                JSONArray jsonRoutes = jsonData.getJSONArray("routes");

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                    Route_details route = new Route_details();

                    JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                    JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                    JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                    JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                    JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                    JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                    JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                    route.distance = new Distance(jsonDistance.getString("text"));
                    route.duration = new Duration(jsonDuration.getString("text"));
                    route.endAddress = jsonLeg.getString("end_address");
                    route.startAddress = jsonLeg.getString("start_address");
                    routes_array.add(route);
                }


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
