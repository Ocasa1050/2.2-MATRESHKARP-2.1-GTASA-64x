package ru.edgar.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.adapter.ServersAdapter;
import ru.edgar.launcher.model.Servers;
import ru.edgar.launcher.other.Lists;
import ru.edgar.space.R;

public class ServerSelectFragment extends MainActivity {

    public ImageView btn_close;
    public TextView serverlist_caption;
    public RecyclerView serverlist_recycler;
    public LinearLayout emptyState;
    public TextView retryButton;

    ServersAdapter serversAdapter;
    ArrayList<Servers> slist;

    public ServerSelectFragment() {
        super();
        serverSelectInit();
    }

    public void serverSelectInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_server_select, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        serverlist_caption = (TextView) viewGroup.findViewById(R.id.serverlist_caption);
        serverlist_recycler = (RecyclerView) viewGroup.findViewById(R.id.serverlist_recycler);
        emptyState = (LinearLayout) viewGroup.findViewById(R.id.serverlist_empty_layout);
        retryButton = (TextView) viewGroup.findViewById(R.id.serverlist_retry);
        btn_close.setOnTouchListener(new animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener(v -> {
            hide();
        });
        retryButton.setOnClickListener(v -> {
            retryButton.setEnabled(false);
            retryButton.postDelayed(() -> retryButton.setEnabled(true), 2_000L);
            MainActivity activity = MainActivity.getMainActivity();
            if (activity != null && activity.splashFragment != null) {
                activity.splashFragment.loadJsons();
            } else {
                retryButton.setEnabled(true);
            }
        });
        serverlist_recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManagerr = new LinearLayoutManager(MainActivity.getMainActivity());
        serverlist_recycler.setLayoutManager(layoutManagerr);
        //slist = Lists.slist;
        viewGroup.setVisibility(View.GONE);
    }

    public void refreshServers() {
        if (serverlist_recycler == null) {
            return;
        }
        if (Lists.slist == null) {
            Lists.slist = new ArrayList<>();
        }
        slist = Lists.slist;
        boolean isEmpty = slist.isEmpty();
        serverlist_recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        retryButton.setEnabled(true);
        serversAdapter = new ServersAdapter(MainActivity.getMainActivity(), slist);
        serverlist_recycler.setAdapter(serversAdapter);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(f10);
        btn_close.clearAnimation();
        btn_close.setTranslationY(f10.y);
        btn_close.animate().setDuration(300L).translationY(0.0f).start();
        serverlist_caption.clearAnimation();
        serverlist_caption.setTranslationY(f10.y);
        serverlist_caption.animate().setDuration(300L).translationY(0.0f).start();
        serverlist_recycler.clearAnimation();
        serverlist_recycler.setTranslationY(f10.y);
        serverlist_recycler.animate().setDuration(300L).translationY(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
        refreshServers();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(f10);
        btn_close.clearAnimation();
        btn_close.setTranslationY(0.0f);
        btn_close.animate().setDuration(300L).translationY(f10.y).start();
        serverlist_caption.clearAnimation();
        serverlist_caption.setTranslationY(0.0f);
        serverlist_caption.animate().setDuration(300L).translationY(f10.y).start();
        serverlist_recycler.clearAnimation();
        serverlist_recycler.setTranslationY(0.0f);
        serverlist_recycler.animate().setDuration(300L).translationY(f10.y).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new ssetVisibility(), 300L);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
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

