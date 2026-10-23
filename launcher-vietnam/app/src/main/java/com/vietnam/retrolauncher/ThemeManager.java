package com.vietnam.retrolauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ThemeManager {

    private static final String PREF_NAME = "retro_launcher_prefs";
    private static final String KEY_THEME = "selected_theme";

    public static final String THEME_RETRO_VN = "retro_vn";
    public static final String THEME_MAGAZINE = "magazine";
    public static final String THEME_PASTEL = "pastel";
    public static final String THEME_SON_MAI = "son_mai";

    public static class ThemeConfig {
        public final String id;
        public final String title;
        public final int backgroundColor;
        public final int textColorPrimary;
        public final int textColorSecondary;
        public final int blocHeaderColor;
        public final int blocNumberColor;
        public final int goldCardBg;
        public final int petrolCardBg;
        public final int petrolCardText;
        public final int calendarCardBg;
        public final int calendarHeaderBg;
        public final int appBorderColor;
        public final boolean isStampStyle;
        public final boolean isSerifFont;

        public ThemeConfig(String id, String title, int bgColor, int textPri, int textSec,
                           int blocHdr, int blocNum, int goldBg, int petBg, int petText,
                           int calBg, int calHdr, int appBorder, boolean stamp, boolean serif) {
            this.id = id;
            this.title = title;
            this.backgroundColor = bgColor;
            this.textColorPrimary = textPri;
            this.textColorSecondary = textSec;
            this.blocHeaderColor = blocHdr;
            this.blocNumberColor = blocNum;
            this.goldCardBg = goldBg;
            this.petrolCardBg = petBg;
            this.petrolCardText = petText;
            this.calendarCardBg = calBg;
            this.calendarHeaderBg = calHdr;
            this.appBorderColor = appBorder;
            this.isStampStyle = stamp;
            this.isSerifFont = serif;
        }
    }

    public static String getSelectedTheme(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_THEME, THEME_RETRO_VN);
    }

    public static void setSelectedTheme(Context context, String themeId) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_THEME, themeId).apply();
    }

    public static ThemeConfig getThemeConfig(String themeId) {
        switch (themeId) {
            case THEME_MAGAZINE:
                return new ThemeConfig(
                        THEME_MAGAZINE, "Bìa tạp chí",
                        Color.parseColor("#F4F4F0"), // Background
                        Color.parseColor("#1A1A1A"), // Text primary
                        Color.parseColor("#5A5A5A"), // Text secondary
                        Color.parseColor("#E6AA1E"), // Header
                        Color.parseColor("#D23B24"), // Bloc number (red-orange)
                        Color.parseColor("#D23B24"), // Gold card
                        Color.parseColor("#0A3896"), // Petrol card
                        Color.parseColor("#FFFFFF"), // Petrol card text
                        Color.parseColor("#E6AA1E"), // Calendar card bg (Mustard yellow)
                        Color.parseColor("#C99214"), // Calendar header
                        Color.parseColor("#D23B24"),
                        false, true
                );
            case THEME_PASTEL:
                return new ThemeConfig(
                        THEME_PASTEL, "Pastel tươi vui",
                        Color.parseColor("#EDF7F4"), // Mint bg
                        Color.parseColor("#263238"), // Dark slate
                        Color.parseColor("#546E7A"), // Blue-grey
                        Color.parseColor("#4DB6AC"), // Teal
                        Color.parseColor("#00897B"), // Number
                        Color.parseColor("#FF8A65"), // Pastel Coral
                        Color.parseColor("#E0F2F1"), // Light mint petrol
                        Color.parseColor("#004D40"), // Petrol text
                        Color.parseColor("#FFFFFF"), // Cal bg
                        Color.parseColor("#80CBC4"), // Cal header
                        Color.parseColor("#4DB6AC"),
                        false, false
                );
            case THEME_SON_MAI:
                return new ThemeConfig(
                        THEME_SON_MAI, "Sơn mài",
                        Color.parseColor("#0D0D11"), // Lacquer black
                        Color.parseColor("#FFF2B2"), // Lacquer gold text
                        Color.parseColor("#B0A678"), // Muted gold
                        Color.parseColor("#8B0000"), // Vermilion red
                        Color.parseColor("#D4AF37"), // Gold number
                        Color.parseColor("#8B0000"), // Deep red gold card
                        Color.parseColor("#1A1A22"), // Petrol card
                        Color.parseColor("#FFF2B2"), // Petrol text
                        Color.parseColor("#1A1A22"), // Cal bg
                        Color.parseColor("#D4AF37"), // Cal header
                        Color.parseColor("#D4AF37"),
                        false, true
                );
            case THEME_RETRO_VN:
            default:
                return new ThemeConfig(
                        THEME_RETRO_VN, "Retro Việt Nam",
                        Color.parseColor("#EFE8D6"), // Vintage paper
                        Color.parseColor("#212121"), // Black/Charcoal
                        Color.parseColor("#5D5749"), // Vintage sepia
                        Color.parseColor("#C62828"), // Traditional red
                        Color.parseColor("#D32F2F"), // Red day
                        Color.parseColor("#C62828"), // Red gold card
                        Color.parseColor("#FAF7EE"), // White/cream petrol
                        Color.parseColor("#212121"), // Petrol text
                        Color.parseColor("#FAF7EE"), // White/cream calendar
                        Color.parseColor("#C62828"), // Cal header
                        Color.parseColor("#C62828"),
                        true, false
                );
        }
    }
}
