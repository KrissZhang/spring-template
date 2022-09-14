package com.self.common.context;

import com.self.common.jwt.JWTInfo;

public class JWTInfoContext {

	private JWTInfoContext() {
	}

	private static final ThreadLocal<JWTInfo> JWT_INFO_THREAD_LOCAL = new ThreadLocal<>();

	public static JWTInfo get() {
		return JWT_INFO_THREAD_LOCAL.get();
	}

	public static void set(JWTInfo jwtInfo) {
		JWT_INFO_THREAD_LOCAL.set(jwtInfo);
	}

	public static void clean() {
		if (JWT_INFO_THREAD_LOCAL.get() != null) {
			JWT_INFO_THREAD_LOCAL.remove();
		}
	}

}
