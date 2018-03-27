package com.arcaneless.myoairblock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ml.xuexin.bleconsultant.BleConsultant;
import ml.xuexin.bleconsultant.entity.BleDevice;
import ml.xuexin.bleconsultant.port.ConnectCallback;
import ml.xuexin.bleconsultant.port.ScanDevicesHelper;

public class BluetoothSelectActivity extends AppCompatActivity {

    private final Context context = this;
    private final AirBlockManager airblockManager = AirBlockManager.getInstance();
    private final BleConsultant bleConsultant = AirBlockManager.getInstance().getConsultant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_select);



        bleConsultant.setScanDevicesHelper(new ScanDevicesHelper() {
            @Override
            public void reportDevices(final List<BleDevice> bleDeviceList) {
                final Map<String, BleDevice> deviceMap = new HashMap<>();
                final List<String> names = new ArrayList<>();

                for (BleDevice bleDevice : bleDeviceList) {
                    deviceMap.put(bleDevice.getAddress(), bleDevice);
                    names.add(bleDevice.getName());
                }

                // View
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(BluetoothSelectActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, names) {
                    @NonNull
                    @Override
                    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        //Log.e("Error", "Error");
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(bleDeviceList.get(position).getName());
                        text2.setText(bleDeviceList.get(position).getAddress());

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final BleDevice bd = deviceMap.get(((TextView) v.findViewById(android.R.id.text2)).getText());
                                Log.i("BDLog", bd.getName());

                                bleConsultant.connect(bd, new ConnectCallback() {
                                    @Override
                                    public void onStateChange(int state) {
                                        switch (state) {
                                            case 0:
                                                Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                                                break;
                                            case 1:
                                                Toast.makeText(context, "Connecting", Toast.LENGTH_LONG).show();
                                                break;
                                            case 2:
                                                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                                                break;
                                            case 3:
                                                Toast.makeText(context, "Disconnecting", Toast.LENGTH_LONG).show();
                                                break;
                                            case 4:
                                                Toast.makeText(context, "Service Discovered", Toast.LENGTH_LONG).show();
                                                airblockManager.setDevice(bd);
                                                finish();
                                                break;
                                            default:
                                                Toast.makeText(context, "Unknown State: " + state, Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onOvertime() {
                                        Toast.makeText(context, "Overtime", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public long getOvertimeTime() {
                                        return 5000L;
                                    }
                                });
                            }
                        });
                        return view;
                    }


                };
                ListView listView = findViewById(R.id.listview);
                listView.setAdapter(adapter);
            }

            @Override
            public boolean deviceFilter(BleDevice bleDevice) {
                return bleDevice.getName() != null && bleDevice.getName().contains("Makeblock");
            }

            @Override
            public long getReportPeriod() {
                return 500;
            }
        });

    }



}
