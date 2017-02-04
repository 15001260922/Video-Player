package user_defined_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.VideoView;

/**
 * Created by previous_off_yuan on 2016/12/22.
 * 作用：自定义了VideoView的屏幕尺寸，播发Mp4时点击放大屏幕，让屏幕全屏
 */
public class Vitamio_Mp4_Screen_Double_Press_Big extends VideoView {
    public Vitamio_Mp4_Screen_Double_Press_Big(Context context) {
        this(context,null);
    }

    public Vitamio_Mp4_Screen_Double_Press_Big(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Vitamio_Mp4_Screen_Double_Press_Big(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //测量屏幕的方法
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);//设置测量的尺寸，Dimension（尺寸）
    }

    //自定义，设置屏幕尺寸的方法,inputWidth(自定义屏幕尺寸的宽度),inputHeight(自定义屏幕尺寸的高度)
    public void setVideoViewScreenSize(int inputWidth,int inputHeight){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = inputWidth;
        params.height = inputHeight;
        setLayoutParams(params);//次方法默认是给本类Mp4_Screen_Double_Press_Big设置布局属性，因为Mp4_Screen_Double_Press_Big继承了VideoView
    }
}
