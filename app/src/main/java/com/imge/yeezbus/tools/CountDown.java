package com.imge.yeezbus.tools;

import android.os.Message;
import android.util.Log;

import com.imge.yeezbus.MainActivity;

public class CountDown extends Thread{
    private int count = 0;
    private final Object lock = new Object();
    public boolean isPause = false;

    @Override
    public void run() {
        super.run();
        synchronized (lock){
            while (true){
                if(isPause == true){
                    try{
                        lock.wait(1000);
                    }catch (Exception e){}
                }else{
                    Message msg = new Message();
                    msg.what = 3;
                    msg.arg1 = count;
                    MainActivity.handler.sendMessage(msg);

                    if(count == 0){
                        try{
                            lock.wait();
                        }catch (Exception e){}
                    }else{
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){}
                        count--;
                    }
                }
            }
        }
    }

    public void resetCount(){
        count = 20;
        synchronized (lock) {
            lock.notify();
        }
    }

    // 更新用，count 不設定成 0 ，主要是因為這個線程大部份的時間都在sleep，
    // 醒來第一件事，就是 count-- ，為了不讓 count 跑去 -1 以下，所以設成 1。
    // 還能順便防止手賤仔，狂按更新的那種，反正 count == 0 才會開始更新。
    public void updateNow(){
        count = 1;
    }

    public void setPause(boolean b){
        isPause = b;
    }
}
