package com.vietnam.retrolauncher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ThemeDialog extends Dialog {

    public interface OnThemeSelectedListener {
        void onThemeSelected(String themeId);
    }

    private final OnThemeSelectedListener listener;
    private final String currentTheme;

    public ThemeDialog(@NonNull Context context, String currentTheme, OnThemeSelectedListener listener) {
        super(context);
        this.currentTheme = currentTheme;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View root = LayoutInflater.from(getContext()).inflate(R.layout.dialog_theme_picker, null);
        setContentView(root);

        setupCard(root, R.id.theme_retro, R.id.badge_retro, ThemeManager.THEME_RETRO_VN);
        setupCard(root, R.id.theme_magazine, R.id.badge_magazine, ThemeManager.THEME_MAGAZINE);
        setupCard(root, R.id.theme_pastel, R.id.badge_pastel, ThemeManager.THEME_PASTEL);
        setupCard(root, R.id.theme_sonmai, R.id.badge_sonmai, ThemeManager.THEME_SON_MAI);
    }

    private void setupCard(View root, int cardId, int badgeId, final String themeId) {
        final View card = root.findViewById(cardId);
        TextView badge = root.findViewById(badgeId);

        if (currentTheme.equals(themeId)) {
            badge.setVisibility(View.VISIBLE);
            card.requestFocus();
        } else {
            badge.setVisibility(View.GONE);
        }

        card.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                card.setScaleX(1.05f);
                card.setScaleY(1.05f);
                card.setBackgroundResource(R.drawable.theme_card_focused);
            } else {
                card.setScaleX(1.0f);
                card.setScaleY(1.0f);
                card.setBackgroundResource(R.drawable.theme_card_unfocused);
            }
        });

        card.setOnClickListener(v -> {
            ThemeManager.setSelectedTheme(getContext(), themeId);
            if (listener != null) {
                listener.onThemeSelected(themeId);
            }
            dismiss();
        });
    }
}
