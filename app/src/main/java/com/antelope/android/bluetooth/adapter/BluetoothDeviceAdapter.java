package com.antelope.android.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.antelope.android.bluetooth.R;

public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private final LayoutInflater mInflater;
    private int mResource;

    public BluetoothDeviceAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = mInflater.inflate(mResource,parent,false);
        }

        TextView name = convertView.findViewById(R.id.device_name);
        TextView info = convertView.findViewById(R.id.device_info);
        BluetoothDevice device = getItem(position);
        if (device != null) {
            name.setText(device.getName());
        }
        if (device != null) {
            info.setText(device.getAddress());
        }

        return convertView;
    }
}
