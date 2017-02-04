package user_defined_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.video_player.R;

import java.util.List;

/**
 * Created by previous_off_yuan on 2016/12/13.
 * 作用：本地媒体TitleBar标题栏，自定义指针，控件
 */
public class LocationMedia_TitleBarIndicator extends LinearLayout {

    public Paint paint;
    public Path path;
    public int titleBar_ItemCount;//定义titleBar子类的个数
    public int defaultTitleBar_ItemCount = 4;
    public int triangleWidth;
    public int triangleHeight;
    public int initTranslate;
    public ViewPager viewPager;
    public int screenWidth;
    public List<String> titleList;
    public int fingerTranslate;//手指滑动偏移量


    public LocationMedia_TitleBarIndicator(Context context) {
        this(context,null);
    }

    public LocationMedia_TitleBarIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获得自定义属性，标题栏子类的数量
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.titlebar_indicator);
        titleBar_ItemCount = typedArray.getInt(R.styleable.titlebar_indicator_tab_item_count,defaultTitleBar_ItemCount);//defaultTitleBar_ItemCount是默认的数量，得到布局中tab_item_count的值
        if(titleBar_ItemCount<0){
            titleBar_ItemCount = defaultTitleBar_ItemCount;
        }
        typedArray.recycle();//清空回收
        //初始化画笔
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setPathEffect(new CornerPathEffect(3));
    }

    //得到屏幕的宽度
    public int getScreenWidth(){
        //获得窗口管理器
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    //初始化三角形的宽度，当屏幕尺寸改变时
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        triangleWidth = (int) (w/3.5*(1.0f/6));//三角形的宽度，是单个标题的六分之一
        triangleHeight = (int) (triangleWidth/2/Math.sqrt(2));
        //三角形，正确位置的偏移量，x轴上，距离圆点的偏移量
         initTranslate = getWidth() / titleBar_ItemCount / 2 - triangleWidth / 2;
        //初始化三角形的路径
        path = new Path();
        path.moveTo(0,0);
        path.lineTo(triangleWidth,0);
        path.lineTo(triangleWidth/2,-triangleHeight);
        path.close();

    }

    //派遣绘画三角形，并将它从初始位置，平移动到正确的位置
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();//保存上次的画面
        canvas.translate(initTranslate+fingerTranslate,getHeight()+1);//平移的坐标位置
        canvas.drawPath(path,paint);//正式绘画三角形
        canvas.restore();//清除绘画痕迹
    }

    //设置标题，字段的内容,根据标题字段的数量来创建TextView的个数
    public void setTitleItemData(List<String> titleList){
        this.titleList = titleList;
        if(titleList!=null && titleList.size()>0){
            for(String title:titleList){
                //把title添加到标题里
                TextView textView = new TextView(getContext());
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ll.width = getScreenWidth()/titleBar_ItemCount;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                textView.setText(title);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(ll);
                addView(textView);
            }
            setOnTitleClick();
        }
    }

    //    设置ViewPagerd的页面改变事件
    public void setViewPager(ViewPager viewPager,int position){
        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //当页面滑动时，设置三角随页面偏移量而滑动的距离位置
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //获得手指偏移量
                fingerTranslate = (int) (getWidth()/titleBar_ItemCount*(position + positionOffset));
                int tabWidth = getScreenWidth()/titleBar_ItemCount;
                if(positionOffset>0 && getChildCount()>titleBar_ItemCount && position>=(titleBar_ItemCount-2)){
                    if(titleBar_ItemCount != 1){
                        LocationMedia_TitleBarIndicator.this.scrollTo((position - (fingerTranslate - 2)) * tabWidth + (int) (tabWidth * positionOffset),0);
                    }else {
                        LocationMedia_TitleBarIndicator.this.scrollTo((int) (position*tabWidth+tabWidth*positionOffset),0);
                    }
                }
                invalidate();
            }

            //当页面被选定时，修改字段的颜色和高亮
            public void onPageSelected(int position) {
                //循环重置文本的颜色
                for(int i=0;i<getChildCount();i++){
                    View view = getChildAt(i);
                    if(view instanceof TextView){
                        ((TextView) view).setTextColor(0x77FFFFFF);
                    }
                }
                //设置当前字体选择的高亮
                View viewPositon = getChildAt(position);
                if(viewPositon instanceof TextView){
                    ((TextView) viewPositon).setTextColor(0xFFFFFFFF);
                }
            }
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //设置标题字段Textview的点击事件
    public void setOnTitleClick(){
        for(int i=0;i<getChildCount();i++){
            final int textViewPosition = i;
            View view =getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    viewPager.setCurrentItem(textViewPosition);
                }
            });

        }

    }
}
