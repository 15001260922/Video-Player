package com.video_player;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import java.util.ArrayList;

import io.vov.vitamio.Vitamio;
import paper_view.BasePaper;
import paper_view.Person_Center_Paper;
import paper_view.Location_MediaPaper;
import paper_view.NetWork_MicusePaper;
import paper_view.NetWork_VideoPaper;

/**
 * Created by previous_off_yuan on 2016/12/10.
 * 作用:应用主页面
 */
public  class MainActivity extends FragmentActivity {
    public RadioGroup radioGroup;
    public ArrayList<BasePaper> paperList;
    public int position ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //给首页按钮设置单独的按钮图标
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton radioButtonOne = (RadioButton) findViewById(R.id.radioButton_one);
        Drawable drawable = getResources().getDrawable(R.drawable.kulou_on);
        drawable.setBounds(0,0,60,60);
        radioButtonOne.setCompoundDrawables(null,drawable,null,null);
        paperList = new ArrayList<>();
        paperList.add(new NetWork_VideoPaper(this,getSupportFragmentManager()));
        paperList.add(new NetWork_MicusePaper(this,getSupportFragmentManager()));
        paperList.add(new Location_MediaPaper(this,getSupportFragmentManager()));
        paperList.add(new Person_Center_Paper(this,getSupportFragmentManager()));

        //设置RadioGroup监听事件
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        radioGroup.check(R.id.radioButton_one);
    }
     class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
         public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
             switch (checkId){
                 default:
                     position = 0;
                     break;
                 case R.id.radioButton_two:
                     position = 1;
                     break;
                 case R.id.radioButton_three:
                     position = 2;
                     break;
                 case R.id.radioButton_four:
                     position = 3;
                     break;
             }
             setFragments();//给FrameLayout帧布局控件，添加Fragment所对应的各个页面
         }
     }
    //把页面添加到Fragment
    private void setFragments() {
        FragmentManager manager = getSupportFragmentManager();//得到Fragment管理者
        FragmentTransaction ft = manager.beginTransaction();//开启事务
        ft.replace(R.id.frameLayout_main,new Fragment(){
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                BasePaper basePaper = getBasepaper();
                if(basePaper!=null){
                    return basePaper.rootView;
                }
                return null;
            }
        });//替换页面
        ft.commit(); //提交
    }

    //根据点击按钮的Id，得到集合中对应的页面
    public BasePaper getBasepaper(){
        BasePaper basePaper = paperList.get(position);
        if(basePaper!=null && !basePaper.isInstance){
            basePaper.initDate();//初始化数据
            basePaper.isInstance = true;//修改各个页面，持有父类的属性
        }
        return basePaper;
    }

}
