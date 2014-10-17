package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Calendar;

public class GsmService extends Service {
    LocationManager GPSmgr, GSMmgr;
    double GSMLat, GSMLong;
    float GSMAccuracy;

    public GsmService() {
        Context mContext;
        GPSmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GSMmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GSMmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                GSMlistener);
        Calendar c = Calendar.getInstance();
        String filename = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-"
                + c.get(Calendar.DATE) + "--" + c.get(Calendar.HOUR) + ":"
                + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND)
                + ".csv";
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    LocationListener GSMlistener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            GSMLat = location.getLatitude();
            GSMLong = location.getLongitude();
            GSMAccuracy = location.getAccuracy();
        }
    };

/*
    LocationListener GPSlistener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            GPSLat = location.getLatitude();
            GPSLong = location.getLongitude();
            GPSAccuracy = location.getAccuracy();
        }
    };

    Thread background = new Thread() {
        @Override
        public void run() {
            t = new Time();
            while (flag == 1) {
                try {
                    t.setToNow();
                    tt = t.monthDay + "/" + (t.month + 1) + "/" + t.year + " "
                            + t.hour + ":" + t.minute + ":" + t.second;
                    Thread.sleep(1000);
                    GsmCellLocation loc = (GsmCellLocation) tm
                            .getCellLocation();
                    cid = String.valueOf(loc.getCid() & 0xffff);
                    lac = String.valueOf(loc.getLac());
                    mccmnc = tm.getNetworkOperator();
                    operator = tm.getNetworkOperatorName();
                    info = tm.getNeighboringCellInfo();
                    int length = info.size();
                    while (length > 0) {
                        neighinfo += String.valueOf(info.get(length - 1)
                                .getCid())
                                + ":"
                                + String.valueOf(info.get(length - 1).getLac())
                                + ":"
                                + String.valueOf(-113 + 2
                                * info.get(length - 1).getRssi()) + "/";
                        length--;
                    }
                    db.insertData(GPSLat, GPSLong, GPSAccuracy, GSMLat,
                            GSMLong, GSMAccuracy, cid, lac, rssi, mccmnc,
                            operator, neighinfo);
                    csv += GPSLat + "," + GPSLong + "," + GPSAccuracy + ","
                            + GSMLat + "," + GSMLong + "," + GSMAccuracy + ","
                            + cid + "," + lac + "," + rssi + "," + mccmnc + ","
                            + operator + "," + neighinfo + "," + tt + "\n";
                    neighinfo = "";
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            txtdata.setText(GPSLat + ":" + GPSLong + ":"
                                    + GPSAccuracy + "/" + GSMLat + ":"
                                    + GSMLong + ":" + GSMAccuracy + "/" + cid
                                    + ":" + rssi + "/");
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    Thread save = new Thread() {
        @Override
        public void run() {
            int length = 1;
            // Toast.makeText(getBaseContext(), dataed,
            // Toast.LENGTH_SHORT).show();
            while (length > 0) {
                try {
                    List<TrackModel> trackPath = db.getTrackData();
                    length = trackPath.size();
                    writeToFile(dataed);
                    writeToFile(csv);
                    db.deleteData();
                    csv = "";
                    dataed = "";
                } catch (Exception ex) {
                }
            }
        }
    };

    Thread sync = new Thread() {
        @Override
        public void run() {
            int length = 1;
            while (length > 0) {
                try {
                    List<TrackModel> trackPath = db.getTrackData();
                    // Send Arraylist to remote server
                    // String url = SERVER_IP+"tracker/insertArray.php";
                    String url = SERVER_IP + "insertDataArray.php";
                    length = trackPath.size();
                    if (length > 0) {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(url);
                        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
                        for (int i = 0; i < length; i++) {
                            param.add(new BasicNameValuePair(
                                    "GPSlatitude[]",
                                    Double.toString(trackPath.get(i).GPSlatitude)));
                            param.add(new BasicNameValuePair(
                                    "GPSlongitude[]",
                                    Double.toString(trackPath.get(i).GPSlongitude)));
                            param.add(new BasicNameValuePair(
                                    "GPSAccuracy[]",
                                    Float.toString(trackPath.get(i).GPSAccuracy)));
                            param.add(new BasicNameValuePair(
                                    "GSMlatitude[]",
                                    Double.toString(trackPath.get(i).GSMlatitude)));
                            param.add(new BasicNameValuePair(
                                    "GSMlongitude[]",
                                    Double.toString(trackPath.get(i).GSMlongitude)));
                            param.add(new BasicNameValuePair(
                                    "GSMAccuracy[]",
                                    Float.toString(trackPath.get(i).GSMAccuracy)));
                            param.add(new BasicNameValuePair("cid[]", trackPath
                                    .get(i).cid));
                            param.add(new BasicNameValuePair("lac[]", trackPath
                                    .get(i).lac));
                            param.add(new BasicNameValuePair("rssi[]",
                                    trackPath.get(i).rssi));
                            param.add(new BasicNameValuePair("mccmnc[]",
                                    trackPath.get(i).mccmnc));
                            param.add(new BasicNameValuePair("operator[]",
                                    trackPath.get(i).operator));
                            param.add(new BasicNameValuePair("neighinfo[]",
                                    trackPath.get(i).neighinfo));
                            param.add(new BasicNameValuePair("created_at[]",
                                    trackPath.get(i).created_at));
                        }
                        param.add(new BasicNameValuePair("number", "1234567890"));
                        param.add(new BasicNameValuePair("count", Integer
                                .toString(length)));

                        httpPost.setEntity(new UrlEncodedFormEntity(param));
                        HttpResponse httpResponse = httpClient
                                .execute(httpPost);

                        // get back acknowledgement
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(httpResponse
                                            .getEntity().getContent()));
                            StringBuffer sb = new StringBuffer();
                            String line;
                            while ((line = in.readLine()) != null)
                                sb.append(line);
                            in.close();
                            String reply = sb.toString();
                            Log.i("Response", reply);
                            if (reply.equals("success")) {
                                // Delete from local db.
                                db.deleteData();
                            }
                        }
                    }
                    // Thread.sleep(5000);
                } catch (Exception ex) {
                }
            }
        }
    };

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int val = -113 + 2 * signalStrength.getGsmSignalStrength();
            rssi = String.valueOf(val);
        }
    };

    public boolean writeToFile(String data) {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "GPS_GSM_Logs" + File.separator + "Logs");
            file.mkdirs();
            File f = new File(file, filename);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter out = new BufferedWriter(fw);
            out.append(data);
            out.close();
            return true;
        } catch (FileNotFoundException f) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
*/
}
