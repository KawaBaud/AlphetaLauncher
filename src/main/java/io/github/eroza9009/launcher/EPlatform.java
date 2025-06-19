
package io.github.KawaBaud.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public enum EPlatform {
	LINUX(Arrays.asList("aix", "nix", "nux"), null), MACOS(Arrays.asList("darwin", "mac"), null),
	WINDOWS(Collections.singletonList("win"), null), AARCH64(null, "aarch64"), AMD64(null, "amd64"), X86(null, "x86");

	public static final String OS_NAME;
	public static final String OS_ARCH;

	static {
		OS_NAME = System.getProperty("os.name");
		OS_ARCH = System.getProperty("os.arch");
	}

	private final List<String> names;
	private final String arch;

	EPlatform(List<String> names, String arch) {
		this.names = Objects.isNull(names) ? null : Collections.unmodifiableList(new ArrayList<>(names));
		this.arch = arch;
	}

	public static EPlatform getOSName() {
		String osNameLowerCase = OS_NAME.toLowerCase(Locale.ROOT);
		for (EPlatform platform : values()) {
			for (String name : platform.names) {
				if (osNameLowerCase.contains(name)) {
					return platform;
				}
			}
		}
		return null;
	}

	public static EPlatform getOSArch() {
		for (EPlatform platform : values()) {
			if (Objects.equals(platform.arch, OS_ARCH)) {
				return platform;
			}
		}
		return null;
	}

	public static boolean isWindows() {
		return Objects.equals(WINDOWS, getOSName());
	}

	public static boolean isMacOS() {
		return Objects.equals(MACOS, getOSName());
	}

	public static boolean isLinux() {
		return Objects.equals(LINUX, getOSName());
	}

	public static boolean isX86() {
		return Objects.equals(X86, getOSArch());
	}

	public static boolean isAMD64() {
		return Objects.equals(AMD64, getOSArch());
	}

	public static boolean isAARCH64() {
		return Objects.equals(AARCH64, getOSArch());
	}
}
