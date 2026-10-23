package com.vietnam.retrolauncher;

import java.util.ArrayList;
import java.util.List;

public class LunarCalendar {

    private static final String[] CAN = {"Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"};
    private static final String[] CHI = {"Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"};

    public static class LunarDate {
        public final int solarDay;
        public final int solarMonth;
        public final int solarYear;
        public final int lunarDay;
        public final int lunarMonth;
        public final int lunarYear;
        public final boolean isLeap;
        public final String canChiYear;
        public final String canChiMonth;
        public final String canChiDay;

        public LunarDate(int sd, int sm, int sy, int ld, int lm, int ly, boolean leap, String ccy, String ccm, String ccd) {
            this.solarDay = sd;
            this.solarMonth = sm;
            this.solarYear = sy;
            this.lunarDay = ld;
            this.lunarMonth = lm;
            this.lunarYear = ly;
            this.isLeap = leap;
            this.canChiYear = ccy;
            this.canChiMonth = ccm;
            this.canChiDay = ccd;
        }
    }

    public static class CalendarDay {
        public final int solarDay;
        public final int lunarDay;
        public final int lunarMonth;
        public final boolean isCurrentMonth;
        public final boolean isToday;

        public CalendarDay(int sd, int ld, int lm, boolean current, boolean today) {
            this.solarDay = sd;
            this.lunarDay = ld;
            this.lunarMonth = lm;
            this.isCurrentMonth = current;
            this.isToday = today;
        }
    }

    public static int jdFromDate(int dd, int mm, int yy) {
        int a = (14 - mm) / 12;
        int y = yy + 4800 - a;
        int m = mm + 12 * a - 3;
        int jd = dd + ((153 * m + 2) / 5) + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
        if (jd < 2299161) {
            jd = dd + ((153 * m + 2) / 5) + 365 * y + y / 4 - 32083;
        }
        return jd;
    }

    private static double newMoon(double k) {
        double T = k / 1236.85;
        double T2 = T * T;
        double T3 = T2 * T;
        double dr = Math.PI / 180.0;
        double Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
        Jd1 += 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr);
        double M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3;
        double Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3;
        double F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3;
        double C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2 * dr * M);
        C1 -= 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(2 * dr * Mpr);
        C1 -= 0.0004 * Math.sin(3 * dr * Mpr);
        C1 += 0.0104 * Math.sin(2 * dr * F) - 0.0051 * Math.sin((M + Mpr) * dr);
        C1 -= 0.0074 * Math.sin((M - Mpr) * dr) + 0.0004 * Math.sin((2 * F + M) * dr);
        C1 -= 0.0004 * Math.sin((2 * F - M) * dr) - 0.0006 * Math.sin((2 * F + Mpr) * dr);
        C1 += 0.0010 * Math.sin((2 * F - Mpr) * dr) + 0.0005 * Math.sin((2 * Mpr + M) * dr);
        double deltat;
        if (T < -11) {
            deltat = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
        } else {
            deltat = -0.000278 + 0.000265 * T + 0.000262 * T2;
        }
        return Jd1 + C1 - deltat;
    }

    private static int getNewMoonDay(double k, double timezone) {
        return (int) Math.floor(newMoon(k) + 0.5 + timezone / 24.0);
    }

    private static double sunLongitude(double jdn) {
        double T = (jdn - 2451545.0) / 36525.0;
        double dr = Math.PI / 180.0;
        double L0 = 280.46646 + 36000.76983 * T + 0.0003032 * T * T;
        double M = 357.52911 + 35999.05029 * T - 0.0001537 * T * T;
        double C = (1.914602 - 0.004817 * T - 0.000014 * T * T) * Math.sin(dr * M);
        C += (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + 0.000289 * Math.sin(dr * 3 * M);
        double L = (L0 + C) * dr;
        L = L - 2 * Math.PI * Math.floor(L / (2 * Math.PI));
        return Math.floor(L / Math.PI * 6.0);
    }

    private static double getSunLongitude(double dayNumber, double timezone) {
        return sunLongitude(dayNumber - 0.5 - timezone / 24.0);
    }

    public static LunarDate convertSolarToLunar(int dd, int mm, int yy) {
        double timezone = 7.0;
        int dayNumber = jdFromDate(dd, mm, yy);
        double k = Math.floor((dayNumber - 2415021.076998695) / 29.530588853);
        int monthStart = getNewMoonDay(k + 1, timezone);
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timezone);
        } else {
            k += 1;
        }

        int a11 = getNewMoonDay(Math.floor((jdFromDate(31, 12, yy) - 2415021.076998695) / 29.530588853), timezone);
        if (getSunLongitude(a11, timezone) >= 9) {
            a11 = getNewMoonDay(Math.floor((jdFromDate(31, 12, yy) - 2415021.076998695) / 29.530588853) - 1, timezone);
        }

        int lunarDay = dayNumber - monthStart + 1;
        int diff = (int) Math.round((monthStart - a11) / 29.0);
        int lunarMonth = (diff + 11) % 12;
        if (lunarMonth == 0) lunarMonth = 12;

        int lunarYear = yy;
        if (mm < 3 && lunarMonth > 10) {
            lunarYear--;
        }

        // Can Chi Year
        int yCan = (lunarYear + 6) % 10;
        int yChi = (lunarYear + 8) % 12;
        String canChiYear = CAN[yCan] + " " + CHI[yChi];

        // Can Chi Month
        int baseCan = (yCan * 2 + 2) % 10;
        int mCan = (baseCan + lunarMonth - 1) % 10;
        int mChi = (lunarMonth + 1) % 12;
        String canChiMonth = CAN[mCan] + " " + CHI[mChi];

        // Can Chi Day
        int dCan = (dayNumber + 9) % 10;
        int dChi = (dayNumber + 1) % 12;
        String canChiDay = CAN[dCan] + " " + CHI[dChi];

        return new LunarDate(dd, mm, yy, lunarDay, lunarMonth, lunarYear, false, canChiYear, canChiMonth, canChiDay);
    }
}
