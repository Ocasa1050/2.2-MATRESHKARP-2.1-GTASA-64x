package ru.edgar.launcher.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.adapter.NewsAdapter;
import ru.edgar.launcher.model.Archive;
import ru.edgar.launcher.model.ArchivePath;
import ru.edgar.launcher.model.Deleted;
import ru.edgar.launcher.model.FixedServer;
import ru.edgar.launcher.model.FilesList;
import ru.edgar.launcher.model.Servers;
import ru.edgar.launcher.model.edgar;
import ru.edgar.launcher.other.Interface;
import ru.edgar.launcher.other.Lists;
import ru.edgar.launcher.network.ApiClient;
import ru.edgar.launcher.other.Utils;
import ru.edgar.space.R;
import ru.edgar.space.SAMP;

public class MainFragment extends MainActivity {

    public static ArrayList<FilesList> filesListArrayList = new ArrayList<>();
    public static ArrayList<FilesList> filesListArrayList2 = new ArrayList<>();
    public static int reyti_edgar = 0;
    public NewsAdapter newsAdapter;
    public LinearLayout header_layout;
    public LinearLayout story_layout;
    public ConstraintLayout server_layout;
    public ConstraintLayout social_layout;
    public CardView bonus_layout;
    public ConstraintLayout footer_layout;
    public RecyclerView story_recycler;
    public ImageView btn_settings;
    public ImageView btn_faq;
    public FrameLayout select_server_layout;
    public ImageView server_background;
    public ImageView server_item_image;
    public LinearLayout select_layout;
    public LinearLayout serverinfo_layout;
    public LinearLayout server_alert;
    public CardView serverinfo_person_card;
    public TextView serverinfo_online;
    public TextView serverinfo_name;
    public TextView serverinfo_person_text;
    public TextView serverinfo_person_name;
    public FrameLayout btn_bonus;
    public ImageView btn_social_vk;
    public ImageView btn_social_youtube;
    public ImageView btn_social_telegram;
    public ImageView btn_social_media;
    public FrameLayout btn_cabinet;
    public FrameLayout btn_play;
    public Long size = 0L;
    public ArrayList<String> url = new ArrayList<>();
    public ArrayList<String> paths = new ArrayList<>();

    public boolean isTo = true;
    private final ExecutorService gamePreparationExecutor = Executors.newSingleThreadExecutor();
    private final Handler playRequestHandler = new Handler(Looper.getMainLooper());

    public MainFragment() {
        super();
        mainInit();
    }

    public void mainInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_main, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        header_layout = (LinearLayout) viewGroup.findViewById(R.id.header_layout);
        story_layout = (LinearLayout) viewGroup.findViewById(R.id.story_layout);
        server_layout = (ConstraintLayout) viewGroup.findViewById(R.id.server_layout);
        social_layout = (ConstraintLayout) viewGroup.findViewById(R.id.social_layout);
        bonus_layout = (CardView) viewGroup.findViewById(R.id.bonus_layout);
        footer_layout = (ConstraintLayout) viewGroup.findViewById(R.id.footer_layout);
        story_recycler = (RecyclerView) viewGroup.findViewById(R.id.story_recycler);
        btn_settings = (ImageView) viewGroup.findViewById(R.id.btn_settings);
        btn_faq = (ImageView) viewGroup.findViewById(R.id.btn_faq); // newLauncher делал эдгар / EDGAR 3.0
        server_alert = (LinearLayout) viewGroup.findViewById(R.id.server_alert);
        select_server_layout = (FrameLayout) viewGroup.findViewById(R.id.select_server_layout);
        server_background = (ImageView) viewGroup.findViewById(R.id.server_background);
        server_item_image = (ImageView) viewGroup.findViewById(R.id.server_item_image);
        select_layout = (LinearLayout) viewGroup.findViewById(R.id.select_layout);
        serverinfo_layout = (LinearLayout) viewGroup.findViewById(R.id.serverinfo_layout);
        serverinfo_online = (TextView) viewGroup.findViewById(R.id.serverinfo_online);
        serverinfo_name = (TextView) viewGroup.findViewById(R.id.serverinfo_name);
        serverinfo_person_card = (CardView) viewGroup.findViewById(R.id.serverinfo_person_card);
        serverinfo_person_text = (TextView) viewGroup.findViewById(R.id.serverinfo_person_text);
        serverinfo_person_name = (TextView) viewGroup.findViewById(R.id.serverinfo_person_name);
        btn_bonus = (FrameLayout) viewGroup.findViewById(R.id.btn_bonus);
        btn_social_vk = (ImageView) viewGroup.findViewById(R.id.btn_social_vk);
        btn_social_youtube = (ImageView) viewGroup.findViewById(R.id.btn_social_youtube);
        btn_social_telegram = (ImageView) viewGroup.findViewById(R.id.btn_social_telegram);
        btn_social_media = (ImageView) viewGroup.findViewById(R.id.btn_social_media);
        btn_cabinet = (FrameLayout) viewGroup.findViewById(R.id.btn_cabinet);
        btn_play = (FrameLayout) viewGroup.findViewById(R.id.btn_play);

