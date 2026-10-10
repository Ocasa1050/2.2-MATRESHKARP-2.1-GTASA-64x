package ru.edgar.space.core.ui.loading;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvidia.devtech.NvEventQueueActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.model.FixedServer;
import ru.edgar.space.InterfacesManager;
import ru.edgar.space.R;
import ru.edgar.space.SAMP;

public class ChooseServer {
    NvEventQueueActivity nvEventQueueActivity;
    ViewGroup viewGroup;

    public final Handler mHandler = new Handler(Looper.getMainLooper());
    public TextView lm_status;
    int loading = 0;
    int type, size;
    String host;
    int port;
    int i = 0;
    //
    public ImageView[] loadingsImage;

    public int idImage;

    public boolean boolImage;

    public TimeSupport mTimeSupport;
    public TimeSupport TimeSupportt;

    public ChooseServer(NvEventQueueActivity nvEventQueueActivity, int guiId){
        this.nvEventQueueActivity = nvEventQueueActivity;
        viewGroup = InterfacesManager.getInterfacesManager().viewGroup[guiId];
    }

    public void show() {
        if (viewGroup == null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadingmenu, (ViewGroup) null);
            SAMP.getInstance().getFrontUILayout().addView(viewGroup, -1, -1);
        }

        host = FixedServer.HOST;
        port = FixedServer.PORT;
        Log.i("ChooseServer", "Connecting to " + host + ":" + port);
        lm_status = (TextView) viewGroup.findViewById(R.id.lm_status);
        //

        ImageView[] imageViewArr2 = new ImageView[5];
        loadingsImage = imageViewArr2;
        imageViewArr2[0] = (ImageView) viewGroup.findViewById(R.id.lm_candle_1);
        loadingsImage[1] = (ImageView) viewGroup.findViewById(R.id.lm_candle_2);
        loadingsImage[2] = (ImageView) viewGroup.findViewById(R.id.lm_candle_3);
        loadingsImage[3] = (ImageView) viewGroup.findViewById(R.id.lm_candle_4);
        loadingsImage[4] = (ImageView) viewGroup.findViewById(R.id.lm_candle_5);
        idImage = 0;
        boolImage = false;
        TimeSupport ponE = new TimeSupport(0.0f, 1.0f);
        mTimeSupport = ponE;
        ponE.Long = 300;
        ponE.Update1();
        TimeSupport pon = new TimeSupport(0.0f, 1.0f);
        this.TimeSupportt = pon;
        pon.TimeC = new SetAlpha();
        pon.TimeB = new mTimeSupportB();
        pon.Long = 500;
        pon.Update1();
    }

    public void Update(int percent, int pon) {
        i = pon;
        if (percent <= 100) {
            lm_status.setText("Загрузка игры...");
        } else {
            //InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
            lm_status.setText("Соединение к игре...");
            // TODO: Соединение к игре... BY EDGAR 3.0 EDGAR 3.0
        }
        if(i == 2){
            lm_status.setText("Соединено. Подготовка к игре...");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
                }
            }, 500L);
        }if (i == 3){
            lm_status.setText("Сервер полон. Переподключение к игре...");
        }if (i == 4){
            lm_status.setText("Проблемы с сетью, переподключение...");
        }if (i == 1){
            SAMP samp = SAMP.getInstance();
            if (samp == null) {
                Log.e("ChooseServer", "SAMP is not ready; cannot connect to " + host + ":" + port);
                return;
            }
            try {
                String nickname = MainActivity.nickName;
                if (nickname == null || nickname.trim().isEmpty()) {
                    nickname = "Guest";
                }
                samp.connn(host, port, nickname);
            } catch (UnsatisfiedLinkError error) {
                Log.e("ChooseServer", "The client connection method is unavailable", error);
            }
            //InterfacesManager.getInterfacesManager().getSpawnMenu().ShowSpawnMenu();
        }
    }



    public class SetAlpha implements TimeSupport.TimeSup {
        public SetAlpha() {
        }

        public final void a(TimeSupport Time) {
            loadingsImage[idImage].setAlpha(Time.Alpha4());
        }
    }

    public class mTimeSupportB implements TimeSupport.b {
        public mTimeSupportB() {
        }

        public final void a() {
            int i10 = boolImage ? idImage - 1 : idImage + 1;
            idImage = i10;
            if (i10 == -1) {
                boolImage = false;
                TimeSupport pon = TimeSupportt;
                pon.Alpha2 = 1.0f;
                idImage = 0;
            }
            if (idImage == 5) {
                boolImage = true;
                TimeSupport pon2 = TimeSupportt;
                pon2.Alpha3 = 1.0f;
                pon2.Alpha2 = 0.0f;
                idImage = 4;
            }
            TimeSupportt.Update1();
        }

        public final void b() {
        }
    }

    public static final class TimeSupport {

        public float Alpha3;

        public float Alpha2;
        public long Long;

        public float Alpha1;

        public long longL;

        public boolean mBool;
        public TimeSup TimeC;

        public b TimeB;

        public TimeInterpolator mTimeInterpolator;
        public Handler mHandler = new Handler();

        public class Update2m implements Runnable {
            public Update2m() {
            }

            public final void run() {
                TimeSupport.this.Update2();
            }
        }

        public interface b {
            void a();

            void b();
        }

        public interface TimeSup {
            void a(TimeSupport eVar);
        }

        public TimeSupport(float f10, float f11) {
            this.Alpha3 = f10;
            this.Alpha2 = f11;
            this.Long = 1000;
            this.Alpha1 = 0.0f;
            this.mBool = false;
            this.mTimeInterpolator = new LinearInterpolator();
        }


        public float Alpha4() {
            float f10 = this.Alpha3;
            return ep(this.Alpha2, this.Alpha3, this.mTimeInterpolator.getInterpolation(this.Alpha1), f10);
        }
        public float ep(float f10, float f11, float f12, float f13) {
            return ((f10 - f11) * f12) + f13;
        }

        public void c() {
            b bVar = this.TimeB;
            if (bVar != null) {
                bVar.a();
            }
        }

        public final void Update1() {
            if (!this.mBool) {
                this.longL = System.currentTimeMillis();
                this.mBool = true;
                b TimeB = this.TimeB;
                if (TimeB != null) {
                    TimeB.b();
                }
                Update2();
            }
        }

        public final void Update2() {
            if (this.mBool) {
                float currentTimeMillis = ((float) (System.currentTimeMillis() - this.longL)) / ((float) this.Long);
                if (currentTimeMillis >= 1.0f) {
                    this.Alpha1 = 1.0f;
                    this.mBool = false;
                } else {
                    this.Alpha1 = currentTimeMillis;
                    this.mHandler.post(new Update2m());
                }
                TimeSup TimeCSupport = this.TimeC;
                if (TimeCSupport != null) {
                    TimeCSupport.a(this);
                }
                if (!this.mBool) {
                    c();
                }
            }
        }
    }


    public void Show() {
        show();
    }

}