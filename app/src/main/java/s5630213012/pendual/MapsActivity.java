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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double lat;
    private double lon;
    private LocationManager locationManager;
    private final String NAMESPACE = "http://192.168.43.166/nusoap/WebServiceServer.php";
    private final String URL = "http://192.168.43.166/nusoap/WebServiceServer.php?wsdl"; // WSDL URL
    private final String SOAP_ACTION = "http://192.168.43.92/nusoap/WebServiceServer.php/HelloWorld";
    private final String METHOD_NAME = "HelloWorld"; // Method on web service
    private final String xSOAP_ACTION = "http://192.168.43.166/nusoap/WebServiceServer.php/HiHi";
    private final String xMETHOD_NAME = "HiHi"; // Method on web service

    LatLng gu;
    LatLng latLng;
    MarkerOptions markerOptions;

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
        categories.add("all Route");
        categories.add("Mylocation");
        categories.add("Route 1");
        categories.add("Route 2");
        categories.add("Route 3");
        categories.add("test");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(move, 21));
                //mMap.moveCamera(CameraUpdateFactory.zoomBy(1000));

                
                //************* 1 ***************//
                PolylineOptions rectLine1 = new PolylineOptions()
                        .add(new LatLng(7.896788, 98.368462))
                        .add(new LatLng(7.901780, 98.367881))
                        .add(new LatLng(7.904144, 98.367557))
                        .add(new LatLng(7.904212, 98.367571))
                        .add(new LatLng(7.905785, 98.367590))
                        .add(new LatLng(7.906075, 98.369101))
                        .add(new LatLng(7.906730, 98.371787))
                        .add(new LatLng(7.906756, 98.371832))
                        .add(new LatLng(7.906768, 98.371909))
                        .add(new LatLng(7.906732, 98.372270))
                        .add(new LatLng(7.906729, 98.372453))
                        .add(new LatLng(7.906761, 98.372639))
                        .add(new LatLng(7.906763, 98.372757))
                        .add(new LatLng(7.906889, 98.373151))
                        .add(new LatLng(7.906894, 98.373223))
                        .add(new LatLng(7.906916, 98.373277))
                        .add(new LatLng(7.906924, 98.373459))
                        .add(new LatLng(7.906907, 98.373612))
                        .add(new LatLng(7.906877, 98.373803))
                        .add(new LatLng(7.906871, 98.373914))
                        .add(new LatLng(7.906895, 98.374439))
                        .add(new LatLng(7.907041, 98.375143))
                        .add(new LatLng(7.907075, 98.375429))
                        .add(new LatLng(7.907120, 98.376661))
                        .add(new LatLng(7.907114, 98.376949))
                        .add(new LatLng(7.907107, 98.377100))
                        .add(new LatLng(7.906758, 98.377877))
                        .add(new LatLng(7.903858, 98.377633))
                        .add(new LatLng(7.903583, 98.377652))
                        .add(new LatLng(7.903216, 98.377725))
                        .add(new LatLng(7.902957, 98.377856))
                        .add(new LatLng(7.902714, 98.377958))
                        .add(new LatLng(7.902524, 98.378120))
                        .add(new LatLng(7.902284, 98.378685))
                        .add(new LatLng(7.901962, 98.379798))
                        .add(new LatLng(7.901750, 98.380434))
                        .add(new LatLng(7.901613, 98.380654))
                        .add(new LatLng(7.901342, 98.380972))
                        .add(new LatLng(7.900129, 98.382112))
                        .add(new LatLng(7.900030, 98.382225))
                        .add(new LatLng(7.899636, 98.382656))
                        .add(new LatLng(7.899353, 98.382897))
                        .add(new LatLng(7.897935, 98.383744))
                        .add(new LatLng(7.895894, 98.384727))
                        .add(new LatLng(7.893920, 98.385681))
                        .add(new LatLng(7.893685, 98.385743))
                        .add(new LatLng(7.893272, 98.385794))
                        .add(new LatLng(7.892532, 98.385812))
                        .add(new LatLng(7.891570, 98.385869))
                        .add(new LatLng(7.891679, 98.386966))
                        .add(new LatLng(7.891904, 98.388901))
                        .add(new LatLng(7.891947, 98.389282))
                        .add(new LatLng(7.891997, 98.389790))
                        .add(new LatLng(7.891311, 98.389897))
                        .add(new LatLng(7.891238, 98.389882))
                        .add(new LatLng(7.890496, 98.389974))
                        .add(new LatLng(7.889858, 98.388067))
                        .add(new LatLng(7.889323, 98.386273))
                        .add(new LatLng(7.889039, 98.385049))
                        .add(new LatLng(7.888492, 98.383030))
                        .add(new LatLng(7.888397, 98.382706))
                        .add(new LatLng(7.888341, 98.382182))
                        .add(new LatLng(7.887769, 98.382244))
                        .add(new LatLng(7.886743, 98.382309))
                        .add(new LatLng(7.886120, 98.382366))
                        .add(new LatLng(7.883768, 98.382520))
                        .add(new LatLng(7.883793, 98.382927))
                        .add(new LatLng(7.883796, 98.383054))
                        .add(new LatLng(7.883779, 98.383252))
                        .add(new LatLng(7.883752, 98.383349))
                        .add(new LatLng(7.883477, 98.383470))
                        .add(new LatLng(7.883110, 98.383460))
                        .add(new LatLng(7.882686, 98.383641))
                        .add(new LatLng(7.882588, 98.383673))
                        .add(new LatLng(7.882147, 98.383812))
                        .add(new LatLng(7.882075, 98.383845))
                        .add(new LatLng(7.881897, 98.383912))
                        .add(new LatLng(7.881463, 98.384074))
                        .add(new LatLng(7.881398, 98.384107))
                        .add(new LatLng(7.881292, 98.384150))
                        .add(new LatLng(7.881881, 98.383898))
                        .add(new LatLng(7.881447, 98.384073))
                        .add(new LatLng(7.881247, 98.384178))
                        .add(new LatLng(7.881019, 98.384392))
                        .add(new LatLng(7.880951, 98.384471))
                        .add(new LatLng(7.880480, 98.384932))
                        .add(new LatLng(7.880180, 98.385265))
                        .add(new LatLng(7.881315, 98.386455))
                        .add(new LatLng(7.881545, 98.386663))
                        .add(new LatLng(7.881953, 98.387064))
                        .add(new LatLng(7.882031, 98.387124))
                        .add(new LatLng(7.882081, 98.387152))
                        .add(new LatLng(7.882117, 98.387164))
                        .add(new LatLng(7.882466, 98.387207))
                        .add(new LatLng(7.882795, 98.387239))
                        .add(new LatLng(7.883045, 98.387261))
                        .add(new LatLng(7.883052, 98.387233))
                        .add(new LatLng(7.883079, 98.387190))
                        .add(new LatLng(7.883098, 98.387176))
                        .add(new LatLng(7.883135, 98.387156))
                        .add(new LatLng(7.883185, 98.387154))
                        .add(new LatLng(7.883234, 98.387172))
                        .add(new LatLng(7.883276, 98.387205))
                        .add(new LatLng(7.883292, 98.387250))
                        .add(new LatLng(7.883292, 98.387291))
                        .add(new LatLng(7.883344, 98.387302))
                        .add(new LatLng(7.883801, 98.387362))
                        .add(new LatLng(7.883727, 98.388303))
                        .add(new LatLng(7.883706, 98.388491))
                        .add(new LatLng(7.883656, 98.389424))
                        .add(new LatLng(7.883574, 98.390827))
                        .add(new LatLng(7.883577, 98.390932))
                        .add(new LatLng(7.883481, 98.392144))
                        .add(new LatLng(7.883448, 98.392743))
                        .add(new LatLng(7.883347, 98.393324))
                        .add(new LatLng(7.883321, 98.393622))
                        .add(new LatLng(7.883334, 98.393719))
                        .add(new LatLng(7.883321, 98.393622))
                        .add(new LatLng(7.883347, 98.393324))
                        .add(new LatLng(7.883448, 98.392743))
                        .add(new LatLng(7.882980, 98.392648))
                        .add(new LatLng(7.882440, 98.392540))
                        .add(new LatLng(7.882255, 98.392519))
                        .add(new LatLng(7.881611, 98.392438))
                        .add(new LatLng(7.880967, 98.392377))
                        .add(new LatLng(7.880570, 98.392365))
                        .add(new LatLng(7.880494, 98.392337))
                        .add(new LatLng(7.880459, 98.392358))
                        .add(new LatLng(7.880428, 98.392365))
                        .add(new LatLng(7.880403, 98.392366))
                        .add(new LatLng(7.880371, 98.392359))
                        .add(new LatLng(7.880337, 98.392345))
                        .add(new LatLng(7.880311, 98.392331))
                        .add(new LatLng(7.880284, 98.392311))
                        .add(new LatLng(7.880264, 98.392291))
                        .add(new LatLng(7.880256, 98.392272))
                        .add(new LatLng(7.880246, 98.392257))
                        .add(new LatLng(7.879935, 98.392365))
                        .add(new LatLng(7.879600, 98.392479))
                        .add(new LatLng(7.879096, 98.392660))
                        .add(new LatLng(7.878639, 98.392849))
                        .add(new LatLng(7.878528, 98.392893))
                        .add(new LatLng(7.878078, 98.393079))
                        .add(new LatLng(7.877342, 98.393369))
                        .add(new LatLng(7.876865, 98.393595))
                        .add(new LatLng(7.876391, 98.393766))
                        .add(new LatLng(7.876363, 98.393754))
                        .add(new LatLng(7.874011, 98.393850))
                        .add(new LatLng(7.872961, 98.393899))
                        .add(new LatLng(7.872375, 98.393902))
                        .add(new LatLng(7.872178, 98.393887))
                        .add(new LatLng(7.871641, 98.393899))
                        .add(new LatLng(7.871347, 98.393918))
                        .add(new LatLng(7.870429, 98.394110))
                        .add(new LatLng(7.869895, 98.394249))
                        .add(new LatLng(7.869637, 98.394367))
                        .add(new LatLng(7.869578, 98.394404))
                        .add(new LatLng(7.869425, 98.394556))
                        .add(new LatLng(7.869151, 98.395014))
                        .add(new LatLng(7.869031, 98.395157))
                        .add(new LatLng(7.868619, 98.395789))
                        .add(new LatLng(7.868636, 98.395892))
                        .add(new LatLng(7.868616, 98.395952))
                        .add(new LatLng(7.868543, 98.396087))
                        .add(new LatLng(7.868458, 98.396191))
                        .add(new LatLng(7.868390, 98.396241))
                        .add(new LatLng(7.868344, 98.396236))
                        .add(new LatLng(7.867963, 98.396919))
                        .add(new LatLng(7.867737, 98.397260))
                        .add(new LatLng(7.867613, 98.397394))
                        .add(new LatLng(7.867442, 98.397507))
                        .add(new LatLng(7.867215, 98.397603))
                        .add(new LatLng(7.866587, 98.397892))
                        .add(new LatLng(7.866496, 98.397936))
                        .add(new LatLng(7.866309, 98.398089))
                        .add(new LatLng(7.866063, 98.398373))
                        .add(new LatLng(7.865509, 98.399013))
                        .add(new LatLng(7.865312, 98.399230))
                        .add(new LatLng(7.864463, 98.400247))
                        .add(new LatLng(7.863974, 98.400775))
                        .add(new LatLng(7.863277, 98.401606))
                        .add(new LatLng(7.863241, 98.401640))
                        .add(new LatLng(7.863215, 98.401651))
                        .add(new LatLng(7.863168, 98.401649))
                        .add(new LatLng(7.863131, 98.401637))
                        .add(new LatLng(7.862839, 98.401451))
                        .add(new LatLng(7.862715, 98.401315))
                        .add(new LatLng(7.862718, 98.401241))
                        .add(new LatLng(7.862943, 98.400855))
                        .add(new LatLng(7.863319, 98.400239))
                        .add(new LatLng(7.863467, 98.400033))
                        .add(new LatLng(7.862629, 98.399302))
                        .add(new LatLng(7.863357, 98.398419))
                        .add(new LatLng(7.863831, 98.397793))
                        .add(new LatLng(7.864306, 98.397163))
                        .add(new LatLng(7.864681, 98.396661))
                        .add(new LatLng(7.865516, 98.395148))
                        .add(new LatLng(7.865881, 98.394406))
                        .add(new LatLng(7.865532, 98.394201))
                        .add(new LatLng(7.865928, 98.393388))
                        .add(new LatLng(7.866139, 98.393043))
                        .add(new LatLng(7.866272, 98.392921))
                        .add(new LatLng(7.867982, 98.392052))
                        .add(new LatLng(7.869351, 98.391407))
                        .add(new LatLng(7.869476, 98.391372))
                        .add(new LatLng(7.869506, 98.391888))
                        .add(new LatLng(7.869513, 98.392483))
                        .width(15).zIndex(10)
                                //.color(Color.rgb(0x23, 0x92, 0x99));
                        .color(Color.YELLOW);

                mMap.addPolyline(rectLine1);
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
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();
                //************* 1 ***************//
                PolylineOptions rectLine01 = new PolylineOptions()
                        .add(new LatLng(7.896788, 98.368462))
                        .add(new LatLng(7.901780, 98.367881))
                        .add(new LatLng(7.904144, 98.367557))
                        .add(new LatLng(7.904212, 98.367571))
                        .add(new LatLng(7.905785, 98.367590))
                        .add(new LatLng(7.906075, 98.369101))
                        .add(new LatLng(7.906730, 98.371787))
                        .add(new LatLng(7.906756, 98.371832))
                        .add(new LatLng(7.906768, 98.371909))
                        .add(new LatLng(7.906732, 98.372270))
                        .add(new LatLng(7.906729, 98.372453))
                        .add(new LatLng(7.906761, 98.372639))
                        .add(new LatLng(7.906763, 98.372757))
                        .add(new LatLng(7.906889, 98.373151))
                        .add(new LatLng(7.906894, 98.373223))
                        .add(new LatLng(7.906916, 98.373277))
                        .add(new LatLng(7.906924, 98.373459))
                        .add(new LatLng(7.906907, 98.373612))
                        .add(new LatLng(7.906877, 98.373803))
                        .add(new LatLng(7.906871, 98.373914))
                        .add(new LatLng(7.906895, 98.374439))
                        .add(new LatLng(7.907041, 98.375143))
                        .add(new LatLng(7.907075, 98.375429))
                        .add(new LatLng(7.907120, 98.376661))
                        .add(new LatLng(7.907114, 98.376949))
                        .add(new LatLng(7.907107, 98.377100))
                        .add(new LatLng(7.906758, 98.377877))
                        .add(new LatLng(7.903858, 98.377633))
                        .add(new LatLng(7.903583, 98.377652))
                        .add(new LatLng(7.903216, 98.377725))
                        .add(new LatLng(7.902957, 98.377856))
                        .add(new LatLng(7.902714, 98.377958))
                        .add(new LatLng(7.902524, 98.378120))
                        .add(new LatLng(7.902284, 98.378685))
                        .add(new LatLng(7.901962, 98.379798))
                        .add(new LatLng(7.901750, 98.380434))
                        .add(new LatLng(7.901613, 98.380654))
                        .add(new LatLng(7.901342, 98.380972))
                        .add(new LatLng(7.900129, 98.382112))
                        .add(new LatLng(7.900030, 98.382225))
                        .add(new LatLng(7.899636, 98.382656))
                        .add(new LatLng(7.899353, 98.382897))
                        .add(new LatLng(7.897935, 98.383744))
                        .add(new LatLng(7.895894, 98.384727))
                        .add(new LatLng(7.893920, 98.385681))
                        .add(new LatLng(7.893685, 98.385743))
                        .add(new LatLng(7.893272, 98.385794))
                        .add(new LatLng(7.892532, 98.385812))
                        .add(new LatLng(7.891570, 98.385869))
                        .add(new LatLng(7.891679, 98.386966))
                        .add(new LatLng(7.891904, 98.388901))
                        .add(new LatLng(7.891947, 98.389282))
                        .add(new LatLng(7.891997, 98.389790))
                        .add(new LatLng(7.891311, 98.389897))
                        .add(new LatLng(7.891238, 98.389882))
                        .add(new LatLng(7.890496, 98.389974))
                        .add(new LatLng(7.889858, 98.388067))
                        .add(new LatLng(7.889323, 98.386273))
                        .add(new LatLng(7.889039, 98.385049))
                        .add(new LatLng(7.888492, 98.383030))
                        .add(new LatLng(7.888397, 98.382706))
                        .add(new LatLng(7.888341, 98.382182))
                        .add(new LatLng(7.887769, 98.382244))
                        .add(new LatLng(7.886743, 98.382309))
                        .add(new LatLng(7.886120, 98.382366))
                        .add(new LatLng(7.883768, 98.382520))
                        .add(new LatLng(7.883793, 98.382927))
                        .add(new LatLng(7.883796, 98.383054))
                        .add(new LatLng(7.883779, 98.383252))
                        .add(new LatLng(7.883752, 98.383349))
                        .add(new LatLng(7.883477, 98.383470))
                        .add(new LatLng(7.883110, 98.383460))
                        .add(new LatLng(7.882686, 98.383641))
                        .add(new LatLng(7.882588, 98.383673))
                        .add(new LatLng(7.882147, 98.383812))
                        .add(new LatLng(7.882075, 98.383845))
                        .add(new LatLng(7.881897, 98.383912))
                        .add(new LatLng(7.881463, 98.384074))
                        .add(new LatLng(7.881398, 98.384107))
                        .add(new LatLng(7.881292, 98.384150))
                        .add(new LatLng(7.881881, 98.383898))
                        .add(new LatLng(7.881447, 98.384073))
                        .add(new LatLng(7.881247, 98.384178))
                        .add(new LatLng(7.881019, 98.384392))
                        .add(new LatLng(7.880951, 98.384471))
                        .add(new LatLng(7.880480, 98.384932))
                        .add(new LatLng(7.880180, 98.385265))
                        .add(new LatLng(7.881315, 98.386455))
                        .add(new LatLng(7.881545, 98.386663))
                        .add(new LatLng(7.881953, 98.387064))
                        .add(new LatLng(7.882031, 98.387124))
                        .add(new LatLng(7.882081, 98.387152))
                        .add(new LatLng(7.882117, 98.387164))
                        .add(new LatLng(7.882466, 98.387207))
                        .add(new LatLng(7.882795, 98.387239))
                        .add(new LatLng(7.883045, 98.387261))
                        .add(new LatLng(7.883052, 98.387233))
                        .add(new LatLng(7.883079, 98.387190))
                        .add(new LatLng(7.883098, 98.387176))
                        .add(new LatLng(7.883135, 98.387156))
                        .add(new LatLng(7.883185, 98.387154))
                        .add(new LatLng(7.883234, 98.387172))
                        .add(new LatLng(7.883276, 98.387205))
                        .add(new LatLng(7.883292, 98.387250))
                        .add(new LatLng(7.883292, 98.387291))
                        .add(new LatLng(7.883344, 98.387302))
                        .add(new LatLng(7.883801, 98.387362))
                        .add(new LatLng(7.883727, 98.388303))
                        .add(new LatLng(7.883706, 98.388491))
                        .add(new LatLng(7.883656, 98.389424))
                        .add(new LatLng(7.883574, 98.390827))
                        .add(new LatLng(7.883577, 98.390932))
                        .add(new LatLng(7.883481, 98.392144))
                        .add(new LatLng(7.883448, 98.392743))
                        .add(new LatLng(7.883347, 98.393324))
                        .add(new LatLng(7.883321, 98.393622))
                        .add(new LatLng(7.883334, 98.393719))
                        .add(new LatLng(7.883321, 98.393622))
                        .add(new LatLng(7.883347, 98.393324))
                        .add(new LatLng(7.883448, 98.392743))
                        .add(new LatLng(7.882980, 98.392648))
                        .add(new LatLng(7.882440, 98.392540))
                        .add(new LatLng(7.882255, 98.392519))
                        .add(new LatLng(7.881611, 98.392438))
                        .add(new LatLng(7.880967, 98.392377))
                        .add(new LatLng(7.880570, 98.392365))
                        .add(new LatLng(7.880494, 98.392337))
                        .add(new LatLng(7.880459, 98.392358))
                        .add(new LatLng(7.880428, 98.392365))
                        .add(new LatLng(7.880403, 98.392366))
                        .add(new LatLng(7.880371, 98.392359))
                        .add(new LatLng(7.880337, 98.392345))
                        .add(new LatLng(7.880311, 98.392331))
                        .add(new LatLng(7.880284, 98.392311))
                        .add(new LatLng(7.880264, 98.392291))
                        .add(new LatLng(7.880256, 98.392272))
                        .add(new LatLng(7.880246, 98.392257))
                        .add(new LatLng(7.879935, 98.392365))
                        .add(new LatLng(7.879600, 98.392479))
                        .add(new LatLng(7.879096, 98.392660))
                        .add(new LatLng(7.878639, 98.392849))
                        .add(new LatLng(7.878528, 98.392893))
                        .add(new LatLng(7.878078, 98.393079))
                        .add(new LatLng(7.877342, 98.393369))
                        .add(new LatLng(7.876865, 98.393595))
                        .add(new LatLng(7.876391, 98.393766))
                        .add(new LatLng(7.876363, 98.393754))
                        .add(new LatLng(7.874011, 98.393850))
                        .add(new LatLng(7.872961, 98.393899))
                        .add(new LatLng(7.872375, 98.393902))
                        .add(new LatLng(7.872178, 98.393887))
                        .add(new LatLng(7.871641, 98.393899))
                        .add(new LatLng(7.871347, 98.393918))
                        .add(new LatLng(7.870429, 98.394110))
                        .add(new LatLng(7.869895, 98.394249))
                        .add(new LatLng(7.869637, 98.394367))
                        .add(new LatLng(7.869578, 98.394404))
                        .add(new LatLng(7.869425, 98.394556))
                        .add(new LatLng(7.869151, 98.395014))
                        .add(new LatLng(7.869031, 98.395157))
                        .add(new LatLng(7.868619, 98.395789))
                        .add(new LatLng(7.868636, 98.395892))
                        .add(new LatLng(7.868616, 98.395952))
                        .add(new LatLng(7.868543, 98.396087))
                        .add(new LatLng(7.868458, 98.396191))
                        .add(new LatLng(7.868390, 98.396241))
                        .add(new LatLng(7.868344, 98.396236))
                        .add(new LatLng(7.867963, 98.396919))
                        .add(new LatLng(7.867737, 98.397260))
                        .add(new LatLng(7.867613, 98.397394))
                        .add(new LatLng(7.867442, 98.397507))
                        .add(new LatLng(7.867215, 98.397603))
                        .add(new LatLng(7.866587, 98.397892))
                        .add(new LatLng(7.866496, 98.397936))
                        .add(new LatLng(7.866309, 98.398089))
                        .add(new LatLng(7.866063, 98.398373))
                        .add(new LatLng(7.865509, 98.399013))
                        .add(new LatLng(7.865312, 98.399230))
                        .add(new LatLng(7.864463, 98.400247))
                        .add(new LatLng(7.863974, 98.400775))
                        .add(new LatLng(7.863277, 98.401606))
                        .add(new LatLng(7.863241, 98.401640))
                        .add(new LatLng(7.863215, 98.401651))
                        .add(new LatLng(7.863168, 98.401649))
                        .add(new LatLng(7.863131, 98.401637))
                        .add(new LatLng(7.862839, 98.401451))
                        .add(new LatLng(7.862715, 98.401315))
                        .add(new LatLng(7.862718, 98.401241))
                        .add(new LatLng(7.862943, 98.400855))
                        .add(new LatLng(7.863319, 98.400239))
                        .add(new LatLng(7.863467, 98.400033))
                        .add(new LatLng(7.862629, 98.399302))
                        .add(new LatLng(7.863357, 98.398419))
                        .add(new LatLng(7.863831, 98.397793))
                        .add(new LatLng(7.864306, 98.397163))
                        .add(new LatLng(7.864681, 98.396661))
                        .add(new LatLng(7.865516, 98.395148))
                        .add(new LatLng(7.865881, 98.394406))
                        .add(new LatLng(7.865532, 98.394201))
                        .add(new LatLng(7.865928, 98.393388))
                        .add(new LatLng(7.866139, 98.393043))
                        .add(new LatLng(7.866272, 98.392921))
                        .add(new LatLng(7.867982, 98.392052))
                        .add(new LatLng(7.869351, 98.391407))
                        .add(new LatLng(7.869476, 98.391372))
                        .add(new LatLng(7.869506, 98.391888))
                        .add(new LatLng(7.869513, 98.392483))
                        .width(15).zIndex(10)
                                //.color(Color.rgb(0x23, 0x92, 0x99));
                        .color(Color.YELLOW);

                mMap.addPolyline(rectLine01);
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.896844, 98.368431)).title("Big C Supermarket"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.906094, 98.369024)).title("Tesco Lotus Phuket"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.898514, 98.383368)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.890437, 98.389899)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.889291, 98.386203)).title("Marker"));
                //mMap.addMarker(new MarkerOptions().position(new LatLng(7.886816, 98.374127)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.881771, 98.383985)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.883005, 98.387231)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.883353, 98.393679)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880165, 98.392313)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.878115, 98.393096)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.874051, 98.393885)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.871419, 98.393953)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.868661, 98.395767)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.863928, 98.400856)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.862845, 98.401488)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.863010, 98.398790)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.866506, 98.392760)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.869540, 98.392430)).title("Marker"));
                break;
            case 3:
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();
                //************** 2 **************//
                PolylineOptions rectLine02 = new PolylineOptions()
                        .add(new LatLng(7.921048, 98.395741))
                        .add(new LatLng(7.921732, 98.395901))
                        .add(new LatLng(7.922228, 98.395985))
                        .add(new LatLng(7.922599, 98.396003))
                        .add(new LatLng(7.922864, 98.395986))
                        .add(new LatLng(7.923167, 98.395955))
                        .add(new LatLng(7.923689, 98.395897))
                        .add(new LatLng(7.924816, 98.395762))
                        .add(new LatLng(7.925505, 98.395680))
                        .add(new LatLng(7.926381, 98.395585))
                        .add(new LatLng(7.927169, 98.395524))
                        .add(new LatLng(7.927514, 98.395514))
                        .add(new LatLng(7.928283, 98.395493))
                        .add(new LatLng(7.928535, 98.395487))
                        .add(new LatLng(7.928544, 98.395657))
                        .add(new LatLng(7.928336, 98.395660))
                        .add(new LatLng(7.927903, 98.395673))
                        .add(new LatLng(7.927577, 98.395685))
                        .add(new LatLng(7.926936, 98.395709))
                        .add(new LatLng(7.926592, 98.395734))
                        .add(new LatLng(7.926040, 98.395791))
                        .add(new LatLng(7.925617, 98.395831))
                        .add(new LatLng(7.925397, 98.395852))
                        .add(new LatLng(7.924708, 98.395935))
                        .add(new LatLng(7.924124, 98.396007))
                        .add(new LatLng(7.923827, 98.396051))
                        .add(new LatLng(7.922956, 98.396149))
                        .add(new LatLng(7.922623, 98.396169))
                        .add(new LatLng(7.922389, 98.396173))
                        .add(new LatLng(7.922172, 98.396160))
                        .add(new LatLng(7.921669, 98.396064))
                        .add(new LatLng(7.921484, 98.396027))
                        .add(new LatLng(7.920895, 98.395893))
                        .add(new LatLng(7.920419, 98.395730))
                        .add(new LatLng(7.919673, 98.395417))
                        .add(new LatLng(7.918626, 98.394977))
                        .add(new LatLng(7.916685, 98.394179))
                        .add(new LatLng(7.915502, 98.393778))
                        .add(new LatLng(7.915145, 98.393677))
                        .add(new LatLng(7.914642, 98.393564))
                        .add(new LatLng(7.913873, 98.393355))
                        .add(new LatLng(7.913720, 98.393322))
                        .add(new LatLng(7.913711, 98.393318))
                        .add(new LatLng(7.913201, 98.393186))
                        .add(new LatLng(7.912728, 98.393044))
                        .add(new LatLng(7.912086, 98.392893))
                        .add(new LatLng(7.911434, 98.392662))
                        .add(new LatLng(7.910469, 98.392259))
                        .add(new LatLng(7.909770, 98.391964))
                        .add(new LatLng(7.908714, 98.391550))
                        .add(new LatLng(7.908104, 98.391250))
                        .add(new LatLng(7.907475, 98.390847))
                        .add(new LatLng(7.907032, 98.390555))
                        .add(new LatLng(7.906109, 98.389947))
                        .add(new LatLng(7.905823, 98.389756))
                        .add(new LatLng(7.905222, 98.389372))
                        .add(new LatLng(7.904670, 98.389025))
                        .add(new LatLng(7.904311, 98.388803))
                        .add(new LatLng(7.904123, 98.388728))
                        .add(new LatLng(7.903723, 98.388629))
                        .add(new LatLng(7.903309, 98.388580))
                        .add(new LatLng(7.902964, 98.388509))
                        .add(new LatLng(7.902665, 98.388462))
                        .add(new LatLng(7.902504, 98.388450))
                        .add(new LatLng(7.901863, 98.388450))
                        .add(new LatLng(7.901521, 98.388486))
                        .add(new LatLng(7.901142, 98.388556))
                        .add(new LatLng(7.900824, 98.388625))
                        .add(new LatLng(7.900715, 98.388633))
                        .add(new LatLng(7.900417, 98.388711))
                        .add(new LatLng(7.899837, 98.388867))
                        .add(new LatLng(7.899553, 98.388947))
                        .add(new LatLng(7.899043, 98.389105))
                        .add(new LatLng(7.898850, 98.389165))
                        .add(new LatLng(7.898717, 98.389184))
                        .add(new LatLng(7.898596, 98.389203))
                        .add(new LatLng(7.898181, 98.389233))
                        .add(new LatLng(7.897775, 98.389256))
                        .add(new LatLng(7.897192, 98.389296))
                        .add(new LatLng(7.896350, 98.389332))
                        .add(new LatLng(7.895798, 98.389365))
                        .add(new LatLng(7.894404, 98.389492))
                        .add(new LatLng(7.894265, 98.389515))
                        .add(new LatLng(7.893103, 98.389649))
                        .add(new LatLng(7.892008, 98.389789))
                        .add(new LatLng(7.891935, 98.389235))
                        .add(new LatLng(7.891902, 98.388915))
                        .add(new LatLng(7.891725, 98.387345))
                        .add(new LatLng(7.891674, 98.386934))
                        .add(new LatLng(7.891555, 98.385865))
                        .add(new LatLng(7.891248, 98.385872))
                        .add(new LatLng(7.890808, 98.385901))
                        .add(new LatLng(7.890044, 98.385934))
                        .add(new LatLng(7.889919, 98.385948))
                        .add(new LatLng(7.889853, 98.385969))
                        .add(new LatLng(7.889665, 98.386043))
                        .add(new LatLng(7.889330, 98.386279))
                        .add(new LatLng(7.889750, 98.387754))
                        .add(new LatLng(7.889848, 98.388057))
                        .add(new LatLng(7.890052, 98.388672))
                        .add(new LatLng(7.890165, 98.389007))
                        .add(new LatLng(7.890354, 98.389606))
                        .add(new LatLng(7.890398, 98.389708))
                        .add(new LatLng(7.890487, 98.389969))
                        .add(new LatLng(7.890638, 98.390299))
                        .add(new LatLng(7.890670, 98.390552))
                        .add(new LatLng(7.890728, 98.390713))
                        .add(new LatLng(7.890969, 98.391448))
                        .add(new LatLng(7.891026, 98.391676))
                        .add(new LatLng(7.891063, 98.391902))
                        .add(new LatLng(7.891057, 98.391994))
                        .add(new LatLng(7.891031, 98.392113))
                        .add(new LatLng(7.890994, 98.392262))
                        .add(new LatLng(7.890840, 98.392687))
                        .add(new LatLng(7.890486, 98.393542))
                        .add(new LatLng(7.890431, 98.393770))
                        .add(new LatLng(7.890391, 98.394038))
                        .add(new LatLng(7.890386, 98.394152))
                        .add(new LatLng(7.890334, 98.394512))
                        .add(new LatLng(7.890249, 98.395263))
                        .add(new LatLng(7.890195, 98.395825))
                        .add(new LatLng(7.890182, 98.396043))
                        .add(new LatLng(7.890182, 98.396501))
                        .add(new LatLng(7.890232, 98.396945))
                        .add(new LatLng(7.890258, 98.397139))
                        .add(new LatLng(7.890314, 98.397404))
                        .add(new LatLng(7.890369, 98.397635))
                        .add(new LatLng(7.889988, 98.397748))
                        .add(new LatLng(7.889278, 98.397926))
                        .add(new LatLng(7.889035, 98.398003))
                        .add(new LatLng(7.888709, 98.397056))
                        .add(new LatLng(7.888187, 98.395617))
                        .add(new LatLng(7.887695, 98.394188))
                        .add(new LatLng(7.887575, 98.393940))
                        .add(new LatLng(7.887512, 98.393873))
                        .add(new LatLng(7.887286, 98.393488))
                        .add(new LatLng(7.887146, 98.393327))
                        .add(new LatLng(7.886895, 98.393285))
                        .add(new LatLng(7.886772, 98.393271))
                        .add(new LatLng(7.886410, 98.393221))
                        .add(new LatLng(7.885956, 98.393147))
                        .add(new LatLng(7.885313, 98.393040))
                        .add(new LatLng(7.884718, 98.392943))
                        .add(new LatLng(7.884457, 98.392910))
                        .add(new LatLng(7.884112, 98.392848))
                        .add(new LatLng(7.883444, 98.392745))
                        .add(new LatLng(7.883352, 98.393329))
                        .add(new LatLng(7.883315, 98.393626))
                        .add(new LatLng(7.883336, 98.393710))
                        .add(new LatLng(7.883300, 98.394031))
                        .add(new LatLng(7.883256, 98.394392))
                        .add(new LatLng(7.883214, 98.394880))
                        .add(new LatLng(7.883182, 98.395228))
                        .add(new LatLng(7.883095, 98.395228))
                        .add(new LatLng(7.882602, 98.395256))
                        .add(new LatLng(7.882422, 98.395250))
                        .add(new LatLng(7.881949, 98.395261))
                        .add(new LatLng(7.881577, 98.395316))
                        .add(new LatLng(7.881304, 98.395357))
                        .add(new LatLng(7.881054, 98.395371))
                        .add(new LatLng(7.880459, 98.395419))
                        .add(new LatLng(7.880131, 98.395443))
                        .add(new LatLng(7.879956, 98.395460))
                        .add(new LatLng(7.879946, 98.396092))
                        .add(new LatLng(7.879892, 98.396620))
                        .add(new LatLng(7.879841, 98.397178))
                        .add(new LatLng(7.879779, 98.397482))
                        .add(new LatLng(7.879547, 98.398040))
                        .add(new LatLng(7.879398, 98.398355))
                        .add(new LatLng(7.879397, 98.398380))
                        .add(new LatLng(7.879393, 98.398404))
                        .add(new LatLng(7.879357, 98.398510))
                        .add(new LatLng(7.879331, 98.398563))
                        .add(new LatLng(7.879293, 98.398611))
                        .add(new LatLng(7.879265, 98.398641))
                        .add(new LatLng(7.879221, 98.398671))
                        .add(new LatLng(7.879164, 98.398703))
                        .add(new LatLng(7.879102, 98.398724))
                        .add(new LatLng(7.879054, 98.398730))
                        .add(new LatLng(7.879008, 98.398732))
                        .add(new LatLng(7.878971, 98.398730))
                        .add(new LatLng(7.878914, 98.398717))
                        .add(new LatLng(7.878805, 98.398687))
                        .add(new LatLng(7.878748, 98.398654))
                        .add(new LatLng(7.878711, 98.398623))
                        .add(new LatLng(7.878668, 98.398575))
                        .add(new LatLng(7.878636, 98.398523))
                        .add(new LatLng(7.878601, 98.398372))
                        .add(new LatLng(7.878599, 98.398317))
                        .add(new LatLng(7.878601, 98.398282))
                        .add(new LatLng(7.878477, 98.398049))
                        .add(new LatLng(7.878397, 98.397852))
                        .add(new LatLng(7.878277, 98.397473))
                        .add(new LatLng(7.878077, 98.396952))
                        .add(new LatLng(7.878016, 98.396790))
                        .add(new LatLng(7.877863, 98.396423))
                        .add(new LatLng(7.877743, 98.396132))
                        .add(new LatLng(7.877621, 98.395711))
                        .add(new LatLng(7.877609, 98.395652))
                        .add(new LatLng(7.877620, 98.395562))
                        .add(new LatLng(7.877572, 98.395458))
                        .add(new LatLng(7.877397, 98.394977))
                        .add(new LatLng(7.877206, 98.394464))
                        .add(new LatLng(7.877153, 98.394306))
                        .add(new LatLng(7.876969, 98.393856))
                        .add(new LatLng(7.876834, 98.393541))
                        .add(new LatLng(7.876672, 98.393122))
                        .add(new LatLng(7.876544, 98.392744))
                        .add(new LatLng(7.876365, 98.392248))
                        .add(new LatLng(7.876231, 98.391928))
                        .add(new LatLng(7.876162, 98.391690))
                        .add(new LatLng(7.876091, 98.391304))
                        .add(new LatLng(7.875908, 98.390673))
                        .add(new LatLng(7.875832, 98.390295))
                        .add(new LatLng(7.875794, 98.390250))
                        .add(new LatLng(7.875728, 98.390130))
                        .add(new LatLng(7.875640, 98.389769))
                        .add(new LatLng(7.875590, 98.389552))
                        .add(new LatLng(7.875564, 98.389373))
                        .add(new LatLng(7.875564, 98.389199))
                        .add(new LatLng(7.875605, 98.389099))
                        .add(new LatLng(7.875666, 98.388984))
                        .add(new LatLng(7.875821, 98.388737))
                        .add(new LatLng(7.876131, 98.388379))
                        .add(new LatLng(7.876546, 98.387939))
                        .add(new LatLng(7.876868, 98.387596))
                        .add(new LatLng(7.877386, 98.387060))
                        .add(new LatLng(7.877850, 98.386544))
                        .add(new LatLng(7.878257, 98.386119))
                        .add(new LatLng(7.878715, 98.385623))
                        .add(new LatLng(7.879214, 98.385095))
                        .add(new LatLng(7.879231, 98.385074))
                        .add(new LatLng(7.879580, 98.384687))
                        .add(new LatLng(7.879570, 98.383387))
                        .add(new LatLng(7.879502, 98.382168))
                        .add(new LatLng(7.879504, 98.381752))
                        .add(new LatLng(7.879498, 98.381422))
                        .add(new LatLng(7.879537, 98.381301))
                        .add(new LatLng(7.879799, 98.380742))
                        .add(new LatLng(7.879954, 98.380373))
                        .add(new LatLng(7.880107, 98.379978))
                        .add(new LatLng(7.880343, 98.379299))
                        .add(new LatLng(7.880429, 98.378947))
                        .add(new LatLng(7.880447, 98.378819))
                        .add(new LatLng(7.880467, 98.378528))
                        .add(new LatLng(7.880468, 98.378035))
                        .add(new LatLng(7.880397, 98.377450))
                        .add(new LatLng(7.880333, 98.377004))
                        .add(new LatLng(7.880319, 98.376615))
                        .add(new LatLng(7.880369, 98.375813))
                        .add(new LatLng(7.880464, 98.374389))
                        .add(new LatLng(7.880484, 98.373958))
                        .add(new LatLng(7.880600, 98.373087))
                        .add(new LatLng(7.880685, 98.372208))
                        .add(new LatLng(7.880761, 98.371493))
                        .add(new LatLng(7.880774, 98.371278))
                        .add(new LatLng(7.880842, 98.370361))
                        .add(new LatLng(7.880897, 98.369503))
                        .add(new LatLng(7.880972, 98.368699))
                        .add(new LatLng(7.881002, 98.368391))
                        .add(new LatLng(7.881034, 98.367942))
                        .add(new LatLng(7.881099, 98.367260))
                        .add(new LatLng(7.881240, 98.365663))
                        .add(new LatLng(7.881332, 98.364500))
                        .add(new LatLng(7.881361, 98.364187))
                        .add(new LatLng(7.881156, 98.363870))
                        .add(new LatLng(7.880950, 98.363800))
                        .add(new LatLng(7.880639, 98.363751))
                        .add(new LatLng(7.879835, 98.363728))
                        .add(new LatLng(7.879449, 98.363720))
                        .add(new LatLng(7.878547, 98.363696))
                        .add(new LatLng(7.877114, 98.363642))
                        .add(new LatLng(7.876675, 98.363614))
                        .add(new LatLng(7.876538, 98.363613))
                        .add(new LatLng(7.875855, 98.363574))
                        .add(new LatLng(7.875451, 98.363549))
                        .add(new LatLng(7.874918, 98.363475))
                        .add(new LatLng(7.874809, 98.363469))
                        .add(new LatLng(7.874633, 98.363433))
                        .add(new LatLng(7.874262, 98.363362))
                        .add(new LatLng(7.874065, 98.363357))
                        .add(new LatLng(7.873484, 98.363422))
                        .add(new LatLng(7.873292, 98.363411))
                        .add(new LatLng(7.873116, 98.363379))
                        .add(new LatLng(7.873010, 98.363352))
                        .add(new LatLng(7.872924, 98.363317))
                        .add(new LatLng(7.872825, 98.363256))
                        .add(new LatLng(7.872722, 98.363171))
                        .add(new LatLng(7.872568, 98.362968))
                        .add(new LatLng(7.872272, 98.362501))
                        .add(new LatLng(7.871847, 98.361833))
                        .add(new LatLng(7.871621, 98.361418))
                        .add(new LatLng(7.871248, 98.360742))
                        .add(new LatLng(7.871177, 98.360606))
                        .add(new LatLng(7.870492, 98.359481))
                        .add(new LatLng(7.870296, 98.359159))
                        .add(new LatLng(7.870218, 98.359047))
                        .add(new LatLng(7.869966, 98.358719))
                        .add(new LatLng(7.869624, 98.358295))
                        .add(new LatLng(7.869285, 98.357917))
                        .add(new LatLng(7.868779, 98.357305))
                        .add(new LatLng(7.868439, 98.356869))
                        .add(new LatLng(7.868295, 98.356687))
                        .add(new LatLng(7.867723, 98.355855))
                        .add(new LatLng(7.867488, 98.355504))
                        .add(new LatLng(7.867290, 98.355204))
                        .add(new LatLng(7.867058, 98.354763))
                        .add(new LatLng(7.866943, 98.354395))
                        .add(new LatLng(7.866942, 98.354181))
                        .add(new LatLng(7.867000, 98.353660))
                        .add(new LatLng(7.867073, 98.353527))
                        .add(new LatLng(7.867200, 98.352528))
                        .add(new LatLng(7.867217, 98.352334))
                        .add(new LatLng(7.867036, 98.352302))
                        .add(new LatLng(7.866971, 98.352274))
                        .add(new LatLng(7.866945, 98.352250))
                        .add(new LatLng(7.866944, 98.352251))
                        .add(new LatLng(7.866861, 98.352150))
                        .add(new LatLng(7.866568, 98.351727))
                        .add(new LatLng(7.866300, 98.351410))
                        .add(new LatLng(7.865765, 98.350640))
                        .add(new LatLng(7.865629, 98.350508))
                        .add(new LatLng(7.865356, 98.350289))
                        .add(new LatLng(7.865107, 98.350105))
                        .add(new LatLng(7.864773, 98.349949))
                        .add(new LatLng(7.864452, 98.349815))
                        .add(new LatLng(7.864269, 98.349676))
                        .add(new LatLng(7.864099, 98.349543))
                        .add(new LatLng(7.863923, 98.349760))
                        .add(new LatLng(7.863612, 98.350170))
                        .add(new LatLng(7.863203, 98.350698))
                        .add(new LatLng(7.862910, 98.351076))
                        .add(new LatLng(7.862542, 98.351572))
                        .add(new LatLng(7.862176, 98.352074))
                        .add(new LatLng(7.861838, 98.352537))
                        .add(new LatLng(7.861608, 98.352837))
                        .add(new LatLng(7.861362, 98.353138))
                        .add(new LatLng(7.860791, 98.353844))
                        .add(new LatLng(7.860362, 98.354378))
                        .add(new LatLng(7.859490, 98.355508))
                        .add(new LatLng(7.859423, 98.355555))
                        .add(new LatLng(7.859075, 98.356021))
                        .add(new LatLng(7.858991, 98.356134))
                        .add(new LatLng(7.858924, 98.356214))
                        .add(new LatLng(7.858602, 98.356631))
                        .add(new LatLng(7.858322, 98.356997))
                        .add(new LatLng(7.857726, 98.357755))
                        .add(new LatLng(7.857686, 98.357809))
                        .add(new LatLng(7.857120, 98.358539))
                        .add(new LatLng(7.856972, 98.358729))
                        .add(new LatLng(7.856706, 98.359073))
                        .add(new LatLng(7.856996, 98.359436))
                        .add(new LatLng(7.857151, 98.359643))
                        .add(new LatLng(7.857187, 98.359773))
                        .add(new LatLng(7.857263, 98.360014))
                        .add(new LatLng(7.857344, 98.360314))
                        .add(new LatLng(7.857452, 98.360768))
                        .add(new LatLng(7.857559, 98.361207))
                        .add(new LatLng(7.857705, 98.361801))
                        .add(new LatLng(7.857841, 98.362296))
                        .add(new LatLng(7.857894, 98.362518))
                        .add(new LatLng(7.857935, 98.362730))
                        .add(new LatLng(7.857961, 98.362913))
                        .add(new LatLng(7.857974, 98.363031))
                        .width(15).zIndex(10)
                                //.color(Color.rgb(0x23, 0x92, 0x99));
                        .color(Color.CYAN);

                mMap.addPolyline(rectLine02);
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.921122, 98.395734)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.927096, 98.395491)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.913441, 98.393301)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.907697, 98.391021)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.899765, 98.388914)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.891908, 98.389219)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.889516, 98.386817)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.890907, 98.391171)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.890054, 98.397753)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.883343, 98.393613)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880042, 98.395478)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.877530, 98.395420)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.878866, 98.385421)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880441, 98.378365)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.880916, 98.368737)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.870214, 98.359144)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.861417, 98.353152)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.857987, 98.362925)).title("Marker"));
                break;
            case 4:
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                mMap.clear();
                //************** 3 **************//
                PolylineOptions rectLine03 = new PolylineOptions()
                        .add(new LatLng(7.868143, 98.396371))
                        .add(new LatLng(7.868213, 98.396243))
                        .add(new LatLng(7.868263, 98.396184))
                        .add(new LatLng(7.868235, 98.396136))
                        .add(new LatLng(7.868222, 98.396077))
                        .add(new LatLng(7.868310, 98.395927))
                        .add(new LatLng(7.867591, 98.395462))
                        .add(new LatLng(7.867024, 98.395111))
                        .add(new LatLng(7.865884, 98.394406))
                        .add(new LatLng(7.865530, 98.394208))
                        .add(new LatLng(7.865925, 98.393400))
                        .add(new LatLng(7.866127, 98.393057))
                        .add(new LatLng(7.866266, 98.392926))
                        .add(new LatLng(7.866467, 98.392807))
                        .add(new LatLng(7.867971, 98.392066))
                        .add(new LatLng(7.868801, 98.391654))
                        .add(new LatLng(7.869363, 98.391406))
                        .add(new LatLng(7.869709, 98.391299))
                        .add(new LatLng(7.869807, 98.391246))
                        .add(new LatLng(7.870065, 98.391041))
                        .add(new LatLng(7.870716, 98.390719))
                        .add(new LatLng(7.871549, 98.390292))
                        .add(new LatLng(7.871954, 98.390097))
                        .add(new LatLng(7.872071, 98.390054))
                        .add(new LatLng(7.872135, 98.390041))
                        .add(new LatLng(7.872234, 98.390032))
                        .add(new LatLng(7.872334, 98.390028))
                        .add(new LatLng(7.872442, 98.390051))
                        .add(new LatLng(7.872925, 98.390232))
                        .add(new LatLng(7.873090, 98.390280))
                        .add(new LatLng(7.873194, 98.390303))
                        .add(new LatLng(7.873473, 98.390330))
                        .add(new LatLng(7.873606, 98.390322))
                        .add(new LatLng(7.873741, 98.390288))
                        .add(new LatLng(7.873882, 98.390225))
                        .add(new LatLng(7.874498, 98.389826))
                        .add(new LatLng(7.874670, 98.389737))
                        .add(new LatLng(7.875088, 98.389598))
                        .add(new LatLng(7.875446, 98.389284))
                        .add(new LatLng(7.875564, 98.389197))
                        .add(new LatLng(7.875687, 98.388951))
                        .add(new LatLng(7.875816, 98.388742))
                        .add(new LatLng(7.876685, 98.387795))
                        .add(new LatLng(7.877400, 98.387041))
                        .add(new LatLng(7.878256, 98.386123))
                        .add(new LatLng(7.878865, 98.385464))
                        .add(new LatLng(7.879333, 98.384969))
                        .add(new LatLng(7.879390, 98.384904))
                        .add(new LatLng(7.879446, 98.384971))
                        .add(new LatLng(7.879295, 98.385160))
                        .add(new LatLng(7.878476, 98.386027))
                        .add(new LatLng(7.877922, 98.386631))
                        .add(new LatLng(7.877462, 98.387133))
                        .add(new LatLng(7.876923, 98.387671))
                        .add(new LatLng(7.876747, 98.387853))
                        .add(new LatLng(7.876586, 98.388039))
                        .add(new LatLng(7.876276, 98.388346))
                        .add(new LatLng(7.876002, 98.388631))
                        .add(new LatLng(7.875894, 98.388777))
                        .add(new LatLng(7.875781, 98.388980))
                        .add(new LatLng(7.875728, 98.389049))
                        .add(new LatLng(7.875699, 98.389129))
                        .add(new LatLng(7.875679, 98.389223))
                        .add(new LatLng(7.875685, 98.389379))
                        .add(new LatLng(7.875777, 98.389851))
                        .add(new LatLng(7.876065, 98.389967))
                        .add(new LatLng(7.876198, 98.389992))
                        .add(new LatLng(7.876301, 98.390005))
                        .add(new LatLng(7.876475, 98.389998))
                        .add(new LatLng(7.876586, 98.389961))
                        .add(new LatLng(7.876724, 98.389890))
                        .add(new LatLng(7.876905, 98.389775))
                        .add(new LatLng(7.877179, 98.389627))
                        .add(new LatLng(7.877376, 98.389540))
                        .add(new LatLng(7.877847, 98.389284))
                        .add(new LatLng(7.878023, 98.389184))
                        .add(new LatLng(7.878210, 98.389057))
                        .add(new LatLng(7.878524, 98.388770))
                        .add(new LatLng(7.878628, 98.388650))
                        .add(new LatLng(7.878710, 98.388574))
                        .add(new LatLng(7.878953, 98.388569))
                        .add(new LatLng(7.879250, 98.388611))
                        .add(new LatLng(7.879627, 98.388679))
                        .add(new LatLng(7.879847, 98.388683))
                        .add(new LatLng(7.880417, 98.388627))
                        .add(new LatLng(7.881411, 98.388518))
                        .add(new LatLng(7.882453, 98.388405))
                        .add(new LatLng(7.882719, 98.388379))
                        .add(new LatLng(7.883114, 98.388356))
                        .add(new LatLng(7.883181, 98.387824))
                        .add(new LatLng(7.883155, 98.387402))
                        .add(new LatLng(7.883125, 98.387397))
                        .add(new LatLng(7.883106, 98.387384))
                        .add(new LatLng(7.883088, 98.387371))
                        .add(new LatLng(7.883064, 98.387345))
                        .add(new LatLng(7.883049, 98.387307))
                        .add(new LatLng(7.883048, 98.387254))
                        .add(new LatLng(7.883059, 98.387225))
                        .add(new LatLng(7.883079, 98.387192))
                        .add(new LatLng(7.883126, 98.387160))
                        .add(new LatLng(7.883161, 98.387153))
                        .add(new LatLng(7.883201, 98.387156))
                        .add(new LatLng(7.883248, 98.387180))
                        .add(new LatLng(7.883279, 98.387213))
                        .add(new LatLng(7.883291, 98.387242))
                        .add(new LatLng(7.883296, 98.387265))
                        .add(new LatLng(7.883298, 98.387289))
                        .add(new LatLng(7.883874, 98.387367))
                        .add(new LatLng(7.884597, 98.387466))
                        .add(new LatLng(7.885318, 98.387553))
                        .add(new LatLng(7.886305, 98.387650))
                        .add(new LatLng(7.887080, 98.387725))
                        .add(new LatLng(7.887294, 98.387658))
                        .add(new LatLng(7.887374, 98.387618))
                        .add(new LatLng(7.887517, 98.387510))
                        .add(new LatLng(7.888336, 98.386961))
                        .add(new LatLng(7.889059, 98.386467))
                        .add(new LatLng(7.889328, 98.386280))
                        .add(new LatLng(7.889548, 98.387063))
                        .add(new LatLng(7.889755, 98.387770))
                        .add(new LatLng(7.890167, 98.389011))
                        .add(new LatLng(7.890356, 98.389608))
                        .add(new LatLng(7.890491, 98.389977))
                        .add(new LatLng(7.890641, 98.390299))
                        .add(new LatLng(7.890671, 98.390552))
                        .add(new LatLng(7.890737, 98.390729))
                        .add(new LatLng(7.890861, 98.391116))
                        .add(new LatLng(7.891030, 98.391665))
                        .add(new LatLng(7.891067, 98.391904))
                        .add(new LatLng(7.891057, 98.391996))
                        .add(new LatLng(7.890994, 98.392258))
                        .add(new LatLng(7.890875, 98.392607))
                        .add(new LatLng(7.890486, 98.393544))
                        .add(new LatLng(7.890448, 98.393665))
                        .add(new LatLng(7.890428, 98.393759))
                        .add(new LatLng(7.890190, 98.393705))
                        .add(new LatLng(7.889709, 98.393639))
                        .add(new LatLng(7.888507, 98.393476))
                        .add(new LatLng(7.887852, 98.393395))
                        .add(new LatLng(7.887147, 98.393329))
                        .add(new LatLng(7.885960, 98.393144))
                        .add(new LatLng(7.885326, 98.393042))
                        .add(new LatLng(7.884717, 98.392941))
                        .add(new LatLng(7.884455, 98.392910))
                        .add(new LatLng(7.883755, 98.392793))
                        .add(new LatLng(7.883456, 98.392746))
                        .add(new LatLng(7.883354, 98.393328))
                        .add(new LatLng(7.883315, 98.393624))
                        .add(new LatLng(7.883330, 98.393716))
                        .add(new LatLng(7.883285, 98.394167))
                        .add(new LatLng(7.883257, 98.394393))
                        .add(new LatLng(7.883182, 98.395234))
                        .add(new LatLng(7.883100, 98.395938))
                        .add(new LatLng(7.883076, 98.396316))
                        .add(new LatLng(7.883025, 98.396809))
                        .add(new LatLng(7.882937, 98.397794))
                        .add(new LatLng(7.882866, 98.398647))
                        .add(new LatLng(7.882782, 98.399533))
                        .add(new LatLng(7.882743, 98.399947))
                        .add(new LatLng(7.882272, 98.400085))
                        .add(new LatLng(7.882056, 98.400187))
                        .add(new LatLng(7.881900, 98.400208))
                        .add(new LatLng(7.881402, 98.400370))
                        .add(new LatLng(7.881258, 98.400522))
                        .add(new LatLng(7.881216, 98.400589))
                        .add(new LatLng(7.881190, 98.400650))
                        .add(new LatLng(7.881179, 98.400699))
                        .add(new LatLng(7.881175, 98.400789))
                        .add(new LatLng(7.881202, 98.400994))
                        .add(new LatLng(7.881411, 98.402081))
                        .add(new LatLng(7.881529, 98.402646))
                        .add(new LatLng(7.881597, 98.402840))
                        .add(new LatLng(7.881660, 98.402924))
                        .add(new LatLng(7.881735, 98.403000))
                        .add(new LatLng(7.882001, 98.403138))
                        .add(new LatLng(7.882216, 98.403337))
                        .add(new LatLng(7.882294, 98.403461))
                        .add(new LatLng(7.882351, 98.403621))
                        .add(new LatLng(7.882444, 98.404270))
                        .add(new LatLng(7.882541, 98.404890))
                        .add(new LatLng(7.882592, 98.405153))
                        .add(new LatLng(7.882590, 98.405210))
                        .add(new LatLng(7.882590, 98.405217))
                        .add(new LatLng(7.882567, 98.405290))
                        .add(new LatLng(7.882109, 98.406633))
                        .add(new LatLng(7.882023, 98.406861))
                        .add(new LatLng(7.881732, 98.407653))
                        .add(new LatLng(7.881333, 98.408761))
                        .add(new LatLng(7.881064, 98.409508))
                        .add(new LatLng(7.880898, 98.410047))
                        .add(new LatLng(7.880787, 98.410541))
                        .add(new LatLng(7.880784, 98.410682))
                        .add(new LatLng(7.881035, 98.410711))
                        .add(new LatLng(7.881311, 98.410759))
                        .add(new LatLng(7.881483, 98.410784))
                        .add(new LatLng(7.881648, 98.410821))
                        .add(new LatLng(7.881814, 98.410830))
                        .add(new LatLng(7.882210, 98.410785))
                        .add(new LatLng(7.882641, 98.410723))
                        .add(new LatLng(7.883043, 98.410655))
                        .add(new LatLng(7.883443, 98.410605))
                        .add(new LatLng(7.883872, 98.410548))
                        .add(new LatLng(7.884517, 98.410477))
                        .add(new LatLng(7.885108, 98.410395))
                        .add(new LatLng(7.885227, 98.411155))
                        .add(new LatLng(7.885409, 98.412205))
                        .add(new LatLng(7.885506, 98.412637))
                        .add(new LatLng(7.885604, 98.413203))
                        .add(new LatLng(7.885665, 98.413264))
                        .add(new LatLng(7.885782, 98.413819))
                        .add(new LatLng(7.885872, 98.414327))
                        .add(new LatLng(7.885884, 98.414468))
                        .add(new LatLng(7.885881, 98.414792))
                        .add(new LatLng(7.885893, 98.415079))
                        .add(new LatLng(7.885870, 98.415911))
                        .add(new LatLng(7.886018, 98.418357))
                        .add(new LatLng(7.886047, 98.418971))
                        .add(new LatLng(7.886052, 98.419528))
                        .add(new LatLng(7.885930, 98.420266))
                        .add(new LatLng(7.885914, 98.420587))
                        .add(new LatLng(7.885983, 98.421279))
                        .add(new LatLng(7.886014, 98.421723))
                        .add(new LatLng(7.886026, 98.422055))
                        .add(new LatLng(7.886056, 98.423378))
                        .add(new LatLng(7.886096, 98.424017))
                        .add(new LatLng(7.886138, 98.424342))
                        .add(new LatLng(7.886271, 98.424734))
                        .add(new LatLng(7.886564, 98.425285))
                        .add(new LatLng(7.886632, 98.425448))
                        .add(new LatLng(7.886661, 98.425607))
                        .add(new LatLng(7.886667, 98.425814))
                        .add(new LatLng(7.886630, 98.426226))
                        .add(new LatLng(7.886574, 98.426498))
                        .add(new LatLng(7.886562, 98.426500))
                        .add(new LatLng(7.886549, 98.426504))
                        .add(new LatLng(7.886535, 98.426510))
                        .add(new LatLng(7.886524, 98.426520))
                        .add(new LatLng(7.886518, 98.426539))
                        .add(new LatLng(7.886513, 98.426558))
                        .add(new LatLng(7.886521, 98.426580))
                        .add(new LatLng(7.886377, 98.426655))
                        .add(new LatLng(7.886244, 98.426718))
                        .add(new LatLng(7.886146, 98.426759))
                        .add(new LatLng(7.886011, 98.426797))
                        .add(new LatLng(7.885854, 98.426832))
                        .add(new LatLng(7.885139, 98.426937))
                        .add(new LatLng(7.884948, 98.426972))
                        .add(new LatLng(7.884559, 98.427004))
                        .add(new LatLng(7.884355, 98.426996))
                        .add(new LatLng(7.884163, 98.426959))
                        .add(new LatLng(7.883950, 98.426902))
                        .add(new LatLng(7.883641, 98.426809))
                        .add(new LatLng(7.883514, 98.426784))
                        .add(new LatLng(7.883409, 98.426778))
                        .add(new LatLng(7.882533, 98.427089))
                        .add(new LatLng(7.882400, 98.427159))
                        .add(new LatLng(7.882264, 98.427269))
                        .add(new LatLng(7.882072, 98.427478))
                        .add(new LatLng(7.881894, 98.427646))
                        .add(new LatLng(7.881262, 98.428067))
                        .add(new LatLng(7.880836, 98.428438))
                        .add(new LatLng(7.880064, 98.428944))
                        .add(new LatLng(7.879560, 98.429127))
                        .add(new LatLng(7.879313, 98.429174))
                        .add(new LatLng(7.877977, 98.429171))
                        .add(new LatLng(7.877014, 98.429109))
                        .add(new LatLng(7.876208, 98.429066))
                        .add(new LatLng(7.875814, 98.429010))
                        .add(new LatLng(7.875302, 98.428865))
                        .add(new LatLng(7.874768, 98.428718))
                        .add(new LatLng(7.874699, 98.428697))
                        .add(new LatLng(7.874616, 98.428698))
                        .add(new LatLng(7.874465, 98.428752))
                        .add(new LatLng(7.873983, 98.429032))
                        .add(new LatLng(7.873986, 98.429064))
                        .add(new LatLng(7.873975, 98.429114))
                        .add(new LatLng(7.873953, 98.429165))
                        .add(new LatLng(7.873935, 98.429243))
                        .add(new LatLng(7.873938, 98.429285))
                        .add(new LatLng(7.873948, 98.429322))
                        .add(new LatLng(7.873982, 98.429398))
                        .add(new LatLng(7.874083, 98.429661))
                        .add(new LatLng(7.874095, 98.429723))
                        .add(new LatLng(7.874083, 98.429791))
                        .add(new LatLng(7.874068, 98.429817))
                        .add(new LatLng(7.873844, 98.429940))
                        .add(new LatLng(7.873685, 98.430065))
                        .add(new LatLng(7.873625, 98.430170))
                        .add(new LatLng(7.873622, 98.430311))
                        .add(new LatLng(7.873669, 98.430549))
                        .add(new LatLng(7.873700, 98.430799))
                        .add(new LatLng(7.873683, 98.430915))
                        .add(new LatLng(7.873664, 98.430972))
                        .add(new LatLng(7.873582, 98.431085))
                        .add(new LatLng(7.873474, 98.431127))
                        .add(new LatLng(7.873420, 98.431127))
                        .add(new LatLng(7.873155, 98.431076))
                        .add(new LatLng(7.873057, 98.431067))
                        .add(new LatLng(7.872971, 98.431091))
                        .add(new LatLng(7.872853, 98.431142))
                        .add(new LatLng(7.872748, 98.431216))
                        .add(new LatLng(7.872684, 98.431296))
                        .add(new LatLng(7.872619, 98.431418))
                        .add(new LatLng(7.872545, 98.431618))
                        .add(new LatLng(7.872511, 98.431773))
                        .add(new LatLng(7.872444, 98.431931))
                        .add(new LatLng(7.872354, 98.432081))
                        .add(new LatLng(7.872274, 98.432166))
                        .add(new LatLng(7.872127, 98.432292))
                        .add(new LatLng(7.872060, 98.432314))
                        .add(new LatLng(7.871753, 98.432367))
                        .add(new LatLng(7.871070, 98.432553))
                        .add(new LatLng(7.870989, 98.432589))
                        .add(new LatLng(7.870947, 98.432597))
                        .add(new LatLng(7.870919, 98.432577))
                        .width(15).zIndex(10)
                                //.color(Color.rgb(0x23, 0x92, 0x99));
                        .color(Color.GREEN);

                mMap.addPolyline(rectLine03);
                //mMap.addMarker(new MarkerOptions().position(new LatLng(7.862926, 98.401545)).title("Marker"));
                //mMap.addMarker(new MarkerOptions().position(new LatLng(7.864453, 98.396915)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.866532, 98.392749)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.870661, 98.390715)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.879085, 98.385419)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.883138, 98.387520)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.887053, 98.387697)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.890897, 98.391148)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.885744, 98.393161)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.883350, 98.393670)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.882823, 98.399380)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.885237, 98.411373)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.884131, 98.426926)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(7.870947, 98.432618)).title("Marker"));
                //new ServiceTask().execute();
                break;
            case 5:
                new ServiceTask().execute();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //*****service background
    private class ServiceTask extends AsyncTask<Void, Void, SoapObject>{

        SoapObject result;
        @Override
        protected SoapObject doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, xMETHOD_NAME);
            request.addProperty("name", "");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {

                androidHttpTransport.call(xSOAP_ACTION, envelope);
                result = (SoapObject) envelope.bodyIn;
                /*
                if (result != null) {
                    String[] latlng = result.getProperty(0).toString().split(",");
                    //txtResult.setText(latlng[0].toString() + "     d" + Double.parseDouble(latlng[1].toString()));
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]))).title("BUZZ"));
                    Toast.makeText(getApplicationContext(), "Latitude " + latlng[0].toString() + "  Longtitude " + latlng[1].toString(),
                                    Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Web Service not Response!", Toast.LENGTH_LONG)
                            .show();
                }
                */
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            try {

                if (result != null) {
                    String[] latlng = result.getProperty(0).toString().split(",");
                    //txtResult.setText(latlng[0].toString() + "     d" + Double.parseDouble(latlng[1].toString()));
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]))).title("BUZZ"));
                    Toast.makeText(getApplicationContext(), "Latitude " + latlng[0].toString() + "  Longtitude " + latlng[1].toString(),
                            Toast.LENGTH_LONG).show();
                    //*** Marker
                    String toolTip = "Latitude " + latlng[0].toString() + "  Longtitude " + latlng[1].toString();
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latlng[0].toString()), Double.parseDouble(latlng[1].toString()))).title(toolTip);
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.car5));
                    mMap.addMarker(marker);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Web Service not Response!", Toast.LENGTH_LONG)
                            .show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
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

}
