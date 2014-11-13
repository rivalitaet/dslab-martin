package util;

import java.util.Iterator;

public class StringUtils {

	/**
	 * Returns a new String composed of copies of the {@code strings} joined together with a copy of
	 * the specified {@code separator}.
	 * <p/>
	 * Note that if an element is {@code null}, then {@code "null"} is added.
	 * 
	 * @param separator
	 *            the delimiter that separates each element
	 * @param strings
	 *            the elements to join together.
	 * @return a new {@code String} that is composed of the {@code strings} separated by the
	 *         {@code separator}
	 */
	public static String join(String separator, Iterable<String> strings) {
		StringBuilder appendable = new StringBuilder();
		separator = separator != null ? separator : "";

		if (strings == null) {
			return "";
		}

		Iterator<String> it = strings.iterator();

		if (strings != null && it.hasNext()) {
			appendable.append(it.next());
		}

		while (it.hasNext()) {
			appendable.append(separator).append(it.next());
		}

		return appendable.toString();
	}
}
