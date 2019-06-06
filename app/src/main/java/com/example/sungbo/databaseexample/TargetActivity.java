package com.example.sungbo.databaseexample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungbo.databaseexample.Model.HistorySummary;
import com.example.sungbo.databaseexample.Model.TargetHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class TargetActivity extends AppCompatActivity {

    private static final String TAG = "TargetActivity";
    private ProgressBar straight_progressbar;
    private ProgressBar jab_progressbar;
    private ProgressBar hook_progressbar;
    private ProgressBar uppercut_progressbar;

    private TextView straight_textview;
    private TextView jab_textview;
    private TextView hook_textview;
    private TextView uppercut_textview;

    private Button target_start_button;
    private Button endButton;
    private Button rightpair, leftpair;
    private boolean isStart;

    private int straight_count;
    private int jab_count;
    private int hook_count;
    private int uppercut_count;
    private int weight;
    private String sex;

    private int straight_thrown =0;
    private int jab_thrown =0;
    private int hook_thrown= 0;
    private int uppercut_thrown =0;

    private int time;
    private int completion;

    int totalPunchesThrown;

    private ListView lstvw;
    private ArrayAdapter aAdapter;

    private Handler h, h2;

    int SummaryTotalPunches = 0;
    int SummaryTotalSessions = 0;
    double SummaryTotalForce = 0;
    double SummaryTotalSpeed = 0;

    private Chronometer chronometer;

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private String uid;

    double currentx, prevx, currenty, prevy, currentz, prevz, force, acceleration, timeTakenForPunch, speed;
    double totalspeed = 0;
    double totalforce = 0;

    final int RECEIVE_MESSAGE = 1;        // Status  for Handler

    //Bluetooth related variables
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothSocket btSocket2 = null; //b2test
    private boolean rightconnected = false;
    private boolean leftconnected = false;
    private ArrayList list;
    private ArrayList<String> maclist;
    private AlertDialog dialog;

    private TargetActivity.ConnectedThread mConnectedThread;
    private TargetActivity.ConnectedThread2 mConnectedThread2;
    private StringBuilder sb = new StringBuilder();


    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // MAC-address of Bluetooth module (default values, get edited ones later)
    private String address = "00:21:13:05:A7:9B";
    private String address2 = "00:21:13:05:A8:55";

    PunchClassify mPunchClassification = new PunchClassify(TargetActivity.this, 0, 0, 0);
    PunchClassificationRightHandedLeft mPunchClassificationRightHandedLeft = new PunchClassificationRightHandedLeft(TargetActivity.this, 0, 0, 0);


    TextView forceview, speedview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        //Database initialization
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();
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

        straight_progressbar =findViewById(R.id.straight_progressbar);
        jab_progressbar =findViewById(R.id.jab_progressbar);
        hook_progressbar =findViewById(R.id.hook_progressbar);
        uppercut_progressbar =findViewById(R.id.uppercut_progressbar);

        speedview = (TextView) findViewById(R.id.speed);
        forceview = (TextView) findViewById(R.id.force);

        straight_textview = findViewById(R.id.straight_progress);
        jab_textview = findViewById(R.id.jab_progress);
        hook_textview = findViewById(R.id.hook_progress);
        uppercut_textview = findViewById(R.id.uppercut_progress);

        target_start_button = findViewById(R.id.target_start);

        chronometer = findViewById(R.id.chronometer);
        endButton = findViewById(R.id.calibrate);
        rightpair = findViewById(R.id.right_pair2);
        leftpair = findViewById(R.id.left_pair2);
        isStart = false;

        Intent intent = getIntent();

        straight_count = intent.getIntExtra("straight", 0);
        jab_count = intent.getIntExtra("jab", 0);
        hook_count = intent.getIntExtra("hook", 0);
        uppercut_count = intent.getIntExtra("uppercut", 0);


        straight_textview.setText(String.valueOf(straight_count));
        jab_textview.setText(String.valueOf(jab_count));
        hook_textview.setText(String.valueOf(hook_count));
        uppercut_textview.setText(String.valueOf(uppercut_count));

        chronometer.setCountDown(true);

        time = intent.getIntExtra("duration", 0);

        chronometer.setBase(SystemClock.elapsedRealtime()  + ((time) * 1000 ));
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;


                if(chronometer.getText().equals("00:00")){
                    chronometer.stop();
                    isStart = false;

                    totalPunchesThrown = straight_thrown + jab_thrown + hook_thrown + uppercut_thrown;

                    mDatabase.child("users").child(uid).child("TargetHistorySummary").child(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
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

                    Log.d("SHOULDBEHERE", "SHOULDBEHERE");

                    Intent intent = new Intent(TargetActivity.this, TargetFinishedPopup.class);
                    startActivityForResult(intent, 2);

                }
            }


        });

        target_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime()  + ((time) * 1000 ));
                chronometer.start();
                isStart = true;
            }
        });

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
                                    updateCounter(tempPunchType);
                                    setProgressBar();

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
                                    updateCounter(tempPunchType);
                                    setProgressBar();

                                }catch(NumberFormatException ex ){ // handle your exception
                                    Log.d(TAG, "ParseFloat failed");
                                }
                                catch(ArrayIndexOutOfBoundsException e)
                                {
                                    Log.d(TAG, "Array size less than 10");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(TargetActivity.this, R.style.myDialog);
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


                        mConnectedThread = new TargetActivity.ConnectedThread(btSocket);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(TargetActivity.this, R.style.myDialog);
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
                        mConnectedThread2 = new TargetActivity.ConnectedThread2(btSocket2); //b2test

                        //mConnectedThread.start();
                        mConnectedThread2.start(); //b2test

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){

            }
        }
        if(requestCode == 2){
            if(resultCode==RESULT_OK) {

                saveSummaryData();
                saveTargetData();

                Intent intent = new Intent(TargetActivity.this, TargetDoneActivity.class);
                intent.putExtra("straight_thrown", straight_thrown);
                intent.putExtra("straight_count", straight_count);
                intent.putExtra("jab_thrown", jab_thrown);
                intent.putExtra("jab_count", jab_count);
                intent.putExtra("hook_thrown", hook_thrown);
                intent.putExtra("hook_count", hook_count);
                intent.putExtra("uppercut_thrown", uppercut_thrown);
                intent.putExtra("uppercut_count", uppercut_count);
                intent.putExtra("duration", time);
                intent.putExtra("completion", completion);
                intent.putExtra("totalForce", totalforce);
                intent.putExtra("totalSpeed", totalspeed);
                startActivity(intent);
                finish();
            }
        }
    }

    public void setProgressBar(){
        if(straight_count != 0) {
            Log.d("PROGRESSBAR ", String.valueOf((double) straight_thrown / (double) straight_count * 100));
            straight_progressbar.setProgress((int) ((double) straight_thrown / (double) straight_count * 100), true);
            if((straight_count - straight_thrown)< 0){
                straight_textview.setText("+" + String.valueOf(Math.abs(straight_count - straight_thrown)));
            }
            else{
                straight_textview.setText(String.valueOf(straight_count - straight_thrown));
            }
        }
        if(jab_count != 0) {
            Log.d("PROGRESSBAR ", String.valueOf((double) jab_thrown / (double) jab_count * 100));
            jab_progressbar.setProgress((int) ((double) jab_thrown / (double) jab_count * 100), true);
            if((jab_count - jab_thrown)< 0){
                jab_textview.setText("+" + String.valueOf(Math.abs(jab_count - jab_thrown)));
            }
            else{
                jab_textview.setText(String.valueOf(jab_count - jab_thrown));
            }        }
        if(hook_count != 0) {
            Log.d("PROGRESSBAR ", String.valueOf((double) hook_thrown / (double) hook_count * 100));
            hook_progressbar.setProgress((int) ((double) hook_thrown / (double) hook_count * 100), true);
            if((hook_count - hook_thrown)< 0){
                hook_textview.setText("+" + String.valueOf(Math.abs(hook_count - hook_thrown)));
            }
            else{
                hook_textview.setText(String.valueOf(hook_count - hook_thrown));
            }        }
        if(uppercut_count != 0) {
            Log.d("PROGRESSBAR ", String.valueOf((double) uppercut_thrown / (double) uppercut_count * 100));
            uppercut_progressbar.setProgress((int) ((double) uppercut_thrown / (double) uppercut_count * 100), true);
            if((uppercut_count - uppercut_thrown)< 0){
                uppercut_textview.setText("+" + String.valueOf(Math.abs(uppercut_count - uppercut_thrown)));
            }
            else{
                uppercut_textview.setText(String.valueOf(uppercut_count - uppercut_thrown));
            }        }

    }

    private void updateCounter(String punchType){
        if(punchType == "straight"){
            straight_thrown++;
        }
        else if(punchType == "hook")
        {
            hook_thrown++;
        }
        else if (punchType == "uppercut"){
            uppercut_thrown++;
        }
        else if(punchType == "jab"){
            jab_thrown++;
        }
    }

    public void saveSummaryData(){
        HistorySummary mHistorySummary = new HistorySummary(SummaryTotalPunches + totalPunchesThrown, SummaryTotalSessions + 1,
                SummaryTotalSpeed + totalspeed, SummaryTotalForce + totalforce);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(uid).child("TargetHistorySummary")
                .child(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                .child(String.valueOf(Calendar.getInstance().get(Calendar.MONTH ) + 1))
                .setValue(mHistorySummary);
    }

    public void saveTargetData(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        int punchesTargeted = straight_count + jab_count + hook_count + uppercut_count;
        int punchesThrown = straight_thrown + jab_thrown + hook_thrown + uppercut_thrown;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH ) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        TargetHistory tempHistory = new TargetHistory(punchesTargeted, punchesThrown, year, month, day, time);

        int tempPercentStraight = calculatePercent(straight_thrown, straight_count);
        int tempPercentJab = calculatePercent(jab_thrown , jab_count);
        int tempPercentHook = calculatePercent(hook_thrown , hook_count);
        int tempPercentUppercut = calculatePercent(uppercut_thrown , uppercut_count);

        int tempAverageCompletion= (int)((double)(tempPercentStraight + tempPercentJab + tempPercentHook + tempPercentUppercut) / (double)4);

        if( punchesTargeted == 0 || punchesThrown == 0){
            tempHistory.setCompletion(0);
        }
        else {
            //completion = (int)((double)punchesThrown / (double)punchesTargeted * 100);
            tempHistory.setCompletion( tempAverageCompletion);
        }

        String temp = String.valueOf(Calendar.getInstance().get(Calendar.DATE) +" "+ sdf.format(cal.getTime()));

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(uid).child("targetHistory")
                .child(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                .child(String.valueOf(Calendar.getInstance().get(Calendar.MONTH ) + 1))
                .child(temp).setValue(tempHistory);
    }

    public void calibrate(View view){
        mConnectedThread.write("2");    // Send "2" via Bluetooth
        mConnectedThread2.write("2");    // Send "2" via Bluetooth
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
    public int calculatePercent(int thrown, int target){
        if(thrown == 0 && target == 0){
            return 100;
        }else if(thrown == 0 && target != 0 ){
            return 0;
        }
        else{
            int temp =  (int)((double)thrown / (double)target * 100);
            if( temp > 100 ){
                return 100;
            }
            else{
                return temp;
            }
        }
    }
}