        story_recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.getMainActivity(), LinearLayoutManager.HORIZONTAL, false);
        story_recycler.setLayoutManager(layoutManager);

        newsAdapter = new NewsAdapter(MainActivity.getMainActivity(), Lists.nlist);
        story_recycler.setAdapter(newsAdapter);

        btn_settings.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_settings));

        btn_settings.setOnClickListener( view -> { MainActivity.getMainActivity().settingsFragment.show(); });

        btn_faq.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_faq));

        btn_faq.setOnClickListener( view -> { MainActivity.getMainActivity().faqFragment.show(); });

        btn_cabinet.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_cabinet));

        btn_cabinet.setOnClickListener( view -> {
            if(!MainActivity.isAuth) {
                MainActivity.getMainActivity().authFragment.show();
            }else {
                //MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "В данный момент вы уже зашли на аккаунт", "Понятно", null, new DialogFragment.closeDialog(), null);
                MainActivity.getMainActivity().mainFragment.hide();
                MainActivity.getMainActivity().cabinetFragment.show();
            }
        });

        btn_social_vk.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_social_vk));

        btn_social_vk.setOnClickListener(view -> {
            MainActivity.getMainActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/edgar_sliv")));
        });

        btn_social_youtube.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_social_youtube));

        btn_social_youtube.setOnClickListener(view -> {
            MainActivity.getMainActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/@EDGAR3.0")));
            /*String url = "jdbc:mysql://164.132.206.179:3306/gs242777";
            String username = "gs242777";
            String password = "jMkeYrkkUDw8";

            System.out.println("Connecting database ...");

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Database connected!");
            } catch (SQLException e) {
                throw new IllegalStateException("Cannot connect the database!", e);
            }*/
        });

        btn_social_telegram.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_social_telegram));

        btn_social_telegram.setOnClickListener(view -> {
            MainActivity.getMainActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/edgar_sliv")));
        });

        btn_play.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_play));

        btn_play.setOnClickListener(view -> {
            if(isAuth) {
                if (server_id != null) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_" + MainActivity.server_id).child(FirebaseAuth.getInstance().getUid()).child("nick").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.e("edgar", "pon" + snapshot.getValue(String.class));
                            if (snapshot.getValue(String.class) == null) {
                                MainActivity.getMainActivity().createCharacterFragment.show();
                                hide();
                            } else {
                                MainActivity.nickName = snapshot.getValue(String.class);
                                //MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), SAMP.class));
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
                                            deleteDirectory(f);
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
                                        size = size + getFileOrDirectorySize(archivePaths.getPath());
                                    }
                                    System.out.println(size + " вес локал");
                                    if (archive.getSize() == size) {
                                        System.out.println(archive.getSize() + " == " + size);
                                        System.out.println("Все ровно");
                                    } else {
                                        for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                                            ArchivePath archivePaths = archive.getPaths().get(i1);
                                            path.add(archivePaths.getPath());
                                        }
                                        toUnZip.add(archive.getZip_path());
                                        unZip.add(archive.getType());
                                        url.add(archive.getUrls());
                                        si = si + archive.getSize();
                                        System.out.println(si);
                                    }
                                }

                                if (url.size() != 0) {
                                    MainActivity.getMainActivity().loadingFragment.hide();
                                    MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Доступно обновление!\nЗагрузить " + Utils.bytesIntoHumanReadable(si) + "?", "Да", "Нет", new DownloadStart(url, path, unZip, toUnZip), new DialogFragment.closeDialog());
                                } else {
                                    MainActivity.getMainActivity().loadingFragment.hide();
                                    MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), SAMP.class));
                                }


                                //TODO тут проверка на кеш!!!!!!!!! и переход в download fragment
                                /*Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://api-free.edgars.site/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                MainActivity.getMainActivity().loadingFragment.show();

                                Interface sInterface = retrofit.create(Interface.class);

                                System.out.println(Lists.filesListUrl);

                                Call<List<FilesList>> call = sInterface.getFilesList(Lists.filesListUrl);

                                call.enqueue(new Callback<List<FilesList>>() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onResponse(Call<List<FilesList>> call, Response<List<FilesList>> response) {

                                        List<FilesList> filesLists = response.body();

                                        for (FilesList filesList : filesLists) {
                                            filesListArrayList.add(new FilesList(filesList.getName(), filesList.getSize(), filesList.getHash(), filesList.getPath(), filesList.getUrl()));
                                            size += Integer.parseInt(filesList.getSize());
                                            System.out.println(size);
                                        }
                                        File directory = new File(Environment.getExternalStorageDirectory() + "/Test_Test");
                                        System.out.println("ponnnnn listFilesRecursively");
                                        List<FilesList> fileInfoList = listFilesRecursively(directory);
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        String json = gson.toJson(fileInfoList);
                                        System.out.println(json);
                                        File file = new File(Environment.getExternalStorageDirectory() + "/spacb/file.txt");

                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        String json = gson.toJson(fileInfoList);
                                        System.out.println(json);
                                        try {
                                            FileWriter writer = new FileWriter(file);

                                            writer.write(json);

                                            writer.flush();
                                            writer.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (fileInfoList.size() == 0) {
                                            url.clear();
                                            paths.clear();
                                            new File(Environment.getExternalStorageDirectory() + "/Edgar").delete();
                                            for (FilesList info : filesListArrayList) {
                                                url.add(info.getUrl());
                                                paths.add(info.getPath());
                                            }
                                            MainActivity.getMainActivity().loadingFragment.hide();
                                            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Доступно обновление!\nЗагрузить " + Utils.bytesIntoHumanReadable(size) + "?", "Да", "Нет", new DownloadStart(), new DialogFragment.closeDialog());
                                        } else {
                                            System.out.println("ponnnnn fileInfoList");
                                            compareFiles(filesListArrayList, fileInfoList);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<FilesList>> call, Throwable t) {
                                        System.out.println("Xynia");
                                    }
                                });*/
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Xynia");
                        }
                    });

                } else {
                    MainActivity.getMainActivity().serverSelectFragment.show();
                }
            } else {
                beginServerEntry();
            }
        });

        // Use the non-blocking fixed-server launch path instead of the legacy callback chain above.
        btn_play.setOnClickListener(view -> beginServerEntry());

        select_server_layout.setOnClickListener(null);
        select_server_layout.setClickable(false);

        bonus_layout.setVisibility(View.GONE);
        viewGroup.setVisibility(View.GONE);
    }

    private void beginServerEntry() {
        MainActivity activity = MainActivity.getMainActivity();
        if (activity == null) {
            return;
        }

        if (MainActivity.server_id == null) {
            MainActivity.server_id = FixedServer.DEFAULT_ID;
        }
        setFixedServerId(MainActivity.server_id);

        FirebaseUser user = null;
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
        } catch (RuntimeException error) {
            Log.w("MainFragment", "Firebase sign-in unavailable; using guest mode.", error);
        }
        if (user == null) {
            MainActivity.isAuth = false;
            activity.loadingFragment.show();
            startGamePreparation(getOrCreateGuestNickname());
            return;
        }

        MainActivity.isAuth = true;

        DatabaseReference nicknameReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child("User-servers")
                .child("Server_" + MainActivity.server_id)
                .child(user.getUid())
                .child("nick");

        activity.loadingFragment.show();
        final boolean[] requestPending = {true};
        final Runnable[] timeout = new Runnable[1];
        ValueEventListener nicknameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!requestPending[0]) {
                    return;
                }
                requestPending[0] = false;
                playRequestHandler.removeCallbacks(timeout[0]);

                String nickname = snapshot.getValue(String.class);
                if (nickname == null || nickname.trim().isEmpty()) {
                    startGamePreparation(getOrCreateGuestNickname());
                    return;
                }

                startGamePreparation(nickname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!requestPending[0]) {
                    return;
                }
                requestPending[0] = false;
                playRequestHandler.removeCallbacks(timeout[0]);
                Log.w("MainFragment", "Nickname lookup failed", error.toException());
                startGamePreparation(getOrCreateGuestNickname());
            }
        };

        timeout[0] = () -> {
            if (!requestPending[0]) {
                return;
            }
            requestPending[0] = false;
            nicknameReference.removeEventListener(nicknameListener);
            Log.w("MainFragment", "Nickname lookup timed out; continuing as guest.");
            startGamePreparation(getOrCreateGuestNickname());
        };
        playRequestHandler.postDelayed(timeout[0], 15_000L);
        nicknameReference.addListenerForSingleValueEvent(nicknameListener);
    }

    private String getOrCreateGuestNickname() {
        SharedPreferences preferences = MainActivity.getMainActivity()
                .getSharedPreferences("guest_profile", Context.MODE_PRIVATE);
        String nickname = preferences.getString("nickname", null);
        if (nickname == null || !nickname.matches("(?i)Guest_[a-f0-9]{6}")) {
            String suffix = UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 6).toUpperCase(Locale.US);
            nickname = "Guest_" + suffix;
            preferences.edit().putString("nickname", nickname).apply();
        }
        return nickname;
    }

    private void startGamePreparation(String nickname) {
        MainActivity.nickName = nickname == null || nickname.trim().isEmpty()
                ? getOrCreateGuestNickname()
                : nickname.trim();
        if (serverinfo_layout != null) {
            UpdateServers();
        }

        ArrayList<Archive> archives = Lists.archives == null
                ? new ArrayList<>()
                : new ArrayList<>(Lists.archives);
        ArrayList<Deleted> deletedFiles = Lists.deleted == null
                ? new ArrayList<>()
                : new ArrayList<>(Lists.deleted);
        gamePreparationExecutor.execute(() -> prepareGameFilesAndLaunch(archives, deletedFiles));
    }

    private void prepareGameFilesAndLaunch(ArrayList<Archive> archives, ArrayList<Deleted> deletedFiles) {
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> unzipTypes = new ArrayList<>();
        ArrayList<String> zipPaths = new ArrayList<>();
        ArrayList<String> downloadUrls = new ArrayList<>();
        long downloadSize = 0;

        try {
            for (Deleted deleted : deletedFiles) {
                if (deleted == null || deleted.getPath() == null) {
                    continue;
                }
                File file = new File(deleted.getPath());
                if (file.exists()) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }

            for (Archive archive : archives) {
                if (archive == null || archive.getPaths() == null) {
                    continue;
                }
                long localSize = 0;
                for (ArchivePath archivePath : archive.getPaths()) {
                    if (archivePath != null && archivePath.getPath() != null) {
                        localSize += getFileOrDirectorySize(archivePath.getPath());
                    }
                }
                if (archive.getSize() == localSize) {
                    continue;
                }

                String archiveUrl = archive.getUrls();
                if (archiveUrl == null || archiveUrl.trim().isEmpty()) {
                    continue;
                }
                for (ArchivePath archivePath : archive.getPaths()) {
                    if (archivePath != null && archivePath.getPath() != null) {
                        paths.add(archivePath.getPath());
                    }
                }
                downloadUrls.add(archiveUrl);
                unzipTypes.add(archive.getType());
                zipPaths.add(archive.getZip_path());
                downloadSize += archive.getSize();
            }
        } catch (Exception error) {
            Log.e("MainFragment", "Game file validation failed", error);
            MainActivity activity = MainActivity.getMainActivity();
            activity.runOnUiThread(() -> {
                activity.loadingFragment.hide();
                Toast.makeText(activity, "Не удалось проверить файлы игры.", Toast.LENGTH_LONG).show();
            });
            return;
        }

        final long totalDownloadSize = downloadSize;
        MainActivity activity = MainActivity.getMainActivity();
        activity.runOnUiThread(() -> {
            activity.loadingFragment.hide();
            if (!downloadUrls.isEmpty()) {
                activity.dialogFragment.show(
                        R.drawable.ic_launcher_question,
                        "Доступно обновление!\nЗагрузить " + Utils.bytesIntoHumanReadable(totalDownloadSize) + "?",
                        "Да",
                        "Нет",
                        new DownloadStart(downloadUrls, paths, unzipTypes, zipPaths),
                        new DialogFragment.closeDialog()
                );
            } else {
                activity.startActivity(new Intent(activity, SAMP.class));
            }
        });
    }

    private void setFixedServerId(Integer id) {
        if (Lists.slist == null) {
            Lists.slist = new ArrayList<>();
        }
        Lists.slist.clear();
        Lists.slist.add(FixedServer.create(id));
        if (MainActivity.getMainActivity().serverSelectFragment != null) {
            MainActivity.getMainActivity().serverSelectFragment.refreshServers();
        }
    }

    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
        System.out.println("Директория успешно удалена.");
    }

    public static long getFileOrDirectorySize(String path) {
        File file = new File(path);

        if (file.isFile()) {
            System.out.println("Это файл: " + path);
            return file.length();
        } else if (file.isDirectory()) {
            System.out.println("Это директория: " + path);
            return calculateDirectorySize(file);
        } else {
            System.out.println("Указанный путь не является ни файлом, ни директорией.");
            return 0;
        }
    }

    private static long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += calculateDirectorySize(file);
                }
            }
        }
        return size;
    }

    public static long calculateTotalSizeOfFiles(String[] paths) {
        long totalSize = 0;

        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                totalSize += file.length();
            }
        }

        return totalSize;
    }

    public static List<File> listSubDirectories(File directory) {
        List<File> subDirectories = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    subDirectories.add(file); // Добавить директорию в список
                    subDirectories.addAll(listSubDirectories(file)); // Рекурсивно вызвать метод для поддиректории
                }
            }
        }
        return subDirectories;
    }

    public static long calculateTotalSizeOfFolders(String[] paths) {
        long totalSize = 0;

        for (String path : paths) {
            File folder = new File(path);
            if (folder.exists() && folder.isDirectory()) {
                totalSize += calculateFolderSize(folder);
            }
        }

        return totalSize;
    }

    public static long calculateFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length(); // Добавить размер файла к общему размеру папки
                } else {
                    size += calculateFolderSize(file); // Рекурсивно вызвать метод для подпапки
                }
            }
        }
        return size;
    }

    public class DownloadStart implements View.OnClickListener {
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

    @SuppressLint("NewApi") //TODO от Эдгара не рабочия не юзай
    public void compareFiles(List<FilesList> firstList, List<FilesList> secondList) {
        isTo = true;
        size = 0L;
        url.clear();
        paths.clear();
        if (secondList.size() >= firstList.size()) {
            for (int i = 0; i < secondList.size(); i++) {
                FilesList localFiles = secondList.get(i);
                FilesList info = firstList.get(i);

                // Эдгар соска я бы его .... Дальше сам додумай by EDGAR 3.0
                System.out.println("инфо path + " + info.getPath());
                System.out.println("localFiles.getPath() + " + localFiles.getPath());
                if (info.getPath().equals(localFiles.getPath())) {
                    if (!info.getSize().equals(localFiles.getSize()) || !info.getHash().equals(localFiles.getHash())) {
                        System.out.println("Файл " + localFiles.getPath() + " не совпадает.");
                        size = size + Integer.parseInt(info.getSize());
                        url.add(info.getUrl());
                        paths.add(info.getPath());
                        new File(localFiles.getPath()).delete();
                        isTo = false;
                    }
                } else {
                    System.out.println("Файл " + localFiles.getPath() + " не совпадает с " + info.getPath() + ".");
                    url.add(info.getUrl());
                    paths.add(info.getPath());
                    size += Integer.parseInt(info.getSize());
                    new File(localFiles.getPath()).delete();
                    isTo = false;
                }
            }
            if (!isTo) {
                MainActivity.getMainActivity().loadingFragment.hide();
                //MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Доступно обновление!\nЗагрузить " + Utils.bytesIntoHumanReadable(size) + "?", "Да", "Нет", new DownloadStart(), new DialogFragment.closeDialog());
            } else {
                MainActivity.getMainActivity().loadingFragment.hide();
                System.out.println("ПРОШЛОт ке");
                //MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), GTASA.class));
            }
        }
    }

    public static List<FilesList> listFilesRecursively(File directory) {
        List<FilesList> fileInfoList = new ArrayList<>();
        listFilesRecursively(directory, fileInfoList);
        return fileInfoList;
    }

    private static void listFilesRecursively(File directory, List<FilesList> fileInfoList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFilesRecursively(file, fileInfoList);
                } else {
                    if (file.length() > 24315616) {
                        System.out.println("Файл пропущен: " + file.getName() + " по причине большого размера");
                        String resultString = file.getPath().replace("/storage/emulated/0/Test_Test", "");
                        FilesList fileInfo = new FilesList(file.getName(), Long.toString(file.length()), "0", file.getAbsolutePath(), "https://cdn-space.spacerp.ru/files/build2" + resultString);
                        fileInfoList.add(fileInfo);
                    } else {
                        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = raf.read(buffer)) != -1) {
                                // Ваши операции обработки данных
                                // Например, можно обрабатывать содержимое файла по частям
                            }
                            String hash = calculateHash(buffer);  // Вычисление хэша из буфера
                            String resultString = file.getPath().replace("/storage/emulated/0/Test_Test", "");
                            System.out.println(hash);
                            FilesList fileInfo = new FilesList(file.getName(), Long.toString(file.length()), hash, file.getAbsolutePath(), "https://cdn-space.spacerp.ru/files/build2" + resultString);
                            fileInfoList.add(fileInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static String calculateHash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data);

            // Преобразование байтов хэша в строку
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String hashString = sb.toString();

            //System.out.println("MD5 хэш для данных: " + hashString);
            return hashString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);// newLauncher делал эдгар / EDGAR 3.0
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        header_layout.clearAnimation();
        header_layout.setAlpha(0.0f);
        header_layout.setTranslationY(-f10.y);
        header_layout.animate().setDuration(300L).alpha(1.0f).translationY(0.0f).start();
        story_layout.clearAnimation();
        story_layout.setAlpha(0.0f);
        mHandler.postDelayed(new anim1(), 150L);
       /* story_layout.setTranslationY(-f10.y); // newLauncher делал эдгар / EDGAR 3.0
        story_layout.animate().setDuration(300L).translationY(0.0f).alpha(1.0f).start();*/
        server_layout.clearAnimation();
        server_layout.setAlpha(0.0f);
        mHandler.postDelayed(new anim2(), 300L);
        /*server_layout.setTranslationY(-f10.y);
        server_layout.animate().setDuration(450L).translationY(0.0f).alpha(1.0f).start();*/
        social_layout.clearAnimation();
        social_layout.setAlpha(0.0f);
        social_layout.setTranslationY(f10.y);
        social_layout.animate().setDuration(300L).translationY(0.0f).alpha(1.0f).start();
        bonus_layout.clearAnimation();
        bonus_layout.setAlpha(0.0f);
        bonus_layout.setTranslationY(f10.y);
        bonus_layout.animate().setDuration(300L).translationY(0.0f).alpha(1.0f).start();
        footer_layout.clearAnimation();
        footer_layout.setAlpha(0.0f);
        footer_layout.setTranslationY(f10.y);
        footer_layout.animate().setDuration(300L).alpha(1.0f).translationY(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().setDuration(450L).start();
        //
        System.out.println(MainActivity.getMainActivity().auth1);
        if (MainActivity.getMainActivity().auth1 == null || MainActivity.getMainActivity().auth1.trim().isEmpty()) {
            Log.w("MainFragment", "Skipping launcher status check because its URL is unavailable.");
            return;
        }
        Retrofit retrofit = ApiClient.create("http://api-free.edgars.site/");

        Interface sInterface = retrofit.create(Interface.class);

        try {
            sInterface.getAuth(MainActivity.getMainActivity().auth1).enqueue(new Callback<edgar>() {

            public void onResponse(Call<edgar> call, Response<edgar> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w("MainFragment", "Launcher status endpoint returned no usable response.");
                    return;
                }
                String status = response.body().getAuth();
                if (status == null) {
                    Log.w("MainFragment", "Launcher status response did not include an auth status.");
                    return;
                }
                try {
                    int statusCode = Integer.parseInt(status);
                    if (statusCode == 200) {
                        Toast.makeText(MainActivity.getMainActivity(), "[EDGAR 3.0]: Лаунчер загружаеться!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("MainFragment", "Remote launcher status " + statusCode + " ignored; keeping the main screen available.");
                    }
                } catch (NumberFormatException error) {
                    Log.w("MainFragment", "Invalid launcher status value: " + status, error);
                }
            }

            @Override
            public void onFailure(Call<edgar> call, Throwable t) {
                Log.w("MainFragment", "Launcher status request failed; keeping the main screen available.", t);
            }
            });
        } catch (RuntimeException error) {
            Log.w("MainFragment", "Could not start the launcher status request.", error);
        }
        //
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        header_layout.clearAnimation();
        header_layout.setAlpha(1.0f);
        header_layout.setTranslationY(0.0f);
        header_layout.animate().setDuration(300L).translationY(-f10.y).alpha(1.0f).start();
        story_layout.clearAnimation();
        story_layout.setAlpha(1.0f);
        story_layout.setTranslationY(0.0f);
        story_layout.animate().setDuration(300L).alpha(0.0f).start();
        server_layout.clearAnimation();
        server_layout.setAlpha(1.0f);
        server_layout.setTranslationY(0.0f);
        server_layout.animate().setDuration(300L).alpha(0.0f).start();
        social_layout.clearAnimation();
        social_layout.setAlpha(1.0f);
        social_layout.setTranslationY(0.0f);
        social_layout.animate().setDuration(300L).translationY(f10.y).alpha(0.0f).start();
        bonus_layout.clearAnimation();
        bonus_layout.setAlpha(1.0f);
        bonus_layout.setTranslationY(0.0f);
        bonus_layout.animate().setDuration(300L).translationY(f10.y).alpha(0.0f).start();
        footer_layout.clearAnimation();
        footer_layout.setAlpha(1.0f);
        footer_layout.setTranslationY(0.0f);
        footer_layout.animate().setDuration(300L).translationY(f10.y).alpha(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().setDuration(300L).start();
    }

    public void upServerId() {
        if (isAuth) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                MainActivity.isAuth = false;
                server_id = FixedServer.DEFAULT_ID;
                setFixedServerId(server_id);
                MainActivity.nickName = getOrCreateGuestNickname();
                UpdateServers();
                return;
            }
            FirebaseDatabase.getInstance().getReference().child("Users").child("User-server").child(currentUser.getUid()).child("server-id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer paramInt = snapshot.getValue(Integer.class);
                    server_id = paramInt == null ? FixedServer.DEFAULT_ID : paramInt;
                    setFixedServerId(server_id);
                    Log.i("edgar", "server_id = " + server_id);
                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_" + MainActivity.server_id).child(currentUser.getUid()).child("nick").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue(String.class) == null) {
                                MainActivity.nickName = null;
                                MainActivity.getMainActivity().cabinetFragment.UpdateServers();
                                UpdateServers();
                            } else {
                                MainActivity.nickName = snapshot.getValue(String.class);
                                MainActivity.getMainActivity().cabinetFragment.UpdateServers();
                                UpdateServers();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("MainFragment", "Could not load the saved character name.", error.toException());
                            MainActivity.nickName = null;
                            UpdateServers();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("MainFragment", "Could not load the saved server ID.", error.toException());
                    server_id = FixedServer.DEFAULT_ID;
                    setFixedServerId(server_id);
                    UpdateServers();
                }
            });
        } else {
            MainActivity.nickName = getOrCreateGuestNickname();
            server_id = FixedServer.DEFAULT_ID;
            setFixedServerId(server_id);
            MainActivity.getMainActivity().cabinetFragment.UpdateServers();
            UpdateServers();
        }
    }

    public void UpdateServers() {
        if (server_id == null) {
            server_id = FixedServer.DEFAULT_ID;
        }
        Servers selectedServer = findServerById(Lists.slist, server_id);
        if (selectedServer == null) {
            server_background.setColorFilter(Color.parseColor("#FF33AAD9"));
            server_item_image.setColorFilter(Color.parseColor("#FF33AAD9"));
            select_layout.setVisibility(View.VISIBLE);
            serverinfo_layout.setVisibility(View.GONE);
            server_alert.setVisibility(View.GONE);
            return;
        }

        server_background.setColorFilter(Color.parseColor("#" + selectedServer.getColor()) - 16777216);
        server_item_image.setColorFilter(Color.parseColor("#" + selectedServer.getColor()) - 16777216);
        select_layout.setVisibility(View.GONE);
        serverinfo_layout.setVisibility(View.VISIBLE);
        serverinfo_name.setText(selectedServer.getName());
        if (!MainActivity.isAuth) {
            serverinfo_person_card.setCardBackgroundColor(-1711292128);
            serverinfo_person_text.setText("Гостевой режим");
            serverinfo_person_name.setText(MainActivity.nickName == null ? "" : MainActivity.nickName);
            serverinfo_person_name.setVisibility(MainActivity.nickName == null ? View.GONE : View.VISIBLE);
        } else if (MainActivity.nickName == null) {
            serverinfo_person_card.setCardBackgroundColor(-1711292128);
            serverinfo_person_text.setText("Нажмите \"Играть\" и создайте персонажа");
            serverinfo_person_name.setText("");
            serverinfo_person_name.setVisibility(View.GONE);
        } else {
            serverinfo_person_card.setCardBackgroundColor(-1725591005);
            serverinfo_person_text.setText("Персонаж: ");
            serverinfo_person_name.setText(MainActivity.nickName);
            serverinfo_person_name.setVisibility(View.VISIBLE);
        }
        if (selectedServer.getStatus() == 2) {
            server_alert.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                server_alert.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#" + selectedServer.getColor()) - 16777216));
            }
        } else {
            server_alert.setVisibility(View.GONE);
        }
    }

    private Servers findServerById(ArrayList<Servers> servers, Integer id) {
        if (servers == null || id == null) {
            return null;
        }
        for (Servers server : servers) {
            if (server != null && server.getId() == id) {
                return server;
            }
        }
        return null;
    }

    public class anim1 implements Runnable {
        public anim1() {
        }

        @Override // newLauncher делал эдгар / EDGAR 3.0
        public final void run() {
            story_layout.setTranslationY(-story_layout.getHeight());
            story_layout.animate().setDuration(150L).translationY(0.0f).alpha(1.0f).start();
        }
    }

    public class anim2 implements Runnable {
        public anim2() {
        }

        @Override
        public final void run() {
            server_layout.setTranslationY(-server_layout.getHeight());
            server_layout.animate().setDuration(150L).translationY(0.0f).alpha(1.0f).start();
        }
    }

    public class anim3 implements Runnable {
        public anim3() {
        }

        @Override
        public final void run() {
            social_layout.setTranslationY(social_layout.getHeight());
            social_layout.animate().setDuration(150L).translationY(0.0f).alpha(1.0f).start();
        }
    }

    public class anim4 implements Runnable {
        public anim4() {
        }

        @Override
        public final void run() {
            bonus_layout.setTranslationY(bonus_layout.getHeight());
            bonus_layout.animate().setDuration(150L).translationY(0.0f).alpha(1.0f).start();
        }
    }

}
