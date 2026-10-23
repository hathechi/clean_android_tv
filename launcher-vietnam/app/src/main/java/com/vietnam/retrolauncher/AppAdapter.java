package com.vietnam.retrolauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    public static class AppEntry {
        public final String packageName;
        public final String label;
        public final ComponentName component;
        public final Drawable icon;
        public final int color;

        public AppEntry(String pkg, String label, ComponentName comp, Drawable icon, int color) {
            this.packageName = pkg;
            this.label = label;
            this.component = comp;
            this.icon = icon;
            this.color = color;
        }
    }

    private final Context context;
    private final List<AppEntry> apps = new ArrayList<>();
    private boolean isStampMode = true;

    // Prioritized apps
    private static final List<String> FAVOURITES = Arrays.asList(
            "com.google.android.youtube.tv",
            "com.viettel.tv360.tv",
            "com.internet.tvbrowser",
            "com.tcl.browser",
            "com.android.vending",
            "com.android.tv.settings"
    );

    public AppAdapter(Context context) {
        this.context = context;
        loadApps();
    }

    public void setStampMode(boolean stamp) {
        this.isStampMode = stamp;
        notifyDataSetChanged();
    }

    public void loadApps() {
        apps.clear();
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            list = pm.queryIntentActivities(intent, 0);
        }

        for (ResolveInfo ri : list) {
            if (ri.activityInfo == null) continue;
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(context.getPackageName())) continue;

            String label = String.valueOf(ri.loadLabel(pm));
            Drawable icon = ri.loadIcon(pm);
            ComponentName comp = new ComponentName(pkg, ri.activityInfo.name);

            int color = getAppColor(pkg);
            apps.add(new AppEntry(pkg, label, comp, icon, color));
        }

        // Sort by FAVOURITES first
        Collections.sort(apps, (a, b) -> {
            int fa = FAVOURITES.indexOf(a.packageName);
            int fb = FAVOURITES.indexOf(b.packageName);
            if (fa >= 0 || fb >= 0) {
                if (fa < 0) return 1;
                if (fb < 0) return -1;
                return Integer.compare(fa, fb);
            }
            return a.label.compareToIgnoreCase(b.label);
        });

        notifyDataSetChanged();
    }

    private int getAppColor(String pkg) {
        if (pkg.contains("youtube")) return Color.parseColor("#C62828");      // Red
        if (pkg.contains("tv360")) return Color.parseColor("#D32F2F");        // Crimson
        if (pkg.contains("vending")) return Color.parseColor("#1B5E20");      // Deep Green
        if (pkg.contains("settings")) return Color.parseColor("#D49B18");     // Amber/Yellow
        if (pkg.contains("browser")) return Color.parseColor("#1565C0");      // Blue
        if (pkg.contains("netflix")) return Color.parseColor("#B71C1C");      // Dark Red
        if (pkg.contains("vlc")) return Color.parseColor("#E65100");          // Orange
        return Color.parseColor("#37474F");                                   // Blue-grey default
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppEntry app = apps.get(position);
        holder.cardView.setCardColor(app.color);
        holder.cardView.setStampMode(isStampMode);
        holder.appLabel.setText(app.label);
        if (app.icon != null) {
            holder.appIcon.setImageDrawable(app.icon);
        }

        holder.cardView.setOnClickListener(v -> {
            Intent launch = context.getPackageManager().getLeanbackLaunchIntentForPackage(app.packageName);
            if (launch == null) {
                launch = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
            }
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final StampCardView cardView;
        final ImageView appIcon;
        final TextView appLabel;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (StampCardView) itemView;
            appIcon = itemView.findViewById(R.id.app_icon);
            appLabel = itemView.findViewById(R.id.app_label);
        }
    }
}
