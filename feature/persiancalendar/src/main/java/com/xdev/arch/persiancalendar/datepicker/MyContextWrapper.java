package com.xdev.arch.persiancalendar.datepicker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.Objects;
public class MyContextWrapper extends ContextWrapper {

    public MyContextWrapper(Context base) {
        super(base);
    }
    public static MyContextWrapper wrap(Context context, Locale newLocale) {
        try {
            Resources res = context.getResources();
            Configuration configuration = res.getConfiguration();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Locale.setDefault(newLocale);
                configuration.setLocale(newLocale);
                configuration.setLayoutDirection(newLocale);
                context = context.createConfigurationContext(configuration);

            } else {
                configuration.locale = newLocale;
                res.updateConfiguration(configuration, res.getDisplayMetrics());
            }
        } catch (Exception e) {
            Log.d("exception", Objects.requireNonNull(e.getMessage()));
        }
        return new MyContextWrapper(context);
    }
}
