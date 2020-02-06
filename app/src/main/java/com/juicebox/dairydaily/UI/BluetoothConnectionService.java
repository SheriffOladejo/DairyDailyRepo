package com.juicebox.dairydaily.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;


public class BluetoothConnectionService {

    private static final String TAG = "Message";
    private static final String appName = "MYAPP";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//8ce255c0-200a-11e0-ac64-0800200c9a66

    private final BluetoothAdapter mBluetoothAdapter;

    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Activity activity;

    private Context mContext;

    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    private ProgressDialog progressDialog;


    public BluetoothConnectionService(Context context, Activity activity) {
        this.activity = activity;
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        start();
    }

    /*
     * Start the communication service. Specifically start AcceptThread to begin a
     * session in listening(server) mode. Called by the Activity onResume()
     * */
    private synchronized void start(){
        Log.d(TAG, "start");

        //Cancel any thread attempting to make a connection
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        //Check for an existing AcceptThread
        if(mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    /* This thread runs while listening for incoming connections. It behaves
       like a server-side client. It runs until a connection is accepted
       (or until cancelled)
     * AcceptThread starts and sits waiting for a connection
     * Then ConnectThread starts and attempts to make a connection with the other device's AcceptThread
     * */
    class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        AcceptThread() {
            BluetoothServerSocket tmp = null;

            //create a new listening server socket
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using" + MY_UUID_INSECURE);
            } catch (Exception e) {
                Log.d(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread running");
            BluetoothSocket socket = null;
            try {
                //This is a blocking call and will only return on a
                //successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start...");
                //Accept a connection
                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection");
            } catch (Exception e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if (socket != null) {
                connected(socket, mmDevice);
            }
            Log.i(TAG, "END mAcceptThread ");

        }
        public void cancel() {
            Log.d(TAG, "cancel: Cancelling AcceptThread");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

    /*
        This thread runs while attempting to connect to make an outgoing connection
        with a device. It runs straight through; the connection either
        fails or succeeds.
    */

    private class ConnectThread extends Thread{
        private BluetoothSocket socket;

        ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            //Get a BluetoothSocket for a connection with the given BluetoothDevice

            try{
                Log.d(TAG, "ConnectThread: Trying to create InsecureRFcommSocket using UUID: " + deviceUUID);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            }catch(IOException e){
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket: " + e.getMessage());
            }
            socket = tmp;

            //Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            /*Make a connection to the BluetoothSocket
             *This is a blocking call and will only return on a
             *successful connection or an exception.
             */
            try {
                socket.connect();
                connected(socket, mmDevice);
                Log.d(TAG, "run: ConnectThread connected");
            } catch (IOException e) {
                // Close the socket
                //Toast.makeText(mContext, "Unable to connect devices", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "run: ConnectThread unable to connect " + e.getMessage());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Unable to connect with " + mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    socket.close();
                    Log.d(TAG, "Closed socket");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
            }
        }
        void cancel(){
            try{
                Log.d(TAG, "cancel: Closing Client Socket");
                socket.close();
            }catch(IOException e){
                Log.e(TAG, "cancel: close() of socket in ConnectThread failed. " + e.getMessage());
            }
        }
    }


    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started");
        //initprogress dialog
        progressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please Wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();

    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: starting");

            mmSocket = socket;
            InputStream tmpin = null;
            OutputStream tmpout = null;

            try {
                tmpin = mmSocket.getInputStream();
                tmpout = mmSocket.getOutputStream();
                progressDialog.dismiss();
                Log.d(TAG, "ConnectedThread: Devices connected");

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Connected to " + mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "ConnectedThread: Streams init");
            } catch (IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Unable to connect to " + mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "ConnectedThread: Error occured. Unable to init streams");
//                Toast.makeText(mContext, "An error occured, please try again", Toast.LENGTH_SHORT).show();
            }
            inputStream = tmpin;
            outputStream = tmpout;
        }

        public void run(){
            byte[] buffer = new byte[2048]; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while(true){
                // Read from the InputStream
                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, incomingMessage);

                    Intent intent = new Intent("incomingMessage");
                    intent.putExtra("theMessage", incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } catch (IOException e) {
                    Log.e(TAG, "read: Error reading InputStream: "+ e.getMessage());
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device i.e write to the OutputStream
        void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                if(DashboardActivity.bluetoothAdapter != null){
                    outputStream.write(bytes);
                    Log.e(TAG, "Printing");
                    Toast.makeText(mContext,"Printing...", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext,"Cannot connect to printer.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(mContext,"Unable to print", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "write: Error writing to outputstream: " + e.getMessage());
            }
        }

        //Call this from the main activity to cancel the connection
        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void connected(BluetoothSocket socket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: starting");
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write*
     * @see ConnectedThread#write(byte[])
     * */
    public void write(byte[] out){
        // Create temporary object
        //Synchronise a copy of the ConnectedThread
        Log.d(TAG, "write: write called");
        //perform the write
        mConnectedThread.write(out);
    }

}

































