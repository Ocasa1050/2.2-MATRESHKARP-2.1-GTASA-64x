package ru.edgar.launcher.activity;


import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.liulishuo.filedownloader.FileDownloader;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.requests.VKRequest;
import com.vk.api.sdk.ui.VKWebViewAuthActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.launcher.fragment.AuthFragment;
import ru.edgar.launcher.fragment.CabinetFragment;
import ru.edgar.launcher.fragment.CreateCharacterFragment;
import ru.edgar.launcher.fragment.DialogFragment;
import ru.edgar.launcher.fragment.DownloadFragment;
import ru.edgar.launcher.fragment.FaqFragment;
import ru.edgar.launcher.fragment.LoadingFragment;
import ru.edgar.launcher.fragment.MainFragment;
import ru.edgar.launcher.fragment.ServerSelectFragment;
import ru.edgar.launcher.fragment.SettingsFragment;
import ru.edgar.launcher.fragment.SplashFragment;
import ru.edgar.launcher.fragment.StoryFragment;
import ru.edgar.launcher.model.Api;
import ru.edgar.launcher.model.edgar;
import ru.edgar.launcher.other.Interface;
import ru.edgar.space.R;
import ru.edgar.space.diagnostics.CrashLogManager;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    public static boolean isAuth = false;
    public static Integer server_id = null;
    public static boolean sApi = false;
    public static boolean testApi = true;
    public static boolean isPermissions = false;
    public static String email_google = null;
    public static String email = null;
    public static String first_name = null;
    public static String last_name = null;
    public static Integer way = null;
    public static String nickName;
    public ViewGroup viewGroup;

    private NotificationManager notifManager = null;

    public SplashFragment splashFragment;
    public MainFragment mainFragment;
    public CabinetFragment cabinetFragment;
    public SettingsFragment settingsFragment;
    public ServerSelectFragment serverSelectFragment;
    public CreateCharacterFragment createCharacterFragment;
    public AuthFragment authFragment;
    public StoryFragment storyFragment;
    public DownloadFragment downloadFragment;
    public FaqFragment faqFragment;
    public DialogFragment dialogFragment;
    public LoadingFragment loadingFragment;

    public FrameLayout front_ui_layout;
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    private FirebaseAuth mAuth;
    public String text3, text4, text6, auth1;
    Timer t;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Launcher);
        setContentView(R.layout.activity_main);
        CrashLogManager.initialize(this);
        CrashLogManager.recordEvent(this, "launcher.onCreate");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getExternalFilesDir("");

        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (notifManager.getNotificationChannel("space_1") == null) {
                NotificationChannel notificationChannel = new NotificationChannel("space_1", "space", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("space");
                notificationChannel.enableVibration(false);
                notificationChannel.setLightColor(-16711936);
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setVibrationPattern(new long[]{0});
                notifManager.createNotificationChannel(notificationChannel);
            }
        }

        /*if (PermissionUtils.hasPermissions(this)) {
            try {
                //startTimer(); mHandler.postDelayed(new AnimClose(), 7800);
                overridePendingTransition(0, 0);
            } catch (Exception e) {

            }
        } else if (!PermissionUtils.hasPermissions(this)) {
            PermissionUtils.requestPermissions(this, 101);
        }*/

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        FirebaseApp.initializeApp(this);

        FileDownloader.init(this);

        VK.initialize(this);

        System.loadLibrary("edgar");
        Rgdjsskhuidsyh704fdj8sdaa3327977jfss997gsd();

        Connect();

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            isAuth = true;
        } else {
            isAuth = false;
        }

        instance = this;

        front_ui_layout = (FrameLayout) findViewById(R.id.front_ui_layout);

        /*
        this.f4353b.add(new v1(this));
        this.f4353b.add(new e1(this));//fragment_splash
        this.f4353b.add(new q0(this));//fragment_main
        this.f4353b.add(new v6.i(this));//fragment_cabinet
        this.f4353b.add(new d1(this));//fragment_social
        this.f4353b.add(new z0(this));//fragment_settings
        this.f4353b.add(new x0(this));//fragment_server_select 03.12.23
        this.f4353b.add(new j(this));//fragment_create_character
        this.f4353b.add(new v6.w0(this));//ragment_recover
        this.f4353b.add(new v6.c(this));//fragment_auth
        this.f4353b.add(new m1(this));//story
        this.f4353b.add(new a0(this));//fragment_download
        this.f4353b.add(new m0(this));//fragment_faq
        this.f4353b.add(new o(this));//fragment_notify_dialog
        this.f4353b.add(new v6.n(this));//fragment_dialog
        this.f4353b.add(new p0(this));//fragment_loading
         */

        splashFragment = new SplashFragment();
        mainFragment = new MainFragment();
        cabinetFragment = new CabinetFragment();
        settingsFragment = new SettingsFragment();
        serverSelectFragment = new ServerSelectFragment();
        createCharacterFragment = new CreateCharacterFragment();
        authFragment = new AuthFragment();
        storyFragment = new StoryFragment();
        downloadFragment = new DownloadFragment();
        faqFragment = new FaqFragment();
        //fragment_notify_dialog
        dialogFragment = new DialogFragment();
        loadingFragment = new LoadingFragment();
        t = new Timer();

