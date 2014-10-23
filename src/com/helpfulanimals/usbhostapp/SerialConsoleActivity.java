	
/* Copyright 2011 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: http://code.google.com/p/usb-serial-for-android/
 */

package com.helpfulanimals.usbhostapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
/**
 * Monitors a single {@link UsbSerialDriver} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

    private final String TAG = SerialConsoleActivity.class.getSimpleName();

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialDriver)}.
     *
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    private static UsbSerialDriver sDriver = null;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private StringBuilder result;
    //implementing GraphView
    private GraphView graphView;
    private GraphViewSeries dataSeries;
    private double graph2LastXValue;
    private String dataKeeper;
    private Double dataToAppend;
    private Double countHolder;

   

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SerialConsoleActivity.this.updateReceivedData(data);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_console);
       /* mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mDumpTextView.setTextSize(30);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);*/
  
        dataKeeper = new String();
        dataKeeper = null;
        dataToAppend = 0d;
        countHolder = 0d;
        //implementing GraphView
        dataSeries = new GraphViewSeries(new GraphViewData[] {
                new GraphViewData(0, 0)
                /*, new GraphViewData(2, 1.5d)
                , new GraphViewData(2.5, 3.0d) // another frequency
                , new GraphViewData(3, 2.5d)
                , new GraphViewData(4, 1.0d)
                , new GraphViewData(5, 3.0d)*/
});
        graphView = new LineGraphView(this, "");
        graphView.addSeries(dataSeries);
        graphView.setViewPort(1, 8);
        graphView.setScalable(true);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter(){
        		int count = 0;
        	   @Override
        	   public String formatLabel(double value, boolean isValueX) {
        	      if (isValueX) {
        	    	  
        	    	  count++;
        	         return Integer.toString(count);
        	      }
        	      return null; // let graphview generate Y-axis label for us
        	   }
        	});
       // graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
        graphView.getGraphViewStyle().setVerticalLabelsWidth(300);
        graphView.getGraphViewStyle().setNumHorizontalLabels(4);
        graphView.getGraphViewStyle().setNumVerticalLabels(4);
      LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
        layout.addView(graphView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sDriver != null) {
            try {
                sDriver.close();
            } catch (IOException e) {
                // Ignore.
            }
            sDriver = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, sDriver=" + sDriver);
        if (sDriver == null) {
            //mTitleTextView.setText("No serial device.");
        } else {
            try {
                sDriver.open();
                sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
               // mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sDriver.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sDriver = null;
                return;
            }
           // mTitleTextView.setText("Serial device: " + sDriver.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sDriver != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sDriver, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }
    private double getRandom() {
        double high = 3;
        double low = 0.5;
        return Math.random() * (high - low) + low;
    }
    private void updateReceivedData(byte[] data) {
       // final String message = /*"Read " + data.length + " bytes: \n"
         //      + */HexDump.dumpHexString(data) + "\n\n";
        
        //mDumpTextView.append(message);
        //mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
      
        //implement appendData for graphView
    	
    	double placeHolder = dataToAppend;
    	
        graph2LastXValue += 1d;
        dataKeeper = HexDump.dumpHexString(data);
        int count = dataKeeper.length();
        
        result = new StringBuilder();
        int y;
        
        for (y = 0; y<count-1; y++){
        	if(dataKeeper.charAt(y)==' ')
        		continue;
        	else{
        		break;
        		}
        }
     
        if(y<count)
        	while(y<count&& dataKeeper.charAt(y)!='.')
        		result.append(dataKeeper.charAt(y++));
        
        
        if(result.toString() !=""){
        		dataToAppend = Double.parseDouble(result.toString());
        }
        
     if(dataToAppend!=null)
        dataSeries.appendData(new GraphViewData(graph2LastXValue, dataToAppend), true, 10);
     	
      
     result = null;
       //dataToAppend= 0d;
       
    }

    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     * @param driver
     */
    static void show(Context context, UsbSerialDriver driver) {
        sDriver = driver;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}
