package com.video_player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java_bean.MP4_Bean;
import user_defined_view.Mp4_Screen_Double_Press_Big;
import utils.GetNetWorkSpeed;
import utils.IsMp4UrlFromNetworkOrLoaction;
import utils.Make_Millisecond_To_Minute;

/**
 * Created by previous_off_yuan on 2016/12/16.
 * 作用：（1）播放视频的页面，也是系统自带的播放控件
 *       （2）改播放器，同时自定义了MediaControl（媒体控制面板的格局）
 */
public class Play_Mp4_Activity extends Activity implements View.OnClickListener{

    private static final int SHOW_NETWORK_SPEED = 3;
    public Mp4_Screen_Double_Press_Big mp4VideoView;
    public Uri uri;
    public TextView txtVideoName,txtSystemTime;
    public SeekBar seekBarSound,seekBarPlayJindu;
    public ImageView imgSoundSwitch,imgWanNengPlayer,imgBattery,img_more_control;
    public TextView txtStartTime,txtEndTime;
    public ImageView imgBack,imgPreviousMp4,imgPlayOrStopMp4,imgNextMp4,imgBigScreen;
    public ImageView imgPressShare;
    public TextView txtTvSide100,txtTvSide75,txtTvSide50,txtTvSideYuanShi;
    public static final int PROGRESS = 1;
    public static final int HIDE_OR_SHOW = 2;
    public Make_Millisecond_To_Minute makeMilliSecondToMinute;
    public Mp4SeekBarChangeListener seekChangeListener;
    public Mp4Battery_Broadcast mp4Battery_broadcast;
    public List<MP4_Bean> mp4List;
    public  int mp4Position;
    public  MP4_Bean mp4;
    public int nextMp4Position;
    public int  previsourMp4Position;
    public GestureDetector gestureDetector;
    public RelativeLayout mediaController;
    public boolean stateHideOrShow = true;
    public boolean longPressHideOrShow = true;
    public int screenWidth;
    public int screenHeight;
    public int yuanMp4Width;//视频原始宽度
    public int yuanMp4Height;//视频原始高度
    public boolean ifFull = true;
    public boolean toRightOrToLeftOrientation = true;
    public  PopupWindow pop;
    public  View popView;
    public boolean screenOrienation;
    public AudioManager audio;
    public int currentVoice;
    public int maxVoice;//音量分为（1-15）等级
    public boolean isMute;
    public TextView txtBufferDisplay;
    public LinearLayout bufferLinearLayout;
    public boolean isSystemOrUserDifined = false;
    public int previousProgress = 0;//上一个播放进度
    public int currentPosition;
    public  boolean isNetOrLoavtion = false;
    public GetNetWorkSpeed getNetWorkSpeed = new GetNetWorkSpeed();



