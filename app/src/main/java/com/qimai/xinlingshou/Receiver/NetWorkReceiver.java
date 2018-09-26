package com.qimai.xinlingshou.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.qimai.xinlingshou.Service.UploadService;

/**
 * Created by wanglei on 18-7-13.
 */

public class NetWorkReceiver extends BroadcastReceiver {

    private static final String TAG = "NetWorkReceiver";
    NetWorkSucess netWorkSucess;


    public void setNetWorkSucess(NetWorkSucess netWorkSucess){

        this.netWorkSucess = netWorkSucess;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){

            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo!=null){


                //连接成功
                if (networkInfo.getState()==NetworkInfo.State.CONNECTED&&networkInfo.isAvailable()){


                    Log.d(TAG, "onReceive: network");

                    Intent intent1 = new Intent(context,UploadService.class);
                    context.startService(intent1);
                    new UploadService("upload");

                    if (netWorkSucess!=null){


                        netWorkSucess.netConnect();




                    }



                }else{
                    Log.d(TAG, "onReceive: no network");

                }


            }


        }

    }

    public interface NetWorkSucess{

        void netConnect();

    }

}
