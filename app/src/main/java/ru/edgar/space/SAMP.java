package ru.edgar.space;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.joom.paranoid.Obfuscate;
import com.nvidia.devtech.HeightProvider;
import com.nvidia.devtech.InputManager;

import ru.edgar.launcher.activity.MainActivity;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import ru.edgar.space.core.ui.noty.Notification;
import ru.edgar.space.core.ui.settingsdialog.DialogClientSettings;

@Obfuscate
public class SAMP extends GTASA implements HeightProvider.HeightListener {
    private static final String TAG = "SAMP";
    private static SAMP instance;

    public native void sendDialogResponse(int i, int i2, int i3, byte[] str);

    public static SAMP getInstance() {
        return instance;
    }

    private FrameLayout mRootFrame = null;
    private View mDarkScreen = null;
    private FrameLayout mHeadUILayout = null;
    private FrameLayout mBackUILayout = null;
    private FrameLayout mFrontUILayout = null;
    private SurfaceView mSurfaceView = null;

    private InputManager mInputManager = null;
    private HeightProvider mHeightProvider = null;
    private DialogClientSettings mDialogClientSettings = null;

    private Notification mNotification = null;
    private GameRender mGameRender = null;
    public static SoundPool soundPool;
    private InterfacesManager mInterfacesManager = null;

    public native void sendCommand(byte[] str);
    public native void onWeaponChanged();
    public native void togglePlayer(int toggle);
    // radar
    // weikton games
    public native void SetRadarBgPos(float x1, float y1, float x2, float y2);
    public native void SetRadarPos(float x1, float y1, float x2, float y2);
    public native void SetRadarEnabled(boolean tf);

    public native void EdgarConnect(String host, int port);

    public native void EdgarConnect2(boolean debug, String nick);

    public native byte[] requestSnapShotVehicle(int iModel, int dwColor, int dwColor1, int dwColor2);
    public native void createSnapShotVehicle(int iModel, int dwColor, int dwColor1, int dwColor2);

    private void showTab()
    {

    }

    private void hideTab()
    {

    }

    private void setTab(int id, String name, int score, int ping)
    {

    }

    private void clearTab()
    {

    }

    private void showLoadingScreen()
    {

    }

