/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.l10n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code LocalizationManager} is a {@code singelton} for localization support.
 * A key is looked up in one or more {@link ResourceBundle} instances. In case the key is not found
 * it is simply returned rather than throwing an {@link Exception}, assuming the key is
 * a technical information like an {@link Exception} text that cannot be translated.
 * 
 * @author Anastasios Patrikis
 */
public class LocalizationManager {
	
	private static Logger LOG = LoggerFactory.getLogger(LocalizationManager.class);
	
	private static LocalizationManager INSTANCE = new LocalizationManager();
	
	private Locale defaultLocale;
	private HashMap<String, ResourceBundle> translations;
	
	/**
	 * Default constructor.
	 * {@code private} for supporting the {@code singleton} pattern.
	 * The default {@link ResourceBundle} is initialized.
	 * @see #DEFAULT_RESOURCE_BUNDLE
	 */
	private LocalizationManager() {
		defaultLocale = Locale.getDefault();
		translations = new HashMap<>(20, 0.9f);
	}
	
	/**
	 * Get the {@code singleton} {@code LocalizationManager} instance.
	 * 
	 * @return the sole instance of this class ({@code singleton} pattern).
	 */
	public static LocalizationManager get() {
		return INSTANCE;
	}
	
	/**
	 * Add an {@link ResourceBundle} to for serving localization.
	 * 
	 * @param bundleName The base name of the {@link ResourceBundle} files.
	 * It the properties file is located at {@code my.package.text_en.properties}
	 * the value to pass is {@code my.package.text}.
	 * The same name will be registered only one time. 
	 */
	public void addBundle(String bundleName) {
		if( ! translations.containsKey(bundleName)) {
			defaultLocale = Locale.getDefault();
			translations.put(bundleName, ResourceBundle.getBundle(bundleName, defaultLocale));
		}
	}
	
	/**
	 * Get a translation without parameter substitution from a {@link ResourceBundle}.
	 * 
	 * @param key the key to look up.
	 * @return a translated string from a {@link ResourceBundle}. In case the key is not found
	 * it is simply returned rather than throwing an {@link Exception}, assuming the key is
	 * a technical information like an {@link Exception} text that cannot be translated.
	 */
	public String get(String key) {
		for(ResourceBundle translation : translations.values()) {
			if(translation.containsKey(key)) {
				return translation.getString(key);
			}
		}
		return key;
	}
	
	/**
	 * Get a translation with parameter substitution from a {@link ResourceBundle}.
	 * 
	 * @param key key the key to look up.
	 * @param args the values to use for substitution of placeholders.
	 * @return a translated string from a {@link ResourceBundle}. In case the key is not found
	 * it is simply returned rather than throwing an {@link Exception}, assuming the key is
	 * a technical information like an {@link Exception} text that cannot be translated.
	 */
	public String get(String key, Object ... args) {
		try {
			MessageFormat formatter = new MessageFormat(get(key), defaultLocale);
			return formatter.format(args);
		} catch (Exception e) {
			LOG.error("I18N error", e);
			return key;
		}
	}
}
