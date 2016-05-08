package s5630213012.pendual;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by plzpeace on 30/3/2559.
 */
public class WebService {
    private final String NAMESPACE = "http://192.168.43.92/nusoap/WebServiceServer.php";
    private final String URL = "http://192.168.43.92/nusoap/WebServiceServer.php?wsdl"; // WSDL URL
    private final String SOAP_ACTION = "http://192.168.43.92/nusoap/WebServiceServer.php/HelloWorld";
    private final String METHOD_NAME = "HelloWorld"; // Method on web service
    private final String xSOAP_ACTION = "http://192.168.43.92/nusoap/WebServiceServer.php/HiHi";
    private final String xMETHOD_NAME = "HiHi"; // Method on web service
    private double latitude;
    private double longitude;
    private String toolTip;

    ServiceTask st;

    public double getLatitude() {

        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void callService(){
        new ServiceTask().execute();
    }

    public String getToolTip() {
        return toolTip;
    }

    //*****service background
    private class ServiceTask extends AsyncTask<Void, Void, SoapObject> {

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
                    //Toast.makeText(getApplicationContext(), "Latitude " + latlng[0].toString() + "  Longtitude " + latlng[1].toString(),
                    //        Toast.LENGTH_LONG).show();

                    latitude = Double.parseDouble(latlng[0].toString());
                    longitude = Double.parseDouble(latlng[1].toString());
                    //*** Marker
                    toolTip = "Latitude " + latlng[0].toString() + "  Longtitude " + latlng[1].toString();
                    //MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latlng[0].toString()), Double.parseDouble(latlng[1].toString()))).title(toolTip);
                    //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.car5));
                    //mMap.addMarker(marker);
                } else {
                    //Toast.makeText(getApplicationContext(),
                    //        "Web Service not Response!", Toast.LENGTH_LONG)
                    //        .show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
