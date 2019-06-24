package com.italsea.bluetoothle;

        import android.bluetooth.BluetoothDevice;
        import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.w3c.dom.Text;

        import java.util.ArrayList;

public class Fragment_DispositiviScansionati extends Fragment {

    public interface IFragmentScansione{
        public void collegaFragment(Fragment fragment,int container);
        public void removeFragment(Fragment fragment);
        public Fragment getfragment();
        public String getTestoTextView();
        ArrayList<String> getArrayListNuoviDevice();
    }

    IFragmentScansione mListener;

    ArrayList<BluetoothDevice> deviceScansionati = MainActivity.getArrayListDeviceScansionati();

    ListView mListView;
    TextView mTestoVariabile;

    int i=0;
    public static boolean ricorsione=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vView = inflater.inflate(R.layout.fragment_scansioni_nuove, container, false);

        mListView = vView.findViewById(R.id.fragment_scansioni_nuove);
        mTestoVariabile = vView.findViewById(R.id.testovariabile);
        mTestoVariabile.setText(""+mListener.getTestoTextView());

        ArrayList<String> nuoviDispositivi = mListener.getArrayListNuoviDevice();

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nuoviDispositivi);

        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                while(i<20 && ricorsione) {
                    deviceScansionati.get(position).createBond(); // accoppia il dispositivo

                    if (MainActivity.isDeviceBonded(deviceScansionati.get(position))) {
                        Toast.makeText(getActivity(), "Dispositivo Accoppiato", Toast.LENGTH_SHORT).show();
                        break;
                    }else{
                        i++;
                        mListView.performItemClick(mListView.getAdapter().getView(position,null,null),position,mListView.getAdapter().getItemId(position));
                    }
                }

                 if (!MainActivity.isDeviceBonded(deviceScansionati.get(position))) {
                     if(i==20)
                        Toast.makeText(getActivity(), "Errore durante l'accoppiamento, Riprova...", Toast.LENGTH_SHORT).show();
                     ricorsione = false;
                     i=0;

                 }

                mListener.removeFragment(mListener.getfragment());
                mListener.collegaFragment(new Fragment_DispositiviAssociati(),MainActivity.container_dispositiviAssociati);

            }

        });

        return vView;
    }

    /*public void setStatoTextview(){
        //if(mTestoVariabile != null){
            mTestoVariabile.setText("cambiami");
        //}
    }*/


   /* @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nuoviDispositivi);

        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof IFragmentScansione)
            mListener=(IFragmentScansione) context;
        else
            mListener=null;

    }



}
