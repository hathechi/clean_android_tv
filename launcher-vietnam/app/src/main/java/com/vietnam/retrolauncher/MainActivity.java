package com.vietnam.retrolauncher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    // Views
    private View rootView;
    private TextView tvGreeting;
    private TextView tvClock;
    private ImageButton btnTheme;
    private ImageButton btnWifi;

    // Bloc calendar views
    private LinearLayout blocContainer;
    private View blocHeader;
    private TextView tvBlocMonthYear;
    private TextView tvBlocDay;
    private TextView tvBlocWeekday;
    private TextView tvBlocLunar;
    private TextView tvBlocCanchi;

    // Mini calendar views
    private LinearLayout miniCalendarCard;
    private TextView tvCalTitle;
    private TextView tvCalLunarHint;
    private LinearLayout calendarGrid;

    // Stats cards
    private LinearLayout cardGold;
    private LinearLayout cardPetrol;
    private TextView tvPriceSjc;
    private TextView tvPriceRing;
    private TextView tvPriceE10;
    private TextView tvPriceE5;
    private TextView tvPriceDo;

    // Apps
    private RecyclerView rvApps;
    private AppAdapter appAdapter;

    private int lastDay = -1;
    private String currentThemeId;

    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            clockHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupApps();
        setupButtons();

        currentThemeId = ThemeManager.getSelectedTheme(this);
        applyTheme(currentThemeId);

        updateCalendar();
        updateTime();

        // Auto fetch gold & petrol prices on startup
        loadMarketPrices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockHandler.post(timeRunnable);
        if (appAdapter != null) {
            appAdapter.loadApps();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clockHandler.removeCallbacks(timeRunnable);
    }

    private void bindViews() {
        rootView = findViewById(R.id.root_view);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvClock = findViewById(R.id.tv_clock);
        btnTheme = findViewById(R.id.btn_theme);
        btnWifi = findViewById(R.id.btn_wifi);

        blocContainer = findViewById(R.id.bloc_container);
        blocHeader = findViewById(R.id.bloc_header);
        tvBlocMonthYear = findViewById(R.id.tv_bloc_month_year);
        tvBlocDay = findViewById(R.id.tv_bloc_day);
        tvBlocWeekday = findViewById(R.id.tv_bloc_weekday);
        tvBlocLunar = findViewById(R.id.tv_bloc_lunar);
        tvBlocCanchi = findViewById(R.id.tv_bloc_canchi);

        miniCalendarCard = findViewById(R.id.mini_calendar_card);
        tvCalTitle = findViewById(R.id.tv_cal_title);
        tvCalLunarHint = findViewById(R.id.tv_cal_lunar_hint);
        calendarGrid = findViewById(R.id.calendar_grid);

        cardGold = findViewById(R.id.card_gold);
        cardPetrol = findViewById(R.id.card_petrol);
        tvPriceSjc = findViewById(R.id.tv_price_sjc);
        tvPriceRing = findViewById(R.id.tv_price_ring);
        tvPriceE10 = findViewById(R.id.tv_price_e10);
        tvPriceE5 = findViewById(R.id.tv_price_e5);
        tvPriceDo = findViewById(R.id.tv_price_do);

        rvApps = findViewById(R.id.rv_apps);
    }

    private void loadMarketPrices() {
        MarketDataService.fetchMarketData(this, (sjc, ring, e10, e5, doVal) -> {
            runOnUiThread(() -> {
                if (tvPriceSjc != null) tvPriceSjc.setText(sjc);
                if (tvPriceRing != null) tvPriceRing.setText(ring);
                if (tvPriceE10 != null) tvPriceE10.setText(e10);
                if (tvPriceE5 != null) tvPriceE5.setText(e5);
                if (tvPriceDo != null) tvPriceDo.setText(doVal);
            });
        });
    }

    private void setupApps() {
        rvApps.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        appAdapter = new AppAdapter(this);
        rvApps.setAdapter(appAdapter);

        // Focus first app by default for remote control
        rvApps.post(() -> {
            View first = rvApps.getChildAt(0);
            if (first != null) first.requestFocus();
        });
    }

    private void setupButtons() {
        btnTheme.setOnClickListener(v -> {
            ThemeDialog dialog = new ThemeDialog(this, currentThemeId, this::applyTheme);
            dialog.show();
        });

        btnWifi.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } catch (Exception ignored) {
                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (Exception ignored2) {}
            }
        });
    }

    private String getGreeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 11) {
            return "Chào buổi sáng";
        } else if (hour >= 11 && hour < 14) {
            return "Chào buổi trưa";
        } else if (hour >= 14 && hour < 18) {
            return "Chào buổi chiều";
        } else if (hour >= 18 && hour < 23) {
            return "Chào buổi tối";
        } else {
            return "Chào buổi đêm";
        }
    }

    private void updateTime() {
        Calendar cal = Calendar.getInstance();
        tvClock.setText(timeFormat.format(cal.getTime()));
        tvGreeting.setText(getGreeting());

        int today = cal.get(Calendar.DAY_OF_YEAR);
        if (today != lastDay) {
            lastDay = today;
            updateCalendar();
        }
    }

    private void updateCalendar() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1; // 1-12
        int year = cal.get(Calendar.YEAR);

        String[] vietnameseMonths = {
                "THÁNG MỘT", "THÁNG HAI", "THÁNG BA", "THÁNG TƯ", "THÁNG NĂM", "THÁNG SÁU",
                "THÁNG BẢY", "THÁNG TÁM", "THÁNG CHÍN", "THÁNG MƯỜI", "THÁNG MƯỜI MỘT", "THÁNG MƯỜI HAI"
        };
        tvBlocMonthYear.setText(vietnameseMonths[month - 1] + " · " + year);
        tvBlocDay.setText(String.valueOf(day));

        String[] vietnameseWeekdays = {
                "CHỦ NHẬT", "THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY"
        };
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 7 = Saturday
        tvBlocWeekday.setText(vietnameseWeekdays[dayOfWeek - 1]);

        // Real-time Lunar date calculation
        LunarCalendar.LunarDate lunar = LunarCalendar.convertSolarToLunar(day, month, year);
        tvBlocLunar.setText(lunar.lunarDay + " / " + lunar.lunarMonth);
        tvBlocCanchi.setText(lunar.canChiYear + "\n" + lunar.canChiMonth + "\n" + lunar.canChiDay);

        // Mini calendar setup
        tvCalTitle.setText("Tháng " + month);
        int prevLunarMonth = (lunar.lunarMonth == 1) ? 12 : lunar.lunarMonth - 1;
        tvCalLunarHint.setText("âm lịch tháng " + prevLunarMonth + " và " + lunar.lunarMonth);

        renderMiniCalendar(cal, day, month, year);
    }

    private void renderMiniCalendar(Calendar todayCal, int curDay, int month, int year) {
        calendarGrid.removeAllViews();

        Calendar tempCal = Calendar.getInstance();
        tempCal.set(year, month - 1, 1);
        int startDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday ...
        int dayOffset = (startDayOfWeek + 5) % 7;
        int maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int totalCells = (dayOffset + maxDays <= 35) ? 35 : 42;
        int rows = totalCells / 7;

        int dayCounter = 1;
        float density = getResources().getDisplayMetrics().density;

        for (int r = 0; r < rows; r++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int c = 0; c < 7; c++) {
                int cellIndex = r * 7 + c;
                LinearLayout cell = new LinearLayout(this);
                cell.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                cell.setOrientation(LinearLayout.HORIZONTAL);
                cell.setGravity(Gravity.CENTER);

                if (cellIndex >= dayOffset && dayCounter <= maxDays) {
                    final int d = dayCounter;
                    boolean isToday = (d == curDay);

                    TextView tvSolar = new TextView(this);
                    tvSolar.setText(String.valueOf(d));
                    tvSolar.setTextSize(13);
                    tvSolar.setTypeface(Typeface.DEFAULT_BOLD);

                    LunarCalendar.LunarDate ld = LunarCalendar.convertSolarToLunar(d, month, year);
                    TextView tvLunar = new TextView(this);
                    tvLunar.setText(String.valueOf(ld.lunarDay));
                    tvLunar.setTextSize(9);
                    tvLunar.setPadding((int)(2 * density), 0, 0, (int)(4 * density));

                    if (isToday) {
                        cell.setBackgroundColor(Color.parseColor("#C62828"));
                        tvSolar.setTextColor(Color.WHITE);
                        tvLunar.setTextColor(Color.parseColor("#FFCDD2"));
                    } else if (c == 6) { // Sunday
                        tvSolar.setTextColor(Color.parseColor("#D32F2F"));
                        tvLunar.setTextColor(Color.parseColor("#9E9E9E"));
                    } else {
                        tvSolar.setTextColor(Color.parseColor("#212121"));
                        tvLunar.setTextColor(Color.parseColor("#9E9E9E"));
                    }

                    cell.addView(tvSolar);
                    cell.addView(tvLunar);
                    dayCounter++;
                }

                rowLayout.addView(cell);
            }

            calendarGrid.addView(rowLayout);
        }
    }

    public void applyTheme(String themeId) {
        this.currentThemeId = themeId;
        ThemeManager.ThemeConfig config = ThemeManager.getThemeConfig(themeId);

        rootView.setBackgroundColor(config.backgroundColor);
        tvClock.setTextColor(config.textColorPrimary);
        tvGreeting.setTextColor(config.blocHeaderColor);

        // Fonts
        Typeface font = config.isSerifFont ? Typeface.SERIF : Typeface.DEFAULT_BOLD;
        tvGreeting.setTypeface(font);

        // Bloc styling
        blocContainer.setBackgroundColor(config.calendarCardBg);
        blocHeader.setBackgroundColor(config.blocHeaderColor);
        tvBlocDay.setTextColor(config.blocNumberColor);
        tvBlocWeekday.setTextColor(config.textColorPrimary);
        tvBlocWeekday.setTypeface(font);
        tvBlocLunar.setTextColor(config.textColorPrimary);
        tvBlocCanchi.setTextColor(config.textColorSecondary);

        // Cards styling
        miniCalendarCard.setBackgroundColor(config.calendarCardBg);
        tvCalTitle.setTextColor(config.textColorPrimary);
        tvCalLunarHint.setTextColor(config.textColorSecondary);

        cardGold.setBackgroundColor(config.goldCardBg);
        cardPetrol.setBackgroundColor(config.petrolCardBg);

        // Apps style
        if (appAdapter != null) {
            appAdapter.setStampMode(config.isStampStyle);
        }

        updateCalendar();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
