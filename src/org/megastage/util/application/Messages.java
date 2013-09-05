package org.megastage.util.application;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Retrieves string messages from a ResourceBundle properties file for i18n.
 *
 * @author mike.angstadt
 *
 */
public final class Messages {

    private final String bundleName;
    private ResourceBundle resourceBundle;
    private Locale locale;
    private static Messages messages;

    public static void init(String bundleName) {
        messages = new Messages(bundleName);
    }

    public static Messages getMessages() {
        return messages;
    }
    
    ResourceBundle.Control localeCandidateSelector = new ResourceBundle.Control() {

        @Override
        public List<Locale> getCandidateLocales(String baseName, Locale locale) {
            if (baseName == null) {
                throw new NullPointerException();
            }

            List<Locale> defaultCandidates = super.getCandidateLocales(baseName, locale);


            List<Locale> enhancedCandidates = new ArrayList<Locale>();
            for (Locale defaultLocale : defaultCandidates) {
                if (Locale.ROOT.equals(defaultLocale)) {
                    // add the root locale in front of the root locale
                    enhancedCandidates.add(Locale.ENGLISH);
                }

                enhancedCandidates.add(defaultLocale);
            }
            return enhancedCandidates;
        }
    };

    /**
     * Constructor.
     *
     * @param bundleName the base name of the resource bundle, a fully qualified
     * class name
     */
    public Messages(String bundleName) {
        this.bundleName = bundleName;

        Locale localeToLoad = Locale.getDefault();
        resourceBundle = Utf8ResourceBundle.getBundle(bundleName, localeToLoad, localeCandidateSelector);
        locale = resourceBundle.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
    }

    /**
     * Gets a message.
     *
     * @param key the message key
     * @return the message
     */
    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Gets a message that has arguments.
     *
     * @param key the message key
     * @param arguments the arguments to populate the message with
     * @return the formatted message
     */
    public String getString(String key, Object... arguments) {
        return MessageFormat.format(getString(key), arguments);
    }

    /**
     * Changes the Locale.
     *
     * @param locale the new Locale
     */
    public void changeLocale(Locale locale) {
        this.locale = locale;
        resourceBundle = Utf8ResourceBundle.getBundle(bundleName, locale);
    }

    /**
     * Gets the current Locale.
     *
     * @return
     */
    public Locale getLocale() {
        return locale;
    }
}
