package com.qimai.xinlingshou.bean;

/**
 *
 *   记录当前优惠选择
 * Created by wanglei on 18-7-3.
 */

public class DiscountBean{


    private String user_id;

    private String vipCard_id;

    private String coupons_id;
    //会员卡的折扣
    private String vip_card_num ;



    //折扣券折扣
    private String zhekou_num;

    //面值券面值
    private String mianzhi_num;
    //面值券要求满足金额

    private String mianzhi_max;

    public String getVip_card_num() {
        return vip_card_num;
    }

    public void setVip_card_num(String vip_card_num) {
        this.vip_card_num = vip_card_num;
    }

    public String getZhekou_num() {
        return zhekou_num;
    }

    public void setZhekou_num(String zhekou_num) {
        this.zhekou_num = zhekou_num;
    }

    public String getMianzhi_num() {
        return mianzhi_num;
    }

    public void setMianzhi_num(String mianzhi_num) {
        this.mianzhi_num = mianzhi_num;
    }


    @Override
    public String toString() {
        return "DiscountBean{" +
                "vip_card_num='" + vip_card_num + '\'' +
                ", zhekou_num='" + zhekou_num + '\'' +
                ", mianzhi_num='" + mianzhi_num + '\'' +
                ", mianzhi_max='" + mianzhi_max + '\'' +
                '}';
    }

    public String getMianzhi_max() {
        return mianzhi_max;
    }

    public void setMianzhi_max(String mianzhi_max) {
        this.mianzhi_max = mianzhi_max;
    }

    public String getCoupons_id() {
        return coupons_id;
    }

    public void setCoupons_id(String coupons_id) {
        this.coupons_id = coupons_id;
    }

    public String getVipCard_id() {
        return vipCard_id;
    }

    public void setVipCard_id(String vipCard_id) {
        this.vipCard_id = vipCard_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