    //每隔一秒，获取一下视频播放的时间进度
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==PROGRESS){
                currentPosition = mp4VideoView.getCurrentPosition();
                seekBarPlayJindu.setProgress(currentPosition);//设置当前进度
                txtStartTime.setText(makeMilliSecondToMinute.makeMillisecond_To_Minute(currentPosition)+"");//把每一次的时间显示出来
                txtSystemTime.setText(getSystemTime());
                //自定义监听卡

                if(!isSystemOrUserDifined && mp4VideoView.isPlaying() && isNetOrLoavtion){//如果能得到当前播放进度，并且正在播发，并且是网络视频
                    int buffer = currentPosition - previousProgress;
                    if(buffer < 500){//视频卡了
                        bufferLinearLayout.setVisibility(View.VISIBLE);
                    }else {//视频不卡了
                        bufferLinearLayout.setVisibility(View.GONE);
                    }
                }else {
                    bufferLinearLayout.setVisibility(View.GONE);
                }
                previousProgress = currentPosition;
                handler.removeMessages(PROGRESS);//清空消息
                handler.sendEmptyMessageDelayed(PROGRESS,1000);//每隔一秒，调用一次方法，从新更新进度
            }else if(msg.what == HIDE_OR_SHOW){
                mediaController.setVisibility(View.GONE);
                pop.dismiss();
            } else if(msg.what == SHOW_NETWORK_SPEED){//显示网速
                String netSpeed = getNetWorkSpeed.showNetSpeed(Play_Mp4_Activity.this);
                System.out.println("我是视频----------------------------网速"+netSpeed);
                txtBufferDisplay.setText(netSpeed);
                handler.removeMessages(SHOW_NETWORK_SPEED);//清空消息
                handler.sendEmptyMessageDelayed(SHOW_NETWORK_SPEED,2000);//每隔一秒，调用一次方法，从新更新进度
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_mp4_activity);
        popView = LayoutInflater.from(Play_Mp4_Activity.this).inflate(R.layout.mp4_mediacontroller_popupwindow,null);
        pop = new PopupWindow(popView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        initView();
        playMp4();
    }

    //初始化播放器控件控件
    public void initView(){
        handler.sendEmptyMessage(SHOW_NETWORK_SPEED);//显示网速
        //实例化音频管理器
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = audio.getStreamVolume(AudioManager.STREAM_MUSIC);//得到当前的音量
        maxVoice = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//得到最大的音量
        makeMilliSecondToMinute = new Make_Millisecond_To_Minute();
        mp4VideoView = (Mp4_Screen_Double_Press_Big) findViewById(R.id.playMp4_videoView);
        txtBufferDisplay = (TextView) findViewById(R.id.txt_mp4_buffer);
        bufferLinearLayout = (LinearLayout) findViewById(R.id.mp4_buffer);
        txtVideoName = (TextView) findViewById(R.id.txt_video_name);
        txtSystemTime = (TextView) findViewById(R.id.txt_system_time);
        imgBattery = (ImageView) findViewById(R.id.img_battery);
        seekBarSound = (SeekBar) findViewById(R.id.seekBar_sound);
        seekBarSound.setMax(maxVoice);
        seekBarSound.setProgress(currentVoice);
        seekBarPlayJindu = (SeekBar) findViewById(R.id.seekBar_jindu);
        imgSoundSwitch = (ImageView) findViewById(R.id.img_sound_switch);
        if(currentVoice==0){
            imgSoundSwitch.setImageResource(R.drawable.sound_off);
            isMute = true;
        }else {
            imgSoundSwitch.setImageResource(R.drawable.sound_on);
            isMute = false;
        }
        imgWanNengPlayer = (ImageView) findViewById(R.id.img_wan_nengPlayer);
        img_more_control = (ImageView) findViewById(R.id.img_more_control);
        txtStartTime = (TextView) findViewById(R.id.txt_start_time);
        txtEndTime = (TextView) findViewById(R.id.txt_end_time);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgPreviousMp4 = (ImageView) findViewById(R.id.img_previous_mp4);
        imgPlayOrStopMp4 = (ImageView) findViewById(R.id.img_stop_play);
        imgNextMp4 = (ImageView) findViewById(R.id.img_next_mp4);
        imgBigScreen = (ImageView) findViewById(R.id.img_big_screen);
        imgPressShare = (ImageView) popView.findViewById(R.id.img_press_share);//分享按钮
        imgPressShare.setOnClickListener(this);
        txtTvSide100 = (TextView) popView.findViewById(R.id.txt_tvSize100);
        txtTvSide100.setOnClickListener(this);
        txtTvSide75 = (TextView) popView.findViewById(R.id.txt_tvSize75);
        txtTvSide75.setOnClickListener(this);
        txtTvSide50 = (TextView) popView.findViewById(R.id.txt_tvSize50);
        txtTvSide50.setOnClickListener(this);
        txtTvSideYuanShi = (TextView) popView.findViewById(R.id.txt_tvSize_yuanshi);
        txtTvSideYuanShi.setOnClickListener(this);
        mediaController = (RelativeLayout) findViewById(R.id.mp4_mediaController);
        mediaController.setVisibility(View.GONE);
        imgSoundSwitch.setOnClickListener(this);
        imgWanNengPlayer.setOnClickListener(this);
        img_more_control.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgPreviousMp4.setOnClickListener(this);
        imgPlayOrStopMp4.setOnClickListener(this);
        imgNextMp4.setOnClickListener(this);
        imgBigScreen.setOnClickListener(this);
        seekChangeListener = new Mp4SeekBarChangeListener();//进度条改变监听对象
        seekBarPlayJindu.setOnSeekBarChangeListener(seekChangeListener);//设置进度条改变时的监听事件
        mp4Battery_broadcast = new Mp4Battery_Broadcast();
        //动态注册，MP4电池电量变化的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mp4Battery_broadcast,intentFilter );
        //实例化手势识别器
        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            public boolean onDoubleTap(MotionEvent e) {//屏幕双击时
                System.out.println("双击屏幕");
                handler.removeMessages(HIDE_OR_SHOW);
                handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,4000);
                doublePressScreenOrClickImgBigScreen();
                return super.onDoubleTap(e);
            }
            public boolean onSingleTapConfirmed(MotionEvent e) {//屏幕单击时
                System.out.println("单击屏幕");
                handler.removeMessages(HIDE_OR_SHOW);
                handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,4000);
                if(stateHideOrShow){
                    mediaController.setVisibility(View.VISIBLE);
                    stateHideOrShow = false;
                }else {
                    mediaController.setVisibility(View.GONE);
                    stateHideOrShow = true;
                }
                return super.onSingleTapConfirmed(e);
            }
            public void onLongPress(MotionEvent e) {//屏幕 长按时
                System.out.println("长按屏幕");
                handler.removeMessages(HIDE_OR_SHOW);
                handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,4000);
                if(longPressHideOrShow){

                    longPressHideOrShow = false;
                    playOrStopMp4();
                }else {
                    mediaController.setVisibility(View.GONE);
                    longPressHideOrShow = true;
                    playOrStopMp4();
                }
                super.onLongPress(e);
            }
        });
        seekBarSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress>maxVoice*0.6f){
                    Toast.makeText(Play_Mp4_Activity.this,"注意保护听力",Toast.LENGTH_SHORT).show();
                }

                if(isMute){
                    imgSoundSwitch.setImageResource(R.drawable.sound_on);
                }

                if(progress > 0){//不是静音
                    if(fromUser){
                        System.out.println("我是fromUser======调用的seekBar");
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);//0表示不调用系统的音量进度条，1表示调用系统的音量进度
                    }else{
                        System.out.println("我是系统=====调用的seekBar");

                    }
                }else if(progress==0){//静音状态
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);//0表示不调用系统的音量进度条，1表示调用系统的音量进度
                    imgSoundSwitch.setImageResource(R.drawable.sound_off);
                    isMute = true;
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_OR_SHOW);
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,4000);
            }
        });
    }

    //媒体控制面板，点击事件监听
    public void onClick(View view) {
        System.out.println("控件点击事件");
        if(view == imgSoundSwitch){//静音开关
            if(isMute){
                imgSoundSwitch.setImageResource(R.drawable.sound_on);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,maxVoice/2,0);
                seekBarSound.setProgress(maxVoice/2);
                isMute = false;
            }else {
                imgSoundSwitch.setImageResource(R.drawable.sound_off);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
                seekBarSound.setProgress(0);
                isMute = true;
            }
        }else if(view == imgWanNengPlayer){//切换到万能播放器
            Toast.makeText(this,"我是-万能播放器",Toast.LENGTH_SHORT).show();
        }else if(view == img_more_control){//更多操作功能，分享和调节屏幕尺寸
            //点击更多按钮，弹出选择框
            pop.setOutsideTouchable(true);
            pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pop.showAsDropDown(img_more_control,0,0);
//            pop.setAnimationStyle(R.style.popup_window_animation);
        }else if(view == imgBack){//返回
            finish();
        }else if(view == imgPreviousMp4){//上一曲
            playPreviousMp4();
        }else if(view == imgPlayOrStopMp4){//暂停或播放按钮
            playOrStopMp4();
        }else if(view == imgNextMp4){//下一曲
            playNextMp4();
        }else if(view == imgBigScreen){//切换到全屏或者小屏
            doublePressScreenOrClickImgBigScreen();
        }else if(view == imgPressShare){
            System.out.println("分享按钮");
            pop.dismiss();
        }else if(view == txtTvSide100){
            System.out.println("屏尺寸100");
            if(isScreenVictroalOrHorienation()){//横屏
                mp4VideoView.setVideoViewScreenSize(getScreenWidth(),getScreenHeight());
            }else {//竖屏
                mp4VideoView.setVideoViewScreenSize(screenWidth,screenHeight);
            }
            pop.dismiss();
        }else if(view == txtTvSide75){
            System.out.println("屏尺寸75");
            if(isScreenVictroalOrHorienation()){//横屏
                mp4VideoView.setVideoViewScreenSize((int) (0.75f*getScreenWidth()), (int) (0.75f*getScreenHeight()));
            }else {//竖屏
                mp4VideoView.setVideoViewScreenSize((int) (0.75f*screenWidth), (int) (0.75f*screenHeight));
            }
            pop.dismiss();
        }else if(view == txtTvSide50){
            System.out.println("屏尺寸50");
            if(isScreenVictroalOrHorienation()){//横屏
                mp4VideoView.setVideoViewScreenSize((int) (0.5f*getScreenWidth()), (int) (0.5f*getScreenHeight()));
            }else {//竖屏
                mp4VideoView.setVideoViewScreenSize((int) (0.5f*screenWidth),(int) (0.5f*screenHeight));
            }
            pop.dismiss();
        }else if(view == txtTvSideYuanShi){
            System.out.println("屏尺寸--原始大小");
            if(isScreenVictroalOrHorienation()){//横屏
                mp4VideoView.setVideoViewScreenSize(yuanMp4Width,yuanMp4Height);
            }else {//竖屏
                mp4VideoView.setVideoViewScreenSize(yuanMp4Width,yuanMp4Height);
            }
            pop.dismiss();
        }
        handler.removeMessages(HIDE_OR_SHOW);
        handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,3000);
    }

    //接收电量变化的广播类
    class Mp4Battery_Broadcast extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            int batteryLevel = intent.getIntExtra("level",0);//设置电量等级（0-100），默认为0
            //设置电池电量
            setBattery(batteryLevel);//调用setBattery（）方法
        }
        private void setBattery(int batteryLevel) {
            if(batteryLevel <= 0){
                imgBattery.setImageResource(R.drawable.dianci_1);
            }else if(batteryLevel <= 20){
                imgBattery.setImageResource(R.drawable.dianci_1);
            }else if(batteryLevel <= 40){
                imgBattery.setImageResource(R.drawable.dianci_2);
            }else if(batteryLevel <= 60){
                imgBattery.setImageResource(R.drawable.dianci_3);
            }else if(batteryLevel <= 80){
                imgBattery.setImageResource(R.drawable.dianci_4);
            }else if(batteryLevel <= 100){
                imgBattery.setImageResource(R.drawable.dianci_5);
            }else {
                imgBattery.setImageResource(R.drawable.dianci_5);
            }
        }
    }




    //播放Mp4视频，在方法里面，自定义了，媒体播放器面板
    public void playMp4(){
        //当给VideoView设置好，播放地址时，回调次方法。底层解码，准备好的监听
        mp4VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                screenWidth = getScreenWidth();
                screenHeight = getScreenHeight();
                yuanMp4Width = mediaPlayer.getVideoWidth();
                yuanMp4Height = mediaPlayer.getVideoHeight();
                //设置默认，视频播放时的大小，把视频原始的宽和高做合适的修改后，重新绘制视频画面的大小
                if(yuanMp4Width*screenHeight < yuanMp4Height*screenWidth){
                    screenWidth = screenHeight*yuanMp4Width/yuanMp4Height;
                }else if(yuanMp4Width*screenHeight > yuanMp4Height*screenWidth){
                    screenHeight = screenWidth*yuanMp4Height/yuanMp4Width;
                }
                //默认设置视频的大小，重新绘制了视频画面的大小，（即自定义了VideoView视频控件）
                mp4VideoView.setVideoViewScreenSize(screenWidth,screenHeight);
                mediaPlayer.start();//准备好后，开始播放视频
                int totalTime = mp4VideoView.getDuration();//得到视频总时长
                txtEndTime.setText(makeMilliSecondToMinute.makeMillisecond_To_Minute(totalTime)+"");//把总时长显示出来
                seekBarPlayJindu.setMax(totalTime);//设置进度条最大的进度
                handler.sendEmptyMessage(PROGRESS);
                //当进度条拖动完成的监听
//                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    public void onSeekComplete(MediaPlayer mediaPlayer) {
//                        Toast.makeText(Play_Mp4_Activity.this,"视频拖动完成",Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
        //视频播放出错的监听
        mp4VideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                //(1)视频格式不正确---->用万能播放器播放
                //（2）播放网络视频网络中断；（如果网络确实断开，就提示用户网络断开。）（网络断断续续，重新播放走生命周期）
                //（3）播放视频本地文件中间有空白
               // 调用万能播放器，显示意图启动模式,播放的控件是系统自带的，
                pop.dismiss();
                Intent intent = new Intent(Play_Mp4_Activity.this,Vitamio_Play_Mp4_Activity.class);
                if(mp4List!=null && mp4List.size()>0){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mp4list", (Serializable) mp4List);
                    intent.putExtras(bundle);
                    intent.putExtra("mp4Position",mp4Position);
                }else if(uri!=null){
                    intent.setData(uri);
                }
                intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
                Play_Mp4_Activity.this.startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                return true;//返回值为false时，会弹出播放出错对话框，true时可以自定义
            }
        });
        //视频播放完成的监听
        mp4VideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                //如果有下一个视频，跳到下一个，如果没有，就退出播放视频
                if(mp4Position<mp4List.size()-1){
                    Toast.makeText(Play_Mp4_Activity.this,"即将-播发下一首",Toast.LENGTH_SHORT).show();
                    playNextMp4();//如果当期歌曲的下面还有，就自动播发下一个
                }else{//如果是最后一首，播忘就停止
                    Toast.makeText(Play_Mp4_Activity.this,"视频播放完成",Toast.LENGTH_SHORT).show();
                    handler.removeCallbacksAndMessages(null);
                    seekBarPlayJindu.setProgress(0);
                    mp4VideoView.seekTo(0);
                    txtStartTime.setText("0:00");
                    imgPlayOrStopMp4.setImageResource(R.drawable.play);
                }
            }
        });
        //设置视频监听卡(VideoView自带的)，当网络不好时，提示用户网络状态，并让进度转圈
        if(isSystemOrUserDifined){//如果不能获取到当前视频播放进度或者（是直播视频的当前进度=0），就用这个系统自带的视频监听卡
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){//android版本大于4.2.2
                mp4VideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                        switch (what){
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频缓冲开始，即视频开始卡了
                                Toast.makeText(Play_Mp4_Activity.this,"网络不好",Toast.LENGTH_SHORT).show();
                                //如果视频卡了，让网络提示显示出来，并且显示加载视频的进度
                                bufferLinearLayout.setVisibility(View.VISIBLE);
//                        txtBufferDisplay.setText(extra+"");
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频不卡了，即卡结束了
                                bufferLinearLayout.setVisibility(View.GONE);
                                break;
                        }
                        return true;//true表示处理了事件
                    }
                });
            }
        }


        //给VideoView设置播放地址
        uri = getIntent().getData();
        mp4List = (List<MP4_Bean>) getIntent().getSerializableExtra("mp4list");
        mp4Position = getIntent().getIntExtra("mp4Position",0);
        nextMp4Position =mp4Position;
        previsourMp4Position =mp4Position ;
        if(mp4List!=null && mp4List.size()>0){
             mp4 = mp4List.get(mp4Position);
            txtVideoName.setText(mp4.getMp4Name());
            mp4VideoView.setVideoURI(Uri.parse(mp4.getMp4Address().toString()));
        }else if(uri != null){
            if(IsMp4UrlFromNetworkOrLoaction.isNewWorkOrLocation(uri.toString())){
                isNetOrLoavtion = true;//是网络视频
            }else {
                isNetOrLoavtion = false;//是本地视频
            }
            System.out.println("我是网络视频的URL==========================URL"+uri.toString());
            txtVideoName.setText(uri.toString());
            mp4VideoView.setVideoURI(uri);
            imgPreviousMp4.setClickable(false);
            imgNextMp4.setClickable(false);
        }else{
            Toast.makeText(this,"亲，没有视频数据",Toast.LENGTH_SHORT).show();
        }
        //回调接口，系统自带的,媒体控制面板。需要传一个View视图，作为参数
