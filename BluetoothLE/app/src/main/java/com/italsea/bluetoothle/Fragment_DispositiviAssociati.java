package com.italsea.bluetoothle;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.util.ArrayList;

public class Fragment_DispositiviAssociati extends Fragment {

    public interface IFragmentAssociati {
        ArrayList<String> getArrayListDispositiviAssociati();

        ArrayList<BluetoothDevice> getArrayListDeviceAccoppiati();
    }

    IFragmentAssociati mListener;


    private static final int SCAN_PERIOD = Toast.LENGTH_SHORT;

    ListView mListView;
    TextView mTestoFisso;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Fragment_DispositiviScansionati.ricorsione = true;

        View vView = inflater.inflate(R.layout.fragment_dispositivi_associati, container, false);

        mListView = vView.findViewById(R.id.fragment_dispositivi_associati);
        mTestoFisso = vView.findViewById(R.id.TestoFisso);

        final ArrayList<String> dispositiviAssociati = mListener.getArrayListDispositiviAssociati();

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dispositiviAssociati);

        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BluetoothDevice b = mListener.getArrayListDeviceAccoppiati().get(position);

                Intent i = new Intent(getActivity(),ConnectActivity.class);
                Bundle vBundle = new Bundle();
                vBundle.putString("ADDRESS",b.getAddress());
                i.putExtras(vBundle);
                startActivity(i);
            }
        });

        return vView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IFragmentAssociati)
            mListener = (IFragmentAssociati) context;
        else
            mListener = null;
    }


}
