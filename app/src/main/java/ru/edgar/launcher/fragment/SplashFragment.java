package ru.edgar.launcher.fragment;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.model.Api;
import ru.edgar.launcher.model.FaqList;
import ru.edgar.launcher.model.FixedServer;
import ru.edgar.launcher.model.Main;
import ru.edgar.launcher.model.News;
import ru.edgar.launcher.model.Servers;
import ru.edgar.launcher.other.Interface;
import ru.edgar.launcher.other.Lists;
import ru.edgar.launcher.network.ApiClient;
import ru.edgar.space.R;

public class SplashFragment extends MainActivity{

    ImageView splash_logo;

    public ee panzto;

    String apiLink;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final long STARTUP_WATCHDOG_MS = 20_000L;
    private static final String API_BASE_URL = "http://api-free.edgars.site/";
    private final Handler startupHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<Call<?>> startupCalls = new ArrayList<>();
    private long startupAttempt;
    private int pendingStartupRequests;
    private boolean startupInProgress;
    private boolean waitingForUpdateDecision;
    private boolean startupIssueShown;
    private Runnable startupWatchdog;

    public SplashFragment() {
        super();
        splashInit();
    }

    public void splashInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_splash, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        splash_logo = (ImageView) viewGroup.findViewById(R.id.splash_logo);

        loadJsons();

