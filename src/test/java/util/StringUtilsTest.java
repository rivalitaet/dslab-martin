package util;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testJoin() {
		String s1 = "Hello";
		String s2 = "World";
		List<String> list = new LinkedList<>();
		list.add(s1);
		list.add(s2);

		String result = StringUtils.join(",", list);
		assertEquals("Hello,World", result);
	}
}
