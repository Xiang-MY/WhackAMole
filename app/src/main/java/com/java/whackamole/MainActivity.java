package com.java.whackamole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/*
用二维数组保存每个洞穴的位置
用Thread线程和Handler消息处理机制控制地鼠随机出现。
在子线程中产生随机整数作为二维数组的行坐标，将行坐标（老鼠会出现的位置）通过hanlder发生给主线程，进程UI的更新。
 */
public class MainActivity extends AppCompatActivity {

    /*
     步骤一：定义变量，对象，洞穴坐标
     */
    private int i = 0; //记录打到的地鼠个数
    private ImageView mouse;//定义mouse对象
    private TextView num; //定义num对象
    private TextView score; //记录游戏得分
    private Handler handler; //声明一个Handler对象
    public int[][] position = new int[][]{
            {120,540},{520,540},
            {350,740},{750,740},
            {270,941},{670,941},
            {240,1185},{640,1185},
            {220,1460},{620,1460}
    };//保存每个洞穴的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        步骤二：绑定控件
         */
        mouse = (ImageView)findViewById(R.id.mouse);
        num = findViewById(R.id.num);
        score = findViewById(R.id.score);

        num.setText("个数："+ i);
        score.setText("分数："+i*10);

        /*
        步骤三：实现地鼠随机出现
         */
        //1.创建Handler消息处理机制
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                //需要处理的消息
                int index;  //接收地洞数组的行坐标
                if(msg.what == 0x101){
                    index = msg.arg1;//获取位置索引值
                    mouse.setX(position[index][0]);
                    mouse.setY(position[index][1]);
                    mouse.setVisibility(View.VISIBLE);
                    Log.v("x: " +position[index][0],"Y: "+position[index][1]);
                }
                super.handleMessage(msg); //发生给主线程
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                while (!Thread.currentThread().isInterrupted()){
                    index = new Random().nextInt(position.length);//产生一个随机整数
                    Message msg = handler.obtainMessage(); //创建消息对象
                    msg.what = 0x101; // 设置消息标志
                    msg.arg1 = index; //保存地鼠位置的索引值
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(new Random().nextInt(500)+1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        /*
        步骤四：实现单击地鼠后的事件
         */
        mouse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setVisibility(View.INVISIBLE);//设置地鼠不可见
                i++;
                num.setText("个数："+ i);//更新个数
                score.setText("分数："+i*10);
                return false;
            }
        });

    }
}