package com.qimai.xinlingshou.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BalancePayBean implements Parcelable {


    //余额支付的类型 单余额支付  混合余额支付

    private int type;


    //用户id
    private String userId;

    //订单金额
    private String orderMoney;

    //账户余额
    private String balanceTotal;

    //账户剩余余额
    private String balanceLeave;


    //同步信息
    private ordersBean ordersBean;

    @Override
    public String toString() {
        return "BalancePayBean{" +
                "type=" + type +
                ", userId='" + userId + '\'' +
                ", orderMoney='" + orderMoney + '\'' +
                ", balanceTotal='" + balanceTotal + '\'' +
                ", balanceLeave='" + balanceLeave + '\'' +
                ", balanceNeedPay='" + balanceNeedPay + '\'' +
                ", orderLeaveMoney='" + orderLeaveMoney + '\'' +
                '}';
    }

    private String balanceNeedPay;

    //如果是混合订单 ，还有订单剩余应支付金额

    private String orderLeaveMoney;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(String orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(String balanceTotal) {
        this.balanceTotal = balanceTotal;
    }

    public String getBalanceLeave() {
        return balanceLeave;
    }

    public void setBalanceLeave(String balanceLeave) {
        this.balanceLeave = balanceLeave;
    }

    public String getOrderLeaveMoney() {
        return orderLeaveMoney;
    }

    public void setOrderLeaveMoney(String orderLeaveMoney) {
        this.orderLeaveMoney = orderLeaveMoney;
    }



    public BalancePayBean() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBalanceNeedPay() {
        return balanceNeedPay;
    }

    public void setBalanceNeedPay(String balanceNeedPay) {
        this.balanceNeedPay = balanceNeedPay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.userId);
        dest.writeString(this.orderMoney);
        dest.writeString(this.balanceTotal);
        dest.writeString(this.balanceLeave);
        dest.writeString(this.balanceNeedPay);
        dest.writeString(this.orderLeaveMoney);
    }

    protected BalancePayBean(Parcel in) {
        this.type = in.readInt();
        this.userId = in.readString();
        this.orderMoney = in.readString();
        this.balanceTotal = in.readString();
        this.balanceLeave = in.readString();
        this.balanceNeedPay = in.readString();
        this.orderLeaveMoney = in.readString();
    }

    public static final Creator<BalancePayBean> CREATOR = new Creator<BalancePayBean>() {
        @Override
        public BalancePayBean createFromParcel(Parcel source) {
            return new BalancePayBean(source);
        }

        @Override
        public BalancePayBean[] newArray(int size) {
            return new BalancePayBean[size];
        }
    };

    public com.qimai.xinlingshou.bean.ordersBean getOrdersBean() {
        return ordersBean;
    }

    public void setOrdersBean(com.qimai.xinlingshou.bean.ordersBean ordersBean) {
        this.ordersBean = ordersBean;
    }
}
