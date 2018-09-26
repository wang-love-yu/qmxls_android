package com.qimai.xinlingshou.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qimai.xinlingshou.BaseFragment;
import com.qimai.xinlingshou.R;
import com.qimai.xinlingshou.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wanglei on 18-5-19.
 */

public class SlideFragment extends BaseFragment {

    @BindView(R.id.tv_slide_crashier)
    TextView tvSlideCrashier;
    @BindView(R.id.tv_slide_setting)
    TextView tvSlideSetting;
    Unbinder unbinder;
    @BindView(R.id.ll_settings)
    LinearLayout llSettings;
    Unbinder unbinder1;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View rootView) {
      /*  textViewItem1 = (TextView) rootView.findViewById(R.id.tv_item1);
        textViewItem2 = (TextView) rootView.findViewById(R.id.tv_item2);

        textViewItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        textViewItem2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {

            }
        });*/

    }

    @Override
    protected int getLayout() {
        return R.layout.slide_pop_view;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick({R.id.tv_slide_crashier, R.id.tv_slide_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_slide_crashier:


                llSettings.setBackgroundColor(activity.getResources().getColor(R.color.TvGray));
                llSettings.setBackground(null);

                tvSlideSetting.setTextColor(activity.getResources().getColor(R.color.TvDefault));


                Drawable drawable1 = getResources().getDrawable(R.drawable.setting_unselect);
                drawable1.setBounds(0,0,drawable1.getIntrinsicWidth(),drawable1.getIntrinsicHeight());
                tvSlideSetting.setCompoundDrawables(drawable1,null,null,null);



                ((MainActivity) activity).showCrashierLayout();
                break;
            case R.id.tv_slide_setting:

                llSettings.setBackgroundColor(activity.getResources().getColor(R.color.TvBlue));

                tvSlideSetting.setTextColor(activity.getResources().getColor(R.color.TvWhite));


                Drawable drawable = getResources().getDrawable(R.drawable.setting_select);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                tvSlideSetting.setCompoundDrawables(drawable,null,null,null);
                ((MainActivity) activity).showSettingLayout();
                break;
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
