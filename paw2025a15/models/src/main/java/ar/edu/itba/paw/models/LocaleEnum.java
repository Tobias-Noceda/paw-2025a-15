package ar.edu.itba.paw.models;

import java.util.Locale;

public enum LocaleEnum {
    EN_US("en", "US"),
    ES_US("es", "US"),
    ES_AR("es", "AR"),
    EN_AR("en", "AR");

    private final String language;
    private final String country;

    LocaleEnum(String language, String country) {
        this.language = language;
        this.country = country;
    }

    public Locale toLocale() {
        return new Locale(language, country);
    }

    public static LocaleEnum fromLocale(Locale locale) {
        for (LocaleEnum localeEnum : LocaleEnum.values()) {
            if (localeEnum.language.equalsIgnoreCase(locale.getLanguage()) &&
                localeEnum.country.equalsIgnoreCase(locale.getCountry())) {
                return localeEnum;
            }
        }
        throw new IllegalArgumentException("Unsupported Locale: " + locale);
    }

    public static LocaleEnum fromInt(int id) {
        LocaleEnum[] values = LocaleEnum.values();
        if (id >= 0 && id < values.length) {
            return values[id];
        }
        throw new IllegalArgumentException("Unsupported LocaleEnum ID: " + id);
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }
}
