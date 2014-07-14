package farhani.apps.sensor.farhani;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.mtn.entity.Accelerometer;
import com.mtn.entity.GpsLocation;
import com.mtn.entity.MagneticField;
import com.mtn.entity.Orientation;
import com.mtn.messages.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FarahaniActivity extends Activity implements SensorEventListener {

    TextView show;
    SensorManager sm;

    LocationManager location;
    LocationListener ls;

    Timer timer;
    TimerTask timerTask;

//    Orientation lastOrientation;
//    GpsLocation lastGpsLocation;
//    MagneticField lastMagneticField;
//    Accelerometer lastAccelerometer;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button acc = (Button) findViewById(R.id.btnAcce);
        Button mag = (Button) findViewById(R.id.btnMag);
        Button tilt = (Button) findViewById(R.id.btnTilt);
        Button gps = (Button) findViewById(R.id.btnGPS);
        show = (TextView) findViewById(R.id.txtShow);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

//        acc.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_ORIENTATION));
        sm.registerListener(FarahaniActivity.this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

//            }
//        });
//        mag.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_ORIENTATION));
        sm.registerListener(FarahaniActivity.this,
                sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

//            }
//        });
//        tilt.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sm.registerListener(FarahaniActivity.this,
                sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

//            }
//        });

//        gps.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
//                sm.unregisterListener(FarahaniActivity.this,
//                        sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ls = new Myloc();
        location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ls);
//            }
//        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendMessages();
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 2000, 2000);


    }//end Oncrearet


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    private void sendMessages() {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        try {
            // Create the socket
            Socket clientSocket = new Socket("192.168.14.112", 21211);
            // Create the input & output streams to the server
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
//            ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());

            // Read modify
            // TODO here

        /* Send the Message Object to the server */
            outToServer.writeObject(messages);
            messages.clear();

            clientSocket.close();

        } catch (Exception e) {
            System.err.println("Client Error: " + e.getMessage());
            System.err.println("Localized: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent arg0) {
        SensorMessage message = null;
        if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            Accelerometer lastAccelerometer = new Accelerometer(arg0.values[0], arg0.values[1], arg0.values[2]);
            message = new AccelerometerMessage(lastAccelerometer, System.currentTimeMillis());

//            show.setText("Acce\nX: " + arg0.values[0] +
//                    "\nY:" + arg0.values[1] +
//                    "\nZ: " + arg0.values[2]);
        } else if (arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            MagneticField lastMagneticField = new MagneticField(arg0.values[0], arg0.values[1], arg0.values[2]);
            message = new MagneticFieldMessage(lastMagneticField, System.currentTimeMillis());
            lastMagneticField.setX(arg0.values[0]);
            lastMagneticField.setY(arg0.values[1]);
            lastMagneticField.setZ(arg0.values[2]);

//            show.setText("Mag\nX: " + arg0.values[0] +
//                    "\nY:" + arg0.values[1] +
//                    "\nZ: " + arg0.values[2]);
        } else if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            Orientation lastOrientation = new Orientation(arg0.values[0], arg0.values[1], arg0.values[2]);
            message = new OrientationMessage(lastOrientation, System.currentTimeMillis());
//            show.setText("Tilt\nX: " + arg0.values[0] +
//                    "\nY:" + arg0.values[1] +
//                    "\nZ: " + arg0.values[2]);
        }
        messages.add(message);
    }

    List<SensorMessage> messages = new ArrayList<>();


    final class Myloc implements LocationListener {

        @Override
        public void onLocationChanged(Location arg0) {
            GpsLocation lastGpsLocation = new GpsLocation();
            lastGpsLocation.setLatitude(arg0.getLatitude());
            lastGpsLocation.setLongitude(arg0.getLongitude());
            GpsLocationMessage message = new GpsLocationMessage(lastGpsLocation, System.currentTimeMillis());
            messages.add(message);
//            show.setText("Long" + arg0.getLongitude() +
//                    "\nLati:" + arg0.getLatitude());

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }
    }

}