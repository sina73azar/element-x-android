package com.drp.superapp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class ChangeLanguage extends android.content.ContextWrapper {

    public ChangeLanguage(Context base) {
        super(base);
    }

    public static ChangeLanguage wrap(Context context, Locale newLocale) {

        try {
            Resources res = context.getResources();
            Configuration configuration = res.getConfiguration();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Locale.setDefault(newLocale);
                configuration.setLocale(newLocale);
                configuration.setLayoutDirection(newLocale);
                context = context.createConfigurationContext(configuration);

            } else {
                configuration.setLocale(newLocale);
                context = context.createConfigurationContext(configuration);
            }

        } catch (Exception e) {
            Log.d("exception",e.getMessage());
        }
        return new ChangeLanguage(context);
    }
}
