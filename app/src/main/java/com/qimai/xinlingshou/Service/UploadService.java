package com.qimai.xinlingshou.Service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.qimai.xinlingshou.App;
import com.qimai.xinlingshou.bean.ordersBean;
import com.qimai.xinlingshou.utils.Xutils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglei on 18-7-16.
 */

public class UploadService extends IntentService{


    public UploadService(){
        super("upload");


    }
    private static final String TAG = "UploadService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "onHandleIntent: ");
        updateOldOrder();

    }

    private void updateOldOrder() {

        ArrayList<ordersBean> ordersBeanArrayList = (ArrayList<ordersBean>)
                LitePal.where("isauto=? and server_order_no not null","0").find(ordersBean.class);


        if (ordersBeanArrayList!=null&&ordersBeanArrayList.size()>0){

            for (ordersBean o:
                    ordersBeanArrayList) {
                uploadDateToServe(o);

            }

        }




    }
    private void uploadDateToServe(final ordersBean mOrderbean) {
        App.store.put("BeforeUpload", mOrderbean.toString());
        App.store.commit();
        //mOrderbean.setOrderInfo("");
/*

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson =gsonBuilder.create();


       try {
           //App.store.put("testgson", gson.toJson(mOrderbean));
          // App.store.commit();
       }catch (Exception e){

          // App.store.put("testError", e.getMessage()+"  "+e.toString()+"  "+e.getCause());
          // App.store.commit();

       }
        String pos_order = gson.toJson(mOrderbean);

        App.store.put("testgson", gson.toJson(mOrderbean));
         App.store.commit();
       Log.d(TAG, "uploadDateToServe: gson= "+gson.toJson(mOrderbean));*/
        //pos_order
        Map<String,String> map = new HashMap<>();
        map.put("user_id",mOrderbean.getUserid());
        map.put("total_amount",mOrderbean.getTotal_amount());
        map.put("amount",mOrderbean.getAmount());
        map.put("user_remarks","abcdef");
        map.put("seller_remarks",mOrderbean.getSeller_remarks());
        map.put("trade_no",mOrderbean.getService_orderId());
        map.put("payment_id",mOrderbean.getPayment_id());
        map.put("minus_amount",mOrderbean.getMinus_amount());
        map.put("card_minus",mOrderbean.getCard_minus());
        map.put("coupon_minus",mOrderbean.getCoupon_minus());
        map.put("card_id",mOrderbean.getCard_id());
        map.put("coupon_id",mOrderbean.getCoupon_id());
        map.put("items",mOrderbean.getOrderInfo());

        // Map<String,String>map = new HashMap<>();
        // map.put("pos_order",pos_order);


    /*    HttpLoggingInterceptor ok = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

                Log.d(TAG, "log: message= "+message);
            }
        });
        ok.setLevel(HttpLoggingInterceptor.Level.BODY);

        FormBody.Builder formBody = new FormBody.Builder();

        Set<Map.Entry<String,String>> entries = map.entrySet();

        *//*for (Map.Entry<String,String> entry:entries){

            formBody.add(entry.getKey(),entry.getValue());
        }*//*


        Request.Builder builder = new Request.Builder()
                .post(formBody.build())

                .url(App.API_URL +
                        App.API_RECEIVE);

        builder.addHeader("Accept","**///*; v=1.0");
      /*  builder.addHeader("Referer", App.API_URL);
        builder.addHeader("Qm-From","android");
        builder.addHeader("Qm-From-Type","reta");
        builder.addHeader("Qm-Account-Token",App.store.getString("cookie_auth"));
        builder.addHeader("User-Agent","wechatdevtools appservice port/62739");
        Request request = builder.

                build();
        //        .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()

                .addNetworkInterceptor(ok)
                .build();

        okHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
*/
        Xutils.getInstance().post(App.API_URL +
                App.API_RECEIVE, map, new Xutils.XCallBack() {
            @Override
            public void onResponse(String result) {

                Log.d(TAG, "onResponse: result= "+result);
                App.store.put("uploadresp",result);
                App.store.commit();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");

                    if (status.equals("true")){

                        mOrderbean.setIsauto("1");

                        JSONObject datas = jsonObject.getJSONObject("data");
                        JSONObject order = datas.getJSONObject("order");


                        String order_no = order.getString("order_no");

                        Log.d(TAG, "onResponse: order= "+order_no);
                        if (!TextUtils.isEmpty(order_no)){

                            mOrderbean.setServer_order_no(order_no);
                        }

                        mOrderbean.save();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                App.store.put("upload",result);
                App.store.commit();
                // ToastUtils.showShortToast("result= "+result);

            }
        });


    }

}