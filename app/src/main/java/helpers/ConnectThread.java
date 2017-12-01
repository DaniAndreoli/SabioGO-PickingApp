package helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Dani on 01/12/2017.
 */

public class ConnectThread extends Thread{
    private BluetoothServerSocket mmServerSocket;
    private static final UUID MY_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    public ConnectThread() {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetoothAdapter.getAddress());

            if (mBluetoothAdapter != null) {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(device.getName(), MY_UUID);
            }
            mmServerSocket = tmp;
        }catch (IOException e) {
        }
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    //manageConnectedSocket(socket);
                    mmServerSocket.close();
                    break;
                }} catch (IOException e) {
                break;
            }

        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
//    final String TAG="ConnectThread";
//    private ReadThread mReadThread = null;
//    private final InputStream mmInStream;
//    private final OutputStream mmOutStream;
//
//    private boolean isDeviceConnected;
//
//
//    public final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private BluetoothSocket mmSocket = null;
//    Handler mHandler;
//    BluetoothDevice bTdevice;
//    private DataInputStream mReadData = null;
//
//
//
//    public ConnectThread(BluetoothDevice bTdevice, Handler mHandler) {
//        super();
//        this.bTdevice = bTdevice;
//        this.mHandler = mHandler;
//
//        InputStream tmpIn = null;
//        OutputStream tmpOut = null;
//
//        BluetoothSocket socket;
//        try {
//            socket = bTdevice.createRfcommSocketToServiceRecord(MY_UUID);
//            System.out.println("**** Socket created using standard way******");
//            tmpIn = socket.getInputStream();
//            tmpOut = socket.getOutputStream();
//            mmSocket = socket;
//
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        mmInStream = tmpIn;
//        mmOutStream = tmpOut;
//    }
//
//    @Override
//    public synchronized void run() {
//        // TODO Auto-generated method stub
//        super.run();
//        // Get a BluetoothSocket to connect with the given BluetoothDevice
//
//        try {
//
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            if (adapter != null) {
//                adapter.cancelDiscovery();
//                Log.i("***Bluetooth Adapter**", "Bluetooth Discovery Canceled");
//            }
//
//            if (mmSocket != null) {
//
//                mmSocket.connect();
////                Log.i("***Socket Connection Successful**", "Socket Connection Successful");
//                isDeviceConnected = true;
//
//                mReadData = new DataInputStream(mmSocket.getInputStream());
//                Log.i("***Read data**", "" + mReadData);
//
//
//
//                if (mReadThread == null) {
//                    mReadThread=new ReadThread(mReadData,mmSocket);
//                    mReadThread.start();
//                }
//
//
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            Log.e("***Error**", "Socket Connection failed");
//            e.printStackTrace();
//            try {
//                mmSocket.close();
//                isDeviceConnected = false;
//            } catch (IOException closeException) {
//                e.printStackTrace();
//
//            }
//
//        }
//
//        // mHandler.obtainMessage(DisplayBtdataActivity.SUCCESS_CONNECT,mmSocket).sendToTarget();
//
//    }
//
//    /** Will cancel an in-progress connection, and close the socket */
//    public void cancel() {
//        try {
//            mmSocket.close();
//        } catch (IOException e) {
//        }
//    }
//
//
//
//
//
//    // Read the data from device
//
//    private class ReadThread extends Thread {
//
//        /** The input. */
//        private DataInputStream input;
//
//        /**
//         * Constructor for ReadThread.
//         *
//         * @param input
//         *            DataInputStream
//         */
//        private BluetoothSocket mSocket;
//
//        public ReadThread(DataInputStream input, BluetoothSocket socket) {
//            this.input = input;
//            this.mSocket = socket;
//        }
//
//        /**
//         * Method run.
//         *
//         * @see java.lang.Runnable#run()
//         */
//        public synchronized void run() {
//            try {
//                Log.d(TAG, "ReadThread run");
//
//                byte[] buffer = new byte[1024]; // buffer store for the stream
//                int bytes; // bytes returned from read()
//                //bytes = input.available(); // always return 0
//                // bytes = mReadData.readInt();
//                //Log.i("***Bytes  data**", "" + bytes);// print 0
//                Log.i("***Data input stream**", "" + input); // Here input is not null
//
//                if (input != null) {
//                    Log.i("***hello world**", "...");
//                    while (isDeviceConnected) {
//                        try {
//
//                            bytes = input.read(buffer);     // this code never executes
//
//                            Log.i("**bytes data**", " " + bytes);
//
//                            if (input != null) {
//                                int len = input.readInt();
//                                Log.i(TAG, "Response Length: " + len);
//
//                                if (len > 65452) {// Short.MAX_VALUE*2
//                                    Log.i(TAG, "Error: Accesory and app are not in sync.");
//                                    continue;
//                                }
//
//                                Log.d(TAG, "Response Length: " + len);
//                                Log.d(TAG, "Reading start time:" + System.currentTimeMillis());
//                                byte[] buf = new byte[len];
//                                Log.d(
//
//                                        TAG, "input.available() " + input.available());
//                                if (input.available() > 0) {
//
//                                    input.readFully(buf);
//                                    System.out.println("Output:=");
//
//
//                                }
//
//                                Log.d(TAG, "Reading end time:" + System.currentTimeMillis());
//                            }
//
//                        } catch (Exception e) {
//                            Log.e(TAG, e.getMessage());
//                            isDeviceConnected = false;
//
//                        }
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                isDeviceConnected = false;
//                Log.e(TAG, "catch block 3 " + e.toString());
//
//            }
//        }
//
//    }

}
