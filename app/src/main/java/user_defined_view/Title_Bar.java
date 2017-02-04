package user_defined_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.video_player.R;

/**
 * Created by previous_off_yuan on 2016/12/13.
 * 作业：自定义控件，主页面（标题栏）TitleBar，并对TitleBar设置对外的点击事件
 */
public class Title_Bar extends LinearLayout implements View.OnClickListener{

    public View titleBar_iconImg;
    public TextView titleBar_txtSearch;
    public View titleBar_historyImg;
    public Context mContext;
    public Drawable drawable,drawable1;

    //在代码中，实例化该类时，调用这个方法
    public Title_Bar(Context context) {
        this(context,null);
    }

    //在布局文件中，使用该类的时候。android系统，通过反射这个构造方法，实例化改类
    public Title_Bar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    //当需要设置样式时，需要系统调用这个构造方法
    public Title_Bar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        drawable = getResources().getDrawable(R.drawable.fangdajing_off);
         drawable1 = getResources().getDrawable(R.drawable.fangdajing_off);
        drawable.setBounds(0,0,50,50);
        drawable1.setBounds(0,0,50,50);
    }

    //当 TitleBar布局文件的内容，加载完成时，调用该系统方法
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleBar_iconImg = getChildAt(0);
        titleBar_txtSearch = (TextView) getChildAt(1);
        titleBar_historyImg = getChildAt(2);
        final Drawable drawable = getResources().getDrawable(R.drawable.fangdajing_on);
        drawable.setBounds(0,0,50,50);
        titleBar_txtSearch.setCompoundDrawables(drawable,null,null,null);
        //设置titleBar中控件的点击事件
        titleBar_iconImg.setOnClickListener(this);
        titleBar_txtSearch.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
              switch (motionEvent.getAction()){
                  case MotionEvent.ACTION_DOWN:
                      titleBar_txtSearch.setCompoundDrawables(drawable1,null,null,null);
                      break;
                  case MotionEvent.ACTION_UP:
                      titleBar_txtSearch.setCompoundDrawables(drawable,null,null,null);
                      break;
              }
                return false;
            }
        });
        titleBar_historyImg.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_icon:
//                Toast.makeText(mContext,"你好，我是头像标志",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_history:
//                Toast.makeText(mContext,"你好，我是==历史记录",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
