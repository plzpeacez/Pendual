package s5630213012.pendual;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double lat;
    private double lon;
    private LocationManager locationManager;

    public String json;
    LatLng gu;
    LatLng latLng;
    MarkerOptions markerOptions;
    WebService ws = null;
    BusLocation bus = null;
    PositionLatLng pos = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        LocationListener locationListener = new LocationListener() {


            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub


                lat = location.getLatitude();
                lon = location.getLongitude();

                //Toast.makeText(getApplicationContext(), "Latitude " + lat + "  Longtitude " + lon,
                //        Toast.LENGTH_LONG).show();


            }

            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub

            }
        };


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch (Exception e){
            e.printStackTrace();
        }

        //*** Keyword
        final EditText txtKeyword = (EditText) findViewById(R.id.txtKeyword);
        //*** Button Search
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GeocoderTask().execute(txtKeyword.getText().toString());
            }
        });

        //spinner
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Your Target");
        categories.add("ตำแหน่งปัจจุบัน");
        categories.add("สาย 1");
        categories.add("สาย 2");
        categories.add("สาย 3");
        //categories.add("test");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        ws = new WebService();
        bus = new BusLocation();
        pos = new PositionLatLng();

        Button btnBuz = (Button) this.findViewById(R.id.btnBuzz);
        btnBuz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Timer timer = new Timer();
                timer.schedule( new TimerTask()
                {
                    public void run() {
                        // do your work
                        //Cadenza(bus.getData());
                        try {
                            buzTimer();
                        }catch (Exception e){

                        }
                    }
                }, 0, 60*(1000*1));*/
                //Cadenza(bus.getData());
                buzTimer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //LatLng psu = new LatLng(7.894919, 98.351722);
        //LatLng psu = new LatLng(lat, lon);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(psu, 16));
        //mMap.addMarker(new MarkerOptions()
        //        .position(psu)
        //        .title("PSU")
        //        .snippet("Prince of Songkla University"));
        /*mMap.addMarker(new MarkerOptions()
                .position(gu)
                .title("I AM HERE")
                .snippet("Welcome to nightmare"));*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        switch (position){
            case 0:
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();
                LatLng move = new LatLng(7.890470, 98.389951);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(move, 13));
                //mMap.moveCamera(CameraUpdateFactory.zoomBy(1000));

                //************** 3 **************//


                mMap.addPolyline(pos.getRectLine03());

                //************** 2 **************//


                mMap.addPolyline(pos.getRectLine02());

                //************* 1 ***************//


                mMap.addPolyline(pos.getRectLine01());
                break;
            case 1:
                //set up map
                gu = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions()
                        .position(gu)
                        .title("I AM HERE")
                        .snippet("Your Position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gu, 16));
                Toast.makeText(getApplicationContext(), "Latitude " + lat + "  Longtitude " + lon,
                        Toast.LENGTH_LONG).show();
                break;
            case 2:
                //ws.callService();
                bus.callServ();
                //Timer();
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();
                //************* 1 ***************//


                mMap.addPolyline(pos.getRectLine01());
                addMarker1();
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(new LatLng(7.890470, 98.389951)).zoom(13).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition1));
                break;
            case 3:
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();

                //************** 2 ***************//
                mMap.addPolyline(pos.getRectLine02());
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.921122, 98.395734)).title("ซุปเปอร์ชีป"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.927096, 98.395491)).title("แยกราชภัฏ"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.913441, 98.393301)).title("ภูเก็ตวิทยาลัย"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.907697, 98.391021)).title("แยกอำเภอ"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.899765, 98.388914)).title("แยกสตรี"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.891908, 98.389219)).title("ศาลากลาง"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.889516, 98.386817)).title("บขส"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.890907, 98.391171)).title("เอ็กโป"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.890054, 98.397753)).title("ตลาดเกษตร"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.883343, 98.393613)).title("แยกบางเหนียว"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880042, 98.395478)).title("รร.เทศบาลเมือง"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.877530, 98.395420)).title("แหลมชั่น"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.878866, 98.385421)).title("เทศบาลวิชิต"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880441, 98.378365)).title("ตลาดสี่มุมเมือง"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880916, 98.368737)).title(""));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.870214, 98.359144)).title(""));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.861417, 98.353152)).title(""));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.857987, 98.362925)).title(""));
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(new LatLng(7.890470, 98.389951)).zoom(13).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));
                break;
            case 4:
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();


                mMap.addPolyline(pos.getRectLine03());
                addMarker3();
                CameraPosition cameraPosition3 = new CameraPosition.Builder()
                        .target(new LatLng(7.885313, 98.413310)).zoom(13).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition3));
                break;
            case 5:
                //MarkerOptions marker3 = new MarkerOptions().position(new LatLng(ws.getLatitude(), ws.getLongitude())).title(ws.getToolTip());
                //json = bus.getData();
                //Cadenza(json);
                //MarkerOptions marker3 = new MarkerOptions().position(bus.getData());
                //marker3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car5));
                //mMap.addMarker(marker3);
                break;
        }
    }

    private void addMarker3() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.866532, 98.392749)).title("วงเวียนสะพานหิน"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.870661, 98.390715)).title("สะพานคลองบางใหญ่"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.879085, 98.385419)).title("รร.เฉลิมพระเกียรติ"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.883138, 98.387520)).title("สนง.ขนส่งภูเก็ต"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.887053, 98.387697)).title("รร.เทศบาลเมืองภูเก็ต"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.890897, 98.391148)).title("วงเวียนสุริยเดช"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.885744, 98.393161)).title("รร.พุทธมงคลนิมิตร"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.883350, 98.393670)).title("รร.สตรีภูเก็ต"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.882823, 98.399380)).title("ไปรษณีย์"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.885237, 98.411373)).title("สถานีขนส่ง"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.884131, 98.426926)).title("รพ.อบจ.ภูเก็ต"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.870947, 98.432618)).title("ท่าเทียบเรือ"));
    }

    private void addMarker1() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.896844, 98.368431)).title("บิ๊กซี"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.906094, 98.369024)).title("โลตัส"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.898514, 98.383368)).title("รพ.วชิระ"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.890437, 98.389899)).title("แยกสตรี"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.889291, 98.386203)).title("แยกอำเภอ"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.881771, 98.383985)).title("แยกไทหัว"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.883005, 98.387231)).title("แยกจุ้ยตุ่ย"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.883353, 98.393679)).title("ซอยภูธร"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.880165, 98.392313)).title("วงเวียนน้ำพุ"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.878115, 98.393096)).title("บขส"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.874051, 98.393885)).title("วงเวียนหอนาฬิกา"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.871419, 98.393953)).title("แยกบางเหนียว"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.868661, 98.395767)).title("สะพานหิน"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.863928, 98.400856)).title("รร.เฉลิมพระเกียรติ"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.862845, 98.401488)).title("วิทยาลัยอาชีวศึกษาภูเก็ต"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.863010, 98.398790)).title(""));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.866506, 98.392760)).title(""));
        mMap.addMarker(new MarkerOptions().position(new LatLng(7.869540, 98.392430)).title(""));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //*** An AsyncTask Background Process
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            //mMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
    }

    private void Timer () {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // do staff
                bus.callServ();
            }
        }, 0, 60000);
    }

    private void buzTimer () {
        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    new MarkerTask().execute(bus.getData());
                    //bus.callServ();
                }
            }, 0, 10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Cadenza (String json) {

        try {
            // De-serialize the JSON string into an array of city objects
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                String[] latl = jsonObj.getString("position").toString().split(",");
                LatLng latLng = new LatLng(Double.parseDouble(latl[0].toString()),
                        Double.parseDouble(latl[1].toString()));

                //move CameraPosition on first result
                if (i == 0) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(13).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

                // Create a marker for each city in the JSON data.
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car5))
                        .title(jsonObj.getString("latlngID"))
                                //.snippet(Integer.toString(jsonObj.getInt("population")))
                        .position(latLng));
                //MarkerOptions marker3 = new MarkerOptions().position(latLng);
                //marker3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car5));
                //mMap.addMarker(marker3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MarkerTask extends AsyncTask<String, Void, String> {

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... jsonz) {
            //bus.callServ();
            //mMap.clear();
            return jsonz.toString();
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String jsonx) {
            try {
                // De-serialize the JSON string into an array of city objects
                if(bus.getData()!=null) {
                    mMap.clear();
                    mMap.addPolyline(pos.getRectLine01());
                    addMarker1();
                    JSONArray jsonArray = new JSONArray(bus.getData());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        String[] latl = jsonObj.getString("position").toString().split(",");
                        LatLng latLng = new LatLng(Double.parseDouble(latl[0].toString()),
                                Double.parseDouble(latl[1].toString()));

                        //move CameraPosition on first result
                        if (i == 99) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(latLng).zoom(13).build();

                            mMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                        }

                        // Create a marker for each city in the JSON data.
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car5))
                                .title(jsonObj.getString("latlngID"))
                                        //.snippet(Integer.toString(jsonObj.getInt("population")))
                                .position(latLng));
                        //MarkerOptions marker3 = new MarkerOptions().position(latLng);
                        //marker3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car5));
                        //mMap.addMarker(marker3);
                    }
                }else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bus.callServ();
            Toast.makeText(getApplicationContext(), "2setp",
                    Toast.LENGTH_LONG).show();
        }
    }
}
