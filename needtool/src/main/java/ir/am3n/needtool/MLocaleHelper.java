package ir.am3n.needtool;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class MLocaleHelper extends ContextWrapper {

    public MLocaleHelper(Context base) {
        super(base);
    }

    public static Context wrap(Context context, String language) {
        Log.d("Me-LocaleHelper", "lang: "+language);
        if (!language.equals("default") && !language.isEmpty()) {
            Configuration config = context.getResources().getConfiguration();
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale);
            } else {
                setSystemLocaleLegacy(context, config, locale);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale);
                context = context.createConfigurationContext(config);
            }
            return new MLocaleHelper(context);
        }
        return context;
    }

    public static String getSystemLanguage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getSystemLocale(context).getLanguage().toLowerCase();
        } else {
            return getSystemLocaleLegacy(context).getLanguage().toLowerCase();
        }
    }

    public static Locale getSystemLocaleLegacy(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.locale;
    }

    public static Locale getSystemLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        }
        return Locale.getDefault();
    }

    public static void setSystemLocaleLegacy(Context context, Configuration config, Locale locale) {
        config.locale = locale;
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
    }

    public static void setSystemLocale(Configuration config, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        }
    }

}