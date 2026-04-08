package client.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {

    private Locale locale;

    private ResourceBundle bundle;

    public LocalizationManager(Locale locale) {

        setLocale(locale);

    }

    public void setLocale(Locale locale) {

        Locale resolvedLocale = locale == null ? new Locale("en") : locale;

        this.locale = resolvedLocale;
        this.bundle = ResourceBundle.getBundle("messages", resolvedLocale);

    }

    public Locale getLocale() {

        return locale;

    }

    public ResourceBundle getBundle() {

        return bundle;

    }

    public String text(String key) {

        return bundle.getString(key);

    }

}
