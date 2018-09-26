package com.qimai.xinlingshou.fragment.left;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.qimai.xinlingshou.App;
import com.qimai.xinlingshou.BaseFragment;
import com.qimai.xinlingshou.R;
import com.qimai.xinlingshou.Receiver.NetWorkReceiver;
import com.qimai.xinlingshou.activity.MainActivity;
import com.qimai.xinlingshou.activity.ViceActivity;
import com.qimai.xinlingshou.adapter.AdapterDiffCallBack;
import com.qimai.xinlingshou.adapter.GoodsSelectAdapter;
import com.qimai.xinlingshou.bean.BalancePayBean;
import com.qimai.xinlingshou.bean.DiscountBean;
import com.qimai.xinlingshou.bean.MoneyBean;
import com.qimai.xinlingshou.bean.PrintInfoBean;
import com.qimai.xinlingshou.bean.SecondScreenInfo;
import com.qimai.xinlingshou.bean.VipInfo;
import com.qimai.xinlingshou.bean.goodsBean;
import com.qimai.xinlingshou.bean.orderIteminfo;
import com.qimai.xinlingshou.bean.ordersBean;
import com.qimai.xinlingshou.calculate.CrashSuper;
import com.qimai.xinlingshou.calculate.MianzhiCrash;
import com.qimai.xinlingshou.calculate.NormalCrash;
import com.qimai.xinlingshou.calculate.VipCrashSuper;
import com.qimai.xinlingshou.calculate.ZhekouCrash;
import com.qimai.xinlingshou.callback.BlancePayCallBack;
import com.qimai.xinlingshou.callback.NetWorkCallBack;
import com.qimai.xinlingshou.dialog.BaseDialogFragment;
import com.qimai.xinlingshou.dialog.PayOrderBalanceDialogFragment;
import com.qimai.xinlingshou.dialog.PayOrderMultipleDialogFragment;
import com.qimai.xinlingshou.fragment.right.MessageEvent;
import com.qimai.xinlingshou.fragment.right.RightFragmentType;
import com.qimai.xinlingshou.model.PayModel;
import com.qimai.xinlingshou.utils.APPUtil;
import com.qimai.xinlingshou.utils.AidlUtil;
import com.qimai.xinlingshou.utils.DataModel;
import com.qimai.xinlingshou.utils.DecimalFormatUtils;
import com.qimai.xinlingshou.utils.ESCUtil;
import com.qimai.xinlingshou.utils.Hint;
import com.qimai.xinlingshou.utils.RandomUntil;
import com.qimai.xinlingshou.utils.SharePreferenceUtil;
import com.qimai.xinlingshou.utils.ToastUtils;
import com.qimai.xinlingshou.utils.UPacketFactory;
import com.qimai.xinlingshou.utils.Xutils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.nostra13.universalimageloader.utils.L;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.ICheckFileCallback;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.callback.QueryCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.DataPacket;

import static com.qimai.xinlingshou.fragment.right.MessageEvent.CALCULATEDISCOUNT;
import static com.qimai.xinlingshou.fragment.right.MessageEvent.CANCELCOUPONS1;
import static com.qimai.xinlingshou.fragment.right.MessageEvent.CANCELCOUPONS2;
import static com.qimai.xinlingshou.fragment.right.MessageEvent.UPDATEDISCOUNT;

/**
 * Created by NIU on 2018/5/18.
 */

public class Left_crashier_fragment extends BaseFragment
        implements GoodsSelectAdapter.onItemClick,NetWorkReceiver.NetWorkSucess
