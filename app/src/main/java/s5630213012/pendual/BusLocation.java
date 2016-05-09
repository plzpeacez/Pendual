package s5630213012.pendual;

import android.os.AsyncTask;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by plzpeace on 9/5/2559.
 */
public class BusLocation {
    private static final String LOG_TAG = "JsOn ErRoR";
    private final String NAMESPACE = "http://192.168.43.92/nusoap/test2.php";
    private final String URL = "http://192.168.43.92/nusoap/test2.php?wsdl"; // WSDL URL
    private final String SOAP_ACTION = "http://192.168.43.92/nusoap/test2.php/Cadenza";
    private final String METHOD_NAME = "Cadenza"; // Method on web service
    private String data = null;

    public void callServ() {
        new ServiceTask().execute();
    }

    public String getData() {
        return data;
    }

    private class ServiceTask extends AsyncTask<Void, Void, SoapObject> {

        SoapObject result;
        @Override
        protected SoapObject doInBackground(Void... params) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            //sdf.format(cal.getTime())
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("strCountry", sdf.format(cal.getTime()));

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {

                androidHttpTransport.call(SOAP_ACTION, envelope);
                result = (SoapObject) envelope.bodyIn;
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
                    data = result.getProperty(0).toString();
                } else {

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
