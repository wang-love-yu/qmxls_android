package com.qimai.xinlingshou.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qimai.xinlingshou.R;
import com.qimai.xinlingshou.bean.BalancePayBean;
import com.qimai.xinlingshou.callback.BlancePayCallBack;
import com.qimai.xinlingshou.callback.NetWorkCallBack;
import com.qimai.xinlingshou.model.PayModel;
import com.qimai.xinlingshou.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 当余额足够的时候调这个Dialog
 */

public class PayOrderBalanceDialogFragment extends BaseDialogFragment {

    private static final String TAG = "PayOrderBalanceDialogFr";
    @BindView(R.id.tv_order_money)
    TextView tvOrderMoney;

    BlancePayCallBack blancePayCallBack;

    String userId;
    PayModel payModel;
    //这里是应付钱
    String totalMoney;
    String balance_leave;
    BalancePayBean balancePayBean;
    public void setBlancePayCallBack(BlancePayCallBack blancePayCallBack) {


        this.blancePayCallBack = blancePayCallBack;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog1);


    }

    public static PayOrderBalanceDialogFragment getInstance(String balance,String orderMoney
    ,String userId) {

        PayOrderBalanceDialogFragment payOrderBalanceDialogFragment = new PayOrderBalanceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("totalMoney", orderMoney);
        bundle.putString("blance",balance);
        bundle.putString("userId",userId);
        payOrderBalanceDialogFragment.setArguments(bundle);
        return payOrderBalanceDialogFragment;
    }

    public static PayOrderBalanceDialogFragment getInstance(BalancePayBean balancePayBean) {

        PayOrderBalanceDialogFragment payOrderBalanceDialogFragment = new PayOrderBalanceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("balance",balancePayBean);
        payOrderBalanceDialogFragment.setArguments(bundle);
        return payOrderBalanceDialogFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_pay, container, false);

        ButterKnife.bind(this, view);

        updateUI(getArguments());

        return view;
    }

    private void updateUI(Bundle arguments) {

        if (arguments==null){
            return;
        }

        balancePayBean = arguments.getParcelable("balance");


        userId = balancePayBean.getUserId();

        totalMoney = balancePayBean.getOrderMoney();

        tvOrderMoney.setText(totalMoney+"元");
    }

    @Override
    public void onResume() {
        super.onResume();


        //String totalMoney = getArguments().getString("totalMoney");

       // tvOrderMoney.setText(totalMoney);

    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCanceledOnTouchOutside(false);

        /*WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();

        layoutParams.width = 500;
        layoutParams.height = 500;
        getDialog().getWindow().setAttributes(layoutParams);*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.tv_ok, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ok:


                if (payModel==null){

                    payModel = new PayModel();
                }


                balancePayBean.getOrdersBean().setAmount("0.00");

                payModel.uploadOrderInfo(balancePayBean.getOrdersBean(),new NetWorkCallBack() {
                    @Override
                    public void onSucess(Object data) {

                        //上传成功就进行余额扣减

                        payModel.blancePay(userId, balancePayBean.getOrdersBean().getOrder(), new NetWorkCallBack() {
                            @Override
                            public void onSucess(Object data) {


                                balancePayBean.setOrderLeaveMoney("0.00");
                                //余额剩余
                                balance_leave = calculateLeavemoney(balancePayBean);
                                if (blancePayCallBack!=null){

                                    Log.d(TAG, "onSucess: balance_leave= "+balance_leave);
                                    balancePayBean.setBalanceLeave(balance_leave);
                                    balancePayBean.setType(BaseDialogFragment.BALANCE_PAY);

                                    Log.d(TAG, "onSucess222: balancePayBean= "+balancePayBean.toString());
                                    blancePayCallBack.onPaySucess(BaseDialogFragment.BALANCE_PAY,balancePayBean);
                                }

                                getDialog().dismiss();


                            }

                            @Override
                            public void onFailed(String msg) {

                                //ToastUtils.showShortToast(msg);
                                if (blancePayCallBack!=null){

                                    blancePayCallBack.onPayFailed(msg);
                                }


                            }
                        });



                    }

                    @Override
                    public void onFailed(String msg) {

                        ToastUtils.showLongToast(msg);
                    }
                });



                break;
            case R.id.tv_cancel:


                blancePayCallBack.onPayCancel();

                getDialog().dismiss();

                break;
        }
    }

    private String calculateLeavemoney(BalancePayBean balancePayBean) {


        Double total = Double.parseDouble(balancePayBean.getBalanceTotal());
        Double need = Double.parseDouble(balancePayBean.getOrderMoney());
        Log.d(TAG, "calculateLeavemoney: = total= "+total+" need= "+need+"  "+((total-need)));
        return (total-need)+"";
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(payModel!=null){
        payModel.onDestory();
        }
    }
}
