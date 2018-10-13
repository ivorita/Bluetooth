package com.antelope.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antelope.android.bluetooth.adapter.BluetoothDeviceAdapter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.open_bt)
    Button mOpenBt;
    @BindView(R.id.search_bt)
    Button mSearchBt;
    @BindView(R.id.connect_state)
    TextView mConnectState;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.text_msg)
    TextView mTextMsg;
    @BindView(R.id.listView)
    ListView mListView;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDeviceAdapter adapter;
    private final int BUFFER_SIZE = 1024;
    private static final String NAME = "BT_DEMO";
    private static final UUID BT_UUID = UUID.fromString("02001101-0001-1000-8080-00805F9BA9BA");
    private Unbinder unbinder;
    private ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initView();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initReceiver();
    }

    private void initView() {
        adapter = new BluetoothDeviceAdapter(getApplicationContext(),R.layout.bt_list_item);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }

                BluetoothDevice device = (BluetoothDevice)adapter.getItem(position);
                /*connectDevice(device);*/
            }
        });
    }

    //动态注册广播
    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,intentFilter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //避免重复添加已经绑定过的设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED ){
                    adapter.add(device);
                    adapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(MainActivity.this,"开始搜索",Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(MainActivity.this,"搜索结束",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 连接蓝牙设备
     */
    private void connectDevice(BluetoothDevice device) {
        mConnectState.setText(getResources().getString(R.string.connecting));

        try{
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(BT_UUID);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*public void init() {
        *//*final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);*//*
        *//*BluetoothAdapter mBluetoothAdapter= bluetoothManager.getAdapter();*//*
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            *//*Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);*//*
            mBluetoothAdapter.enable();
        }
    }*/

    @OnClick({R.id.open_bt, R.id.search_bt, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_bt:
                open_bt();
                break;
            case R.id.search_bt:
                search_devices();
                break;
            case R.id.btn_send:
                break;
        }
    }

    private void open_bt() {
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }

        //开启被其他蓝牙设备发现的功能
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

            //设置为一直开启
            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
            startActivity(i);
        }
    }

    private void search_devices() {
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        getBoundedDevices();
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 获取已经配对的设备
     * */
    private void getBoundedDevices() {
        //获取已经配对的设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        adapter.clear();
        //添加到列表里
        if (pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                adapter.add(device);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mReceiver);
        unbinder.unbind();
    }


    /**
     * 连接线程
     * */
    private class ConnectThread extends Thread {

    }
}