    private void hideLoadingScreen()
    {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingScreen.hide();
            }
        });*/
    }

    public void exitGame(){
       /* FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);

        finishAndRemoveTask();
        System.exit(0);*/
    }

    public void showDialog(int dialogId, int dialogTypeId, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        final String caption = new String(bArr, StandardCharsets.UTF_8);
        final String content = new String(bArr2, StandardCharsets.UTF_8);
        final String leftBtnText = new String(bArr3, StandardCharsets.UTF_8);
        final String rightBtnText = new String(bArr4, StandardCharsets.UTF_8);
       // runOnUiThread(() -> { this.mDialog.show(dialogId, dialogTypeId, caption, content, leftBtnText, rightBtnText); });
    }

    private native void onInputEnd(byte[] str);
    public void OnInputEnd(String str)
    {
        byte[] toReturn = null;
        try
        {
            toReturn = str.getBytes("windows-1251");
        }
        catch(UnsupportedEncodingException e)
        {

        }

        try {
            onInputEnd(toReturn);
        }
        catch (UnsatisfiedLinkError e5) {
            Log.e(TAG, e5.getMessage());
        }
    }

    private void showKeyboard()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("AXL", "showKeyboard()");
               // mKeyboard.ShowInputLayout();
            }
        });
    }

    private void hideKeyboard()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // mKeyboard.HideInputLayout();
            }
        });
    }

    private void showEditObject()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // mAttachEdit.show();
            }
        });
    }

    private void hideEditObject()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // mAttachEdit.hide();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "**** onCreate");
        super.onCreate(savedInstanceState);

        instance = this;

        //if(!SignatureChecker.isSignatureValid(this, getPackageName()))
        //{
            //Toast.makeText(this, "Use original launcher! No remake", Toast.LENGTH_LONG).show();
            //return;
        //}

        //mHeightProvider = new HeightProvider(this);

       /* mKeyboard = new CustomKeyboard(this);

        mDialog = new DialogManager(this);

        mAttachEdit = new AttachEdit(this);

        mLoadingScreen = new LoadingScreen(this);*/

        SurfaceView view = findViewById(R.id.main_sv);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();

        mSurfaceView = view;
        mRootFrame = findViewById(R.id.main_fl_root);
        mHeadUILayout = (FrameLayout) findViewById(R.id.head_ui_layout);
        mBackUILayout = (FrameLayout) findViewById(R.id.back_ui_layout);
        mFrontUILayout = (FrameLayout) findViewById(R.id.front_ui_layout);

        mDarkScreen = findViewById(R.id.dark_screen);
        mDarkScreen.setAlpha(1.0f);
        //setScreenDecay(false, 1000);

        SurfaceHolder holder = view.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        holder.setKeepScreenOn(true);

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        mRootFrame.setOnTouchListener(this);

        mInputManager = new InputManager(this);
        mHeightProvider = new HeightProvider(this).init(mRootFrame).setHeightListener(this);
        mNotification = new Notification(this);
        mGameRender = new GameRender(this);
        mInterfacesManager = new InterfacesManager(this);
        //mInterfacesManager = new ru.edgar.space.core.ui.keyboard.j5.a(this);

        DoResumeEvent();

        try {
            initializeSAMP();
        } catch (UnsatisfiedLinkError e5) {
            Log.e(TAG, e5.getMessage());
        }

    }

    private native void initializeSAMP();


    @Override
    public void onStart() {
        Log.i(TAG, "**** onStart");
        super.onStart();
    }

    @Override
    public void onRestart() {
        Log.i(TAG, "**** onRestart");
        super.onRestart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "**** onResume");
        super.onResume();
        //mHeightProvider.init(view);
    }

    public native void onEventBackPressed();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onEventBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            onEventBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "**** onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "**** onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "**** onDestroy");
        super.onDestroy();
    }

    @Override
    public void onHeightChanged(int orientation, int height) {
        //mKeyboard.onHeightChanged(height);
        //mDialog.onHeightChanged(height);
    }

    public void showInputLayout()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInputManager.ShowInputLayout();
            }
        });
    }

    public void hideInputLayout()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInputManager.HideInputLayout();
            }
        });
    }

    public void showDialog(int dialogId, int dialogTypeId, String caption, String content, String leftBtnText, String rightBtnText) {
        runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getDialogManager().show(dialogId, dialogTypeId, caption, content, leftBtnText, rightBtnText); });
    }

    public void hideDialogWithoutReset() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getDialogManager().hideWithoutReset(); }); }

    public void showDialogWithOldContent() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getDialogManager().showWithOldContent(); }); }

    public byte[] getClipboardText()
    {
        String retn = " ";

        if(mClipboardManager.getPrimaryClip() != null)
        {
            ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
            if(item != null)
            {
                CharSequence sequence = item.getText();
                if(sequence != null)
                {
                    retn = sequence.toString();
                }
            }
        }

        byte[] toReturn = null;
        try
        {
            toReturn = retn.getBytes("windows-1251");
        }
        catch(UnsupportedEncodingException e)
        {

        }
        return toReturn;
    }
    //
    public native void clickSpeedometr(int buttonid);
    //
    public FrameLayout getRootFrame() {
        return this.mRootFrame;
    }

    public FrameLayout getFrontUILayout() {
        return this.mFrontUILayout;
    }

    public FrameLayout getBackUILayout() {
        return this.mBackUILayout;
    }
    //

    public void updateHudInfo(int health, int armour, int hunger, int weaponidweik, int ammo, int playerid, int money, int wanted) { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().UpdateHudInfo(health, armour, hunger, weaponidweik, ammo, playerid, money, wanted); }); }

    public void showHud() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().ShowHud(); }); }

    public void hideHud() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().HideHud(); }); }

    public void showGps() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().ShowGps(); }); }

    public void hideGps() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().HideGps(); }); }

    public void showZona() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().ShowZona(); }); }

    public void hideZona() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().HideZona(); }); }

    public void showx2() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().ShowX2(); }); }

    public void hidex2() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().HideX2(); }); }

    public void setPauseState(final boolean z6) {
        if (mHeadUILayout == null) {
            mHeadUILayout = (FrameLayout) findViewById(R.id.front_ui_layout);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHeadUILayout.setVisibility(z6 ? View.GONE : View.VISIBLE);
            }
        });
    }

    public void updateSpeedInfo(int speed, int fuel, int hp, int mileage, int engine, int light, int belt, int lock) { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getSpeedometerManager().UpdateSpeedInfo(speed, fuel, hp, mileage, engine, light, belt, lock); }); }

    public void showSpeed() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getSpeedometerManager().ShowSpeed(); }); } //setNativeHudElementScale(6, 5, 5);

    public void hideSpeed() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getSpeedometerManager().HideSpeed(); }); }

    //public void RadarBR() { runOnUiThread(() -> { setNativeHudElementPosition(6, 5, 5); }); }

    public void showNotification(int type, String text, int duration, String actionforBtn, String textBtn) { runOnUiThread(() -> mNotification.ShowNotification(type, text, duration, actionforBtn, textBtn)); }

    public void showradar() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().ShowRadar(); }); }

    public void hideradar() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getHudManager().HideRadar(); }); }

    public void updateSplash(int percent, int pon) { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getChooseServerManager().Update(percent, pon); } ); }

    public void showSplash() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getChooseServerManager().show(); } ); }
    //BY EDGAR 3.0
    public void AddChatMessage(String msg, int color) {runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getChatManager().AddChatMessage(msg, color);});}

    public GameRender getGameRender() {
        return this.mGameRender;
    }

    public void setTurnState(int state) { runOnUiThread(() -> InterfacesManager.getInterfacesManager().getSpeedometerManager().setTurnlight(state)); }

    public void setUseFullscreen(int b)
    {
        //mUseFullscreen = b;
    }

    public void showClientSettings()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mDialogClientSettings != null)
                {
                    mDialogClientSettings = null;
                }
                mDialogClientSettings = new DialogClientSettings();
                mDialogClientSettings.show(getSupportFragmentManager(), "test");
            }
        });
    }

    public void showSpawnMenu() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getSpawnMenu().ShowSpawnMenu(); } ); }

    public void hideSpawnMenu() { runOnUiThread(() -> { InterfacesManager.getInterfacesManager().getSpawnMenu().HideSpawnMenu(); } ); }


    public void RadarBR() {/* runOnUiThread(() -> { setNativeHudElementPosition(6, 5, 5); });*/ }

    public native void connn(String host, int port, String nickname);

    public void connectEdgar() {
        String nickname = MainActivity.nickName;
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "Guest";
        }
        connn(EdgarConectV2.host, EdgarConectV2.port, nickname);
    }
}
