package com.vietnam.retrolauncher;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketDataService {

    private static final String PREF_MARKET = "market_data_prefs";
    private static final String KEY_SJC = "price_sjc";
    private static final String KEY_RING = "price_ring";
    private static final String KEY_E10 = "price_e10";
    private static final String KEY_E5 = "price_e5";
    private static final String KEY_DO = "price_do";

    public interface OnDataLoadedListener {
        void onDataLoaded(String sjc, String ring, String e10, String e5, String doPrice);
    }

    public static void fetchMarketData(Context context, OnDataLoadedListener listener) {
        SharedPreferences sp = context.getSharedPreferences(PREF_MARKET, Context.MODE_PRIVATE);

        // 1. Immediately return cached values first so UI is never blank
        final String cachedSjc = sp.getString(KEY_SJC, "142,0");
        final String cachedRing = sp.getString(KEY_RING, "141,0");
        final String cachedE10 = sp.getString(KEY_E10, "28.250");
        final String cachedE5 = sp.getString(KEY_E5, "27.700");
        final String cachedDo = sp.getString(KEY_DO, "29.120");

        if (listener != null) {
            listener.onDataLoaded(cachedSjc, cachedRing, cachedE10, cachedE5, cachedDo);
        }

        // 2. Fetch fresh values in background thread on app start
        new Thread(() -> {
            String sjc = cachedSjc;
            String ring = cachedRing;
            String e10 = cachedE10;
            String e5 = cachedE5;
            String doVal = cachedDo;
            boolean updated = false;

            // Fetch Gold Price from public API
            try {
                URL url = new URL("https://www.vang.today/api/prices");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONObject root = new JSONObject(sb.toString());
                    if (root.optBoolean("success")) {
                        JSONObject prices = root.optJSONObject("prices");
                        if (prices != null) {
                            JSONObject sjcObj = prices.optJSONObject("PQHNVM");
                            if (sjcObj == null) sjcObj = prices.optJSONObject("SJL1L10");
                            if (sjcObj == null) sjcObj = prices.optJSONObject("DOJINHTV");

                            JSONObject ringObj = prices.optJSONObject("SJ9999");
                            if (ringObj == null) ringObj = prices.optJSONObject("PQHN24NTT");

                            if (sjcObj != null && sjcObj.has("sell")) {
                                double sell = sjcObj.getDouble("sell") / 1000000.0;
                                sjc = String.format("%.1f", sell).replace(".", ",");
                                updated = true;
                            }
                            if (ringObj != null && ringObj.has("sell")) {
                                double sell = ringObj.getDouble("sell") / 1000000.0;
                                ring = String.format("%.1f", sell).replace(".", ",");
                                updated = true;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}

            // Fetch Petrol Price from webgia.com
            try {
                URL url = new URL("https://webgia.com/gia-xang-dau/petrolimex/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    String html = sb.toString();

                    Matcher mE10 = Pattern.compile("Xăng E10[^<]*</th>\\s*<td[^>]*>([0-9.]+)").matcher(html);
                    if (mE10.find()) {
                        e10 = mE10.group(1);
                        updated = true;
                    }
                    Matcher mE5 = Pattern.compile("Xăng E5[^<]*</th>\\s*<td[^>]*>([0-9.]+)").matcher(html);
                    if (mE5.find()) {
                        e5 = mE5.group(1);
                        updated = true;
                    }
                    Matcher mDo = Pattern.compile("Dầu DO 0,05S[^<]*</th>\\s*<td[^>]*>([0-9.]+)").matcher(html);
                    if (mDo.find()) {
                        doVal = mDo.group(1);
                        updated = true;
                    }
                }
            } catch (Exception ignored) {}

            if (updated) {
                sp.edit()
                        .putString(KEY_SJC, sjc)
                        .putString(KEY_RING, ring)
                        .putString(KEY_E10, e10)
                        .putString(KEY_E5, e5)
                        .putString(KEY_DO, doVal)
                        .apply();

                final String fSjc = sjc;
                final String fRing = ring;
                final String fE10 = e10;
                final String fE5 = e5;
                final String fDo = doVal;

                if (listener != null) {
                    listener.onDataLoaded(fSjc, fRing, fE10, fE5, fDo);
                }
            }
        }).start();
    }
}
