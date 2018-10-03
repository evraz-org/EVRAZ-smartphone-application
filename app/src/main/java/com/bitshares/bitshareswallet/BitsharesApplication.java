package com.bitshares.bitshareswallet;

import android.arch.persistence.room.Room;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.bitshares.bitshareswallet.room.BitsharesDatabase;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.franmontiel.localechanger.LocaleChanger;

import org.evrazcoin.evrazwallet.BuildConfig;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;


public class BitsharesApplication extends MultiDexApplication {
    private static BitsharesApplication theApp;
    private BitsharesDatabase bitsharesDatabase;
    public static final Locale LOCALE_EN = new Locale("en", "US");
    public static final Locale LOCALE_RU = new Locale("ru", "RU");
    public static final Locale LOCALE_ES = new Locale("es", "ES");
    public static final Locale LOCALE_NL = new Locale("nl", "NL");
    public static final Locale LOCALE_ZH = new Locale("zh", "ZH");
    public static final List<Locale> SUPPORTED_LOCALES =
            Arrays.asList(LOCALE_EN, LOCALE_RU, LOCALE_ES, LOCALE_NL, LOCALE_ZH);

    /*
     * 是否需要把涨跌的颜色互换
     */
    public static BitsharesApplication getInstance() {
        return theApp;
    }

    public BitsharesApplication() {
        theApp = this;
    }

    public BitsharesDatabase getBitsharesDatabase() {
        return bitsharesDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        bitsharesDatabase = Room.databaseBuilder(
                this,
                BitsharesDatabase.class,
                "evrazwallet.db"
        ).build();

        LocaleChanger.initialize(this, SUPPORTED_LOCALES);
        // 注册回调，保证数据更新
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleChanger.onConfigurationChanged();
    }
}
