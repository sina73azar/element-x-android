package io.element.android.x.refa.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class ChangeLanguage extends android.content.ContextWrapper {

    public ChangeLanguage(Context base) {
        super(base);
    }

    public static ChangeLanguage wrap(Context context, Locale newLocale) {

        try {
            Resources res = context.getResources();
            Configuration configuration = res.getConfiguration();

            Locale.setDefault(newLocale);
            configuration.setLocale(newLocale);
            configuration.setLayoutDirection(newLocale);
            context = context.createConfigurationContext(configuration);

        } catch (Exception e) {
        }
        return new ChangeLanguage(context);
    }
}
