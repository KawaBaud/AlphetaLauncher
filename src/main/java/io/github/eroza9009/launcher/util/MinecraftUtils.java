
package io.github.KawaBaud.launcher.util;

import io.github.KawaBaud.launcher.LauncherConfig;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinecraftUtils {

	private static final Logger LOGGER;
	private static final Path LOGS_DIRECTORY_PATH;

	static {
		LOGGER = LoggerFactory.getLogger(MinecraftUtils.class);

		LOGS_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("logs");
	}

	private MinecraftUtils() {
	}

	private static Path getFilePath(String username) {
		String selectedVersion = (String) LauncherConfig.get(4);
		selectedVersion = selectedVersion.replaceAll("[._]", "");

		long currentTime = System.currentTimeMillis();

		String fileName = String.format("%s_%s_%s.log", selectedVersion, username, currentTime);

		Path filePath = LOGS_DIRECTORY_PATH.resolve(fileName);
		try {
			Files.createDirectories(LOGS_DIRECTORY_PATH);
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
			return filePath;
		} catch (IOException ioe) {
			LOGGER.error("Cannot create {}", filePath, ioe);
		}
		return null;
	}

	public static void reassignOutputStream(String username) {
		Path filePath = getFilePath(username);
		if (Objects.isNull(filePath)) {
			return;
		}

		try {
			PrintStream ps = new PrintStream(filePath.toFile()) {
				@Override
				public void println(String x) {
					String filter = addToFilter(x);
					super.println(filter);
				}

				private String addToFilter(String x) {
					String settingUser = "Setting user: ";
					if (x.startsWith(settingUser)) {
						String[] split = x.split(",");
						String[] tokenSplit = split[1].split(":");
						if (tokenSplit.length == 3) {
							String accessToken = tokenSplit[1];
							String profileId = tokenSplit[2];
							return new StringBuilder().append(split[0]).append(", token:").append(accessToken, 0, 0)
									.append("<accessToken>:").append(profileId).toString();
						}
					}
					return x;
				}
			};
			System.setOut(ps);
			System.setErr(ps);
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("Cannot create {}", filePath, fnfe);
		}
	}
}