//        mp4VideoView.setMediaController(new MediaController(this));//new MediaControl(this),是一个view视图，就是媒体控制面板
    }

    //播放上一曲
    private void playPreviousMp4() {
        previsourMp4Position = (mp4Position--)-1;
        if(mp4List.size() == 1){
            Toast.makeText(this,"sorry,this is  first!",Toast.LENGTH_SHORT).show();
            imgPreviousMp4.setClickable(false);
        }else{
            if(previsourMp4Position >=0){
                String mp4Path = mp4List.get(previsourMp4Position).getMp4Address();
                mp4VideoView.setVideoPath(mp4Path);
                txtVideoName.setText(mp4List.get(previsourMp4Position).getMp4Name());
                imgPlayOrStopMp4.setImageResource(R.drawable.stop);
                handler.sendEmptyMessage(PROGRESS);
                imgNextMp4.setClickable(true);
            }else {
                Toast.makeText(this,"sorry,this is  first!",Toast.LENGTH_SHORT).show();
                imgPreviousMp4.setClickable(false);
                mp4Position++;
            }
        }
    }
    //暂停或播放视频
    private void playOrStopMp4() {
        if(mp4VideoView.isPlaying()){//如果视频正在播放
            //设置按钮状态为，暂停
            mp4VideoView.pause();
            imgPlayOrStopMp4.setImageResource(R.drawable.play);
        }else{//如果视频处于停止状态
            imgPlayOrStopMp4.setImageResource(R.drawable.stop);
            mp4VideoView.start();
            handler.sendEmptyMessage(PROGRESS);
        }
    }

    //播放下一曲
    private void playNextMp4() {
         nextMp4Position = (1+mp4Position++);
        //如果指针的位置，处于最后一个，或者列表中只有一个视频。吐司：已经是最后一个视频
       if(mp4Position == (mp4List.size()-1) && (mp4List.size()==1)){
           Toast.makeText(this,"sorry, the last one!",Toast.LENGTH_SHORT).show();
           imgNextMp4.setClickable(false);
       }else{
           if(nextMp4Position<mp4List.size()){
               String mp4Path = mp4List.get(nextMp4Position).getMp4Address();
               mp4VideoView.setVideoPath(mp4Path);
               txtVideoName.setText(mp4List.get(nextMp4Position).getMp4Name());
               imgPlayOrStopMp4.setImageResource(R.drawable.stop);
               handler.sendEmptyMessage(PROGRESS);
               imgPreviousMp4.setClickable(true);
           }else {
               Toast.makeText(this,"sorry, the last one!",Toast.LENGTH_SHORT).show();
               imgNextMp4.setClickable(false);
               mp4Position--;
           }
       }
    }

    //进度条改变时，监听
    class Mp4SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        //当进度滑动时，会回调这个方法
        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {//当进度改变的时候
            if(fromUser){//（1）fromUser为true时，这事有用户引起的滑动回调。（2）每秒自动跟新进度时，为false
                mp4VideoView.seekTo(i);
            }
        }
        public void onStartTrackingTouch(SeekBar seekBar) {//当开始触碰的时候
            handler.removeMessages(HIDE_OR_SHOW);
        }
        public void onStopTrackingTouch(SeekBar seekBar) {//当停止触碰的时候
            handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,3000);
        }

    }

    //双击屏幕或点击全屏按钮，先判断横竖屏状态，根据屏幕方向状态让屏幕旋转，
    public void doublePressScreenOrClickImgBigScreen(){
        if(isScreenVictroalOrHorienation()){
            System.out.println("横屏");
            if(ifFull){
                imgBigScreen.setImageResource(R.drawable.qie_xiaoping); //让图标变小
                mp4VideoView.setVideoViewScreenSize(getScreenWidth(),getScreenHeight());
                ifFull=false;
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//让屏幕，竖过来
                imgBigScreen.setImageResource(R.drawable.qie_quanping);//让图标变大
                mp4VideoView.setVideoViewScreenSize(screenWidth,screenHeight);
                ifFull = true;
            }
        }else {
            System.out.println("竖屏");
            //横屏时让方向，随toRightOrToLeftOrientation的值改变而改变
            if(toRightOrToLeftOrientation){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//让屏幕，横过来
                toRightOrToLeftOrientation = false;
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//让屏幕，横过来
                toRightOrToLeftOrientation = true;
            }
            imgBigScreen.setImageResource(R.drawable.qie_xiaoping); //让图标变小
            mp4VideoView.setVideoViewScreenSize(getScreenWidth(),getScreenHeight());
        }
    }

