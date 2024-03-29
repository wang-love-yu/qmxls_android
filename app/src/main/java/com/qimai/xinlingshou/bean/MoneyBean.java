package com.qimai.xinlingshou.bean;

/**
 * Created by wanglei on 18-7-3.
 */

public class MoneyBean {


    private double totalMoney;
    private double discountMoney;
    private double endMoney;
    private double vipDiscount;

    //如果商品金额不满足优惠券使用条件，则为true
    private boolean isCanDiscount;


        //面值券或者折扣券优惠金额
    private double mianzhiOrZhrkouDiscount;
    public double getDiscountMoney() {
        return discountMoney;
    }

        //这里的折扣是包括 vip会员的优惠金额 + 面值券/折扣券 优惠金额
    public void setDiscountMoney(double discountMoney) {
        this.discountMoney = discountMoney;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Override
    public String toString() {
        return "MoneyBean{" +
                "totalMoney=" + totalMoney +
                ", discountMoney=" + discountMoney +
                ", endMoney=" + endMoney +
                ", vipDiscount=" + vipDiscount +
                ", isCanDiscount=" + isCanDiscount +
                ", mianzhiOrZhrkouDiscount=" + mianzhiOrZhrkouDiscount +
                '}';
    }

    public double getEndMoney() {
        return totalMoney-discountMoney;
    }

    public void setEndMoney(double endMoney) {
        this.endMoney = endMoney;
    }

    public boolean isCanDiscount() {
        return isCanDiscount;
    }

    public void setCanDiscount(boolean canDiscount) {
        isCanDiscount = canDiscount;
    }

    public double getVipDiscount() {
        return vipDiscount;
    }

    public void setVipDiscount(double vipDiscount) {
        this.vipDiscount = vipDiscount;
    }

    public double getMianzhiOrZhrkouDiscount() {
        return mianzhiOrZhrkouDiscount;
    }

    public void setMianzhiOrZhrkouDiscount(double mianzhiOrZhrkouDiscount) {
        this.mianzhiOrZhrkouDiscount = mianzhiOrZhrkouDiscount;
    }
}
