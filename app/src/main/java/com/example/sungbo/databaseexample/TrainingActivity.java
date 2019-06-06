package com.example.sungbo.databaseexample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import java.util.ArrayList;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungbo.databaseexample.Model.HistorySummary;
import com.example.sungbo.databaseexample.Model.TargetHistory;
import com.example.sungbo.databaseexample.Model.TrainingHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class TrainingActivity extends AppCompatActivity {

    private static final String TAG = "trainingactivity";
    private Chronometer chronometer;
    private boolean isStart;
    private ListView lstvw;
    private ArrayAdapter aAdapter;
    TextView text_straight, text_jab, text_hook, text_uppercut;
    int count_straight, count_jab, count_hook, count_uppercut;
    int weight;
    String sex;
    int armSpan;
    Button calibrate;
    int duration;
    ArrayList list;
    ArrayList<String> maclist;
    private AlertDialog dialog;

    Button rightpair;
    Button leftpair;

    boolean leftconnected = false;
    boolean rightconnected = false;
    boolean leftSuccess = false;
    boolean rightSuccess = false;

    int punchesThrown;
    int SummaryTotalPunches = 0;
    int SummaryTotalSessions = 0;
    double SummaryTotalForce = 0;
    double SummaryTotalSpeed = 0;

    private Handler h, h2;

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private String uid;

    TextView txtArduino, right, power, forceview, speedview;
    PunchClassify mPunchClassification = new PunchClassify(TrainingActivity.this, 0, 0, 0);
    PunchClassificationRightHandedLeft mPunchClassificationRightHandedLeft = new PunchClassificationRightHandedLeft(TrainingActivity.this, 0, 0, 0);


    double averageSpeed;
    double averageForce;



    double currentx, prevx, currenty, prevy, currentz, prevz, force, totalforce, totalspeed, acceleration, timeTakenForPunch, speed;

    final int RECEIVE_MESSAGE = 1;        // Status  for Handler

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothSocket btSocket2 = null; //b2test

    private ConnectedThread mConnectedThread;
    private ConnectedThread2 mConnectedThread2;
    private StringBuilder sb = new StringBuilder();


    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // MAC-address of Bluetooth module (you must edit this line)
    private String address = "00:21:13:05:A7:9B";
    private String address2 = "00:21:13:05:A8:55";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        chronometer = findViewById(R.id.chronometer);
        text_straight = findViewById(R.id.straight);
        text_jab = findViewById(R.id.jab);
        text_hook = findViewById(R.id.hook);
        text_uppercut = findViewById(R.id.uppercut);
        txtArduino = (TextView) findViewById(R.id.Bluetoothtest);
        calibrate = findViewById(R.id.calibrate);
        speedview = (TextView) findViewById(R.id.speed);
        forceview = (TextView) findViewById(R.id.force);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        retrieveSummaryData();

        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weight = Integer.parseInt(dataSnapshot.child("weight").getValue().toString());
                sex = dataSnapshot.child("sex").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        totalforce = 0;
        totalspeed = 0;
        prevx = 0;
        prevy = 0;
        prevz = 0;

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;
            }
        });

        /*calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blinkTextView("jab");
            }
        });*/


        //Handler for Right Punch
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECEIVE_MESSAGE:                                                   // if receive massage


                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("~");                            // determine the end-of-line
                        int startOfLineIndex = sb.indexOf("`");
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            //if timer is started
                            if(isStart){
                                String sbprint = sb.substring(startOfLineIndex+1 , endOfLineIndex);               // extract string
                                String[] arr = sbprint.split(",");
                                try{
                                    currentx = Double.parseDouble(arr[0]);
                                    currenty = Double.parseDouble(arr[1]);
                                    currentz = Double.parseDouble(arr[2]);
                                    timeTakenForPunch = Float.parseFloat(arr[3]) / 1000;
                                    acceleration =  Math.sqrt(currentx*currentx + currenty*currenty + currentz*currentz);
                                    speed = acceleration*timeTakenForPunch;
                                    if(sex.equals("male")){
                                        force =  (acceleration*weight*0.057);
                                    }
                                    else if (sex.equals("female")){
                                        force = (acceleration*weight* 0.0497);
                                    }
                                    mPunchClassification.mSample.setFeatures(new double[]{currentx, currenty, currentz});
                                    String tempPunchType = mPunchClassification.classifyPunch();
                                    blinkTextView(tempPunchType);

                                }catch(NumberFormatException ex ){ // handle your exception
                                    Log.d(TAG, "ParseFloat failed");
                                }
                                catch(ArrayIndexOutOfBoundsException e)
                                {
                                    Log.d(TAG, "Array size less than 4");
                                }

                                totalforce = (totalforce + force);
                                totalspeed =(totalspeed +speed);
                                speedview.setText(String.format("%.2f", speed));
                                forceview.setText(String.format("%.2f", force));

                            }
                            sb.delete(0, sb.length());                                      // and clear

                        }
                        Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;



                }
            };
        };

        //Handler for Left Punch
        h2 = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECEIVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("~");                            // determine the end-of-line
                        int startOfLineIndex = sb.indexOf("`");
                        if (endOfLineIndex > 0) {                       // if end-of-line,
                            //if timer is started
                            if(isStart){
                                String sbprint = sb.substring(startOfLineIndex+1 , endOfLineIndex);               // extract string
                                String[] arr = sbprint.split(",");
                                try{
                                    currentx = Double.parseDouble(arr[0]);
                                    currenty = Double.parseDouble(arr[1]);
                                    currentz = Double.parseDouble(arr[2]);
                                    timeTakenForPunch = Float.parseFloat(arr[3]) / 1000;
                                    acceleration =  Math.sqrt(currentx*currentx + currenty*currenty + currentz*currentz);
                                    speed = acceleration*timeTakenForPunch;
                                    if(sex.equals("male")){
                                        force =  (acceleration*weight*0.057);
                                    }
                                    else if (sex.equals("female")){
                                        force = (acceleration*weight* 0.0497);
                                    }
                                    mPunchClassificationRightHandedLeft.mSample.setFeatures(new double[]{currentx, currenty, currentz});
                                    String tempPunchType = mPunchClassificationRightHandedLeft.classifyPunch();
                                    blinkTextView(tempPunchType);

                                }catch(NumberFormatException ex ){ // handle your exception
                                    Log.d(TAG, "ParseFloat failed");
                                }
                                catch(ArrayIndexOutOfBoundsException e)
                                {
                                    Log.d(TAG, "Array size less than 10");
                                }

                                totalforce = (totalforce + force);
                                totalspeed =(totalspeed +speed);

                                txtArduino.setText("Data from Arduino: " + sbprint );            // update TextView

                                speedview.setText(String.format("%.2f", speed));
                                forceview.setText(String.format("%.2f", force));
                            }
                            sb.delete(0, sb.length());                                      // and clear

                        }
                        Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };

        rightpair = (Button)findViewById(R.id.right_pair);
        leftpair = (Button)findViewById(R.id.left_pair);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        //Alert Dialog for Right Glove Button
        rightpair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btAdapter==null){
                    Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
                }
                else{
                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    list = new ArrayList();
                    maclist = new ArrayList<String>();
                    if(pairedDevices.size()>0){
                        for(BluetoothDevice device: pairedDevices){
                            String devicename = device.getName();
                            String macAddress = device.getAddress();
                            list.add("Name: "+devicename+" MAC Address: "+macAddress);
                            maclist.add(macAddress);
                        }
                        aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);

                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this, R.style.myDialog);
                View row= getLayoutInflater().inflate(R.layout.bluetooth_list,null);
                lstvw=(ListView)row.findViewById(R.id.deviceList);
                lstvw.setAdapter(aAdapter);
                builder.setTitle("Select device to connect to(Right Glove):")
                        .setCancelable(true)
                        .setView(row);
                //Creating dialog box
                dialog  = builder.create();
                lstvw.setOnItemClickListener(new AdapterView.OnItemClickListener () {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, maclist.get(position).toString());
                        address=maclist.get(position);



                        // Set up a pointer to the remote node using it's address.
                        BluetoothDevice device = btAdapter.getRemoteDevice(address);
                        //BluetoothDevice device2 = btAdapter.getRemoteDevice(address2); //b2test

                        try {
                            btSocket = createBluetoothSocket(device);
                            //btSocket2 = createBluetoothSocket(device2); //b2test
                        } catch (IOException e) {
                            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                        }

                        // Discovery is resource intensive.  Make sure it isn't going on
                        // when you attempt to connect and pass your message.
                        btAdapter.cancelDiscovery();

                        // Establish the connection.  This will block until it connects.
                        Log.d(TAG, "...Connecting...");
                        Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
                        try {
                            btSocket.connect();
                            //btSocket2.connect(); //b2test
                            Log.d(TAG, "....Connection ok...");
                            Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
                            rightpair.setBackgroundResource(R.drawable.ic_bluetooth_connected);
                            rightconnected = true;

                        } catch (IOException e) {
                            try {
                                btSocket.close();
                                //btSocket2.close(); //b2test
                            } catch (IOException e2) {
                                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                            }
                        }
                        if(!rightconnected){
                            Toast.makeText(getApplicationContext(), "Failed to Connect", Toast.LENGTH_SHORT).show();
                        }

                        // Create a data stream so we can talk to server.
                        Log.d(TAG, "...Create Socket...");


                        mConnectedThread = new ConnectedThread(btSocket);
                        //mConnectedThread2 = new ConnectedThread2(btSocket2); //b2test

                        mConnectedThread.start();
                        //mConnectedThread2.start(); //b2test

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //Alert Dialog for Left Glove Button
        leftpair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btAdapter==null){
                    Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
                }
                else{
                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    list = new ArrayList();
                    maclist = new ArrayList<String>();
                    if(pairedDevices.size()>0){
                        for(BluetoothDevice device: pairedDevices){
                            String devicename = device.getName();
                            String macAddress = device.getAddress();
                            list.add("Name: "+devicename+" MAC Address: "+macAddress);
                            maclist.add(macAddress);
                        }
                        aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);

                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this, R.style.myDialog);
                View row= getLayoutInflater().inflate(R.layout.bluetooth_list,null);
                lstvw=(ListView)row.findViewById(R.id.deviceList);
                lstvw.setAdapter(aAdapter);
                builder.setTitle("Select device to connect to(Left Glove):")
                        .setCancelable(true)
                        .setView(row);
                //Creating dialog box
                dialog  = builder.create();
                lstvw.setOnItemClickListener(new AdapterView.OnItemClickListener () {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, maclist.get(position).toString());
                        address2=maclist.get(position);



                        // Set up a pointer to the remote node using it's address.
                        //BluetoothDevice device = btAdapter.getRemoteDevice(address);
                        BluetoothDevice device2 = btAdapter.getRemoteDevice(address2); //b2test

                        // Two things are needed to make a connection:
                        //   A MAC address, which we got above.
                        //   A Service ID or UUID.  In this case we are using the UUID for SPP.

                        try {
                            //btSocket = createBluetoothSocket(device);
                            btSocket2 = createBluetoothSocket(device2); //b2test
                        } catch (IOException e) {
                            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                        }

                        // Discovery is resource intensive.  Make sure it isn't going on
                        // when you attempt to connect and pass your message.
                        btAdapter.cancelDiscovery();

                        // Establish the connection.  This will block until it connects.
                        Log.d(TAG, "...Connecting...");
                        Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
                        try {
                            //btSocket.connect();
                            btSocket2.connect(); //b2test
                            Log.d(TAG, "....Connection ok...");
                            Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
                            leftpair.setBackgroundResource(R.drawable.ic_bluetooth_connected);
                            leftconnected = true;


                        } catch (IOException e) {
                            try {
                                //btSocket.close();
                                btSocket2.close(); //b2test
                            } catch (IOException e2) {
                                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                            }
                        }
                        if(!leftconnected){
                            Toast.makeText(getApplicationContext(), "Failed to Connect", Toast.LENGTH_SHORT).show();
                        }

                        // Create a data stream so we can talk to server.
                        Log.d(TAG, "...Create Socket...");


                        //mConnectedThread = new ConnectedThread(btSocket);
                        mConnectedThread2 = new ConnectedThread2(btSocket2); //b2test

                        //mConnectedThread.start();
                        mConnectedThread2.start(); //b2test

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


    }

    public void calibrate(View view){
        mConnectedThread.write("2");    // Send "0" via Bluetooth
        mConnectedThread2.write("2");    // Send "0" via Bluetooth
        //Log.d(TAG, list.get(1).toString());
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(getBaseContext(), "Connection Lost", Toast.LENGTH_SHORT).show(); //Device has disconnected
                leftpair.setBackgroundResource(R.drawable.ic_bluetooth_disabled);
                rightpair.setBackgroundResource(R.drawable.ic_bluetooth_disabled);
                rightconnected = false;
                leftconnected = false;
                try{
                    btSocket.close();
                    //btSocket2.close(); //b2test
                } catch (Exception e2) {
                    Log.d(TAG, "socket 1 not made yet" + e2.getMessage() + ".");
                }

                try{
                    //btSocket.close();
                    btSocket2.close(); //b2test
                } catch (Exception e2) {
                    Log.d(TAG, "socket 2 not made yet" + e2.getMessage() + ".");
                }
            }
        }
    };

    public void startStopChronometer(View view) throws InterruptedException {
        if (isStart) {
            chronometer.stop();
            duration = (int)(SystemClock.elapsedRealtime() - chronometer.getBase());
            isStart = false;

            saveTrainingData();
            saveSummaryData();



            ((Button) view).setText("Start");
            mConnectedThread.write("0");    // Send "0" via Bluetooth
            Toast.makeText(getBaseContext(), "Stop Bluetooth", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(TrainingActivity.this, TrainingDoneActivity.class);
            intent.putExtra("duration", duration);
            intent.putExtra("straight", count_straight);
            intent.putExtra("jab", count_jab);
            intent.putExtra("hook", count_hook);
            intent.putExtra("uppercut", count_uppercut);
            intent.putExtra("averageSpeed", averageSpeed);
            intent.putExtra("averageForce", averageForce);

            startActivity(intent);
            finish();




        } else {
            if (rightconnected == true && leftconnected == true) {
                //startActivityForResult(new Intent(TrainingActivity.this, PopActivity.class), 1);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                isStart = true;

                count_straight = 0;
                count_hook = 0;
                count_jab = 0;
                count_uppercut = 0;

                text_straight.setText("0");
                text_jab.setText("0");
                text_hook.setText("0");
                text_uppercut.setText("0");

                Button start = findViewById(R.id.start);
                start.setText("Stop");
                //mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Start Bluetooth", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getBaseContext(), "Check Bluetooth Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");
/*
        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        //BluetoothDevice device2 = btAdapter.getRemoteDevice(address2); //b2test

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
            //btSocket2 = createBluetoothSocket(device2); //b2test
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
        try {
            btSocket.connect();
            //btSocket2.connect(); //b2test
            Log.d(TAG, "....Connection ok...");
            Toast.makeText(this, "connection ok", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            try {
                btSocket.close();
                //btSocket2.close(); //b2test
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");


        mConnectedThread = new ConnectedThread(btSocket);
        //mConnectedThread2 = new ConnectedThread2(btSocket2); //b2test

        mConnectedThread.start();
        //mConnectedThread2.start(); //b2test */
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try{
            btSocket.close();
            btSocket2.close(); //b2test
        } catch (Exception e2) {
            Log.d(TAG, "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (!btAdapter.isEnabled()) {
                try {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);

                } catch (ActivityNotFoundException ex) {
                    Log.d(TAG,"Can't setup request for bluetooth");
                }
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private static final String TAG = "TAG";
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[2048];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    /* b2test handler */
    private class ConnectedThread2 extends Thread {
        private static final String TAG = "TAG";
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread2(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[2048];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h2.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */ /* b2test */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
    /**/

    public void blinkTextView(String punchType){
        /*if (punchType == 0){
            Drawable background = text_straight.getBackground();
            ((GradientDrawable)background).setColor(ContextCompat.getColor(this,R.color.straight));
        }*/

        if(punchType.equals("straight")){
            final Drawable background = text_straight.getBackground();

            new CountDownTimer(200, 200) {

                @Override
                public void onTick(long arg0) {
                    // TODO Auto-generated method stub
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.straight));
                    text_straight.setText(String.valueOf(++count_straight));
                }

                @Override
                public void onFinish() {
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.background));
                }
            }.start();
        }
        else if(punchType.equals("jab")){
            final Drawable background = text_jab.getBackground();

            new CountDownTimer(200, 200) {

                @Override
                public void onTick(long arg0) {
                    // TODO Auto-generated method stub
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.jab));
                    text_jab.setText(String.valueOf(++count_jab));
                }

                @Override
                public void onFinish() {
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.background));
                }
            }.start();
        }
        else if(punchType.equals("hook")){
            final Drawable background = text_hook.getBackground();

            new CountDownTimer(200, 200) {

                @Override
                public void onTick(long arg0) {
                    // TODO Auto-generated method stub
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.hook));
                    text_hook.setText(String.valueOf(++count_hook));
                }

                @Override
                public void onFinish() {
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.background));
                }
            }.start();
        }
        else if(punchType.equals("uppercut")){
            final Drawable background = text_uppercut.getBackground();

            new CountDownTimer(200, 200) {

                @Override
                public void onTick(long arg0) {
                    // TODO Auto-generated method stub
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.uppercut));
                    text_uppercut.setText(String.valueOf(++count_uppercut));
                }

                @Override
                public void onFinish() {
                    ((GradientDrawable)background).setColor(ContextCompat.getColor(TrainingActivity.this,R.color.background));
                }
            }.start();
        }
    }

    public void saveSummaryData(){
        HistorySummary mHistorySummary = new HistorySummary(SummaryTotalPunches + punchesThrown, SummaryTotalSessions + 1,
                SummaryTotalSpeed + totalspeed, SummaryTotalForce + totalforce);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(uid).child("TrainingHistorySummary")
                .child(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                .child(String.valueOf(Calendar.getInstance().get(Calendar.MONTH ) + 1))
                .setValue(mHistorySummary);
    }

    public void saveTrainingData(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        punchesThrown = count_straight + count_jab + count_hook + count_uppercut;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH ) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        if(punchesThrown != 0 ){
            averageSpeed= totalspeed / punchesThrown;
            averageForce= totalforce / punchesThrown;
        }
        else{
            averageForce = 0;
            averageSpeed = 0;
        }

        duration = (int)((float)duration / (float)1000);

        TrainingHistory tempHistory = new TrainingHistory(punchesThrown, averageSpeed, averageForce, year, month, day, duration);


        String temp = String.valueOf(Calendar.getInstance().get(Calendar.DATE) +" "+ sdf.format(cal.getTime()));

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(uid).child("trainingHistory")
                .child(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                .child(String.valueOf(Calendar.getInstance().get(Calendar.MONTH ) + 1))
                .child(temp).setValue(tempHistory);
    }

    public void retrieveSummaryData(){
        mDatabase.child("users").child(uid).child("TrainingHistorySummary").child(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                .child(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1))
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("totalPunches").exists()) {
                            SummaryTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                            SummaryTotalSessions = Integer.parseInt(dataSnapshot.child("totalSessions").getValue().toString());
                            SummaryTotalForce = Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString());
                            SummaryTotalSpeed = Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString());

                            Log.d("FROM DATABASE SUMMARY", String.valueOf(SummaryTotalPunches));
                            Log.d("FROM DATABASE SUMMARY", String.valueOf(SummaryTotalSessions));
                            Log.d("FROM DATABASE SUMMARY", String.valueOf(SummaryTotalForce));
                            Log.d("FROM DATABASE SUMMARY", String.valueOf(SummaryTotalSpeed));
                        }
                        else{
                            SummaryTotalPunches = 0;
                            SummaryTotalSessions = 0;
                            SummaryTotalForce = 0;
                            SummaryTotalSpeed = 0;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