//    public boolean dispatchKeyEvent(KeyEvent event) {
//        int action = event.getAction();
//
//        if (action ==KeyEvent.KEYCODE_VOLUME_DOWN) {
//            Toast.makeText(this,"ACTION_DOWN-------",Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        if (action== KeyEvent.KEYCODE_VOLUME_UP) {
//            Toast.makeText(this,"+++++ACTION_UP++++++++++",Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    //判断Back按键的点击事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            //如果是横屏幕，那么点击Back键时，让屏幕竖起来
            if(isScreenVictroalOrHorienation()){
                System.out.println("横屏");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//让屏幕，竖过来
                imgBigScreen.setImageResource(R.drawable.qie_quanping);//让图标变大
                mp4VideoView.setVideoViewScreenSize(screenWidth,screenHeight);
                ifFull = true;
            }else{
                //如果是竖屏幕时，点击back返回键，让当前页面结束
                finish();
            }
        }else if(keyCode == event.KEYCODE_VOLUME_DOWN){
//            Toast.makeText(this,"我是音量减",Toast.LENGTH_SHORT).show();
            currentVoice = audio.getStreamVolume(AudioManager.STREAM_MUSIC)-1;//得到当前的音量
            System.out.println("我是音量减"+currentVoice);
            seekBarSound.setProgress(currentVoice);
//            audio.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);

            mediaController.setVisibility(View.VISIBLE);
            handler.removeMessages(HIDE_OR_SHOW);
            handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,4000);

        }else if(keyCode == event.KEYCODE_VOLUME_UP){
//            Toast.makeText(this,"我是音量加++++++",Toast.LENGTH_SHORT).show();
            currentVoice = 1+audio.getStreamVolume(AudioManager.STREAM_MUSIC);//得到当前的音量
            System.out.println("我是音量+++++加"+currentVoice);
            seekBarSound.setProgress(currentVoice);
//            audio.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);

            mediaController.setVisibility(View.VISIBLE);
            handler.removeMessages(HIDE_OR_SHOW);
            handler.sendEmptyMessageDelayed(HIDE_OR_SHOW,4000);
        }
        return super.onKeyDown(keyCode, event);
    }

    public float startY;
    public float startX;
    public float endX;
    public float endY;
    public int mVol;
    public int mp4JIndu;
    public float touchRang;
    public int mp4CurrentPosition;
    public boolean isMp4Jindu;//设置这个变量，判断用户手指，先按到左边还是右边，如果是左边，就让亮度调节失效，如果是有边就让音量调节失效
    public boolean isMp4BrightOrVoice;
    public float sinDistance;

