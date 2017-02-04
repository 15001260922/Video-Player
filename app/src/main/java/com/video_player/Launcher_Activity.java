package com.video_player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by previous_off_yuan on 2016/12/10.
 * 作用:启动页面
 */
public class Launcher_Activity extends Activity {

    private ImageView imgLauncher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        imgLauncher = (ImageView) findViewById(R.id.img_launch);
        launchAnimation();
    }
    //设置启动图片动画集合
    public void launchAnimation(){
        AnimationSet launchAnimation = new AnimationSet(true);


        //创建透明度动画
        AlphaAnimation aa = new AlphaAnimation(1,0.2f);
        aa.setDuration(800);
        aa.setRepeatCount(1);
//        aa.setRepeatMode(Animation.REVERSE);
        launchAnimation.addAnimation(aa);

        //创建旋转动画
        RotateAnimation ra = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(1200);
        ra.setRepeatCount(1);
        ra.setRepeatMode(Animation.REVERSE);
        launchAnimation.addAnimation(ra);
        //创建缩放动画
        ScaleAnimation sa; sa = new ScaleAnimation(1,0,1,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(1200);
        sa.setRepeatMode(Animation.REVERSE);
        sa.setRepeatCount(1);
        //将缩放动画，添加到动画集合中
        launchAnimation.addAnimation(sa);



        imgLauncher.setAnimation(launchAnimation);
        launchAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            //当动画结束时
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(Launcher_Activity.this,MainActivity.class);
                startActivity(intent);
                finish();
//                Toast.makeText(Launcher_Activity.this,"你好，我是启动页面动画",Toast.LENGTH_SHORT).show();

            }
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //xingXingAnimation.start();
    }
}
