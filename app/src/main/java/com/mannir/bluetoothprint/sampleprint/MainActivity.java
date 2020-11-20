package com.mannir.bluetoothprint.sampleprint;
/*
MIT License
Copyright (C) 2015/1393  <Saeed Khalafinejad, Kasabeh group>

You may contact-us via info@kasabeh.org.
*/

import android.content.Intent;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mannir.bluetoothprint.R;
import com.mannir.bluetoothprint.lib.DeviceListActivity;
import com.mannir.bluetoothprint.utils.PrefMng;
import com.mannir.bluetoothprint.utils.Tools;
import com.mannir.bluetoothprint.utils.printerFactory;

//import org.kasabeh.androidprint.R;

import com.mannir.bluetoothprint.lib.IPrintToPrinter;
import com.mannir.bluetoothprint.lib.WoosimPrnMng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CONNECT = 100;
    private WoosimPrnMng mPrnMng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnPrint).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnPrint){
            //Check if the Bluetooth is available and on.
            if (!Tools.isBlueToothOn(this)) return;

            /*Save selected printer brand.
              Later, it is used for creating appropriate printer manager.*/
            if (!saveSelectedPrinterBrand()) return;

            //Pick a Bluetooth device
            Intent i = new Intent(this, DeviceListActivity.class);
            startActivityForResult(i, REQUEST_CONNECT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CONNECT&&resultCode==RESULT_OK){
            try {
                //Get device address to print to.
                String blutoothAddr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                //The interface to print text to thermal printers.
                IPrintToPrinter testPrinter = new TestPrinter(this);
                //Connect to the printer and after successful connection issue the print command.
                mPrnMng = printerFactory.createPrnMng(this, blutoothAddr, testPrinter);
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mPrnMng!=null) mPrnMng.releaseAllocatoins();
        super.onDestroy();
    }

    private boolean saveSelectedPrinterBrand() {
        RadioButton radBtn;

        radBtn = findViewById(R.id.radOthers);
        if (radBtn.isChecked()){
            PrefMng.saveActivePrinter(this, PrefMng.PRN_OTHER_PRINTERS_SELECTED);
            return true;
        }

        Toast.makeText(this, R.string.choose_printer, Toast.LENGTH_LONG).show();
        return false;

    }
}
