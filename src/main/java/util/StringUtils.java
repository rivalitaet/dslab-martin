package util;

import java.util.List;

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
	public static String join(String separator, List<String> strings) {
		StringBuilder appendable = new StringBuilder();
		if (strings != null && strings.size() > 0) {
			separator = separator != null ? separator : "";
			appendable.append(strings.get(0));
			for (int i = 1; i < strings.size(); i++) {
				appendable.append(separator).append(strings.get(i));
			}
		}
		return appendable.toString();
	}
}
