package com.yanosik.rcd.utils;

import java.util.UUID;

public class Utilities {
		Utilities() {}

		public static String generateId() {
				return UUID.randomUUID().toString();
		}

}
