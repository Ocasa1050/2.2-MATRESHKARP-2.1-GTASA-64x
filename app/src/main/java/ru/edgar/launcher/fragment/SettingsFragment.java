package ru.edgar.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.model.Archive;
import ru.edgar.launcher.model.ArchivePath;
import ru.edgar.launcher.model.Deleted;
import ru.edgar.launcher.other.Lists;
import ru.edgar.launcher.other.Utils;
import ru.edgar.space.R;
import ru.edgar.space.diagnostics.CrashLogManager;

public class SettingsFragment extends MainActivity {

    public ConstraintLayout settings_layout;
    public ImageView btn_close;
    public TextView account_not_auth_text;
    public FrameLayout account_layout;
    public ImageView account_background;
    public ImageView account_image;
    public TextView account_text;
    public FrameLayout btn_reinstall_client;
    public FrameLayout btn_reinstall_data;
    public FrameLayout btn_reinstall_data_dev;
    public FrameLayout btn_changepass;
    public FrameLayout btn_share_log;
    public TextView faq_text;
    public ConstraintLayout footer_layout;
    public FrameLayout btn_logout;
    public FirebaseAuth mAuth;

    public SettingsFragment() {
        super();
        settingsInit();
    }

    public void settingsInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_settings, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        mAuth = FirebaseAuth.getInstance();
        settings_layout = (ConstraintLayout) viewGroup.findViewById(R.id.settings_layout);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        account_not_auth_text = (TextView) viewGroup.findViewById(R.id.account_not_auth_text);
        account_layout = (FrameLayout) viewGroup.findViewById(R.id.account_layout);
        account_background = (ImageView) viewGroup.findViewById(R.id.account_background);
        account_image = (ImageView) viewGroup.findViewById(R.id.account_image);
        account_text = (TextView) viewGroup.findViewById(R.id.account_text);
        btn_reinstall_client = (FrameLayout) viewGroup.findViewById(R.id.btn_reinstall_client);
        btn_reinstall_data = (FrameLayout) viewGroup.findViewById(R.id.btn_reinstall_data);
        btn_reinstall_data_dev = (FrameLayout) viewGroup.findViewById(R.id.btn_reinstall_data_dev);
        btn_changepass = (FrameLayout) viewGroup.findViewById(R.id.btn_changepass);
        btn_share_log = (FrameLayout) viewGroup.findViewById(R.id.btn_share_log);
        faq_text = (TextView) viewGroup.findViewById(R.id.faq_text);
        footer_layout = (ConstraintLayout) viewGroup.findViewById(R.id.footer_layout);
        btn_logout = (FrameLayout) viewGroup.findViewById(R.id.btn_logout);
        btn_share_log.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_share_log));
        btn_share_log.setOnClickListener(view -> CrashLogManager.shareLog(MainActivity.getMainActivity()));
        btn_close.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener(view -> { hide(); });
        SpannableString spannableString = new SpannableString("Проблемы? Мы можем вам помочь!");
        spannableString.setSpan((Typeface.defaultFromStyle(Typeface.NORMAL)), 10, spannableString.length(), 33);
        spannableString.setSpan(new UnderlineSpan(), 10, spannableString.length(), 33);
        faq_text.setText(spannableString);
        faq_text.setOnClickListener(view -> {
            MainActivity.getMainActivity().faqFragment.show();
        });
        btn_logout.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_logout));
        btn_logout.setOnClickListener(v -> {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_exit, "Вы уверены, что хотите выйти из аккаунта?", "Да", "Нет", new AccSignOut(), new DialogFragment.closeDialog());
        });
        btn_reinstall_client.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_reinstall_client));
        btn_reinstall_client.setOnClickListener(v -> {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Вы действительно хотите переустановить клиент?", "Да", "Нет", new ReInstallClient(), new DialogFragment.closeDialog());
        });
        btn_reinstall_data.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_reinstall_data));
        btn_reinstall_data.setOnClickListener(v -> {
            MainActivity.getMainActivity().loadingFragment.show();

            List<Archive> archiveList = Lists.archives;
            List<Deleted> deletedList = Lists.deleted;

            List<String> path = new ArrayList<>();
            List<String> unZip = new ArrayList<>();
            List<String> toUnZip = new ArrayList<>();
            List<String> url = new ArrayList<>();
            long si = 0;

            for (int i = 0; deletedList.size() > i; i++) {
                Deleted deleted = deletedList.get(i);
                File f = new File(deleted.getPath());
                if (f.exists()) {
                    if (f.isDirectory()) {
                        MainFragment.deleteDirectory(f);
                    } else if (f.isFile()) {
                        f.delete();
                    }
                }
            }

            for (int i = 0; archiveList.size() > i; i++) {
                Archive archive = archiveList.get(i);
                long size = 0;
                for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                    ArchivePath archivePaths = archive.getPaths().get(i1);
                    size = size + MainActivity.getMainActivity().mainFragment.getFileOrDirectorySize(archivePaths.getPath());
                }
                for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                    ArchivePath archivePaths = archive.getPaths().get(i1);
                    path.add(archivePaths.getPath());
                }
                toUnZip.add(archive.getZip_path());
                unZip.add(archive.getType());
                url.add(archive.getUrls());
                si = si + archive.getSize();

            }
            MainActivity.getMainActivity().loadingFragment.hide();
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Вы действительно хотите загрузить " + Utils.bytesIntoHumanReadable(si) + "?", "Да", "Нет", new DownloadStart(url, path, unZip, toUnZip), new DialogFragment.closeDialog());

        });
        upSettings();
        btn_reinstall_data_dev.setVisibility(View.GONE);
        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point point = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
        settings_layout.clearAnimation();
        settings_layout.setTranslationY(point.y);
        settings_layout.animate().setDuration(300L).translationY(0.0f).start();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point point = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
        mHandler.postDelayed(new ssetVisibility(), 300L);
        settings_layout.clearAnimation();
        settings_layout.setTranslationY(0.0f);
        settings_layout.animate().setDuration(300L).translationY(point.y).start();
    }

    public void upSettings() {
        if (isAuth) {
            FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email_g = snapshot.child("google-email").getValue(String.class);
                    email_google = email_g;
                    String email_e = snapshot.child("email").getValue(String.class);
                    email = email_e;
                    String first_name = snapshot.child("first_name").getValue(String.class);
                    MainActivity.first_name = first_name;
                    String last_name = snapshot.child("last_name").getValue(String.class);
                    MainActivity.last_name = last_name;
                    Integer paramInt = snapshot.child("way").getValue(Integer.class);
                    way = paramInt;
                    Log.i("edgar", "way = " + way);
                    UpdateSettings();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            email_google = null;
            way = null;
            UpdateSettings();
        }
    }

    public void UpdateSettings() {
        if (isAuth) {
            footer_layout.setVisibility(View.VISIBLE);
            btn_changepass.setVisibility(View.GONE);
            if (way == 3) {
                account_not_auth_text.setVisibility(View.GONE);
                account_layout.setVisibility(View.VISIBLE);
                account_image.setVisibility(View.VISIBLE);
                account_layout.setOnTouchListener(null);
                account_layout.setOnClickListener(null);
                account_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                account_text.setText(first_name + " " + last_name);
                account_background.setImageResource(R.drawable.auth_bg_vk);
                account_image.setImageResource(R.drawable.ic_launcher_vk);
            } else if (way == 2) {
                account_not_auth_text.setVisibility(View.GONE);
                account_layout.setVisibility(View.VISIBLE);
                account_image.setVisibility(View.VISIBLE);
                account_layout.setOnTouchListener(null);
                account_layout.setOnClickListener(null);
                account_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                account_text.setText(email_google);
                account_background.setImageResource(R.drawable.auth_bg_google);
                account_image.setImageResource(R.drawable.ic_launcher_google);
            } else if (way == 1) {
                btn_changepass.setVisibility(View.VISIBLE);
                account_not_auth_text.setVisibility(View.GONE);
                account_layout.setVisibility(View.VISIBLE);
                account_image.setVisibility(View.VISIBLE);
                account_layout.setOnTouchListener(null);
                account_layout.setOnClickListener(null);
                account_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                account_text.setText(email);
                account_background.setImageResource(R.drawable.auth_bg_email);
                account_image.setImageResource(R.drawable.ic_launcher_mail);
            }
        } else {
            footer_layout.setVisibility(View.GONE);
            btn_changepass.setVisibility(View.GONE);
            account_not_auth_text.setVisibility(View.VISIBLE);
            account_layout.setVisibility(View.VISIBLE);
            account_image.setVisibility(View.GONE);
            account_layout.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), account_layout));
            account_layout.setOnClickListener(v -> {
                hide();
                MainActivity.getMainActivity().authFragment.show();
            });
            account_background.setImageResource(R.drawable.auth_bg_button);
            account_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            account_text.setText("Авторизоваться");
        }
    }

    public class AccSignOut implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mAuth.signOut();
            MainActivity.isAuth = false;
            MainActivity.getMainActivity().mainFragment.upServerId();// EDGAR 3.0 newLauncher version от 06.01.2024
            MainActivity.getMainActivity().settingsFragment.hide();
            MainActivity.getMainActivity().cabinetFragment.hide();
            MainActivity.getMainActivity().mainFragment.show();
            MainActivity.getMainActivity().dialogFragment.hide();
            upSettings();
        }
    }

    public class ReInstallClient implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MainActivity.getMainActivity().dialogFragment.hide();// EDGAR 3.0 newLauncher version от 21.03.2024
            MainActivity.getMainActivity().settingsFragment.hide();
            MainActivity.getMainActivity().downloadFragment.startDownloadApk(Lists.launcher_dan[0], Lists.launcher_dan[1], Lists.launcher_dan[2]);
        }
    }

    public class DownloadStart implements View.OnClickListener { // EDGAR 3.0 newLauncher version от 21.03.2024
        List<String> path = null;
        List<String> unZip = null;
        List<String> toUnZip = null;
        List<String> url = null;

        public DownloadStart(List<String> url, List<String> path, List<String> unZip, List<String> toUnZip) {
            this.url = url;
            this.path = path;
            this.unZip = unZip;
            this.toUnZip = toUnZip;
        }

        @Override
        public void onClick(View v) {
            MainActivity.getMainActivity().dialogFragment.hide();
            MainActivity.getMainActivity().downloadFragment.startDownload(url, path, unZip, toUnZip);
            //TODO загрузка
        }
    }

    public class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }
}