/*
        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
        launcher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY);*/

        /*String str;
        float dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen._9sdp);
        float dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen._9sdp);
        float dimensionPixelSize3 = getResources().getDimensionPixelSize(R.dimen._92sdp) + dimensionPixelSize;
        float dimensionPixelSize4 = getResources().getDimensionPixelSize(R.dimen._92sdp) + dimensionPixelSize2;
        Drawable b10 = getResources().getDrawable(R.drawable.bg_hud_map);
        Drawable b11 = getResources().getDrawable(R.drawable.fg_hud_map);
        if (b10 == null || b11 == null) {
            str = "Radar texture not found!";
        } else {
            int i10 = (int) (dimensionPixelSize3 - dimensionPixelSize);
            int i11 = (int) (dimensionPixelSize4 - dimensionPixelSize2);
            Bitmap createBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            b10.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            b10.draw(canvas);
            Bitmap createBitmap2 = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(createBitmap2);
            b11.setBounds(0, 0, canvas2.getWidth(), canvas2.getHeight());
            b11.draw(canvas2);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            createBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            createBitmap2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
            byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
            int width = createBitmap.getWidth();
            int height = createBitmap.getHeight();
            String downloadPath = Helper.androidPath + "/fg_hud_map.png";
            String downloadPath1 = Helper.androidPath + "/bg_hud_map.png";

            // Создание файла и запись в него массива байт
            File file = new File(downloadPath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(byteArray2);
                fos.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File file1 = new File(downloadPath1);
            FileOutputStream fos1 = null;
            try {
                fos1 = new FileOutputStream(file1);
                fos1.write(byteArray);
                fos1.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (createBitmap.getWidth() == createBitmap2.getWidth() && createBitmap.getHeight() == createBitmap2.getHeight()) {
                createBitmap.recycle();
                createBitmap2.recycle();
                ByteBuffer allocate = ByteBuffer.allocate(byteArray.length + byteArray2.length + 24);
                allocate.putInt(Integer.reverseBytes(byteArray.length));
                allocate.putInt(Integer.reverseBytes(byteArray2.length));
                allocate.putInt(Integer.reverseBytes(width));
                allocate.putInt(Integer.reverseBytes(height));
                allocate.putInt(Integer.reverseBytes((int) dimensionPixelSize));
                allocate.putInt(Integer.reverseBytes((int) dimensionPixelSize2));
                allocate.put(byteArray);
                allocate.put(byteArray2);
                //setRadarTexture(allocate.array());
                //B0 = true;
                return;
            }
            str = "Radar texture not loaded!";
        }
        Log.e("jekmant", str);*/

        /*File directory = new File(Environment.getExternalStorageDirectory() + "/Edgar");
        List<FilesList> fileInfoList = listFilesRecursively(directory);

        // Преобразование списка FileInfo в JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(fileInfoList);
        System.out.println(json);
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "fileName.txt");
            FileWriter writer = new FileWriter(file);

            writer.write(json);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //startTimer();
        /*String hexColor = String.format("#%06X", (0xFFFFFF & -10990239)); //#584D61 // #4D5B61 //-10990239 //-11707551 //4D5D61
        System.out.println(hexColor);
        System.out.println(Color.parseColor("#4D5B61"));*/
        splashFragment.show();
        onRequestPermissions();
    }
    public native void edgarBan();
    public class onDes implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            edgarBan();
        }
    }

    public void Auhtch(String text, String text2, String text5, String auth)
    {
            auth1 = auth;
            text3 = text;
            text4 = text2;
            text6 = text5;
            if(text == null){
            }else {System.out.println(text);}
            if(text2 == null){
            }else {System.out.println(text2);}
            if(text5 == null){
            }else {System.out.println(text5);}
            System.out.println(auth);
        // Логирование значений
        System.out.println("text: " + text);
        System.out.println("text2: " + text2);
        System.out.println("text5: " + text5);
        System.out.println("auth1: " + auth1); // Выводим значение auth1
    }

    native void Rgdjsskhuidsyh704fdj8sdaa3327977jfss997gsd();

    public static MainActivity getMainActivity() {
        return instance;
    }

    public native void Connect();

    public static Point getPointSz(Display display) {
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    public void openDialog(int idDrawable, String text, String text2, String text3, View.OnClickListener click, View.OnClickListener click2) {
        dialogFragment.show(idDrawable, text, text2, text3, click, click2);
    }

    public static class MainOpen implements Runnable {

        @Override
        public void run() {
            getMainActivity().mainFragment.show();
        }
    }

    public void onRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean canReadStorage = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean canWriteStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean canRecordAudio = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            CrashLogManager.recordEvent(this, "permissions.check readStorage=" + canReadStorage
                    + " writeStorage=" + canWriteStorage
                    + " recordAudio=" + canRecordAudio
                    + " apiReady=" + sApi);

            if (!canReadStorage || !canWriteStorage || !canRecordAudio) {
                CrashLogManager.recordEvent(this, "permissions.requested runtime storage and microphone permissions");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO/*Manifest.permission.READ_CONTACTS*/}, 1);
            } else {
                isPermissions = true;
                if (sApi) {
                    CrashLogManager.recordEvent(this, "permissions.granted; opening launcher");
                    if (splashFragment == null || mainFragment == null) {
                        CrashLogManager.recordEvent(this, "permissions.granted but launcher fragments are not initialized");
                        return;
                    }
                    splashFragment.hide();
                    mainFragment.upServerId();
                    mHandler.postDelayed(new MainOpen(), 300L);
                } else {
                    CrashLogManager.recordEvent(this, "permissions.granted; waiting for launcher API");
                }
            }
        }
        /*if (PermissionUtils.hasPermissions(this)) {
            try {
                overridePendingTransition(0, 0);
            } catch (Exception e) {

            }
        } else if (!PermissionUtils.hasPermissions(this)) {
            PermissionUtils.requestPermissions(this, 101);//доступ к data
        }*/
    }

    //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            StringBuilder result = new StringBuilder("permissions.result");
            if (permissions != null && grantResults != null) {
                for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
                    result.append(' ').append(permissions[i]).append('=')
                            .append(grantResults[i] == PackageManager.PERMISSION_GRANTED ? "granted" : "denied");
                }
            }
            CrashLogManager.recordEvent(this, result.toString());

            boolean canReadStorage = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean canWriteStorage = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean canRecordAudio = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            if (canReadStorage && canWriteStorage && canRecordAudio) {
                onRequestPermissions();
            } else {
                CrashLogManager.recordEvent(this, "permissions.incomplete; launcher remains on splash screen");
            }
        }
    }

    public void onClickAuthGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(MainActivity.getMainActivity().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, options);
        if (GoogleSignIn.getLastSignedInAccount(MainActivity.getMainActivity()) != null) {
            googleSignInClient.signOut().addOnCompleteListener(MainActivity.getMainActivity(), new AuthFragment.a(googleSignInClient));
            return;
        }
        Intent i = googleSignInClient.getSignInIntent();
        MainActivity.getMainActivity().startActivityForResult(i, 1234);
    }

    public static boolean equals(Object obj, Object obj2) {
        if (obj == null) {
            if (obj2 == null) {
                return true;
            }
            return false;
        }
        return obj.equals(obj2);
    }

    public void onClickAuthVk() {
        //boolean isApkVk = false;
        // VK.login(MainActivity.this, Arrays.asList(VKScope.EMAIL));
        /* EDGAR 3.0 */
        /*List<ResolveInfo> queryIntentActivities;
        PackageManager packageManager = MainActivity.getMainActivity().getPackageManager();
        if (packageManager != null && (queryIntentActivities = packageManager.queryIntentActivities(new Intent("com.vkontakte.android.action.SDK_AUTH", (Uri) null), 65536)) != null) {
            List<ResolveInfo> list = queryIntentActivities;
            if (!(list instanceof Collection) || !list.isEmpty()) {
                Iterator<ResolveInfo> it = list.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (equals(((ResolveInfo) it.next()).activityInfo.packageName, "com.vkontakte.android")) {
                            System.out.println("Нашел!");
                            isApkVk = true;
                            break;
                        }
                    }
                }
            }
        }
        if (isApkVk) { // TODO APP AUTH VK no work
            Intent intent = new Intent("com.vkontakte.android.action.SDK_AUTH", null);
            intent.setPackage("com.vkontakte.android");
            Bundle bundle = new Bundle();
            bundle.putInt("client_id", 51874098);
            bundle.putBoolean("revoke", true);
            bundle.putString("scope", TextUtils.join(",", new VKScope[]{VKScope.PHONE}));
            bundle.putString("redirect_url", "https://oauth.vk.com/blank.html");
            intent.putExtras(bundle);
            MainActivity.getMainActivity().startActivityForResult(intent, 282);// online
            return;
        }*/
        Intent intent2 = new Intent(MainActivity.getMainActivity(), VKWebViewAuthActivity.class);// VK web View Клиент
        Bundle bundle2 = new Bundle();
        bundle2.putInt("vk_app_id", 51874098);
        bundle2.putString("vk_app_scope", Arrays.asList(VKScope.EMAIL).toString());
        bundle2.putString("vk_app_redirect_url", "https://oauth.vk.com/blank.html");
        Intent putExtra = intent2.putExtra("vk_auth_params", bundle2);
        //io.sentry.util.a.r(putExtra, "Intent(activity, VKWebVi…ARAMS, params.toBundle())");
        MainActivity.getMainActivity().startActivityForResult(putExtra, 282);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 282) {
            VKAuthCallback callback = new VKAuthCallback() {
                @Override
                public void onLogin(VKAccessToken token) {
                    // Авторизация прошла успешно
                    // ...
                    loadingFragment.show();
                    VKRequest request  = new VKRequest("users.get", VK.getApiVersion())
                            .addParam("fields","domain, screen_name");
                    VK.execute(request , new VKApiCallback<JSONObject>() {
                        @Override
                        public void success(JSONObject response) {
                            try {
                                System.out.println(response);
                                JSONArray responseArray = response.getJSONArray("response");

                                JSONObject userObject = responseArray.getJSONObject(0);

                                long id = userObject.getLong("id");
                                String firstName = userObject.getString("first_name");
                                String lastName = userObject.getString("last_name");
                                String domain = userObject.getString("domain");
                                String screen_name = userObject.getString("screen_name");
                                boolean canAccessClosed = userObject.getBoolean("can_access_closed");
                                boolean isClosed = userObject.getBoolean("is_closed");

                                System.out.println("ID: " + id);
                                System.out.println("First name: " + firstName);
                                System.out.println("Last name: " + lastName);
                                System.out.println("Can access closed: " + canAccessClosed);
                                System.out.println("Is closed: " + isClosed);

                                FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean isAcc = false;
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            Long paramValue = userSnapshot.child("vk-id").getValue(Long.class);
                                            if (paramValue != null) {
                                                if (paramValue.equals(id)) {
                                                    isAcc = true;
                                                    mAuth.signInWithEmailAndPassword(id + "@vk.com", id + "pass").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if(task.isSuccessful()){
                                                                mHandler.removeCallbacksAndMessages(null);
                                                                Point point = new Point();
                                                                MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
                                                                authFragment.viewGroup.clearAnimation();
                                                                authFragment.viewGroup.setAlpha(1.0f);
                                                                authFragment.viewGroup.setVisibility(View.VISIBLE);
                                                                authFragment.viewGroup.animate().alpha(0.0f).setDuration(300L);
                                                                mHandler.postDelayed(new AuthFragment.ssetVisibility(), 300L);
                                                                authFragment.btn_close.clearAnimation();
                                                                authFragment.btn_close.setTranslationY(0.0f);
                                                                authFragment.btn_close.animate().setDuration(300L).translationY(-point.y).start();
                                                                authFragment.btn_faq.clearAnimation();
                                                                authFragment.btn_faq.setTranslationY(0.0f);
                                                                authFragment.btn_faq.animate().setDuration(300L).translationY(-point.y).start();
                                                                authFragment.auth_bottom.clearAnimation();
                                                                authFragment.auth_bottom.setTranslationY(0.0f);
                                                                authFragment.auth_bottom.animate().setDuration(300L).translationY(point.y).start();
                                                                authFragment.auth_layout_main.clearAnimation();
                                                                authFragment.auth_layout_main.setAlpha(1.0f);
                                                                authFragment.auth_layout_main.setVisibility(View.VISIBLE);
                                                                authFragment.auth_layout_main.setTranslationX(0.0f);
                                                                authFragment.auth_layout_main.animate().alpha(0.0f).translationX(-point.y).setDuration(300L);
                                                                authFragment.auth_layout_email.clearAnimation();
                                                                authFragment.auth_layout_email.setAlpha(0.0f);
                                                                authFragment.auth_layout_email.setVisibility(View.GONE);
                                                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                                                if(currentUser != null) {
                                                                    MainActivity.isAuth = true;
                                                                } else {
                                                                    MainActivity.isAuth = false;
                                                                }

                                                                HashMap<String, Object> Info = new HashMap<>();
                                                                Info.put("first_name", firstName);
                                                                Info.put("last_name", lastName);
                                                                Info.put("domain", domain);
                                                                Info.put("screen_name", screen_name);
                                                                Info.put("vk-id", id);
                                                                Info.put("way", 3);
                                                                FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                                                authFragment.ReturnToZero();
                                                                authFragment.regStatus = 1;
                                                                mainFragment.upServerId(); // EDGAR 3.0 newLauncher version от 02.04.2024
                                                                settingsFragment.upSettings();
                                                                loadingFragment.hide();
                                                            } else {
                                                                loadingFragment.hide();
                                                                MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        if (!isAcc) {
                                            mAuth.createUserWithEmailAndPassword(id + "@vk.com", id + "pass").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        mHandler.removeCallbacksAndMessages(null);
                                                        Point point = new Point();
                                                        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
                                                        authFragment.viewGroup.clearAnimation();
                                                        authFragment.viewGroup.setAlpha(1.0f);
                                                        authFragment.viewGroup.setVisibility(View.VISIBLE);
                                                        authFragment.viewGroup.animate().alpha(0.0f).setDuration(300L);
                                                        mHandler.postDelayed(new AuthFragment.ssetVisibility(), 300L);
                                                        authFragment.btn_close.clearAnimation();
                                                        authFragment.btn_close.setTranslationY(0.0f);
                                                        authFragment.btn_close.animate().setDuration(300L).translationY(-point.y).start();
                                                        authFragment.btn_faq.clearAnimation();
                                                        authFragment.btn_faq.setTranslationY(0.0f);
                                                        authFragment.btn_faq.animate().setDuration(300L).translationY(-point.y).start();
                                                        authFragment.auth_bottom.clearAnimation();
                                                        authFragment.auth_bottom.setTranslationY(0.0f);
                                                        authFragment.auth_bottom.animate().setDuration(300L).translationY(point.y).start();
                                                        authFragment.auth_layout_main.clearAnimation();
                                                        authFragment.auth_layout_main.setAlpha(1.0f);
                                                        authFragment.auth_layout_main.setVisibility(View.VISIBLE);
                                                        authFragment.auth_layout_main.setTranslationX(0.0f);
                                                        authFragment.auth_layout_main.animate().alpha(0.0f).translationX(-point.y).setDuration(300L);
                                                        authFragment.auth_layout_email.clearAnimation();
                                                        authFragment.auth_layout_email.setAlpha(0.0f);
                                                        authFragment.auth_layout_email.setVisibility(View.GONE);
                                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                                        if(currentUser != null) {
                                                            MainActivity.isAuth = true;
                                                        } else {
                                                            MainActivity.isAuth = false;
                                                        }

                                                        HashMap<String, Object> Info = new HashMap<>();
                                                        Info.put("first_name", firstName);
                                                        Info.put("last_name", lastName);
                                                        Info.put("domain", domain);
                                                        Info.put("screen_name", screen_name);
                                                        Info.put("vk-id", id);
                                                        Info.put("way", 3);
                                                        FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                                        authFragment.ReturnToZero();
                                                        authFragment.regStatus = 1;
                                                        mainFragment.upServerId(); // EDGAR 3.0 newLauncher version от 02.04.2024
                                                        settingsFragment.upSettings();
                                                        loadingFragment.hide();
                                                    } else {
                                                        loadingFragment.hide();
                                                        MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } catch (JSONException e) {
                                loadingFragment.hide();
                                MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                            }
                        }

                        @Override
                        public void fail(@NonNull Exception e) {
                            // Авторизация не удалась
                            // ...
                            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                            loadingFragment.hide();
                        }
                    });
                }

                @Override
                public void onLoginFailed(int e) {
                    // Авторизация не удалась
                    // ...
                    MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                }
            };

            VK.onActivityResult(requestCode, resultCode, data, callback);
        }
        if (requestCode == 101 && Build.VERSION.SDK_INT >= 30 && !PermissionUtils.hasPermissions(this)) {
            Toast.makeText(getApplicationContext(), "Дайте разрешение!", Toast.LENGTH_SHORT).show();
        } else //replaceFragment(mainFragment);*
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loadingFragment.show();

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mHandler.removeCallbacksAndMessages(null);
                                Point point = new Point();
                                MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
                                authFragment.viewGroup.clearAnimation();
                                authFragment.viewGroup.setAlpha(1.0f);
                                authFragment.viewGroup.setVisibility(View.VISIBLE);
                                authFragment.viewGroup.animate().alpha(0.0f).setDuration(300L);
                                mHandler.postDelayed(new AuthFragment.ssetVisibility(), 300L);
                                authFragment.btn_close.clearAnimation();
                                authFragment.btn_close.setTranslationY(0.0f);
                                authFragment.btn_close.animate().setDuration(300L).translationY(-point.y).start();
                                authFragment.btn_faq.clearAnimation();
                                authFragment.btn_faq.setTranslationY(0.0f);
                                authFragment.btn_faq.animate().setDuration(300L).translationY(-point.y).start();
                                authFragment.auth_bottom.clearAnimation();
                                authFragment.auth_bottom.setTranslationY(0.0f);
                                authFragment.auth_bottom.animate().setDuration(300L).translationY(point.y).start();
                                authFragment.auth_layout_main.clearAnimation();
                                authFragment.auth_layout_main.setAlpha(1.0f);
                                authFragment.auth_layout_main.setVisibility(View.VISIBLE);
                                authFragment.auth_layout_main.setTranslationX(0.0f);
                                authFragment.auth_layout_main.animate().alpha(0.0f).translationX(-point.y).setDuration(300L);
                                authFragment.auth_layout_email.clearAnimation();
                                authFragment.auth_layout_email.setAlpha(0.0f);
                                authFragment.auth_layout_email.setVisibility(View.GONE);
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if(currentUser != null) {
                                    MainActivity.isAuth = true;
                                } else {
                                    MainActivity.isAuth = false;
                                }

                                mainFragment.upServerId(); // EDGAR 3.0 newLauncher version от 02.01.2024
                                settingsFragment.upSettings();

                                HashMap<String, Object> Info = new HashMap<>();
                                Info.put("google-email", mAuth.getCurrentUser().getEmail());
                                Info.put("way", 2);
                                FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                authFragment.ReturnToZero();
                                authFragment.regStatus = 1;
                                loadingFragment.hide();
                            } else {
                                MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через Google!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                                loadingFragment.hide();
                            }

                        }
                    });
            } catch (ApiException e) {
                MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка авторизации через Google!\nПопробуйте ещё раз.", "Понятно", null, new DialogFragment.closeDialog(), null);
                loadingFragment.hide();
                e.printStackTrace();
            }
        }
    }

    public final static class anim extends Animation {

        public final View f12986a;
        public final int f12987b = 0;
        public final int c;
        public final boolean f12988d;
        public final int f12989e;

        public anim(View view, int i10, int i11, boolean z6) {
            this.f12986a = view;
            this.c = i11;
            this.f12988d = z6;
            this.f12989e = i11 + 0 + i10 + 0;
            if (z6) {
                ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = i11;
                ((LinearLayout.LayoutParams) view.getLayoutParams()).height = i10;
            } else {
                ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = 0;
                ((LinearLayout.LayoutParams) view.getLayoutParams()).height = 0;
            }
            view.requestLayout();
        }

        @Override // android.view.animation.Animation
        public final void applyTransformation(float f10, Transformation transformation) {
            if (f10 == 1.0f) {
                ((LinearLayout.LayoutParams) this.f12986a.getLayoutParams()).topMargin = this.c;
                this.f12986a.getLayoutParams().height = -2;
                this.f12986a.requestLayout();
                this.f12986a.setVisibility(this.f12988d ? View.GONE : View.VISIBLE);
                return;
            }
            if (this.f12988d) {
                f10 = 1.0f - f10;
            }
            int i10 = (int) (this.f12989e * f10);
            int i11 = this.f12987b;
            int min = Math.min(this.c - i11, i10) + i11;
            ((LinearLayout.LayoutParams) this.f12986a.getLayoutParams()).topMargin = min;
            ((LinearLayout.LayoutParams) this.f12986a.getLayoutParams()).height = i10 - min;
            this.f12986a.requestLayout();
        }

        @Override // android.view.animation.Animation
        public final void initialize(int i10, int i11, int i12, int i13) {
            super.initialize(i10, i11, i12, i13);
        }

        @Override // android.view.animation.Animation
        public final boolean willChangeBounds() {
            return true;
        }
    }

    public final static void Animka(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.getAnimation().cancel();
        }
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        boolean z6 = view instanceof TextView;
        if (z6) {
            view.measure(-2, -2);
        } else {
            view.measure(-1, -2);
        }
        anim aVar = new anim(view, view.getMeasuredHeight(), z6 ? 0 : MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._12sdp), false);
        aVar.setDuration(z6 ? 150L : 300L);
        aVar.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(aVar);
    }

    public static class PermissionUtils {
        public static boolean hasPermissions(Context context) {
            if (Build.VERSION.SDK_INT >= 30) {
                return Environment.isExternalStorageManager();
            }
            if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                return true;
            }
            return false;
        }

        public static void requestPermissions(Activity activity, int requestCode) {
            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", new Object[]{activity.getPackageName()})));
                    activity.startActivityForResult(intent, requestCode);
                } catch (Exception e) {
                    Intent intent2 = new Intent();
                    intent2.setAction("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION");
                    activity.startActivityForResult(intent2, requestCode);
                }
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, requestCode);
            }
        }
    }

    public final static class animClickBtn implements View.OnTouchListener {

        public final AnimatorSet animatorSet;

        public final AnimatorSet animatorSet1;
        public final View view;

        public animClickBtn(Context context, View view) {
            this.view = view;
            view.setClickable(true);
            this.animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.reduce_size);
            this.animatorSet1 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.regain_size);
        }

        @Override // android.view.View.OnTouchListener
        @SuppressLint({"ClickableViewAccessibility"})
        public final boolean onTouch(View view, MotionEvent motionEvent) {
            AnimatorSet animatorSet;
            int action = motionEvent.getAction() & 255;
            if (action == 0) {
                if (this.animatorSet1.isRunning()) {
                    this.animatorSet1.end();
                }
                this.animatorSet.setTarget(this.view);
                animatorSet = this.animatorSet;
            } else if (action != 1 && action != 3) {
                return false;
            } else {
                if (this.animatorSet.isRunning()) {
                    this.animatorSet.end();
                }
                this.animatorSet1.setTarget(this.view);
                animatorSet = this.animatorSet1;
            }
            animatorSet.start();
            return false;
        }
    }


    public void  onDestroy() {
        super.onDestroy();
        
    }

    public void onRestart() {
        super.onRestart();
        
    }
}