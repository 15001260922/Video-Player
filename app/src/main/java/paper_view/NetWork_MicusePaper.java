package paper_view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by previous_off_yuan on 2016/12/11.
 * 作用：网络音乐页面，继承父类
 */
public class NetWork_MicusePaper extends BasePaper {
    public  TextView textView;

    public NetWork_MicusePaper(Context context, FragmentManager fragmentManager) {
        super(context,fragmentManager);
    }

    public View initView() {
         textView = new TextView(mContext);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        System.out.println("网络音乐initView()页面，被初始化了");
        return textView;
    }

    public void initDate() {
        super.initDate();
        textView.setText("网络音乐");
        System.out.println("网络音乐======initDate()数据，被初始化了");
    }
}