        viewGroup.setVisibility(View.GONE);
    }

    public class loadJsonRepit implements View.OnClickListener {
        public loadJsonRepit() {
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            MainActivity.getMainActivity().dialogFragment.hide();
            loadJsons();
        }
    }

    public class noUpdate implements View.OnClickListener {
        private final long attempt;
        private final Api api;
        private final Interface apiService;

        public noUpdate(long attempt, Api api, Interface apiService) {
            this.attempt = attempt;
            this.api = api;
            this.apiService = apiService;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            MainActivity.getMainActivity().dialogFragment.hide();
            if (!isCurrentAttempt(attempt)) {
                return;
            }
            waitingForUpdateDecision = false;
            startStartupWatchdog(attempt);
            continueWithApi(attempt, api, apiService);
        }
    }

    public class downloadApk implements View.OnClickListener {
        public String[] launcher = new String[3];

        public downloadApk(String url, String path, String name) {
            launcher[0] = url;
            launcher[1] = path;
            launcher[2] = name;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            MainActivity.getMainActivity().dialogFragment.hide();
            stopStartupLoad();
            MainActivity.getMainActivity().splashFragment.hide();
            MainActivity.getMainActivity().downloadFragment.startDownloadApk(launcher[0], launcher[1], launcher[2]);
        }
    }

    public void loadJsons() {
        final long attempt = ++startupAttempt;
        stopStartupLoad();
        startupAttempt = attempt;
        startupInProgress = true;
        waitingForUpdateDecision = false;
        startupIssueShown = false;
        pendingStartupRequests = 0;
        MainActivity.sApi = true;
        MainActivity.testApi = true;

        resetStartupLists();
        showHomeImmediately(attempt);
        startStartupWatchdog(attempt);

        try {
            Interface apiService = ApiClient.create(API_BASE_URL).create(Interface.class);
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(1)
                    .build();

            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
                    .addOnCompleteListener(MainActivity.getMainActivity(), settingsTask -> {
                        if (!isCurrentAttempt(attempt)) {
                            return;
                        }
                        if (!settingsTask.isSuccessful()) {
                            stopStartupLoadWithIssue(attempt, "Remote Config settings", settingsTask.getException());
                            return;
                        }
                        fetchApiLink(attempt, apiService);
                    });
        } catch (RuntimeException exception) {
            stopStartupLoadWithIssue(attempt, "Startup initialization", exception);
        }
    }

    private void resetStartupLists() {
        if (Lists.slist == null) {
            Lists.slist = new ArrayList<>();
        } else {
            Lists.slist.clear();
        }
        if (Lists.nlist == null) {
            Lists.nlist = new ArrayList<>();
        } else {
            Lists.nlist.clear();
        }
        if (Lists.faqlist == null) {
            Lists.faqlist = new ArrayList<>();
        } else {
            Lists.faqlist.clear();
        }

        if (MainActivity.server_id == null) {
            MainActivity.server_id = FixedServer.DEFAULT_ID;
        }
        Lists.slist.add(FixedServer.create(MainActivity.server_id));
    }

    private void showHomeImmediately(long attempt) {
        startupHandler.postDelayed(() -> {
            if (attempt != startupAttempt) {
                return;
            }
            MainActivity activity = MainActivity.getMainActivity();
            if (activity == null || activity.mainFragment == null) {
                return;
            }
            activity.splashFragment.hide();
            activity.mainFragment.UpdateServers();
            activity.mainFragment.upServerId();
            activity.mainFragment.show();
        }, 100L);
    }

    private void fetchApiLink(long attempt, Interface apiService) {
        try {
            mFirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(MainActivity.getMainActivity(), task -> {
                        if (!isCurrentAttempt(attempt)) {
                            return;
                        }
                        if (!task.isSuccessful()) {
                            stopStartupLoadWithIssue(attempt, "Remote Config fetch", task.getException());
                            return;
                        }
                        apiLink = mFirebaseRemoteConfig.getString("api1Link");
                        if (isBlank(apiLink)) {
                            stopStartupLoadWithIssue(attempt, "Remote Config api1Link", null);
                            return;
                        }
                        requestApiConfig(attempt, apiService, apiLink);
                    });
        } catch (RuntimeException exception) {
            stopStartupLoadWithIssue(attempt, "Remote Config fetch", exception);
        }
    }

    private void requestApiConfig(long attempt, Interface apiService, String link) {
        if (isBlank(link)) {
            stopStartupLoadWithIssue(attempt, "API link", null);
            return;
        }
        try {
            enqueueStartupCall(attempt, "API config", apiService.getApi(link), api -> {
                if (api.getLauncherVersion() == null) {
                    stopStartupLoadWithIssue(attempt, "API config version", null);
                    return;
                }
                if (api.getLauncherVersion() != 34) {
                    waitingForUpdateDecision = true;
                    cancelStartupWatchdog();
                    MainActivity.getMainActivity().openDialog(
                            R.drawable.ic_launcher_question,
                            "Доступна новая версия клиента!\nЗагрузить обновление?",
                            "Да",
                            "Нет",
                            new downloadApk(api.getLauncherUrl(), api.getLauncherPath(), api.getLauncherName()),
                            new noUpdate(attempt, api, apiService)
                    );
                    return;
                }
                continueWithApi(attempt, api, apiService);
            });
        } catch (RuntimeException exception) {
            stopStartupLoadWithIssue(attempt, "API config", exception);
        }
    }

    private void continueWithApi(long attempt, Api api, Interface apiService) {
        if (!isCurrentAttempt(attempt)) {
            return;
        }

        MainActivity.testApi = !api.getIsTest() || api.getTestApi();
        if (!MainActivity.testApi) {
            Toast.makeText(
                    MainActivity.getMainActivity(),
                    "Тестовая версия клиента закрыта. Используется фиксированный сервер.",
                    Toast.LENGTH_LONG
            ).show();
        }

        Lists.archives.clear();
        if (api.getArchives() != null) {
            Lists.archives.addAll(api.getArchives());
        }
        Lists.deleted.clear();
        if (api.getDeleted() != null) {
            Lists.deleted.addAll(api.getDeleted());
        }
        Lists.launcher_dan = new String[]{
                api.getLauncherUrl(),
                api.getLauncherPath(),
                api.getLauncherName()
        };

        if (isBlank(api.getApi())) {
            stopStartupLoadWithIssue(attempt, "Main config link", null);
            return;
        }
        try {
            enqueueStartupCall(attempt, "Main config", apiService.getMain(api.getApi()), main -> {
                Lists.createCharacterUrl = main.getCreateCharacter();
                Lists.verifyAuthUrl = main.getVerifyAuth();
                Lists.accountDetailsUrl = main.getAccountDetails();
                Lists.isAccUrl = main.getIsAcc();
                Lists.skinsCDNUrl = main.getSkinsCDN();

                if (isBlank(main.getStories())) {
                    Log.w("Startup", "Stories URL is missing; continuing with the fixed server.");
                } else {
                    requestStories(attempt, apiService, main.getStories());
                }
                if (isBlank(main.getFaq())) {
                    Log.w("Startup", "FAQ URL is missing; continuing with the fixed server.");
                } else {
                    requestFaq(attempt, apiService, main.getFaq());
                }
            });
        } catch (RuntimeException exception) {
            stopStartupLoadWithIssue(attempt, "Main config", exception);
        }
    }

    private void requestStories(long attempt, Interface apiService, String link) {
        try {
            enqueueStartupCall(attempt, "Stories", apiService.getStories(link), news -> {
                Lists.nlist.clear();
                for (News item : news) {
                    if (item != null) {
                        Lists.nlist.add(new News(
                                item.getImageUrl(),
                                item.getTitle(),
                                item.getTitleBig(),
                                item.getUrl(),
                                item.getImageFullUrl()
                        ));
                    }
                }
                MainActivity activity = MainActivity.getMainActivity();
                if (activity != null && activity.mainFragment != null && activity.mainFragment.newsAdapter != null) {
                    activity.mainFragment.newsAdapter.notifyDataSetChanged();
                }
            });
        } catch (RuntimeException exception) {
            stopStartupLoadWithIssue(attempt, "Stories", exception);
        }
    }

    private void requestFaq(long attempt, Interface apiService, String link) {
        try {
            enqueueStartupCall(attempt, "FAQ", apiService.getFaqList(link), faqResponse -> {
                Lists.faqlist.clear();
                if (faqResponse.getArray() != null) {
                    Lists.faqlist.addAll(faqResponse.getArray());
                }
            });
        } catch (RuntimeException exception) {
            stopStartupLoadWithIssue(attempt, "FAQ", exception);
        }
    }

    private interface StartupResponse<T> {
        void onResponse(T body);
    }

    private <T> void enqueueStartupCall(long attempt, String stage, Call<T> call, StartupResponse<T> handler) {
        if (!isCurrentAttempt(attempt)) {
            call.cancel();
            return;
        }
        startupCalls.add(call);
        pendingStartupRequests++;
        try {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    startupCalls.remove(call);
                    pendingStartupRequests = Math.max(0, pendingStartupRequests - 1);
                    if (!isCurrentAttempt(attempt)) {
                        return;
                    }
                    if (!response.isSuccessful() || response.body() == null) {
                        stopStartupLoadWithIssue(
                                attempt,
                                stage + " HTTP " + response.code(),
                                null
                        );
                        return;
                    }
                    try {
                        handler.onResponse(response.body());
                    } catch (RuntimeException exception) {
                        stopStartupLoadWithIssue(attempt, stage, exception);
                        return;
                    }
                    finishStartupLoadIfIdle(attempt);
                }

                @Override
                public void onFailure(Call<T> call, Throwable error) {
                    startupCalls.remove(call);
                    pendingStartupRequests = Math.max(0, pendingStartupRequests - 1);
                    if (isCurrentAttempt(attempt)) {
                        stopStartupLoadWithIssue(attempt, stage, error);
                    }
                }
            });
        } catch (RuntimeException exception) {
            startupCalls.remove(call);
            pendingStartupRequests = Math.max(0, pendingStartupRequests - 1);
            stopStartupLoadWithIssue(attempt, stage, exception);
        }
    }

    private void finishStartupLoadIfIdle(long attempt) {
        if (isCurrentAttempt(attempt) && pendingStartupRequests == 0 && !waitingForUpdateDecision) {
            startupInProgress = false;
            cancelStartupWatchdog();
        }
    }

    private void startStartupWatchdog(long attempt) {
        cancelStartupWatchdog();
        startupWatchdog = () -> {
            if (isCurrentAttempt(attempt)) {
                stopStartupLoadWithIssue(
                        attempt,
                        "Startup watchdog",
                        new TimeoutException("Startup data load exceeded 20 seconds")
                );
            }
        };
        startupHandler.postDelayed(startupWatchdog, STARTUP_WATCHDOG_MS);
    }

    private void cancelStartupWatchdog() {
        if (startupWatchdog != null) {
            startupHandler.removeCallbacks(startupWatchdog);
            startupWatchdog = null;
        }
    }

    private void stopStartupLoadWithIssue(long attempt, String stage, Throwable error) {
        if (!isCurrentAttempt(attempt)) {
            return;
        }
        Log.w("Startup", stage + " failed; continuing with the fixed server.", error);
        stopStartupLoad();
        if (!startupIssueShown) {
            startupIssueShown = true;
            MainActivity activity = MainActivity.getMainActivity();
            if (activity != null) {
                Toast.makeText(
                        activity,
                        "تعذر تحميل بعض البيانات. يمكنك استخدام السيرفر الثابت.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void stopStartupLoad() {
        startupInProgress = false;
        waitingForUpdateDecision = false;
        cancelStartupWatchdog();
        for (Call<?> call : new ArrayList<>(startupCalls)) {
            call.cancel();
        }
        startupCalls.clear();
        pendingStartupRequests = 0;
    }

    private boolean isCurrentAttempt(long attempt) {
        return startupInProgress && startupAttempt == attempt;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public void show() {
        viewGroup.clearAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.setAlpha(1.0f);
        viewGroup.animate().setDuration(0L).start();
        m();
    }

    public void hide() {
        ee eVar = this.panzto;
        if (eVar != null) {
            eVar.eeB = null;
            eVar.eeC = null;
            eVar.a();
            this.panzto = null;
        }
        splash_logo.clearAnimation();
        splash_logo.setScaleX(1.0f);
        splash_logo.setScaleY(1.0f);
        splash_logo.setTranslationY(0.0f);
        splash_logo.animate().setDuration(150L).scaleX(0.0f).scaleY(0.0f).translationY(this.splash_logo.getHeight()).start();
        viewGroup.clearAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.setAlpha(1.0f);
        viewGroup.animate().alpha(0.0f).setDuration(150L).start();
    }

    public class onDes implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            MainActivity.getMainActivity().dialogFragment.hide();
            MainActivity.getMainActivity().onDestroy();
            loadJsons();
        }
    }

    public static final class ee {

        public float mFloat1;
        public float mFloat;
        public long mLong;
        public float mFloat2;
        public long Long;
        public boolean mBool;
        public c eeC;
        public b eeB;
        public TimeInterpolator mTimeInterpolator;
        public Handler mHandler = new Handler();

        public class EEE implements Runnable {
            public EEE() {
            }

            public final void run() {
                ee.this.e();
            }
        }

        public interface b {
            void a();

            void b();
        }

        public interface c {
            void a(ee eVar);
        }

        public ee(float f10, float f11) {
            this.mFloat1 = f10;
            this.mFloat = f11;
            this.mLong = 1000;
            this.mFloat2 = 0.0f;
            this.mBool = false;
            this.mTimeInterpolator = new LinearInterpolator();
        }

        public final void a() {
            if (this.mBool) {
                this.mBool = false;
                this.mHandler.removeCallbacksAndMessages((Object) null);
                b bVar = this.eeB;
                if (bVar != null) {
                    bVar.a();
                }
            }
        }

        public final float b() {
            float f10 = this.mFloat1;
            return e(this.mFloat, this.mFloat1, this.mTimeInterpolator.getInterpolation(this.mFloat2), f10);
        }
        public static float e(float f10, float f11, float f12, float f13) {
            return ((f10 - f11) * f12) + f13;
        }

        public final void c() {
            b bVar = this.eeB;
            if (bVar != null) {
                bVar.a();
            }
        }

        public final void d() {
            if (!this.mBool) {
                this.Long = System.currentTimeMillis();
                this.mBool = true;
                b bVar = this.eeB;
                if (bVar != null) {
                    bVar.b();
                }
                e();
            }
        }

        public final void e() {
            if (this.mBool) {
                float currentTimeMillis = ((float) (System.currentTimeMillis() - this.Long)) / ((float) this.mLong);
                if (currentTimeMillis >= 1.0f) {
                    this.mFloat2 = 1.0f;
                    this.mBool = false;
                } else {
                    this.mFloat2 = currentTimeMillis;
                    this.mHandler.post(new EEE());
                }
                c cVar = this.eeC;
                if (cVar != null) {
                    cVar.a(this);
                }
                if (!this.mBool) {
                    c();
                }
            }
        }
    }

    //

    public class d implements ee.c {
        public d() {
        }

        public final void a(ee eVar) {
            float f11;
            float floatValue = Float.valueOf(eVar.b()).floatValue();
            float f10 = (floatValue - 1.3f) + 1.0f;
            if (floatValue <= 1.0f) {
                splash_logo.setScaleX(1.0f);
                splash_logo.setScaleY(1.0f);
                return;
            }
            if (floatValue <= 1.0f || floatValue > 1.15f) {
                if (floatValue > 1.15f && floatValue <= 1.3f) {
                    f10 = 1.15f - (floatValue - 1.15f);
                    splash_logo.setScaleX(f10);
                } else if (floatValue > 1.3f && floatValue <= 1.45f) {
                    f11 = (floatValue - 1.3f) + 1.0f;
                    //f10 = 1.15f - (floatValue - 1.3f);
                    splash_logo.setScaleX(f11);
                } else if (floatValue > 1.45f && floatValue <= 1.6f) {
                    f10 = 1.15f - (floatValue - 1.45f);
                    splash_logo.setScaleX(f10);
                } else {
                    return;
                }
                splash_logo.setScaleY(f10);
                return;
            }
            f11 = (floatValue - 1.0f) + 1.0f;
            splash_logo.setScaleX(f11);
            splash_logo.setScaleY(f11);
        }
    }

    public class e implements ee.b {
        public e() {
        }

        public final void a() {
            splash_logo.setScaleX(1.0f);
            splash_logo.setScaleY(1.0f);
            m();
        }

        public final void b() {
        }
    }

    public final void m() {
        ee eVar = this.panzto;
        if (eVar != null) {
            eVar.eeB = null;
            eVar.eeC = null;
            eVar.a();
            this.panzto = null;
        }
        ee eVar2 = new ee(0.0f, 1.6f);
        this.panzto = eVar2;
        eVar2.eeC = new d();
        eVar2.eeB = new e();
        eVar2.mTimeInterpolator = new LinearInterpolator();
        ee eVar3 = this.panzto;
        eVar3.mLong = 1600;
        eVar3.d();
    }

}