,BlancePayCallBack{


    public static final int WECHAT_ZFB = 1;
    public static final int CRASH = 2;
    public static final int BIAOJI = 3;
    public static final int BALANCE = 4;
    public static final int MULTIPLE = 5;


    int selectPosition;
    private static final String TAG = "Left_crashier_fragment";
    @BindView(R.id.rv_select_goods)
    RecyclerView rvSelectedGoods;
    String isPay="0";
    View alphaView;
    String out_trade_no;
    ordersBean mOrderbean;
    ZLoadingDialog dialog = new ZLoadingDialog(getActivity());
    String payment_id="0";
    String payType;
    private DSKernel mDSKernel = null;
    int payMethod  = 0;
     String order;
    int number = 1;
    MoneyBean moneyBean = null;
    DiscountBean discountBean = null;
    String select_card_id;
    String select_coupons_id;

    //实际上收的钱
    private String acual_collect_money = "";
    //找零
    private String zhaoling = "";
    VipInfo vipInfo;
    boolean isPaySucess;
    PrintInfoBean printInfoBean;

    //扫码支付对话框
    Dialog payTipsDialog;
    final Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            number= number +1;
            if (number<=15){
               // ToastUtils.showLongToast("执行："+number);
                LoadOrder();
                handler.postDelayed(this, 5000);
            }else {
                handler.removeCallbacks(runnable);

            }

        }
    };

    //559000
    //上一次的旧数据
    ArrayList<goodsBean> oldSelectedGoodsArrayList;

    //本次的新数据
    ArrayList<goodsBean> selectedGoodsArrayList;

    GoodsSelectAdapter goodsSelectAdapter;

    LinearLayoutManager linearLayoutManager;

    //存储当前商品Id,与数量
    Map<String, Integer> goodsSelectMap;

    //商品id 对应选择的商品
    Map<String, goodsBean> selectedGoodsMap;

    //挂单id对应所选商品
    Map<Integer,  Map<String, goodsBean>>pendOrderMap;
    //会员优惠信息
    ordersBean selectedOrderMap;
    //对应挂单次数
    private int pendOrderId;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.ll_top)
    RelativeLayout llTop;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.tv_total_discount)
    TextView tvTotalDiscount;
    @BindView(R.id.tv_all_cancel)
    TextView tvAllCancel;
    @BindView(R.id.tv_all_collection)
    TextView tvAllCollection;
    @BindView(R.id.ll_bottom_menu)
    LinearLayout llBottomMenu;
    @BindView(R.id.ll_empty_goods)
    LinearLayout llEmptyGoods;
    @BindView(R.id.rl_youhuiquan)
    RelativeLayout rlYouhuiquan;
    @BindView(R.id.youhuimoney)
    TextView youhuimoney;
    private onPendingOrderSucess onPendingOrderSucess;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @BindView(R.id.ll_goods_items)
    RelativeLayout llGoodsItems;
    double totalMoney = 0.00;
    double youhuiMoney = 0.00;
    goodsBean goodsBean;

    private String oldType;
    private String oldAddInfo;
    JSONObject storeinfo;

    PayModel payModel;
    //这个userId只有扫码支付的时候才有值，在退出登录时候清空
    String user_id;
    BalancePayBean balancePayBean;
    double testMoney = 12;
    public static final boolean isMain = Build.MODEL.equals("t1host") || Build.MODEL.equals("T1-G");
    private final String imgKey = "MAINSCREENIMG";
    //轮播图文件在本地缓存key
    private final String imgsKey = "MAINSCREENIMGS";
    /**
     * 轮播图集合
     * slide collection
     */
    List<String> imgs = new ArrayList<>();
    private MyHandler myHandler;
    private List<String> products = new ArrayList<>();
    private List<String> prices = new ArrayList<>();
    NetWorkReceiver netWorkReceiver;


    ExecutorService executorService;
    private IConnectionCallback mIConnectionCallback = new IConnectionCallback() {
        @Override
        public void onDisConnect() {
            Message message = new Message();
            message.what = 1;
            message.obj = "与远程服务连接中断";
            myHandler.sendMessage(message);
        }

        @Override
        public void onConnected(ConnState state) {
            Message message = new Message();
            message.what = 1;
            switch (state) {
                case AIDL_CONN:
                    message.obj = "与远程服务绑定成功";
                    break;
                case VICE_SERVICE_CONN:
                    message.obj = "与副屏服务通讯正常";
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = null;
                            if (!isMain) {
                                intent1 = new Intent(getActivity(), ViceActivity.class);
                            }
//                            startActivity(intent1);
                        }
                    }, 2000);
                    break;
                case VICE_APP_CONN:
                    message.obj = "与副屏app通讯正常";
                    break;
                default:
                    break;
            }
            myHandler.sendMessage(message);
        }
    };

    @Override
    public void netConnect() {


        updateOldOrder();

    }

    //余额支付成功回掉
    @Override
    public void onPaySucess(int type,BalancePayBean balancePayBean1) {

       //setCancelAndClooectionEnable(true);

        //混合支付
        if (type==BaseDialogFragment.BALANCE_MULTIP_PAY){

            //((MainActivity)activity).showRightCrashierLayout();


            //NotificationCrashFragment(balancePayBean.getOrderLeaveMoney());

            showMultiPayNextDialog(balancePayBean1);


            //单余额支付
        }else if (type==BaseDialogFragment.BALANCE_PAY){


            ((MainActivity)activity).showPaySucessLayout();

            Log.d(TAG, "onPaySucess: orderBean= "+mOrderbean.toString());


            if (payModel==null){

                payModel = new PayModel();

            }
            //支付完成同步订单完成状态

            payModel.asyncBalanceOrder(mOrderbean.getOrder(), "", ""
                    , new NetWorkCallBack() {
                        @Override
                        public void onSucess(Object data) {


                        }

                        @Override
                        public void onFailed(String msg) {

                            ToastUtils.showShortToast(msg);
                        }
                    }
            );

            Log.d(TAG, "onPaySucess: balancePayBean1= "+balancePayBean1.toString());
            NotificationPaySucessFragment(balancePayBean1);
            balancePay();

        }

    }

    /** 混合支付后 剩余部分需要支付的弹窗
     * @param balancePayBean
     */
    private void showMultiPayNextDialog(final BalancePayBean balancePayBean) {

        final Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);

     View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_multiplte_next_pay,null);


     dialog.setContentView(view);

        TextView  textView = (TextView) view.findViewById(R.id.tv_leave_money);


        Log.d(TAG, "showMultiPayNextDialog: = "+balancePayBean.getOrderLeaveMoney());
        textView.setText(balancePayBean.getOrderLeaveMoney()+"元");
        view.findViewById(R.id.tv_next)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((MainActivity) getActivity()).showPayFragment();

                        NotificationCrashFragment(balancePayBean.getOrderLeaveMoney());
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }


    /**
     * 向支付成功界面发送信息
     * */
    private void NotificationPaySucessFragment(BalancePayBean balancePayBean) {

        MessageEvent messageEvent = new MessageEvent(MessageEvent.BLANCE_PAY);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MessageEvent.BLANCE_PAY,balancePayBean);
        messageEvent.setBundle(bundle);

        EventBus.getDefault().post(messageEvent);
    }


    /***
     * 余额支付完成打印
     * */
    private void balancePay() {

        printOrder("标记支付","");

    }

    @Override
    public void onPayCancel() {

        setCancelAndClooectionEnable(true);

    }

    @Override
    public void onPayFailed(String msg) {


        ToastUtils.showShortToast(msg);

    }

    private static class MyHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() != null && !mActivity.get().isFinishing()) {
                switch (msg.what) {
                    case 1://消息提示用途
//                        Toast.makeText(mActivity.get(), msg.obj + "", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }

    }

    private IReceiveCallback mIReceiveCallback = new IReceiveCallback() {
        @Override
        public void onReceiveData(DSData data) {

        }

        @Override
        public void onReceiveFile(DSFile file) {

            Log.d(TAG, "onReceiveFile: file= "+file.path);
        }

        @Override
        public void onReceiveFiles(DSFiles files) {

        }

        @Override
        public void onReceiveCMD(DSData cmd) {

        }
    };
    private IReceiveCallback mIReceiveCallback2 = new IReceiveCallback() {
        @Override
        public void onReceiveData(DSData data) {

        }

        @Override
        public void onReceiveFile(DSFile file) {

        }

        @Override
        public void onReceiveFiles(DSFiles files) {

        }

        @Override
        public void onReceiveCMD(DSData cmd) {

        }
    };

    @Override
    protected void initData() {

        printInfoBean = new PrintInfoBean();

        executorService = Executors.newCachedThreadPool();
        oldSelectedGoodsArrayList = new ArrayList<>();
        netWorkReceiver = new NetWorkReceiver();


        //展示对话框



        //设置个倒计时，每隔1小时上传一次数据

        Observable.interval(10, TimeUnit.HOURS)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Long>>() {
                    @Override
                    public ObservableSource<? extends Long> apply(Throwable throwable) throws Exception {

                        Log.d(TAG, "apply: throwable= "+throwable.getMessage());
                        return Observable.interval(10, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {


                        Log.d(TAG, "accept: aLong= "+aLong);
                        updateOldOrder();
                    }
                });





        //首次先同步数据库中未上传订单

        updateOldOrder();


        //List<ordersBean> orderIteminfoList = LitePal.findAll(ordersBean.class);

        /*for (int i = 0;i<orderIteminfoList.size();i++) {
            Log.d(TAG, "initData: "+(orderIteminfoList.get(i).getOrderIteminfoList().size())
            +" "+orderIteminfoList.get(i).getService_orderId());

        }*/




        initSdk();

        pendOrderMap = new HashMap<>();
        selectedGoodsMap = new LinkedHashMap<>();
        selectedOrderMap = new ordersBean();
        selectedGoodsArrayList = new ArrayList<>();
        goodsSelectMap = new HashMap<>();
        goodsSelectAdapter = new GoodsSelectAdapter(activity, selectedGoodsArrayList);
        goodsSelectAdapter.setOnItemClick(this);
        linearLayoutManager = new LinearLayoutManager(activity);
        rvSelectedGoods.setLayoutManager(linearLayoutManager);
        rvSelectedGoods.addItemDecoration(new DividerItemDecoration(activity,DividerItemDecoration.VERTICAL));
        rvSelectedGoods.setAdapter(goodsSelectAdapter);

        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_shoukuan.png");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg");
//        imgs.add(Environment.getExternalStorageDirectory().getPath() + "/img_02.png");





    }

    private void updateOldOrder() {

    ArrayList<ordersBean> ordersBeanArrayList = (ArrayList<ordersBean>) LitePal.where("isauto=? and server_order_no not null","0").find(ordersBean.class);


    if (ordersBeanArrayList!=null&&ordersBeanArrayList.size()>0){

        for (ordersBean o:
             ordersBeanArrayList) {
            uploadDateToServe(o);

        }

    }




    }


    @Override
    protected void initView(View rootView) {






        EventBus.getDefault().register(this);
        alphaView = LayoutInflater.from(activity)
                .inflate(R.layout.alpha_background_view, null);


        initEvent();

    }


    private void initEvent() {

        RxTextView.textChanges(tvTotalMoney)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {

                        Log.d(TAG, "accept: charSequence= "+charSequence.toString());

                        MessageEvent messageEvent = new MessageEvent(MessageEvent.TOTALMONEY);


                        if (!isPaySucess&&!charSequence.toString().contains("0.00")){

                            sendOrderListToSecondScreen(SecondScreenInfo.UPDATE);
                        }
                        if (charSequence.toString().contains("0.00")){

                            messageEvent.setStringValues(charSequence.toString());
                            EventBus.getDefault().post(messageEvent);

                        }

                    }
                });

        RxTextView.textChanges(tvTotalDiscount).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {

                Log.d(TAG, "accept: charSequence22= "+charSequence.toString());

                MessageEvent messageEvent = new MessageEvent(MessageEvent.TOTALDISCOUNT);



                if (!isPaySucess&&!charSequence.toString().contains("0.00")){

                    sendOrderListToSecondScreen(SecondScreenInfo.UPDATE);
                }

                if (!charSequence.toString().contains("0.00")){

                    messageEvent.setStringValues(charSequence.toString());
                    EventBus.getDefault().post(messageEvent);

                }

            }
        });


    }

    @Override
    protected int getLayout() {
        return R.layout.cashier_left_fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.tv_all_collection, R.id.iv_menu, R.id.rl_youhuiquan})
    public void onViewClicked1(View view) {

        switch (view.getId()) {
            case R.id.tv_all_collection:

                //点击收款时候副屏应该显示 请出示支付宝/微信二维码....

                sendOrderListToSecondScreen(SecondScreenInfo.BEGINPAY);
   //13516492591
                //收款时 整单取消和收款不能点击
                setCancelAndClooectionEnable(false);

                MessageEvent messageEvent = new MessageEvent("crashPay");

                String totalMoney1 = tvTotalMoney.getText().toString();

                if (totalMoney1.contains("¥")){
                    Log.d(TAG, "onViewClicked1111: before totalMoney1= "+totalMoney1);

                    totalMoney1 = totalMoney1.replace("¥ ","");


                    Log.d(TAG, "onViewClicked11111: after totalMoney1= "+totalMoney1);
                }


                //如果有用户id 就先判断是否余额支付

                Log.d(TAG, "onViewClicked1: user_id= "+user_id);
                if (!TextUtils.isEmpty(user_id)){

                    //先进行网络请求判断余额是否存在，如果存在就去余额支付

                    //把用户id加上
                    //mOrderbean.setUserid(user_id);
                    veriftyAccountBalancePay(user_id);


                 /*   if (testMoney>Double.parseDouble(totalMoney1)){

                        payDialogFragment = PayOrderBalanceDialogFragment.getInstance(totalMoney1);

                    }else{

                        payDialogFragment = PayOrderMultipleDialogFragment.getInstance(totalMoney1);


                    }*/
                    /*payDialogFragment.setBlancePayCallBack(this);

                        payDialogFragment.show(getChildFragmentManager(),"pay");*/


                }else{


                    //这里就是通知右边现金收款应收多少
                    NotificationCrashFragment(totalMoney1);
                    //不存在，就进行是普通支付
                    ((MainActivity) getActivity()).showPayFragment();



                }



                App.store.put("goods",selectedGoodsMap.toString());
                App.store.commit();
                L.d("BBBBBB",selectedGoodsMap.toString());


//                String totalMoney = tvTotalMoney.getText().toString();

               /* if (totalMoney.length()!=0){

                    double money = Double.parseDouble(totalMoney);
                    messageEvent.setTotalMoney(money);
                    //必须发两个，一个黏性，一个非黏性
                    EventBus.getDefault().postSticky(messageEvent);
                    EventBus.getDefault().post(messageEvent);
                    Log.d(TAG, "onViewClicked1: send finish");
                }*/


                break;


            case R.id.rl_youhuiquan:
                rlYouhuiquan.setVisibility(View.GONE);
                tvTotalDiscount.setText("0.00");
                MessageEvent messageEvent1 = new MessageEvent(CANCELCOUPONS2);

                EventBus.getDefault().post(messageEvent1);

                break;
            case R.id.iv_menu:



                ((MainActivity) activity).openSlideMenu();

                /*View view1 = LayoutInflater.from(activity)
                        .inflate(R.layout.slide_pop_view,null);
                //弹出一个popWindow
                PopupWindow popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

                //设置popwindow type ji
                popupWindow.setWindowLayoutType(WindowManager.LayoutParams
                .TYPE_TOAST);

                view1.findViewById(R.id.item2)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, SettingActivity
                                .class);
                                activity.startActivity(intent);
                            }
                        });
                popupWindow.setAnimationStyle(R.style.AnimationLeftFade);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        removeAlphaView();
                    }
                });

                setWindowBackGround();
                popupWindow.showAtLocation(ivMenu, Gravity.NO_GRAVITY,0,0);

*/

               /* SlideMenuPopupWindow.show(activity,ivMenu,R.layout.slide_pop_view
                );

                setWindowBackGround();*/

                /*SlideMenuPopupWindow.setOnItem1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity, MainActivity.class));
                         SlideMenuPopupWindow.disimiss();
                    }
                });

                SlideMenuPopupWindow.setOnItem2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity, SettingActivity.class));
                        //SlideMenuPopupWindow.disimiss();

                    }
                });
                SlideMenuPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        removeAlphaView();
                    }
                });
*/

                break;
        }


    }

    /**
     * 向CrashFragment发送订单金额
     * */

    private void NotificationCrashFragment(String money) {


        MessageEvent messageEvent = new MessageEvent("crashPay");

        messageEvent.setTotalMoney(Double.parseDouble(money));
        EventBus.getDefault().post(messageEvent);

    }


    /***
     * 验证余额是否存在 及进行下一步操作
     * */
    private void veriftyAccountBalancePay(final String user_id) {


        if (payModel==null){

            payModel = new PayModel();
        }

        payModel.queryBlance(user_id, new NetWorkCallBack<String>() {
            @Override
            public void onSucess(String balance) {


                Log.d(TAG, "onSucess: balance= "+balance);
                //获取账单的总金额

                String totalMoney = tvTotalMoney.getText().toString();


                if (totalMoney.equals("0.00")||totalMoney.equals("￥0.00")){

                    return;
                }
                if (totalMoney.contains("¥")){
                    Log.d(TAG, "onViewClicked1111: before totalMoney1= "+totalMoney);

                    totalMoney = totalMoney.replace("¥ ","");


                    Log.d(TAG, "onViewClicked11111: after totalMoney1= "+totalMoney);
                }


                //如果没有余额/余额为0 还是跳转其他三种支付
                if (TextUtils.isEmpty(balance)||
                Double.parseDouble(balance)==0){


                    NotificationCrashFragment(totalMoney);
                    ((MainActivity) getActivity()).showPayFragment();


                 //进行余额支付
                }else{

                    BaseDialogFragment payDialogFragment = null;
                    //判断余额是否满足当前账单金额
                    Double accountBlance = Double.parseDouble(balance);

                    //订单总金额
                    Double orderMoney = Double.parseDouble(totalMoney);


                     balancePayBean = new BalancePayBean();
                    balancePayBean.setBalanceTotal(accountBlance+"");
                    //balancePayBean.setBalanceNeedPay();
                    balancePayBean.setOrderMoney(orderMoney+"");
                    balancePayBean.setUserId(user_id);

                  //  order = RandomUntil.getNumLargeLetter(18);
                   // order = "P"+order.substring(1,order.length());


                    Log.d(TAG, "onSucess: user_id= "+user_id);


                    if (accountBlance>orderMoney){

                        //创建ordersBean 保存订单信息
                        saveOrderInfo(BALANCE);
                       // mOrderbean.setStatus(1);


                        mOrderbean.setUse_wallet(1);

                        mOrderbean.setWallet_amount(orderMoney+"");

                        mOrderbean.setAmount("0.00");
                        balancePayBean.setOrdersBean(mOrderbean);
                        payDialogFragment = PayOrderBalanceDialogFragment.getInstance(balancePayBean);
                    }
                    //混合支付
                    else{

                        saveOrderInfo(BALANCE);


                        mOrderbean.setWallet_amount((accountBlance)+"");

                        mOrderbean.setAmount((orderMoney-accountBlance)+"");
                        mOrderbean.setUse_wallet(1);
                        balancePayBean.setOrdersBean(mOrderbean);

                        balancePayBean.setOrderLeaveMoney(orderMoney-accountBlance+"");
                        payDialogFragment = PayOrderMultipleDialogFragment.getInstance(balancePayBean);


                    }
                    payDialogFragment.setBlancePayCallBack(Left_crashier_fragment.this);

                    payDialogFragment.show(getChildFragmentManager(),"pay");



                }



            }

            @Override
            public void onFailed(String msg) {

                ToastUtils.showShortToast(msg);
            }
        });


    }


    public void LoadOrder() {
        String url = App.API_URL+"reta/cashier/query-order";
        Map<String,String> stringMap = new HashMap<>();
        //ToastUtils.showShortToast(""+mOrderbean.getService_orderId());
        stringMap.put("out_trade_no", mOrderbean.getService_orderId());
        stringMap.put("pay_type",   payment_id);
        Xutils.getInstance().post(url, stringMap, new Xutils.XCallBack() {
            @Override
            public void onResponse(String str) {
                try {
                    JSONObject mjsonObjects = new JSONObject(str);
                    String result = mjsonObjects.getString("status");
                    String message = mjsonObjects.getString("message");
                    if (result.equals("true")) {
                        JSONObject mjsonObject = mjsonObjects.getJSONObject("data");

                    if (mjsonObject.getString("result_code").equals("01")){
                        //支付成功

                        payMethod = 0;
                        printOrder("扫码支付","");
//                       sendPay();
                        handler.removeCallbacks(runnable);

                       /* MessageEvent  messageEvent = new MessageEvent("allDelete");
                        EventBus.getDefault().post(messageEvent);
                        ((MainActivity)activity).showRightCrashierLayout();
                        uploadDateToServe(mOrderbean);*/

                    }else  if (mjsonObject.getString("result_code").equals("02")){
                        //支付失败
                        Hint.Short(getActivity(), message);
                        handler.removeCallbacks(runnable);
                        //轮训
                    }else  if (mjsonObject.getString("result_code").equals("03")){
                        Hint.Short(getActivity(), message);

                    }

                    } else {
                        Hint.Short(getActivity(), message);
                        handler.removeCallbacks(runnable);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendPay(String auth_code) {

        if (payTipsDialog==null) {

            payTipsDialog = new Dialog(getActivity(),R.style.CustomDialog);
            payTipsDialog.setContentView(R.layout.layout_waiting_scan_pay);

        }

        payTipsDialog.show();


       // order = RandomUntil.getNumLargeLetter(18);

        //保存订单到本地 这是是未支付的也要先保存，支付完成后在更新状态

        if (balancePayBean==null){

            saveOrderInfo(WECHAT_ZFB);

        }
try {
    //order = RandomUntil.getNumLargeLetter(18);
    //App.store.put("order 11", order);
  //  App.store.commit();
   // ToastUtils.showShortToast("订单"+order);
    String url = App.API_URL + "reta/cashier/do-pay";

    Map<String, String> stringMap = new HashMap<>();
    stringMap.put("order_no", order);
    stringMap.put("amount", "0.01");
    //stringMap.put("amount", mOrderbean.getAmount());

    Log.d(TAG, "sendPay: mOrderbean= "+mOrderbean.toString());
//        stringMap.put("amount", tvTotalMoney.getText().toString());

    if (auth_code.substring(0, 2).contains("10") || auth_code.substring(0, 2).contains("11") || auth_code.substring(0, 2).contains("12") || auth_code.substring(0, 2).contains("13") || auth_code.substring(0, 2).contains("14") || auth_code.substring(0, 2).contains("15")) {
        payment_id = "1";
        payType = "010";
    } else if (auth_code.substring(0, 2).contains("28")) {
        payment_id = "2";
        payType = "020";

    }

    mOrderbean.setPayment_id(payment_id);
    mOrderbean.saveAsync();
    stringMap.put("pay_type", payType);
    stringMap.put("auth_code", auth_code);
    /**
     * *****************************本地数据库存储******************************
     */
   /* mOrderbean = new ordersBean();
    if (discountBean!=null){
        mOrderbean.setUserid(discountBean.getUser_id());

    }else{
        mOrderbean.setUserid("");

    }
    mOrderbean.setTotal_amount(totalMoney + "");
    mOrderbean.setAmount(tvTotalMoney.getText().toString());
    mOrderbean.setUser_remarks("");
    mOrderbean.setSeller_remarks("");
    mOrderbean.setOrder(order);
    mOrderbean.setPayment_id(payment_id);

    mOrderbean.setMinus_amount(tvTotalDiscount.getText().toString());//优惠总金额
    if (moneyBean!=null) {
        mOrderbean.setCard_minus(moneyBean.getVipDiscount() + "");//会员卡优惠
        mOrderbean.setCoupon_minus(moneyBean.getMianzhiOrZhrkouDiscount() + "");//优惠券优惠

    }else{
        mOrderbean.setCard_minus("");//会员卡优惠
        mOrderbean.setCoupon_minus("");//优惠券优惠


    }
    mOrderbean.setCard_id("");
    mOrderbean.setCoupon_id("");
    mOrderbean.setIspay("0");
    mOrderbean.setIsauto("0");

    List<orderIteminfo> orderIteminfoList = new ArrayList<>();


    for (Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet()) {
        String namse = entry.getValue().getGoodsName();
        orderIteminfo items = new orderIteminfo();
        items.setData_id(entry.getValue().getGoodsId());//商品id
        items.setEntity_id(entry.getValue().getGoodsId());//实体id
        items.setProduct_no(entry.getValue().getProduct_no());//条形码
        items.setName(entry.getValue().getGoodsName());//
        items.setImage(entry.getValue().getGoodsimg());//
        items.setPrice(entry.getValue().getPrice() + "");//价格
        items.setQty(entry.getValue().getNumber() + "");//数量
        items.setSpec(entry.getValue().getUnit());//规格
        items.setMinus("");//优惠金额
        items.setMessages(""); //描述
        //    items.save();
        orderIteminfoList.add(items);//


    }

    Gson gson = new Gson();

    String str = gson.toJson(orderIteminfoList);

    mOrderbean.setOrderInfo(str);*/
    // mOrderbean.setOrderIteminfoList(orderIteminfoList);


    /*******************************************************************************/
//    Xutils.getInstance().post(url, stringMap, new Xutils.XCallBackWithError() {
//        @Override
//        public void onError(Throwable throwable) {
//
//            App.store.put("neterror",throwable.getMessage());
//            App.store.commit();
//        }
//
//        @Override
//        public void onResponse(String result) {
//
//            App.store.put("netsucess",result);
//            App.store.commit();
//
//        }
//    });



    //1

    Xutils.getInstance().post(url, stringMap, new Xutils.XCallBack() {
        @Override
        public void onResponse(String str) {
            try {
                Log.d(TAG, "onResponse: str= " + str);

               // ToastUtils.showShortToast(str);
                App.store.put("test123", str);
                App.store.commit();
                dialog.dismiss();
                JSONObject mjsonObjects = new JSONObject(str);
                String result = mjsonObjects.getString("status");
                String message = mjsonObjects.getString("message");
                if (result.equals("true")) {

                    mOrderbean.saveAsync().listen(new SaveCallback() {
                        @Override
                        public void onFinish(final boolean status) {

                            rvSelectedGoods.post(new Runnable() {
                                @Override
                                public void run() {


                                    if (status) {

                                        //  ToastUtils.showShortToast("sucess");


                                    } else {

                                        // ToastUtils.showShortToast("failed");
                                    }


                                }
                            });
                        }
                    });


                    JSONObject jsonObject = mjsonObjects.getJSONObject("data");

                    out_trade_no = jsonObject.getString("out_trade_no");

                    // ToastUtils.showShortToast(out_trade_no);

                    mOrderbean.setService_orderId(out_trade_no);
                    mOrderbean.save();

                    if (mjsonObjects.getJSONObject("data").getString("result_code").equals("01")) {



                        //第三方支付成功，去掉对话框

                        payTipsDialog.dismiss();

                        printOrder("扫码支付","");


                          /*  mOrderbean.setIspay("1");
                            mOrderbean.save();

                            //String out_trade_no = mjsonObjects.getString("out_trade_no");
                            String store = App.store.getString("storeinfo");
                            JSONObject storeinfo = new JSONObject(store);
                            AidlUtil.getInstance().printText(
                                    storeinfo.getString("name"), 2, 50, true,
                                    false, ESCUtil.alignCenter());
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            AidlUtil.getInstance().printText("销售订单:" + order, 2, 35, false,
                                    false, ESCUtil.alignLeft());
                            AidlUtil.getInstance().printText("交易时间:" + format.format(date), 1, 35, false,
                                    false, ESCUtil.alignLeft());
                            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                                    null);
                            AidlUtil.getInstance().printLine(1);
                            for (Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet()) {
                                String namse = entry.getValue().getGoodsName();
                                System.out.println(entry.getKey() + ": " + entry.getValue() + namse);
                                AidlUtil.getInstance().printText(
                                        entry.getValue().getGoodsName() + "    x" + entry.getValue().getNumber(), 0, 35, false,
                                        false, ESCUtil.alignLeft());
                                AidlUtil.getInstance().printText("    ¥" + entry.getValue().getPrice(), 2, 35, false,
                                        false, null);
                            }
                            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                                    null);
                            AidlUtil.getInstance().printLine(1);
                            AidlUtil.getInstance().printText("合   计：" + selectedGoodsMap.size(), 2, 35,
                                    false, false, ESCUtil.alignLeft());
                            AidlUtil.getInstance().printText("订单金额：" + tvTotalMoney.getText().toString(), 2, 35,
                                    false, false, ESCUtil.alignLeft());
                            AidlUtil.getInstance().printText("实付金额：" + tvTotalMoney.getText().toString(), 2, 35,
                                    false, false, ESCUtil.alignLeft());

                            AidlUtil.getInstance().printText("优惠券  ：0.00", 2, 35,
                                    false, false, ESCUtil.alignLeft());

                            AidlUtil.getInstance().printText("支付方式：扫码支付", 2, 35,
                                    false, false, ESCUtil.alignLeft());
                            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 1, 25, false, false,
                                    null);
                            AidlUtil.getInstance().printLine(1);
                            AidlUtil.getInstance().printText("  谢谢您的惠顾，欢迎下次光临！", 2, 35, false,
                                    false, null);

                            AidlUtil.getInstance().print3Line();

                            AidlUtil.getInstance().cutPrint();
                            MessageEvent  messageEvent = new MessageEvent("allDelete");
                            EventBus.getDefault().post(messageEvent);
                            ((MainActivity)activity).showRightCrashierLayout();
                            uploadDateToServe(mOrderbean);*/

                    } else if (mjsonObjects.getJSONObject("data").getString("result_code").equals("02")) {
                        Hint.Short(getActivity(), mjsonObjects.getJSONObject("data").getString("return_msg"));
                    } else if (mjsonObjects.getJSONObject("data").getString("result_code").equals("03")) {

                    //    ToastUtils.showShortToast("03");
                        handler.postDelayed(runnable, 1000);//每两秒执行一次runnable。
                    }


                } else {
                    //失败也取消
                    payTipsDialog.dismiss();

                    Hint.Short(getActivity(), message);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    });
}catch (Exception e){

    App.store.put("exception1",e.getMessage()+"  "+e.getCause());
    App.store.commit();

}

    }

    private void saveOrderInfo(int type) {


         order = RandomUntil.getNumLargeLetter(17);

        mOrderbean = new ordersBean();

        mOrderbean.setOrder(order);

        if (type==BALANCE||type==MULTIPLE){

            mOrderbean.setStatus(1);

            if (!TextUtils.isEmpty(user_id)) {

                mOrderbean.setUserid(user_id);

            }

        }else{

            mOrderbean.setStatus(5);

        }


        if (discountBean!=null){
            //mOrderbean.setUserid(discountBean.getUser_id());
            mOrderbean.setCard_id(discountBean.getVipCard_id());
            mOrderbean.setCoupon_id(discountBean.getCoupons_id());
        }else{
           // mOrderbean.setUserid("");
            mOrderbean.setCard_id("");
            mOrderbean.setCoupon_id("");
        }
        mOrderbean.setTotal_amount(totalMoney + "");

        String amount = tvTotalMoney.getText().toString();
        if (!TextUtils.isEmpty(amount)&&amount.contains("¥")){

            amount = amount.replace("¥ ","");
        }




         if (type==WECHAT_ZFB||type==CRASH||type==BIAOJI){
        mOrderbean.setAmount(amount);
        }
        mOrderbean.setUser_remarks("");
        mOrderbean.setSeller_remarks("");
        mOrderbean.setPayment_id(payment_id);

        mOrderbean.setMinus_amount(tvTotalDiscount.getText().toString());//优惠总金额
        if (moneyBean!=null) {
            mOrderbean.setCard_minus(moneyBean.getVipDiscount() + "");//会员卡优惠
            mOrderbean.setCoupon_minus(moneyBean.getMianzhiOrZhrkouDiscount() + "");//优惠券优惠

        }else{
            mOrderbean.setCard_minus("");//会员卡优惠
            mOrderbean.setCoupon_minus("");//优惠券优惠


        }

        mOrderbean.setIspay("0");
        mOrderbean.setIsauto("0");

        List<orderIteminfo> orderIteminfoList = new ArrayList<>();


        for (Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet()) {
            String namse = entry.getValue().getGoodsName();
            orderIteminfo items = new orderIteminfo();
            items.setData_id(entry.getValue().getGoodsId());//商品id
            items.setEntity_id(entry.getValue().getGoodsId());//实体id
            items.setProduct_no(entry.getValue().getProduct_no());//条形码
            items.setName(entry.getValue().getGoodsName());//
            items.setImage(entry.getValue().getGoodsimg());//
            items.setPrice(entry.getValue().getPrice() + "");//价格
            items.setQty(entry.getValue().getNumber() + "");//数量
            items.setSpec(entry.getValue().getUnit());//规格
            items.setMinus("");//优惠金额
            items.setMessages(""); //描述
            //    items.save();
            orderIteminfoList.add(items);//


        }

        Gson gson = new Gson();

        String str = gson.toJson(orderIteminfoList);

        mOrderbean.setOrderInfo(str);
    }

    private void printOrder(String type,String addInfo) {

        oldType = type;
        this.oldAddInfo = addInfo;

        mOrderbean.setIspay("1");
        mOrderbean.save();

        //String out_trade_no = mjsonObjects.getString("out_trade_no");
        String TotalMoney =  tvTotalMoney.getText().toString();
        String TotalDiscount = tvTotalDiscount.getText().toString();
        String store = App.store.getString("storeinfo");
        try {
            MessageEvent messageEvent1 = new MessageEvent("ThridFragmentdata");
            messageEvent1.setThridFragmentdata(store);
            messageEvent1.setGood_order(order);
//            messageEvent1.setType(type);
//            messageEvent1.setTotalMoney(Double.parseDouble(TotalMoney));
            messageEvent1.setCheap_money(TotalDiscount);
            messageEvent1.setSelectedGoodsMap(selectedGoodsMap);
            messageEvent1.setShifu_pay(TotalDiscount);
            EventBus.getDefault().post(messageEvent1);
            storeinfo = new JSONObject(store);

            EventBus.getDefault().post(new MessageEvent("cancelCollection"));

            printInfoBean = getPrintInfoBean(type);
            //打印
            singlePrint(printInfoBean);

            ((MainActivity)activity).showPaySucessLayout();


            uploadDateToServe(mOrderbean);

            isPaySucess = true;

            MessageEvent messageEventSucess = new MessageEvent("paySucess");
            EventBus.getDefault().post(messageEventSucess);

            MessageEvent messageEvent = new MessageEvent("allDelete");

            EventBus.getDefault().post(messageEvent);

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private PrintInfoBean getPrintInfoBean(String type) {

        PrintInfoBean printInfoBean = new PrintInfoBean();
        try {

            printInfoBean.setStoreName(storeinfo.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printInfoBean.setStoreOrderId(order);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        printInfoBean.setOrderTime(format.format(date));


        ArrayList<goodsBean>goodsBeanArrayList = new ArrayList<>();
        Log.d(TAG, "getPrintInfoBean: selectedGoodsMap size= "+selectedGoodsMap.size());
        for (Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet()) {

            goodsBeanArrayList.add(entry.getValue());

        }
        printInfoBean.setGoodsBeanArrayList(goodsBeanArrayList);

        printInfoBean.setTotalGoods(goodsBeanArrayList.size());

        printInfoBean.setOrderTotal(DecimalFormatUtils.doubleToMoney(totalMoney));


        printInfoBean.setActualPay(tvTotalMoney.getText().toString());


        printInfoBean.setDiscountMoney(tvTotalDiscount.getText().toString());

        printInfoBean.setPayType(type);

        return printInfoBean;


    }

    /**
     * 因printOrder耦合度太高，所以给打印单独抽出来
     * */
    private void singlePrint(PrintInfoBean printInfoBean) throws JSONException {


        Log.d(TAG, "singlePrint: PrintInfoBean= "+printInfoBean.toString());
        Log.d(TAG, "singlePrint: storeinfo= "+storeinfo.toString());
       /* AidlUtil.getInstance().printText(
                storeinfo.getString("name"), 2, 50, true,
                false, ESCUtil.alignCenter());*/

        AidlUtil.getInstance().printText(
                printInfoBean.getStoreName(), 2, 50, true,
                false, ESCUtil.alignCenter());


        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        AidlUtil.getInstance().printText("销售订单:" + printInfoBean.getStoreOrderId(), 2, 35, false,
                false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printText("交易时间:" + printInfoBean.getOrderTime(), 1, 35, false,
                false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                null);
        AidlUtil.getInstance().printLine(1);
        AidlUtil.getInstance().printText(
                formatStr("商品名称") + " "
                        + formatStr2("数量"), 0, 28, false, false,
                ESCUtil.alignLeft());

        AidlUtil.getInstance().printText(
                //formatRight("￥" + jsinfo.getString("price")),
                "  单价" ,
                1, 28, false, false,
                ESCUtil.alignLeft());
        //for (Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet())
        for (goodsBean entry:
             printInfoBean.getGoodsBeanArrayList())


        {
            /*String namse = entry.getValue().getGoodsName();
            System.out.println(entry.getKey() + ": " + entry.getValue() + namse);*/
//                AidlUtil.getInstance().printText(
//                        entry.getValue().getGoodsName() + "    x" + entry.getValue().getNumber(), 0, 35, false,
//                        false, ESCUtil.alignLeft());
//                AidlUtil.getInstance().printText("    ¥" + entry.getValue().getPrice(), 2, 35, false,
//                        false, null);
            AidlUtil.getInstance().printText(
                    formatStr(entry.getGoodsName()) + " x"
                            + formatStr2(""+entry.getNumber()), 0, 28, false, false,
                    ESCUtil.alignLeft());

            AidlUtil.getInstance().printText(
                    //formatRight("￥" + jsinfo.getString("price")),
                    "" + DecimalFormatUtils.doubleToMoney(entry.getPrice()),
                    1, 28, false, false,
                    ESCUtil.alignLeft());
        }
        AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                null);
//            AidlUtil.getInstance().printLine(1);
        AidlUtil.getInstance().printText("合   计：" + printInfoBean.getTotalGoods(), 1, 35,
                false, false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printLine(1);
        AidlUtil.getInstance().printText("订单金额：" + printInfoBean
                .getOrderTotal(), 2, 35,
                false, false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printText("实付金额：" +
                        printInfoBean.getActualPay(), 1, 35,
                false, false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printLine(1);
        AidlUtil.getInstance().printText("优惠金额："+"¥ "+printInfoBean
                .getDiscountMoney(), 1, 35,
                false, false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printLine(1);
            /*if (moneyBean!=null) {
                AidlUtil.getInstance().printText("优惠券  ："+
                                , 2, 35,
                        false, false, ESCUtil.alignLeft());
            }else{

                AidlUtil.getInstance().printText("优惠券  ：0", 2, 35,
                        false, false, ESCUtil.alignLeft());
            }*/
        AidlUtil.getInstance().printText("支付方式："+printInfoBean.getPayType(), 2, 35,
                false, false, ESCUtil.alignLeft());
        AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 1, 25, false, false,
                null);
        AidlUtil.getInstance().printLine(1);
        AidlUtil.getInstance().printText("  谢谢您的惠顾，欢迎下次光临！", 2, 35, false,
                false, null);

        AidlUtil.getInstance().print3Line();

        AidlUtil.getInstance().cutPrint();
    }


    public void moveToPosition(LinearLayoutManager linearLayoutManager,RecyclerView recyclerView

    ,int position){

        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();


        //if (position<firstVisibleItemPosition){

            recyclerView.scrollToPosition(position);
        //}



    }

    /**
     * @Function 上传订单信息到服务器
     * */

    private void uploadDateToServe(final ordersBean mOrderbean) {


        Log.d(TAG, "uploadDateToServe: mOrderbean= "+mOrderbean.toString());
        //表示使用余额的混合支付
        if (mOrderbean.getUse_wallet() == 1) {

            if(payModel==null){

                payModel = new PayModel();
            }
            payModel.asyncBalanceOrder(mOrderbean.getOrder(), mOrderbean.getServer_order_no(), mOrderbean.getPayment_id(), new NetWorkCallBack() {
                @Override
                public void onSucess(Object data) {



                }

                @Override
                public void onFailed(String msg) {

                    ToastUtils.showShortToast(msg);
                }
            });

        }else {


            Log.d(TAG, "uploadDateToServe: mOrderbean= " + mOrderbean.toString());
            Map<String, String> map = new HashMap<>();
            map.put("user_id", mOrderbean.getUserid());
            map.put("total_amount", mOrderbean.getTotal_amount());
            map.put("amount", mOrderbean.getAmount());
            Log.d(TAG, "uploadDateToServe: amount= " + mOrderbean.getAmount());
            map.put("user_remarks", "abcdef");
            map.put("seller_remarks", mOrderbean.getSeller_remarks());
            map.put("trade_no", mOrderbean.getService_orderId());
            map.put("payment_id", mOrderbean.getPayment_id());
            map.put("minus_amount", mOrderbean.getMinus_amount());
            map.put("card_minus", mOrderbean.getCard_minus());
            map.put("coupon_minus", mOrderbean.getCoupon_minus());
            map.put("card_id", mOrderbean.getCard_id());
            map.put("coupon_id", mOrderbean.getCoupon_id());
            map.put("items", mOrderbean.getOrderInfo());
            map.put("status", mOrderbean.getStatus() + "");
            map.put("wallet_amount", mOrderbean.getWallet_amount());
            map.put("order_no", mOrderbean.getOrder());

            Xutils.getInstance().post(App.API_URL +
                    App.API_RECEIVE, map, new Xutils.XCallBack() {
                @Override
                public void onResponse(String result) {

                    Log.d(TAG, "onResponse: result= " + result);
                    App.store.put("uploadresp", result);
                    App.store.commit();

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");

                        if (status.equals("true")) {

                            mOrderbean.setIsauto("1");

                            JSONObject datas = jsonObject.getJSONObject("data");
                            JSONObject order = datas.getJSONObject("order");


                            String order_no = order.getString("order_no");

                            Log.d(TAG, "onResponse: order= " + order_no);
                            if (!TextUtils.isEmpty(order_no)) {

                                mOrderbean.setServer_order_no(order_no);
                            }

                            mOrderbean.save();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    App.store.put("upload", result);
                    App.store.commit();
                    // ToastUtils.showShortToast("result= "+result);

                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);

        Log.d(TAG, "onResume: ------------>" + (mDSKernel == null));
        if (mDSKernel != null) {
            mDSKernel.checkConnection();
        } else {
            initSdk();
        }
    }

    private void initSdk() {
        myHandler = new MyHandler(getActivity());
        mDSKernel = DSKernel.newInstance();
        mDSKernel.init(getActivity(), mIConnectionCallback);
        mDSKernel.addReceiveCallback(mIReceiveCallback);
        mDSKernel.addReceiveCallback(mIReceiveCallback2);
        mDSKernel.removeReceiveCallback(mIReceiveCallback);
        mDSKernel.removeReceiveCallback(mIReceiveCallback2);
        long imgTaskId = (long) SharePreferenceUtil.getParam(getActivity(), imgKey, 0L);
        checkImgFileExist(imgTaskId);
    }

    public void XianjinPay(String shifu,String zhaolin ) {


        Log.d(TAG, "XianjinPay: mOrder= "+mOrderbean.toString());

        acual_collect_money = shifu;
        this.zhaoling = zhaolin;

        if (balancePayBean==null){

            saveOrderInfo(CRASH);

        }
        mOrderbean.setPayment_id("4");
         mOrderbean.save();
        payMethod = 1;
        printOrder("现金支付","");
        /*final String order =RandomUntil.getNumLargeLetter(18);
        try {
            String store= App.store.getString("storeinfo");

            *//*mOrderbean.setOrder(order);
            //mOrderbean.setPayment_id("2");
           // printOrder();
            mOrderbean.setIspay("1");
            mOrderbean.save();*//*
            JSONObject storeinfo=new JSONObject(store);

            AidlUtil.getInstance().printText(
                    storeinfo.getString("name"), 2, 50, true,
                    false, ESCUtil.alignCenter());
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            AidlUtil.getInstance().printText("销售订单:"+order , 2, 35, false,
                    false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("交易时间:"+format.format(date) , 1, 35, false,
                    false, ESCUtil.alignLeft());

            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                    null);
            AidlUtil.getInstance().printLine(1);
            for(Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet()) {
                String namse= entry.getValue().getGoodsName();
                System.out.println(entry.getKey() + ": "  + entry.getValue()+namse);
                AidlUtil.getInstance().printText(
                        entry.getValue().getGoodsName() + "    x" + entry.getValue().getNumber(), 0, 35, false,
                        false, ESCUtil.alignLeft());
                AidlUtil.getInstance().printText("    ￥" + entry.getValue().getPrice(), 2, 35, false,
                        false, null);
            }
            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                    null);
            AidlUtil.getInstance().printLine(1);

            AidlUtil.getInstance().printText("合   计："+selectedGoodsMap.size(), 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("订单金额："+tvTotalMoney.getText().toString(), 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("优惠金额  ：0.00" , 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("现金收款："+shifu+".00", 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("应付金额："+tvTotalMoney.getText().toString(), 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("找    零："+zhaolin , 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("支付方式：现金收款" , 2, 35,
                    false, false, ESCUtil.alignLeft());
//                    AidlUtil.getInstance().printText("支付方式：标记收款-"+payType , 2, 35,
//                            false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 1, 25, false, false,
                    null);
            AidlUtil.getInstance().printLine(1);
//            AidlUtil.getInstance().printText(" 本店地址"  , 2, 35, false,
//                    false, null);

            AidlUtil.getInstance().printText("  谢谢您的惠顾，欢迎下次光临！"  , 2, 35, false,
                    false, null);

            AidlUtil.getInstance().print3Line();

            AidlUtil.getInstance().cutPrint();

            MessageEvent  messageEvent = new MessageEvent("allDelete");
            EventBus.getDefault().post(messageEvent);
            ((MainActivity)activity).showRightCrashierLayout();
//                    } else {
//                        Hint.Short(getActivity(), message);
//                    }

        } catch (Exception e) {
            e.printStackTrace();
        }

//            }
//
//            @Override
//            protected Object clone() throws CloneNotSupportedException {
//                return super.clone();
//            }
//
//        });*/
    }

    public void biaojiPay(String payType) {
        //final String order =RandomUntil.getNumLargeLetter(18);
        try {



            if (balancePayBean==null){

            saveOrderInfo(BIAOJI);
            }

            //mOrderbean.setPayment_id("");
            payMethod = 2;

            printOrder("标记支付",payType);

            /*mOrderbean.setOrder(order);
            //mOrderbean.setPayment_id("2");

            mOrderbean.setIspay("1");
            mOrderbean.save();

            String store= App.store.getString("storeinfo");
            JSONObject storeinfo=new JSONObject(store);

            AidlUtil.getInstance().printText(
                    storeinfo.getString("name"), 2, 50, true,
                    false, ESCUtil.alignCenter());
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            AidlUtil.getInstance().printText("销售订单:"+order , 2, 35, false,
                    false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("交易时间:"+format.format(date) , 1, 35, false,
                    false, ESCUtil.alignLeft());

            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                    null);
            AidlUtil.getInstance().printLine(1);
            for(Map.Entry<String, goodsBean> entry : selectedGoodsMap.entrySet()) {
                String namse= entry.getValue().getGoodsName();
                System.out.println(entry.getKey() + ": "  + entry.getValue()+namse);
                AidlUtil.getInstance().printText(
                        entry.getValue().getGoodsName() + "    x" + entry.getValue().getNumber(), 0, 35, false,
                        false, ESCUtil.alignLeft());
                AidlUtil.getInstance().printText("    ￥" + entry.getValue().getPrice(), 2, 35, false,
                        false, null);
            }
            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 2, 25, false, false,
                    null);
            AidlUtil.getInstance().printLine(1);

            AidlUtil.getInstance().printText("合   计："+selectedGoodsMap.size(), 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("订单金额："+tvTotalMoney.getText().toString(), 2, 35,
                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("优惠金额  ：0.00" , 2, 35,
                    false, false, ESCUtil.alignLeft());
//            AidlUtil.getInstance().printText("现金收款："+shifu+".00", 2, 35,
//                    false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("应付金额："+tvTotalMoney.getText().toString(), 2, 35,
                    false, false, ESCUtil.alignLeft());
//            AidlUtil.getInstance().printText("找    零："+zhaolin , 2, 35,
//                    false, false, ESCUtil.alignLeft());

                    AidlUtil.getInstance().printText("支付方式：标记收款-"+payType , 2, 35,
                            false, false, ESCUtil.alignLeft());
            AidlUtil.getInstance().printText("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _", 1, 25, false, false,
                    null);
            AidlUtil.getInstance().printLine(1);
//            AidlUtil.getInstance().printText(" 本店地址"  , 2, 35, false,
//                    false, null);

            AidlUtil.getInstance().printText("  谢谢您的惠顾，欢迎下次光临！"  , 2, 35, false,
                    false, null);

            AidlUtil.getInstance().print3Line();

            AidlUtil.getInstance().cutPrint();

            MessageEvent  messageEvent = new MessageEvent("allDelete");
            EventBus.getDefault().post(messageEvent);
            ((MainActivity)activity).showRightCrashierLayout();*/
//                    } else {
//                        Hint.Short(getActivity(), message);
//                    }

        } catch (Exception e) {
            e.printStackTrace();
        }

//            }
//
//            @Override
//            protected Object clone() throws CloneNotSupportedException {
//                return super.clone();
//            }
//
//        });
    }


    private void removeAlphaView() {

        ((ViewGroup) activity.getWindow().getDecorView().
                findViewById(android.R.id.content)).removeView(alphaView);
    }

    private void setWindowBackGround() {

        ViewGroup viewGroup = activity.getWindow().getDecorView().
                findViewById(android.R.id.content);


        viewGroup.addView(alphaView);

    }

    public void updateSelectedGoods(goodsBean goodsBean) {

        /*if (selectedGoodsMap!=null){

            oldSelectedGoodsArrayList.clear();

            for (Map.Entry<String, goodsBean> map :
                    selectedGoodsMap.entrySet()) {


                Log.d(TAG, "updateSelectedGoods: copy before= "+map.getValue().getNumber());
                try {
                    oldSelectedGoodsArrayList.add(map.getValue().clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            }

            if (oldSelectedGoodsArrayList.size()>0){
            Log.d(TAG, "updateSelectedGoods: copy after= "+oldSelectedGoodsArrayList.get(0).getNumber());
            }

            Log.d(TAG, "updateSelectedGoods: oldSelectedGoodsArrayList= "+oldSelectedGoodsArrayList.toString());
        }*/
        isPaySucess = false;

        Log.d(TAG, "updateSelectedGoods: goodsbean= "+goodsBean.toString());


        //   if (selectedGoodsMap.get(goodsBean.getGoodsId())!=null) {


        selectedGoodsMap.put(goodsBean.getGoodsId(), goodsBean);

        Log.d(TAG, "updateSelectedGoods: selectedGoodsMap= "+selectedGoodsMap.size());
        //   }

           /* for (int i = 0; i < selectedGoodsArrayList.size(); i++) {



                if (selectedGoodsArrayList.get(i).getGoodsId().equals(goodsBean.getGoodsId())){

                       selectedGoodsArrayList.set(i,goodsBean);
                }else {
                    selectedGoodsArrayList.add(goodsBean);
                }
            }

            if (selectedGoodsArrayList.size()==0){
                selectedGoodsArrayList.add(goodsBean);
            }*/

        selectedGoodsArrayList.clear();

        if (!selectedGoodsMap.isEmpty()) {
            for (Map.Entry<String, goodsBean> map :
                    selectedGoodsMap.entrySet()) {


                selectedGoodsArrayList.add(map.getValue());

            }




        }
        Log.d(TAG, "updateSelectedGoods: copy finish= "+selectedGoodsArrayList.get(0).getNumber());


        /*Log.d(TAG, "updateSelectedGoods: size= "+selectedGoodsArrayList.size()

        +selectedGoodsArrayList.get(0).toString());*/

        /*File jj = new File(Environment.getExternalStorageDirectory().getPath() + "/img_04.png");

        long imgsMenuTaskId = (long) SharePreferenceUtil.getParam(getActivity(), imgsKey, 0L);
        checkImgsMenuExists(imgsMenuTaskId);*/


        Log.d(TAG, "updateSelectedGoods: oldSelectedGoodsArrayList= "+oldSelectedGoodsArrayList.toString()+"   selectedGoodsArrayList= "+selectedGoodsArrayList.toString());

        Log.d(TAG, "updateSelectedGoods: oldSelectedGoodsArrayList= "+oldSelectedGoodsArrayList.hashCode()+" selectedGoodsArrayList= "+selectedGoodsArrayList.hashCode());
        //也是更新，换一种方式更新，用DiffUtils
        //updateDiffUtils(selectedGoodsArrayList,oldSelectedGoodsArrayList);

        goodsSelectAdapter.updateData(selectedGoodsArrayList);

//        selectPosition = getAddItemPosition(goodsBean,selectedGoodsArrayList);




        //13516492591 sj12345678
        moveToPosition(linearLayoutManager,rvSelectedGoods,selectPosition);

        /*for (goodsBean good:selectedGoodsArrayList) {


            goodsSelectMap.put(good.getGoodsId(),good.getNumber());


        }

        //传递选择的商品到副屏

        MessageEvent messageEvent = new MessageEvent(MessageEvent.SELECT_GOODS);

       // messageEvent.setSelectGoddsMap(goodsSelectMap);
*/

    }


    public void updateSelectedGoodsWithType(goodsBean goodsBean,int type) {



        /*if (selectedGoodsMap!=null){

            oldSelectedGoodsArrayList.clear();

            for (Map.Entry<String, goodsBean> map :
                    selectedGoodsMap.entrySet()) {


                Log.d(TAG, "updateSelectedGoods: copy before= "+map.getValue().getNumber());
                try {
                    oldSelectedGoodsArrayList.add(map.getValue().clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            }

            if (oldSelectedGoodsArrayList.size()>0){
            Log.d(TAG, "updateSelectedGoods: copy after= "+oldSelectedGoodsArrayList.get(0).getNumber());
            }

            Log.d(TAG, "updateSelectedGoods: oldSelectedGoodsArrayList= "+oldSelectedGoodsArrayList.toString());
        }*/
        isPaySucess = false;

        Log.d(TAG, "updateSelectedGoods: goodsbean= "+goodsBean.toString());


        //   if (selectedGoodsMap.get(goodsBean.getGoodsId())!=null) {


        selectedGoodsMap.put(goodsBean.getGoodsId(), goodsBean);

        Log.d(TAG, "updateSelectedGoods: selectedGoodsMap= "+selectedGoodsMap.size());
        //   }

           /* for (int i = 0; i < selectedGoodsArrayList.size(); i++) {



                if (selectedGoodsArrayList.get(i).getGoodsId().equals(goodsBean.getGoodsId())){

                       selectedGoodsArrayList.set(i,goodsBean);
                }else {
                    selectedGoodsArrayList.add(goodsBean);
                }
            }

            if (selectedGoodsArrayList.size()==0){
                selectedGoodsArrayList.add(goodsBean);
            }*/

        selectedGoodsArrayList.clear();

        if (!selectedGoodsMap.isEmpty()) {
            for (Map.Entry<String, goodsBean> map :
                    selectedGoodsMap.entrySet()) {


                selectedGoodsArrayList.add(map.getValue());

            }

//            Collections.reverse(selectedGoodsArrayList);

        }
        Log.d(TAG, "updateSelectedGoods: copy finish= "+selectedGoodsArrayList.get(0).getNumber());


        /*Log.d(TAG, "updateSelectedGoods: size= "+selectedGoodsArrayList.size()

        +selectedGoodsArrayList.get(0).toString());*/

        /*File jj = new File(Environment.getExternalStorageDirectory().getPath() + "/img_04.png");

        long imgsMenuTaskId = (long) SharePreferenceUtil.getParam(getActivity(), imgsKey, 0L);
        checkImgsMenuExists(imgsMenuTaskId);*/


        Log.d(TAG, "updateSelectedGoods: oldSelectedGoodsArrayList= "+oldSelectedGoodsArrayList.toString()+"   selectedGoodsArrayList= "+selectedGoodsArrayList.toString());

        Log.d(TAG, "updateSelectedGoods: oldSelectedGoodsArrayList= "+oldSelectedGoodsArrayList.hashCode()+" selectedGoodsArrayList= "+selectedGoodsArrayList.hashCode());
        //也是更新，换一种方式更新，用DiffUtils
        //updateDiffUtils(selectedGoodsArrayList,oldSelectedGoodsArrayList);

//        if (type==0){
//
//            goodsSelectAdapter.insertItemChange(0);
//
//        }else if (type==1){
//
//            goodsSelectAdapter.updateItemChange(selectedGoodsArrayList.indexOf(goodsBean));
//
//
//        }

                goodsSelectAdapter.updateData(selectedGoodsArrayList);



//        selectPosition = getAddItemPosition(goodsBean,selectedGoodsArrayList);




        //13516492591 sj12345678
        moveToPosition(linearLayoutManager,rvSelectedGoods,selectPosition);

        /*for (goodsBean good:selectedGoodsArrayList) {


            goodsSelectMap.put(good.getGoodsId(),good.getNumber());


        }

        //传递选择的商品到副屏

        MessageEvent messageEvent = new MessageEvent(MessageEvent.SELECT_GOODS);

       // messageEvent.setSelectGoddsMap(goodsSelectMap);
*/

    }

    /**
     *
     * 用DiffUtils局部刷新

     * */

    private void updateDiffUtils(ArrayList<goodsBean> selectedGoodsArrayList,
                                 ArrayList<goodsBean> oldSelectedGoodsArrayList) {



        if (goodsSelectAdapter!=null){


            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AdapterDiffCallBack(oldSelectedGoodsArrayList,selectedGoodsArrayList));

            diffResult.dispatchUpdatesTo(goodsSelectAdapter);
        }



    }

    private int getAddItemPosition(goodsBean goodsBean, ArrayList<goodsBean> selectedGoodsArrayList) {


        if (goodsBean==null||selectedGoodsArrayList.size()==0){

            return -1;
        }

        int position = selectedGoodsArrayList.indexOf(goodsBean);

        Log.d(TAG, "getAddItemPosition: position= "+position);
        return position;
    }

    @Override
    public void onItemClick(View view, int position) {

        if (((MainActivity)getActivity()).isRightFragmentShow(RightFragmentType.CHANGE_GOODS_COUNT)||
                ((MainActivity)getActivity()).isRightFragmentShow(RightFragmentType.UNSELECTGOODS)||
                ((MainActivity)getActivity()).isRightFragmentShow(RightFragmentType.PAY)
                ){

            /*Toast.makeText(activity, "请先完成当前操作", Toast.LENGTH_SHORT)
                    .show();*/

            ToastUtils.showShortToast("请先完成当前操作");
            return;
        }

        ((MainActivity) activity).showChangeGoodsFragment();

        MessageEvent messageEvent = new MessageEvent("changeCount");
        messageEvent.setGoodsBean(selectedGoodsArrayList.get(position));
        EventBus.getDefault().postSticky(messageEvent);

       /* for (goodsBean g:
                
             selectedGoodsArrayList) {
         
            sendImgsMenu(g.getLocal_image());
        }*/
        
    }


    /***
     * @Function 整单取消
     */
    public void allDelete(){

        rlYouhuiquan.setVisibility(View.GONE);
        selectedGoodsMap.clear();
        goodsSelectMap.clear();

        selectedGoodsArrayList.clear();

        Log.d(TAG, "allDelete: selectedGoodsArrayList= "+selectedGoodsArrayList.size());
        goodsSelectAdapter.updateData(selectedGoodsArrayList);
        long imgsMenuTaskId = (long) SharePreferenceUtil.getParam(getActivity(), imgsKey, 0L);
        checkImgsMenuExists(imgsMenuTaskId);
        updateTotalMoney();
    }
    @Override
    public void onDelete(View view, int position) {

        if (((MainActivity)getActivity()).isRightFragmentShow(RightFragmentType.CHANGE_GOODS_COUNT)
                ||((MainActivity)getActivity()).isRightFragmentShow(RightFragmentType.UNSELECTGOODS)||
                ((MainActivity)getActivity()).isRightFragmentShow(RightFragmentType.PAY)
                ) {
           /* Toast.makeText(activity, "请先完成当前操作", Toast.LENGTH_SHORT)
                    .show();*/

            ToastUtils.showShortToast("请先完成当前操作");
            return;
        }
        selectedGoodsMap.remove(selectedGoodsArrayList.get(position).getGoodsId());

        Log.d(TAG, "onDelete: goodsId= " + selectedGoodsMap.get(selectedGoodsArrayList.get(position).getGoodsId())

                + " id= " + selectedGoodsArrayList.get(position).getGoodsId());

        if (!goodsSelectMap.isEmpty() && goodsSelectMap.get(selectedGoodsArrayList.get(position).getGoodsId()) != null) {

            goodsSelectMap.remove(selectedGoodsArrayList.get(position).getGoodsId());
        }
        selectedGoodsArrayList.remove(position);

        goodsSelectAdapter.notifyItemRemoved(position);

        goodsSelectAdapter.updateData(selectedGoodsArrayList);
        updateTotalMoney();

    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true,priority = 10)
    public void onEventsticky(MessageEvent messageEvent) {

       try {
           Log.d(TAG, "onEvent: pay= " + messageEvent.getAuthCode() + " type= " +
                   messageEvent.getType());




           if (messageEvent.getType().equals("ThridisPay")) {
               Log.d(TAG, "onEvent: ThridFragmentPay= " + messageEvent.getIsPay());
               isPay = messageEvent.getIsPay();

           }
           if (messageEvent.getType().equals("ThridFragmentPay")) {
               Log.d(TAG, "onEvent: ThridFragmentPay= ");
               //if (isPay.equals("1"))

               //判断是否是支付页面在显示
               boolean isPayFragmentVisivity = ((MainActivity) getActivity()).isRightFragmentShow(RightFragmentType.PAY);

               App.store.put("vidibity", isPayFragmentVisivity);

               boolean isRightCrashierVisivity = ((MainActivity) getActivity()).isRightFragmentShow(RightFragmentType.RIGHT_CRASHIER);
               if (isPayFragmentVisivity) {
                   String payment_num = messageEvent.getAuthCode();
                   /*Toast.makeText(getActivity(), "支付码： " + messageEvent.getAuthCode(), Toast.LENGTH_SHORT).show();*/
                   App.store.put("isThirdPay", APPUtil.isThirdPay(payment_num));
                   App.store.commit();
                   if (APPUtil.isThirdPay(payment_num)) {

                       MessageEvent messageEvent1 = EventBus.getDefault().getStickyEvent(MessageEvent.class);

                       if (messageEvent1 != null) {

                           EventBus.getDefault().removeAllStickyEvents();
                       }


                       sendPay(messageEvent.getAuthCode());
                   /* dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                            .setLoadingColor(Color.BLACK)//颜色
                            .setHintText("支付中，请稍后...")
                            .show();*/
                   } else {
                       Toast.makeText(getActivity(), "请正确扫码支付！ ", Toast.LENGTH_SHORT).show();

                   }

               }
               //说明要么是商品列表界面，要么是会员登录界面
               if (isRightCrashierVisivity){


                   //(()((MainActivity)activity).right_crashier_fragment)

                   //右边商品列表界面可见
                   if (((MainActivity) getActivity()).getRightGoodsragmentVisibity()){

                       Log.d(TAG, "onEventsticky: right_goods");

                       String payment_num = messageEvent.getAuthCode();
                       // ToastUtils.showShortToast(payment_num);

                       // queryFromSQL(payment_num);
                       List<goodsBean> goodsBeanList = DataSupport.where("product_no = ? and  storeid = ?"
                               , payment_num, App.store.getString("storeId")).find(com.qimai.xinlingshou.bean.goodsBean.class);

                       if (goodsBeanList.size() > 0) {
                    /*ToastUtils.showShortToast(""
                            +(((MainActivity)getActivity()).
                            isRightFragmentShow(RightFragmentType.PAY)));*/

                           //ToastUtils.showShortToast("you");
                           MessageEvent message = new MessageEvent("update");
                           message.setGoodsBean(goodsBeanList.get(0));


                           goodsBean = goodsBeanList.get(0);
                           if (goodsBean != null) {

                               if (goodsSelectMap.get(goodsBean.getGoodsId()) == null) {

                                   goodsSelectMap.put(goodsBean.getGoodsId(), 1);

                               } else {

                                   int number = goodsSelectMap.get(goodsBean.getGoodsId());
                                   Log.d(TAG, "onEvent: number= " + number + " id= " +
                                           goodsBean.getGoodsId()

                                           + " type= " + goodsBean.getChangeType());
                                   if (goodsBean.getChangeType() == null) {
                                       Log.d(TAG, "onEvent: number++");
                                       number++;
                                   } else if (goodsBean.getChangeType().equals("countChange")) {
                                       number = goodsBean.getNumber();
                                       goodsBean.setChangeType("");
                                   } else {
                                       number++;


                                   }
                                   goodsSelectMap.put(goodsBean.getGoodsId(), number);
                               }

                               goodsBean.setNumber(goodsSelectMap.get(goodsBean.getGoodsId()));
                           }

                           //去更新集合
                           updateSelectedGoods(goodsBean);

                           //去更新应收金额有两个地方 商品选择数量变化时与删除商品时

                           updateTotalMoney();


                           //EventBus.getDefault().post(messageEvent);
                       } else {

                           Toast.makeText(getActivity(), "未搜索到该商品信息！ ", Toast.LENGTH_SHORT).show();

                       }

                       //是右边会员登录
                   }else{


                       Log.d(TAG, "onEventsticky: right_vip");


                       MessageEvent messageEvent1 = new MessageEvent(MessageEvent.SCAN_VIP_LOGIN);

                       messageEvent1.setStringValues(messageEvent.getAuthCode());
                       EventBus.getDefault().post(messageEvent1);
                   }


               }

           }
           //现金支付
           if (messageEvent.getType().equals("XianjingPay")) {


               Log.d(TAG, "onEvent: XianjingPay= " + messageEvent.getAuthCode());
               XianjinPay(messageEvent.getShifu_pay(), messageEvent.getZhaoling_pay());
           }
           //标记支付
           if (messageEvent.getType().equals("BiaojiPay")) {
//            Log.d(TAG, "onEvent: BiaojiPay= " + messageEvent.getAuthCode());
               biaojiPay(messageEvent.getIsPay());
           }
           //优惠券相关操作
           if (messageEvent.getType().equals("coupons")) {
               if (messageEvent.getYouhuiquan().equals("1")) {
                   try {
                       //面值券计算
                       if (messageEvent.getYouhuitype().equals("1")) {
                           //
                           Double cny2 = string2Double(tvTotalDiscount.getText().toString());
                           Double min_ = string2Double(messageEvent.getMin_amount_use());
                           if (min_ <= cny2) {
                               Hint.Short(getActivity(), "该优惠券不满足使用条件");
                               return;
                           }
                           selectedOrderMap.setCoupon_minus(messageEvent.getCheap_money());
                           //优惠金额
//                        String CNY3 = df.format(Double.parseDouble()); //6.20   这个是字符串，但已经是我要的两位小数了
                           Double cny3 = string2Double(messageEvent.getCheap_money());
                           Double TotalDiscount = cny3 + cny2;
                           rlYouhuiquan.setVisibility(View.VISIBLE);
                           tvTotalDiscount.setText("" + TotalDiscount);
                           youhuimoney.setText("优惠" + messageEvent.getCheap_money() + "元");
                           //折扣券计算
                       } else if (messageEvent.getYouhuitype().equals("2")) {
                           //优惠折扣
                           Double cny2 = string2Double(tvTotalDiscount.getText().toString());
                           //应付金额
                           Double TotalMoney = string2Double(tvTotalMoney.getText().toString().substring(1, tvTotalMoney.getText().toString().length()));
                           Double bbb = TotalMoney - cny2;
                           Double cny3 = string2Double(messageEvent.getCheap_money());
                           Double TotalDiscount = (1 - cny3 * 0.1) * bbb;
                           youhuimoney.setText("折扣" + messageEvent.getCheap_money() + "折");
                           tvTotalDiscount.setText("" + TotalDiscount);
                       }
                   } catch (Exception e) {

                   }

               } else {
                   //面值券取消
                   if (messageEvent.getYouhuitype().equals("1")) {
                       rlYouhuiquan.setVisibility(View.GONE);

                       Double cny3 = string2Double(tvTotalDiscount.getText().toString()); //6.20
                       Double min_ = string2Double(messageEvent.getMin_amount_use());
                       Hint.Short(getActivity(), min_ + "》》》" + cny3);
                       if (min_ <= cny3) {
//                        Hint.Short(getActivity(),"该优惠券不满足使用条件");
                           return;
                       }
                       Double cny = string2Double(messageEvent.getCheap_money());
                       Double abb = cny3 + (-cny);
                       tvTotalDiscount.setText(abb + "");
                   } else {
                       //折扣券取消
                       rlYouhuiquan.setVisibility(View.GONE);

                       Double cny3 = string2Double(tvTotalDiscount.getText().toString());

                       Double cny = string2Double(messageEvent.getCheap_money());
                       Double abb = cny - cny3;
                       tvTotalDiscount.setText(abb + "");
                   }

               }

           }
           //会员卡逻辑

           if (messageEvent.getType().equals("vip")) {
               if (messageEvent.getYouhuiquan().equals("1")) {
                   try {
//                    int a = Integer.parseInt(str);
                       Double cny = (1 - Double.parseDouble(messageEvent.getUservip()) * 0.1) * Double.parseDouble(tvTotalMoney.getText().toString().substring(1, tvTotalMoney.getText().toString().length()));
//                    Double cny = Double.parseDouble();//6.2041    这个是转为double类型
                       DecimalFormat df = new DecimalFormat("0.00");
                       String CNY = df.format(cny); //6.20   这个是字符串，但已经是我要的两位小数了
                       Log.i(TAG, CNY);
                       Double cny2 = Double.parseDouble(CNY); //6.20
                       tvTotalDiscount.setText("" + cny2);
                   } catch (NumberFormatException e) {
                       e.printStackTrace();
                   }

               } else {
//                rlYouhuiquan.setVisibility(View.GONE);
                   tvTotalDiscount.setText("0.00");
                   try {
//                    int a = Integer.parseInt(str);

                       Double cny = (Double.parseDouble(tvTotalDiscount.getText().toString()));
//                    Double cny = Double.parseDouble();//6.2041    这个是转为double类型
                       DecimalFormat df = new DecimalFormat("0.00");
                       String CNY = df.format(cny); //6.20   这个是字符串，但已经是我要的两位小数了
                       Log.i(TAG, CNY);
                       Double cny2 = Double.parseDouble(CNY); //6.20
                       tvTotalDiscount.setText("" + cny2);
                   } catch (NumberFormatException e) {
                       e.printStackTrace();
                   }
               }

           }
           if (messageEvent.getType().equals(CALCULATEDISCOUNT)) {

               Log.d(TAG, "onEventsticky: CALCULATEDISCOUNT");
               discountBean = messageEvent.getDiscountBean();
            /*Log.d(TAG, "onEventsticky: diacountBean = "+discountBean.toString()


            +" totalMoney= "+totalMoney);*/
               double vip_card = 0;

               if (discountBean == null) return;

               String vip_Card_num = discountBean.getVip_card_num();
               String zhekou_num = discountBean.getZhekou_num();
               String mian_zhi_num = discountBean.getMianzhi_num();


               String mian_zhi_max = discountBean.getMianzhi_max();
               CrashSuper endCrashSuper = null;

               if (totalMoney == 0.00) {

                   if (!TextUtils.isEmpty(zhekou_num) || !TextUtils.isEmpty(mian_zhi_num)) {


                       ToastUtils.showShortToast("请先选择商品");
                       MessageEvent cancelCoupons = new MessageEvent(CANCELCOUPONS2);

                       EventBus.getDefault().post(cancelCoupons);
                   }

                   return;
               }

               Log.d(TAG, "onEventsticky: totalMoney= " + totalMoney);
               NormalCrash normalCrash = new NormalCrash(totalMoney);

               endCrashSuper = normalCrash;

               VipCrashSuper vipCrashSuper = null;
               ZhekouCrash zhekouCrash = null;
               MianzhiCrash mianzhiCrash = null;
               if (!TextUtils.isEmpty(vip_Card_num)) {

                   vip_card = Double.parseDouble(vip_Card_num);
                   vipCrashSuper = new VipCrashSuper(vip_card * 0.1 + "");

                   vipCrashSuper.attach(normalCrash);
                   endCrashSuper = vipCrashSuper;

               }

               if (!TextUtils.isEmpty(zhekou_num)) {


                   zhekouCrash = new ZhekouCrash(Double.parseDouble(zhekou_num) * 0.1);

                   if (vipCrashSuper == null) {


                       zhekouCrash.attach(normalCrash);

                   } else {
                       zhekouCrash.attach(vipCrashSuper);

                   }
                   endCrashSuper = zhekouCrash;

               }
               if (!TextUtils.isEmpty(mian_zhi_num)) {


                   mianzhiCrash = new MianzhiCrash(Double.parseDouble(mian_zhi_max),
                           Double.parseDouble(mian_zhi_num));

                   if (vipCrashSuper == null) {


                       mianzhiCrash.attach(normalCrash);

                   } else {
                       mianzhiCrash.attach(vipCrashSuper);

                   }


                   endCrashSuper = mianzhiCrash;
               }

               moneyBean = endCrashSuper.calculateMoney();
               rlYouhuiquan.setVisibility(View.GONE);

               youhuimoney.setText(TextUtils.isEmpty(mian_zhi_num) ? "优惠" + discountBean.getZhekou_num()
                       + "折" : "优惠" + discountBean.getMianzhi_num()
                       + "元");

               tvTotalMoney.setText("¥ " + DecimalFormatUtils.doubleToMoneyWithOutSymbol(moneyBean.getEndMoney()));


               tvTotalDiscount.setText(DecimalFormatUtils.doubleToMoneyWithOutSymbol(moneyBean.getDiscountMoney()));

               if (!TextUtils.isEmpty(mian_zhi_num) || !TextUtils.isEmpty(zhekou_num)) {
                   rlYouhuiquan.setVisibility(View.VISIBLE);


               }

               if (moneyBean.isCanDiscount()) {

                   ToastUtils.showShortToast("该优惠券不满足使用条件");
                   discountBean.setCoupons_id("");
                   discountBean.setZhekou_num("");
                   discountBean.setMianzhi_max("");
                   discountBean.setMianzhi_num("");

                   if (!isPaySucess)
                       sendOrderListToSecondScreen(SecondScreenInfo.UPDATE);
                   MessageEvent messageEvent1 = new MessageEvent(CANCELCOUPONS2);

                   EventBus.getDefault().post(messageEvent1);
               }


               Log.d(TAG, "onEventsticky: moneyBean= " + moneyBean.toString() + "" +
                       "    " + endCrashSuper.toString());
           /* if (!TextUtils.isEmpty(vip_Card_num)){

                vip_card = Double.parseDouble(vip_Card_num)*0.1;


                VipCrashSuper vipCrashSuper = new VipCrashSuper(vip_card+"");

                vipCrashSuper.attach(normalCrash);
                MoneyBean moneyBean = vipCrashSuper.calculateMoney();
                Log.d(TAG, "onEventsticky: moneyBean = "+moneyBean.toString());


            }*/

               if (!isPaySucess)
                   sendOrderListToSecondScreen(SecondScreenInfo.UPDATE);


           }
           if (messageEvent.getType().equals(CANCELCOUPONS1)) {


               rlYouhuiquan.setVisibility(View.GONE);

           }

        /* if (messageEvent.getType().equals("allDelete")){

              moneyBean = null;
              discountBean = null;
         }*/


           if (messageEvent.getType().equals("client_info")) {


               //在支付完成后会置空会员信息
               vipInfo = new VipInfo();
               String msg = messageEvent.getClientinfo();
               if (TextUtils.isEmpty(msg)) {

                   return;
               }
               try {
                   JSONObject jsonObject = new JSONObject(msg);

                   JSONObject jsonObject1 = jsonObject.getJSONObject("base_info");
                   JSONObject blance = jsonObject.optJSONObject("balance");

                   if (blance!=null){

                       user_id = jsonObject1.optString("id");


                   }

                   Log.d(TAG, "onEventsticky: jsonObject1= "+jsonObject1.toString());
                  //这里用来发送给副屏
                   vipInfo.setMobile(jsonObject1.getString("mobile"));
                   vipInfo.setName(jsonObject1.getString("nickname"));
                   sendOrderListToSecondScreen(SecondScreenInfo.ADD_VIP_INFO);



               } catch (JSONException e) {
               }


           }

           if (messageEvent.getType().equals("remove_vip")) {

               vipInfo = null;
               user_id = "";
               balancePayBean = null;
               Log.d(TAG, "onEventsticky: remove_vip");
               sendOrderListToSecondScreen(SecondScreenInfo.REMOVE_VIP_INFO);
           }

           EventBus.getDefault().removeAllStickyEvents();
       }catch (Exception e){

           Log.d(TAG, "onEventsticky: e= "+e.getMessage());

           App.store.put("error  "," e= "+e.getMessage().toString());
           App.store.commit();
       }

    }
    private  Double string2Double(String nub){
        Double cny = Double.parseDouble(nub);//6.2041    这个是转为double类型
        DecimalFormat df = new DecimalFormat("0.00");
        String CNY = df.format(cny); //6.20   这个是字符串，但已经是我要的两位小数了
        Log.i(TAG, CNY);
        Double cny2 = Double.parseDouble(CNY); //6.20
        return cny2;
    }


    @Subscribe(threadMode = ThreadMode.MAIN ,priority = 1)

    public void onEventPriorityLow(MessageEvent messageEvent){


        if (messageEvent.getType().equals("paySucess")){

            sendOrderListToSecondScreen(SecondScreenInfo.PAYSUCESS);
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 10)
    public void onEvent(MessageEvent messageEvent) {
        Log.d(TAG, "onEvent: Left_crashier_fragment");
        if (messageEvent.getType().equals("update")) {


            int type = 0;
            //判断当前商品选择数量
            int number;
            goodsBean = messageEvent.getGoodsBean();

            Log.d(TAG, "onEvent: goodBean= "+goodsBean.toString());
            if (goodsBean != null) {

                if (goodsSelectMap.get(goodsBean.getGoodsId()) == null) {

                    number = 1;

                    type = 0;

                } else {

                    type = 1;
                    number = goodsSelectMap.get(goodsBean.getGoodsId());
                    Log.d(TAG, "onEvent: number= " + number + " id= " +
                            goodsBean.getGoodsId()

                            + " type= " + goodsBean.getChangeType());
                    if (goodsBean.getChangeType() == null) {
                        Log.d(TAG, "onEvent: number++");
                        number++;
                    } else if (goodsBean.getChangeType().equals("countChange")) {
                        number = goodsBean.getNumber();
                        goodsBean.setChangeType("");
                    }else{
                        number++;


                    }

                }



                goodsSelectMap.put(goodsBean.getGoodsId(), number);


       /*         oldSelectedGoodsArrayList.clear();
                for (Map.Entry<String,goodsBean>item:
                        selectedGoodsMap.entrySet()) {

                    try {
                        oldSelectedGoodsArrayList.add(((com.qimai.xinlingshou.bean.goodsBean) (item.getValue())).clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }*/



                goodsBean.setNumber(goodsSelectMap.get(goodsBean.getGoodsId()));
            }





            //去更新集合
            updateSelectedGoodsWithType(goodsBean,type);

            //去更新应收金额有两个地方 商品选择数量变化时与删除商品时

            updateTotalMoney();


        }else if (messageEvent.getType().equals("allDelete")){

            moneyBean = null;
            discountBean = null;
            allDelete();

        }

        else if (messageEvent.getType().equals("cancelAllDelete")){

            Log.d(TAG, "onEvent: cancelCollection 111");
            setCancelAndClooectionEnable(true);

        }else if (messageEvent.getType().equals("cancelCollection")){
            Log.d(TAG, "onEvent: cancelCollection 2222");
            //说明是混合支付
            if (mOrderbean.getUse_wallet()==1){

                payModel.cancelOrder(mOrderbean.getOrder(), new NetWorkCallBack() {
                    @Override
                    public void onSucess(Object data) {

                        ToastUtils.showShortToast("退款成功！");
                        //这里是当支付界面三个页面任一个页面点击取消订单
                        setCancelAndClooectionEnable(true);

                        ((MainActivity) activity).showRightCrashierLayout();
                        sendOrderListToSecondScreen(SecondScreenInfo.CANCELPAY);

                        MessageEvent messageEvent2 = new MessageEvent("ThridisPay");
                        messageEvent2.setIsPay("0");
                        EventBus.getDefault().post(messageEvent2);
                    }

                    @Override
                    public void onFailed(String msg) {

                        ToastUtils.showShortToast(msg);
                    }
                });

            }else{

                //这里是当支付界面三个页面任一个页面点击取消订单
                setCancelAndClooectionEnable(true);
                ((MainActivity) activity).showRightCrashierLayout();
                sendOrderListToSecondScreen(SecondScreenInfo.CANCELPAY);

                MessageEvent messageEvent2 = new MessageEvent("ThridisPay");
                messageEvent2.setIsPay("0");
                EventBus.getDefault().post(messageEvent2);
            }


        }else if(messageEvent.getType().equals("pendingOrder")){


            //目前只支持挂一单
            if (pendOrderId>=1){

                return;
            }

            if (!selectedGoodsMap.isEmpty()){
                //因为map不是值传递,所以要进行深拷贝
                Map<String, goodsBean> selectedGoodsMap2 = new HashMap<>();

                goodsBean goodsBean ;
                for (Map.Entry<String, goodsBean>map:
                     selectedGoodsMap.entrySet()) {
                    goodsBean = map.getValue();

                    selectedGoodsMap2.put(map.getKey(),map.getValue());
                }

                //selectedGoodsMap2.putAll(selectedGoodsMap);
                Log.d(TAG, "onEvent: putall");

                for (Map.Entry<String,goodsBean>item:
                     selectedGoodsMap.entrySet()) {

                    selectedGoodsMap2.put(item.getKey(),
                            (com.qimai.xinlingshou.bean.goodsBean) deepClone(item.getValue()));
                }


                pendOrderMap.put(pendOrderId, selectedGoodsMap2);


                /*for (Map.Entry<Integer, Map<String, goodsBean>> map:
                     pendOrderMap.entrySet()) {
                    int key = map.getKey();
                    HashMap<String,goodsBean> map1 = (HashMap<String, goodsBean>) pendOrderMap.get(key);


                    for (Map.Entry goods:
                         map1.entrySet()) {

                        Log.d(TAG, "onEvent: key= "+goods.getKey());
                        Log.d(TAG, "onEvent: map= "+goods.getValue());

                    }
                    
                    
                    
                }*/
                allDelete();




                messageEvent = new MessageEvent("pendingOrderSucess");

                messageEvent.setTypeInt(pendOrderId);
                EventBus.getDefault().post(messageEvent);

                sendOrderListToSecondScreen(SecondScreenInfo.PENDINGORDERSUCESS);
               // pendOrderId = 0;
                pendOrderId++;
            }



        }else if (messageEvent.getType().equals("takeOrder")){


           // ToastUtils.showShortToast("take order");
            showPendingOrder(messageEvent.getTypeInt());

        }else if (messageEvent.getType().equals(MessageEvent.REPEATPRINT)){


           // printOrder(oldType,oldAddInfo);

            try {
                singlePrint(printInfoBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showPendingOrder(int typeInt) {


        //ToastUtils.showShortToast("take order typeInt= "+typeInt);
        Log.d(TAG, "showPendingOrder: pendOrder size= "+pendOrderMap.isEmpty()

        +" typeId= "+typeInt);
        Map<String, goodsBean> orderMap = pendOrderMap.get(typeInt);


        //updateSelectedGoods();




        //ToastUtils.showShortToast("take order typeInt= isEmpty= "+orderMap.isEmpty());

        if (!orderMap.isEmpty()) {
            allDelete();
            for (Map.Entry<String, goodsBean> goodsBean1:
                 orderMap.entrySet()) {
                Log.d(TAG, "showPendingOrder: "+(goodsBean1.getValue()==goodsBean));
               /* selectedGoodsMap.clear();
                goodsSelectMap.clear();

                selectedGoodsArrayList.clear();

                goodsSelectAdapter.updateData(selectedGoodsArrayList);
                updateTotalMoney();*/

                updateSelectedGoods(goodsBean1.getValue());

                //去更新应收金额有两个地方 商品选择数量变化时与删除商品时

                updateTotalMoney();

                //取单结束在把他设为0
                pendOrderId = 0;
            }

        }
    }

    public static String formatStr(String str) {


        int len = strlen(str);

        if (len >= 20) {
            str = str.substring(0, 10);
            return str;
        }

        int padLen = 20 - len;
        for (int i = 0; i < padLen; i++) {
            str += " ";
        }

        return str;


    }
    public static String formatStr2(String str) {


        int len = strlen(str);

        if (len >= 8) {
            str = str.substring(0, 8);
            return str;
        }

        int padLen = 8 - len;
        for (int i = 0; i < padLen; i++) {
            str += " ";
        }

        return str;


    }
    public static int strlen(String str) {

        if (str == null) {
            return 0;
        }

        return str.replaceAll("[^\\x00-\\xff]", "**").length();
    }

    /***
     *
     * @Function 更新左侧应收金额
     */
    public void updateTotalMoney() {

        if (selectedGoodsArrayList!=null&&selectedGoodsArrayList.size()!=0){
            totalMoney = 0.00;
            Log.d(TAG, "updateTotalMoney: ");
           setCancelAndClooectionEnable(true);
            for (goodsBean goods:
                 selectedGoodsArrayList) {
                totalMoney+=goods.getPrice()*goods.getNumber();
                Log.d(TAG, "updateTotalMoney: price= "+goods.getPrice()+" number= "+
                goods.getNumber()+" totalMoney= "+totalMoney);
                tvTotalMoney.setText(DecimalFormatUtils.doubleToMoney(totalMoney));
            }
            //totalMoney = 0.00;

        }else{

            totalMoney = 0.00;

            setCancelAndClooectionEnable(false);

            tvTotalMoney.setText("¥ "+totalMoney);
            tvTotalDiscount.setText("0.0");


        }

        if (totalMoney!=0.00){

            MessageEvent messageEvent = new MessageEvent(UPDATEDISCOUNT);

            EventBus.getDefault().post(messageEvent);

        }
        if (totalMoney==0.00){
            MessageEvent messageEvent = new MessageEvent(CANCELCOUPONS2);

            EventBus.getDefault().post(messageEvent);

        }
            long imgsMenuTaskId = (long) SharePreferenceUtil.getParam(getActivity(), imgsKey, 0L);
            checkImgsMenuExists(imgsMenuTaskId);


            if (!isPaySucess) {
                sendOrderListToSecondScreen(SecondScreenInfo.UPDATE);

            }
            Log.d(TAG, "updateTotalMoney: sendOrderListToSecondScreen");
            MessageEvent messageEvent = new MessageEvent(MessageEvent.SELECT_GOODS);
            messageEvent.setGoodsBeanArrayList(selectedGoodsArrayList);
            EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void checkImgsMenuExists(final long taskId) {
//        Hint.Short(getActivity(),"taskId---->" + taskId);
       // sendImgsMenu();
        if (taskId < 0) {
          //  sendImgsMenu();
            return;
        }

        checkFileExist(taskId, new ICheckFileCallback() {
            @Override
            public void onCheckFail() {
                Log.d(TAG, "onCheckFail: ------->file is not exist");
                Hint.Short(getActivity(),"onCheckFail: ------->file is not exist");
               // sendImgsMenu();
            }

            @Override
            public void onResult(boolean exist) {
                if (exist) {
                    Log.d(TAG, "onResult: ---------->file is exist");
                   // Hint.Short(getActivity(),"发送轮播图失败---->" + "file is exist");
                    showImgsMenu(taskId);
                } else {
                   // Hint.Short(getActivity(),"发送轮播图失败---->" + "file is not exist");
                    Log.d(TAG, "onResult: --------->file is not exists");
                   // sendImgsMenu();
                }
            }
        });
    }

        private void sendImgsMenu(String path) {
            Log.d(TAG, "sendImgsMenu: fileName= "+path);
           /* mDSKernel.sendFile("com.example.niu.myapplication",
                  path
                    , new ISendCallback() {
                        @Override
                        public void onSendSuccess(long taskId) {
                          *//* String path =  FilesManager.getInstance().getFile(taskId)
                                    .path;*//*
                            Log.d(TAG, "onSendSuccess: path= "+taskId);
                        }

                        @Override
                        public void onSendFail(int errorId, String errorInfo) {

                            Log.d(TAG, "onSendFail: errInfo= "+errorInfo);
                        }

                        @Override
                        public void onSendProcess(long totle, long sended) {

                            Log.d(TAG, "onSendProcess: ");
                        }
                    });*/
         
         
           /* mDSKernel.sendFiles("com.example.niu.myapplication", "", imgs, new ISendFilesCallback() {
                @Override
                public void onAllSendSuccess(long fileId) {
                    showImgsMenu(fileId);
                    SharePreferenceUtil.setParam(getActivity(), imgsKey, fileId);
                }

                @Override
                public void onSendSuccess(String path, long taskId) {
                }

                @Override
                public void onSendFaile(int errorId, String errorInfo) {
        //            showToast("发送轮播图失败---->" + errorInfo);
        //            dismissDialog();
                    Hint.Short(getActivity(),"发送轮播图失败---->" + errorInfo);
                }

                @Override
                public void onSendFileFaile(String path, int errorId, String errorInfo) {
        //            dismissDialog();
        //            showToast("发送轮播图失败---->" + errorInfo);
                }

                @Override
                public void onSendProcess(String path, long totle, long sended) {
                }
            });*/
        }



    private void sendOrderListToSecondScreen(final String type){







      /*  if (isPaySucess&&type.equals(SecondScreenInfo.PAYSUCESS)){

            type = SecondScreenInfo.PAYSUCESS;

        }else if (!isPaySucess&&type.equals(SecondScreenInfo.UPDATE)){

            type = SecondScreenInfo.UPDATE;
        }*/
        //Intent intent = new Intent("test.data");

        // getActivity().sendBroadcast(intent);



        //利用线程池节省频繁创建线程
        executorService
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        DataPacket packet1 = UPacketFactory.
                                buildShowText("com.example.niu.myapplication",
                                        "hello world",null);
                        // mDSKernel.sendData(packet1);


                        Log.d(TAG, "sendOrderListToSecondScreen: ispaySucess= "+isPaySucess);
                        SecondScreenInfo secondScreenInfo = new SecondScreenInfo();
                        secondScreenInfo.setType(type);
                        //secondScreenInfo.setTotalCostMoney(tvTotalMoney.getText().toString());
                        secondScreenInfo.setTotalCostMoney(totalMoney+"");
                        secondScreenInfo.setTotalDiscountMoney(tvTotalDiscount.getText().toString());

                        String totalMoney1 = tvTotalMoney.getText().toString();
                        if (totalMoney1.contains("¥")){
                            Log.d(TAG, "onViewClicked1: before totalMoney1= "+totalMoney1);

                            totalMoney1 = totalMoney1.replace("¥ ","");


                            Log.d(TAG, "onViewClicked1: after totalMoney1= "+totalMoney1);
                        }
                        secondScreenInfo.setUser_need_pay(totalMoney1);

                        secondScreenInfo.setPayMethod(payMethod);

                        //只有现金支付才需要传
                        if (payMethod==1&&isPaySucess) {


                            secondScreenInfo.setAcual_collect_money(acual_collect_money);
                            secondScreenInfo.setZhaoling(zhaoling);
                        }


                        if (moneyBean!=null) {
                            secondScreenInfo.setTotalActualPay((totalMoney-moneyBean.getDiscountMoney())+"");


                        }else{

                            secondScreenInfo.setTotalActualPay(totalMoney+"");

                        }


                        Log.d(TAG, "sendOrderListToSecondScreen: tvTotalDiscount= "+(tvTotalDiscount.getText().toString()));
                        secondScreenInfo.setGoodsBeanList(selectedGoodsArrayList);
                        if (vipInfo!=null) {
                            secondScreenInfo.setVipInfo(vipInfo);
                        }

                        if (moneyBean!=null){
                            secondScreenInfo.setMoneyBean(moneyBean);

                        }

                        Gson gson = new Gson();
                        //   JSONObject jsonObject = new JSONObject();
                        String jsonStr = gson.toJson(secondScreenInfo);


                        Log.d(TAG, "sendOrderListToSecondScreen: str= "+jsonStr+"  type= "+secondScreenInfo.getType());
        /*
        try {
           // jsonObject.put("title","test");

            //jsonObject.put("content",jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

                        DataPacket dataPacket = UPacketFactory.buildShowText("com.example.niu.myapplication",
                                jsonStr, new ISendCallback() {
                                    @Override
                                    public void onSendSuccess(long taskId) {

                                        Log.d(TAG, "onSendSuccess: ");
                                    }

                                    @Override
                                    public void onSendFail(int errorId, String errorInfo) {

                                        Log.d(TAG, "onSendFail: ");
                                    }

                                    @Override
                                    public void onSendProcess(long totle, long sended) {

                                        Log.d(TAG, "onSendProcess: ");
                                    }
                                });

                        mDSKernel.sendData(dataPacket);


                    }
                });
       /* new Thread(new Runnable() {
            @Override
            public void run() {




            }
        }).start();*/


    }


   /* private void sendOrderListToSecondScreen(String type){







      *//*  if (isPaySucess&&type.equals(SecondScreenInfo.PAYSUCESS)){

            type = SecondScreenInfo.PAYSUCESS;

        }else if (!isPaySucess&&type.equals(SecondScreenInfo.UPDATE)){

            type = SecondScreenInfo.UPDATE;
        }*//*
        //Intent intent = new Intent("test.data");

       // getActivity().sendBroadcast(intent);
        DataPacket packet1 = UPacketFactory.
                buildShowText("com.example.niu.myapplication",
                        "hello world",null);
       // mDSKernel.sendData(packet1);


        Log.d(TAG, "sendOrderListToSecondScreen: ispaySucess= "+isPaySucess);
        SecondScreenInfo secondScreenInfo = new SecondScreenInfo();
        secondScreenInfo.setType(type);
        //secondScreenInfo.setTotalCostMoney(tvTotalMoney.getText().toString());
        secondScreenInfo.setTotalCostMoney(totalMoney+"");
        secondScreenInfo.setTotalDiscountMoney(tvTotalDiscount.getText().toString());

        String totalMoney1 = tvTotalMoney.getText().toString();
        if (totalMoney1.contains("¥")){
            Log.d(TAG, "onViewClicked1: before totalMoney1= "+totalMoney1);

            totalMoney1 = totalMoney1.replace("¥ ","");


            Log.d(TAG, "onViewClicked1: after totalMoney1= "+totalMoney1);
        }
        secondScreenInfo.setUser_need_pay(totalMoney1);

        secondScreenInfo.setPayMethod(payMethod);

        //只有现金支付才需要传
        if (payMethod==1&&isPaySucess) {


            secondScreenInfo.setAcual_collect_money(acual_collect_money);
            secondScreenInfo.setZhaoling(zhaoling);
        }


        if (moneyBean!=null) {
            secondScreenInfo.setTotalActualPay((totalMoney-moneyBean.getDiscountMoney())+"");


        }else{

            secondScreenInfo.setTotalActualPay(totalMoney+"");

        }


        Log.d(TAG, "sendOrderListToSecondScreen: tvTotalDiscount= "+(tvTotalDiscount.getText().toString()));
        secondScreenInfo.setGoodsBeanList(selectedGoodsArrayList);
        if (vipInfo!=null) {
            secondScreenInfo.setVipInfo(vipInfo);
        }

        if (moneyBean!=null){
            secondScreenInfo.setMoneyBean(moneyBean);

        }

        Gson gson = new Gson();
     //   JSONObject jsonObject = new JSONObject();
        String jsonStr = gson.toJson(secondScreenInfo);


        Log.d(TAG, "sendOrderListToSecondScreen: str= "+jsonStr+"  type= "+secondScreenInfo.getType());
        *//*
        try {
           // jsonObject.put("title","test");

            //jsonObject.put("content",jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }*//*

            DataPacket dataPacket = UPacketFactory.buildShowText("com.example.niu.myapplication",
                    jsonStr, new ISendCallback() {
                        @Override
                        public void onSendSuccess(long taskId) {

                            Log.d(TAG, "onSendSuccess: ");
                        }

                        @Override
                        public void onSendFail(int errorId, String errorInfo) {

                            Log.d(TAG, "onSendFail: ");
                        }

                        @Override
                        public void onSendProcess(long totle, long sended) {

                            Log.d(TAG, "onSendProcess: ");
                        }
                    });

            mDSKernel.sendData(dataPacket);

    }*/

        private void showImgsMenu(long taskId) {
        final JSONObject data = new JSONObject();
        try {
          //  Hint.Short(getActivity(),taskId+"");
            data.put("title", "企小店欢迎您");
            JSONObject head = new JSONObject();
            head.put("param1", "商品名");
            head.put("param2", "单价");
            head.put("param3", "数量");
            head.put("param4", "金额");
            data.put("head", head);
            data.put("alternateTime", 1000);
            JSONArray list = new JSONArray();

            for (int i = 0; i < selectedGoodsArrayList.size(); i++) {
                JSONObject listItem = new JSONObject();
                listItem.put("param1", selectedGoodsArrayList.get(i).getGoodsName());
                listItem.put("param2", "¥ "+selectedGoodsArrayList.get(i).getPrice());
                listItem.put("param3", "x "+selectedGoodsArrayList.get(i).getNumber());
                listItem.put("param4","¥ "+selectedGoodsArrayList.get(i).getPrice()*selectedGoodsArrayList.get(i).getNumber() );
                list.put(listItem);
            }
            data.put("list", list);
            JSONArray KVPList = new JSONArray();
            JSONObject KVPListOne = new JSONObject();
            KVPListOne.put("name", "应收金额 ");
            KVPListOne.put("value", tvTotalMoney.getText().toString());
            JSONObject KVPListTwo = new JSONObject();
            KVPListTwo.put("name", "优惠 ");
            KVPListTwo.put("value", tvTotalDiscount.getText().toString());
            JSONObject KVPListThree = new JSONObject();
            KVPListThree.put("name", "会员折扣 ");
            KVPListThree.put("value","22222" );
//            JSONObject KVPListFour = new JSONObject();
//            KVPListFour.put("name", "应收金额 ");
//            KVPListFour.put("value", "120.00");
            KVPList.put(0, KVPListOne);
            KVPList.put(1, KVPListTwo);
            KVPList.put(2, KVPListThree);
//            KVPList.put(3, KVPListFour);
            data.put("KVPList", KVPList);
        } catch (JSONException e) {
            e.printStackTrace();
           // ToastUtils.showLongToast(e.toString());
        }
        String json1 = UPacketFactory.createJson(DataModel.SHOW_IMGS_LIST, data.toString());
        mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json1, taskId, null);
    }
    @OnClick({R.id.ll_top, R.id.tv_total_money, R.id.tv_total_discount, R.id.tv_all_cancel, R.id.ll_bottom_menu, R.id.ll_empty_goods, R.id.rv_select_goods, R.id.ll_goods_items})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.ll_top:
                break;
            case R.id.tv_total_money:
                break;
            case R.id.tv_total_discount:
                break;
            case R.id.tv_all_cancel:

                //整单取消时候不能点击

                setCancelAndClooectionEnable(false);
                ((MainActivity)activity).showUnselectGoodsLayout();


                break;

            case R.id.ll_bottom_menu:
                break;
            case R.id.ll_empty_goods:
                break;
            case R.id.rv_select_goods:
                break;
            case R.id.ll_goods_items:
                break;
        }
    }

    /**
     *
     * @Function 整单取消按钮与收款不能点击
     * */

    private void setCancelAndClooectionEnable(boolean enable) {

        tvAllCancel.setEnabled(enable);
        tvAllCollection.setEnabled(enable);

        MessageEvent messageEvent = new MessageEvent("ThridisPay");
        messageEvent.setIsPay("1");
        EventBus.getDefault().post(messageEvent);

    }




    public void setOnPendingOrderSucess(onPendingOrderSucess onPendingOrderSucess){

        this.onPendingOrderSucess = onPendingOrderSucess;

    }



    public interface onPendingOrderSucess{


        public void onPendingOrderSucess(int position);


    }


    public static Object deepClone(Object obj) {
        try {// 将对象写到流里
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);// 从流里读出来
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查单张图图缓存问价是否存在
     *
     * @param taskID
     */
    private void checkImgFileExist(final long taskID) {

        sendPicture();
        if (taskID < 0) {
            sendPicture();
            return;
        }
        checkFileExist(taskID, new ICheckFileCallback() {
            @Override
            public void onCheckFail() {
                //检查缓存文件失败
                Log.d(TAG, "onCheckFail: ------------>file not exist");
                sendPicture();
            }

            @Override
            public void onResult(boolean exist) {
                if (exist) {
                    //缓存文件存在
                    Log.d(TAG, "onResult: --------->file is exist");
//                    dismissDialog();
                    showPicture(taskID);
                } else {
                    //缓存文件不存在
                    Log.d(TAG, "onResult: --------->file is not exist");
                    sendPicture();
                }
            }
        });
    }
    private void sendPicture() {

//        Hint.Short(getActivity(),"--");
        Log.d(TAG, "sendPicture: --------->1111111111111111111");
        mDSKernel.sendFile(DSKernel.getDSDPackageName(), Environment.getExternalStorageDirectory().getPath() + "/f_index.jpg", new ISendCallback() {
            @Override
            public void onSendSuccess(long taskId) {
//                dismissDialog();
                SharePreferenceUtil.setParam(getActivity(), imgKey, taskId);
//                Log.d(TAG, "sendPicture: --------->222222222222222222");
                showPicture(taskId);
            }

            @Override
            public void onSendFail(int errorId, String errorInfo) {
                Log.d("TAG", "onSendFail: -------------------->" + errorId + "  " + errorInfo);
                //                showToast("发送单张图片失败---->" + errorInfo);
//                dismissDialog();

            }

            @Override
            public void onSendProcess(long totle, long sended) {
                Log.d(TAG, "sendPicture: --------->"+totle+"  "+sended);
            }
        });
    }

    /**
     * 展示单张图片
     *
     * @param taskId
     */
    private void showPicture(long taskId) {
        //显示图片
        try {
            JSONObject json = new JSONObject();
            json.put("dataModel", "SHOW_IMG_WELCOME");
            json.put("data", "default");
            mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json.toString(), taskId, new ISendCallback() {
                @Override
                public void onSendSuccess(long taskId) {
                    Log.d(TAG, "sendPicture: --------->33333333333333");
                }

                @Override
                public void onSendFail(int errorId, String errorInfo) {

                }

                @Override
                public void onSendProcess(long totle, long sended) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void checkFileExist(long fileId, final ICheckFileCallback mICheckFileCallback) {
        DataPacket packet = new DataPacket.Builder(DSData.DataType.CHECK_FILE).data("def").
                recPackName(DSKernel.getDSDPackageName()).addCallback(new ISendCallback() {
            @Override
            public void onSendSuccess(long taskId) {

            }
            @Override
            public void onSendFail(int errorId, String errorInfo) {
                if (mICheckFileCallback != null) {
                    mICheckFileCallback.onCheckFail();
                }
            }
            @Override
            public void onSendProcess(long totle, long sended) {

            }
        }).isReport(true).build();
        packet.getData().fileId = fileId;
        mDSKernel.sendQuery(packet, new QueryCallback() {
            @Override
            public void onReceiveData(DSData data) {
                boolean exist = TextUtils.equals("true", data.data);
                if (mICheckFileCallback != null) {
                    mICheckFileCallback.onResult(exist);
                }
            }
        });
    }

    public void calculateInSampleSize(int reqWidth,int reqHeight){


       BitmapFactory.Options options = new BitmapFactory.Options();
       options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_welcome
        ,options);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int inSampleSize = 1;

        int halfWidth = width/2;
        int halfHeight = height/2;


        // pic    500*600
        // target 300*300


        while (halfWidth/inSampleSize >= reqWidth && halfHeight/inSampleSize >= reqHeight){

            inSampleSize*=2;

        }




    }

}
