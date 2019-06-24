package com.italsea.bluetoothle;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements Fragment_DispositiviScansionati.IFragmentScansione, Fragment_DispositiviAssociati.IFragmentAssociati {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    static ArrayList<String> devices = new ArrayList<String>();
    static ArrayList<String> boundedDevices = new ArrayList<String>();

    static ArrayList<BluetoothDevice> bluetoothDevicesScansionati = new ArrayList<BluetoothDevice>();
    static ArrayList<BluetoothDevice> bluetoothDevicesAccoppiati = new ArrayList<BluetoothDevice>();

    static int container_nuoviDispositivi = R.id.container_NuoviDispositivi;
    static int container_dispositiviAssociati = R.id.container_DispositiviAssociati;

    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner scanner;
    BluetoothDevice device;

    Fragment_DispositiviScansionati fragment_dispositiviScansionati = new Fragment_DispositiviScansionati();
    Fragment_DispositiviAssociati fragment_dispositiviAssociati = new Fragment_DispositiviAssociati();

    // variabili d'istanza della classe
    private boolean scanning;
    private Handler handler = new Handler();
    public final static int SCAN_PERIOD = 10000;

    boolean risultatoAccoppiamento=false;
    String mTestoTextView="Nuovi Dispositivi...";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Il bluetooth LE non è supportato", Toast.LENGTH_SHORT).show();
        }


        // recuperiamo un riferimento al BluetoothManager
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        // recuperiamo un riferimento all'adapter Bluetooth
        bluetoothAdapter = bluetoothManager.getAdapter();
        // verifichiamo che Bluetooth sia attivo nel dispositivo
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        if(bluetoothAdapter.isEnabled()) {
            collegaFragment(fragment_dispositiviAssociati, container_dispositiviAssociati);
            scanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        collegaFragment(fragment_dispositiviScansionati, container_nuoviDispositivi);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        collegaFragment(fragment_dispositiviAssociati, container_dispositiviAssociati);
        scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }
//----------------------------------------------------------------------------------

    public void onClickStart(View view) {
        Intent i = new Intent(MainActivity.this, Pop.class);
        startActivity(i);
        devices.clear();
        startBleScan();
    }


    public static boolean isDeviceBonded(BluetoothDevice device){
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }


    public void startBleScan() {
        // scanning a true significa "scansione in corso"
        scanning = true;
        // avviamo la scansione da un thread secondario
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // avvio della scansione
                scanner.startScan(scanCallback);
            }
        });
        // l'oggetto Handler viene utilizzato per impostare un timeout
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // tempo scaduto per la scansione
                // scansione interrotta
                scanner.stopScan(scanCallback);

                removeFragment(fragment_dispositiviScansionati);

                fragment_dispositiviScansionati = new Fragment_DispositiviScansionati();

                if(devices.size() == 0) {
                    mTestoTextView = "Nessun dispositivo trovato";
                }else {
                    mTestoTextView ="Dispositivi disponibili";
                }
                collegaFragment(fragment_dispositiviScansionati, container_nuoviDispositivi);

                // scanning=false significa "Nessuna scansione in corso"
                scanning = false;
            }
        }, SCAN_PERIOD);
        // SCAN_PERIOD indica una durata in millisecondi
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            boolean nuovoDispositivo = true;
            device = result.getDevice();

            String nomeDevice = device.getName();

            String addressDevice = device.getAddress();

            if (nomeDevice != null) {
                if (devices.size() != 0)
                    for (int i = 0; i < devices.size(); i++) {
                        if (devices.get(i).equals(nomeDevice + "\n" + addressDevice)) {
                            nuovoDispositivo = false;
                            break;
                        }
                    }

                for (BluetoothDevice b : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
                    if ((b.getName() + "\n" + b.getAddress()).equals(nomeDevice + "\n" + addressDevice)) {
                        nuovoDispositivo = false;
                        break;
                    }
                }

                    if (nuovoDispositivo) {
                        devices.add(nomeDevice + "\n" + addressDevice);
                        bluetoothDevicesScansionati.add(device);
                    }


            }
        }
    };


    public void collegaFragment(Fragment fragment, int container) {
        FragmentManager vManager = getSupportFragmentManager();
        FragmentTransaction vTransaction = vManager.beginTransaction();
        fragment.setRetainInstance(true);
        vTransaction.replace(container, fragment);
        vTransaction.commit();

    }

    public void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        // Toast.makeText(this, "Rimosso fragment", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getfragment() {
        return fragment_dispositiviAssociati;
    }

    @Override
    public String getTestoTextView() {
        return mTestoTextView;
    }

    public ArrayList<String> getArrayListNuoviDevice() {
        return devices;
    }

    public static ArrayList<BluetoothDevice> getArrayListDeviceScansionati() {
        return bluetoothDevicesScansionati;
    }

    public ArrayList<BluetoothDevice> getArrayListDeviceAccoppiati() {
        return bluetoothDevicesAccoppiati;
    }

    public ArrayList<String> getArrayListDispositiviAssociati() {

        boundedDevices.clear();

        for (BluetoothDevice b : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            String nome = b.getName();
            String indirizzo = b.getAddress();

            boundedDevices.add(nome + "\n" + indirizzo);
            bluetoothDevicesAccoppiati.add(b);
        }
        return boundedDevices;
    }

}


//----------------------------------------------------------------------------------------




