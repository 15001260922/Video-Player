package utils;

import java.text.Format;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by previous_off_yuan on 2016/12/14.
 * 作用:把毫秒转变成，时分秒形式，工具类
 */
public class Make_Millisecond_To_Minute {

    private StringBuilder stringBuilder;
    private Formatter formatter;

    //构造方法
    public Make_Millisecond_To_Minute(){
            stringBuilder = new StringBuilder();
            formatter = new Formatter(stringBuilder, Locale.getDefault());
    }
    //把毫秒转成，时 分 秒形式，并以字符串形式返回
    public String makeMillisecond_To_Minute(int milliSecond){
        int allSeconds = milliSecond/1000;        //一秒=1000毫秒，得到所有的秒数
        int seconds = allSeconds % 60;           //总秒数，除以60，得到秒单位的数，例如 (2 : 25 : 32)
        int mintues = (allSeconds/60) % 60;     //总分钟数%60，即得到，分钟单位
        int hours = allSeconds / 3600;         //得到小时单位
        stringBuilder.setLength(0);         //设置可变长度字符串为0

        if (hours > 0){                     //如果小时单位有进位，就以 时 分 秒，形式格式转换并返回
            Formatter format = formatter.format("%d:%02d:%02d", hours, mintues, seconds);
            String str = format.toString();
            return str;
        }else {                          //如果小时没有进位，就以 分 秒，形式转换并返回
            Formatter format = formatter.format("%02d:%02d", mintues, seconds);
            String str = format.toString();
            return str;
        }

    }

}