//
//  Activity把事件传递给，手势识别器
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                startX = event.getX();
                mp4CurrentPosition = mp4VideoView.getCurrentPosition();
                mVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(HIDE_OR_SHOW);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                mediaController.setVisibility(View.VISIBLE);
                //2.移动的记录相关值
                endY = event.getY();
                endX = event.getX();
                float distanceY = startY - endY;
                float distanceX = endX - startX;
                sinDistance = (float) Math.sqrt(distanceX*distanceX+distanceY*distanceY);
                System.out.println("左右滑动----X轴--cos"+Math.cos(distanceX/sinDistance));
                System.out.println("上下滑动-YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY---Y轴--cos"+Math.cos(distanceY/sinDistance));
                if(Math.abs(distanceX)>50 && Math.abs(distanceY)<40 && Math.cos(distanceX/sinDistance)<0.56 &&Math.cos(distanceY/sinDistance)>Math.cos(distanceX/sinDistance)){//如果在X轴移动的距离大于50像素，并且Y轴的位移量50px，并且x轴的角度小于15度，就是（左右滑动屏幕，实现快进、快退效果）
                    //视频进度调节
                    float mp4Detils = distanceX/screenWidth*(mp4VideoView.getDuration());
                    mp4JIndu = (int) Math.min(Math.max(0,mp4CurrentPosition+mp4Detils),mp4VideoView.getDuration());
                    mp4VideoView.seekTo(mp4JIndu);
                    seekBarPlayJindu.setProgress(mp4JIndu);
                }else if (endX < screenWidth / 2 && Math.abs(distanceY)>50 && Math.abs(distanceX)<50 && Math.cos(distanceY/sinDistance)<0.56 &&Math.cos(distanceY/sinDistance)<Math.cos(distanceX/sinDistance)) {
                        //左边屏幕-调节亮度
                        final double FLING_MIN_DISTANCE = 0.5;
                        final double FLING_MIN_VELOCITY = 0.5;
                        if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {//向上滑动
                            setBrightness(10);
                        }
                        if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {//向下滑动
                            setBrightness(-10);
                        }
                        isMp4Jindu = false;
                    } else if(endX > screenWidth / 2 && Math.abs(distanceY)>50 && Math.abs(distanceX)<50 && Math.cos(distanceY/sinDistance)<0.56 &&Math.cos(distanceY/sinDistance)<Math.cos(distanceX/sinDistance)){
                        //右边屏幕-调节声音
                        //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                        isMp4Jindu = false;
                        float delta = (distanceY / touchRang) * maxVoice;
                        //最终声音 = 原来的 + 改变声音；
                        int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                        if (delta != 0) {
                            updateVoice(voice);
                        }
                    }

                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_OR_SHOW, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }


    private void updateVoice(int progress) {
//        if (isMute) {
//            audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            seekBarSound.setProgress(0);
//        } else {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekBarSound.setProgress(progress);
            currentVoice = progress;
//        }
    }
    /*
 *
 * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
 */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        System.out.println("调节后亮度"+(float) 0.1);
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
//            System.out.println("调节前-----------------------亮度"+lp.screenBrightness);
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);
    }

    //手机屏幕方向改变的监听
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            System.out.println("横屏"+getScreenHeight());
            if(pop.isShowing()){
                pop.dismiss();
            }
        }
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            System.out.println("纵屏"+getScreenHeight());
            if(pop.isShowing()){
                pop.dismiss();
            }
        }

    }

    //判断屏幕时横屏（true），还是竖屏（false）
    public boolean isScreenVictroalOrHorienation(){
        Configuration configuration = this.getResources().getConfiguration();//获取设置的配置信息
        int orientation = configuration.orientation;//获取屏幕方向
        //如果是横屏幕，那么点击Back键时，让屏幕竖起来
        if(orientation ==  Configuration.ORIENTATION_LANDSCAPE){
            System.out.println("横屏"+getScreenHeight());
            screenOrienation = true;
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
            //如果是竖屏幕时
            System.out.println("纵屏"+getScreenHeight());
            screenOrienation = false;
        }
        return screenOrienation;
    }


    //销毁 activity,解除电量广播
    protected void onDestroy() {
        handler.removeMessages(SHOW_NETWORK_SPEED);
        //取消注册，在销毁父类的Activity之前取消注册。但是初始化时，要先初始化父类的，才初始化子类
        if(mp4Battery_broadcast != null){
            unregisterReceiver(mp4Battery_broadcast);
            System.out.println("我是广播，我被注册了");
        }
        super.onDestroy();
    }

    //获取屏幕的宽度
    public int getScreenWidth(){
        WindowManager windowManager = getWindowManager();//实例化窗体管理器
        DisplayMetrics displayMetrics = new DisplayMetrics();//实例化到测量器
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;//返回测量结果
    }
    //获取屏幕的高度
    public int getScreenHeight(){
        WindowManager windowManager = getWindowManager();//实例化窗体管理器
        DisplayMetrics displayMetrics = new DisplayMetrics();//实例化到测量器
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;//返回测量结果
    }

    //获得系统时间
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String systemTime = format.format(new Date());//获得系统时间，并转化成字符串
        return systemTime;
    }
}