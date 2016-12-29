package com.abc.team.becathon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class Navigation extends AppCompatActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    private static String TAG = "testBeacon";
    TextView tv_currentInstruction;
    TextView tv_currentClass;
    ListView lv_previousInstruction;
    Button bt_stopNavigation;
    ArrayList<String> instructions;
    ArrayAdapter<String> adapter;
    String classSelected = "Python";
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        if(getIntent().getStringExtra("selected_class")!=null){
            classSelected = getIntent().getStringExtra("selected_class");
        }

        tv_currentInstruction = (TextView) findViewById(R.id.current_instruction);
        tv_currentClass = (TextView) findViewById(R.id.current_class);
        bt_stopNavigation = (Button) findViewById(R.id.stop_button);
        lv_previousInstruction = (ListView) findViewById(R.id.previous_instructions);
        instructions = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.previous_instructions_layout,instructions);

        bt_stopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));

                changeInstruction(String.valueOf(cnt++));
            }
        });
        lv_previousInstruction.setAdapter(adapter);
        lv_previousInstruction.setBackgroundColor(Color.YELLOW);
        tv_currentClass.setText(classSelected + " class");
    }

    public void changeInstruction(String newInstruction){
        instructions.add(0,tv_currentInstruction.getText().toString());
        adapter.notifyDataSetChanged();
        tv_currentInstruction.setText(newInstruction);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }



    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeacons", /*Identifier.parse("b9407f30-f5f8-466e-aff9-25556b57fe6d")*/null,null,null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons){

                    Log.d(TAG,beacon.getId1()+" "+beacon.getId2()+" "+beacon.getId3()+" distance : "+beacon.getDistance());
                    //changeInstruction(beacon.getId1()+" "+beacon.getId2()+" "+beacon.getId3()+" distance : "+beacon.getDistance());
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
