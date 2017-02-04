package paper_view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by previous_off_yuan on 2016/12/11.
 * 作用：网络视频类页面，继承父类
 */
public class NetWork_VideoPaper extends BasePaper {
    public TextView textView;
    public NetWork_VideoPaper(Context context, FragmentManager fragmentManager) {
        super(context,fragmentManager);
    }

    public View initView() {
         textView = new TextView(mContext);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        System.out.println("网络视频initView()页面，被初始化了");
        return textView;
    }

    public void initDate() {
        super.initDate();
        textView.setText("我是，网络视频页面");
        System.out.println("网络视频======initDate()数据，被初始化了");
    }
}
