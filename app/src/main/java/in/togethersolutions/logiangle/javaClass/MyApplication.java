package in.togethersolutions.logiangle.javaClass;

import android.app.Application;

import in.togethersolutions.logiangle.receivers.ConnectivityReceiver;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
       // System.out.println("1112");
    }


}
