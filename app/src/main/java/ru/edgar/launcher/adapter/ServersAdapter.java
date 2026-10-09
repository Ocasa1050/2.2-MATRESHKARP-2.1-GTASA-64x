package ru.edgar.launcher.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.model.Servers;
import ru.edgar.space.R;

public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServersViewHolder> {
	Context context;
	ArrayList<Servers> slist;

	boolean serverrec = false;
	boolean servernew = false;
	
	public ServersAdapter(Context context, ArrayList<Servers> slist){
		this.context = context;
		this.slist = slist == null ? new ArrayList<>() : slist;
	}
	
	@NonNull
	@Override
    public ServersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.serverlist_item, parent, false);
		return new ServersViewHolder(v); 
    }
  
    @Override
    public void onBindViewHolder(@NonNull ServersViewHolder holder, int position) {
        Servers servers = slist.get(position);
        if (servers == null) {
            return;
        }
		((ViewGroup.MarginLayoutParams) holder.view.getLayoutParams()).bottomMargin = MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._12sdp);
		if (!servers.getRecommend()) {
			ConstraintLayout.LayoutParams dVar2 = (ConstraintLayout.LayoutParams) holder.server_item_background.getLayoutParams();
			dVar2.dimensionRatio = "791:186";
			holder.server_item_background.setLayoutParams(dVar2);
			holder.server_recommend_card.setVisibility(View.GONE);
			holder.server_recommend_text.setVisibility(View.GONE);
		} else {
			ConstraintLayout.LayoutParams dVar3 = (ConstraintLayout.LayoutParams) holder.server_item_background.getLayoutParams();
			Resources resources = MainActivity.getMainActivity().getResources();
			if (!servers.getNewStatus()) {
				if (!serverrec) {
					dVar3.dimensionRatio = "791:217";
					holder.server_item_background.setLayoutParams(dVar3);
					holder.server_recommend_card.setVisibility(View.VISIBLE);
					holder.server_recommend_text.setVisibility(View.VISIBLE);
					holder.server_recommend_text.setText(resources.getString(R.string.launcher_recommended));
					serverrec = true;
				} else {
					holder.server_recommend_card.setVisibility(View.GONE);
					holder.server_recommend_text.setVisibility(View.GONE);
				}
			} else {
				if (!servernew) {
					dVar3.dimensionRatio = "791:217";
					holder.server_item_background.setLayoutParams(dVar3);
					holder.server_recommend_card.setVisibility(View.VISIBLE);
					holder.server_recommend_text.setVisibility(View.VISIBLE);
					holder.server_recommend_text.setText(resources.getString(R.string.launcher_new));
					servernew = true;
				} else {
					holder.server_recommend_card.setVisibility(View.GONE);
					holder.server_recommend_text.setVisibility(View.GONE);
				}
			}
		}
		holder.server_item_background.setColorFilter(Color.parseColor("#" + servers.getColor()) - 16777216);
		holder.server_recommend_card.setColorFilter(Color.parseColor("#" + servers.getColor()) - 16777216);
		holder.server_item_image.setColorFilter(Color.parseColor("#" + servers.getColor()) - 16777216);
		holder.server_item_name.setText(servers.getName());
		int i13 = servers.getStatus();
		if (i13 == 0) {
			holder.server_item_status.setText("Низкая загруженность");
			holder.server_item_status_image.setImageResource(R.drawable.ic_launcher_star);
			holder.server_item_status.setAlpha(1.0f);
			holder.server_item_status_image.setAlpha(1.0f);
			holder.server_item_status_image.setVisibility(View.VISIBLE);
		} else if (i13 == 1) {
			holder.server_item_status.setText("Средняя загруженность");
			holder.server_item_status_image.setImageResource(R.drawable.ic_launcher_star);
			holder.server_item_status.setAlpha(0.4f);
			holder.server_item_status_image.setVisibility(View.GONE);
		} else {
			holder.server_item_status.setText("Высокая загруженность");
			holder.server_item_status_image.setImageResource(R.drawable.ic_launcher_alert);
			holder.server_item_status.setAlpha(0.4f);
			holder.server_item_status_image.setAlpha(0.4f);
			holder.server_item_status_image.setVisibility(View.VISIBLE);
		}
		holder.view.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), holder.view));
		holder.view.setOnClickListener(v -> {
			if(!MainActivity.isAuth) {
				MainActivity.getMainActivity().serverSelectFragment.hide();
				MainActivity.getMainActivity().authFragment.show();
			} else {
				HashMap<String, Integer> serverInfo = new HashMap<>();
                serverInfo.put("server-id", servers.getId());

				FirebaseDatabase.getInstance().getReference().child("Users").child("User-server").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(serverInfo);
				MainActivity.getMainActivity().serverSelectFragment.hide();
				//.child("Server_" + servers.getId())
				MainActivity.getMainActivity().mainFragment.upServerId(); // EDGAR 3.0 newLauncher version от 02.01.2024
			}
		});
    }

    @Override
    public int getItemCount() {
        return slist == null ? 0 : slist.size();
    }

    public static class ServersViewHolder extends RecyclerView.ViewHolder {

		public final ImageView server_recommend_card;
		public final TextView server_recommend_text;
		public final View view;
		public final ImageView server_item_background;
		public final ImageView server_item_image;
		public final TextView server_item_name;
		public final ImageView server_item_status_image;
		public final TextView server_item_status;
	    
        public ServersViewHolder(View view) {
            super(view);
			this.view = view;
			server_item_background = (ImageView) view.findViewById(R.id.server_item_background);
			server_item_image = (ImageView) view.findViewById(R.id.server_item_image);
			server_item_name = (TextView) view.findViewById(R.id.server_item_name);
			server_item_status_image = (ImageView) view.findViewById(R.id.server_item_status_image);
			server_item_status = (TextView) view.findViewById(R.id.server_item_status);
			server_recommend_card = (ImageView) view.findViewById(R.id.server_recommend_card);
			server_recommend_text = (TextView) view.findViewById(R.id.server_recommend_text);
        }
    }
	
}