
package io.github.KawaBaud.launcher.minecraft;

import io.github.KawaBaud.launcher.EPlatform;
import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.ui.MinecraftAppletWrapper;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinecraftUpdate {

	private static final Logger LOGGER;
	private static final Path BIN_DIRECTORY_PATH;
	private static final Path NATIVES_DIRECTORY_PATH;
	private static final Path VERSION_DIRECTORY_PATH;
	private static final Path VERSIONS_DIRECTORY_PATH;

	static {
		LOGGER = LoggerFactory.getLogger(MinecraftUpdate.class);

		BIN_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("bin");
		NATIVES_DIRECTORY_PATH = BIN_DIRECTORY_PATH.resolve("natives");
		if (!Files.exists(NATIVES_DIRECTORY_PATH)) {
			try {
				Files.createDirectories(NATIVES_DIRECTORY_PATH);
			} catch (IOException ioe) {
				LOGGER.error("Cannot create {}", NATIVES_DIRECTORY_PATH);
			}
		}

		String selectedVersion = (String) LauncherConfig.get(4);
		VERSIONS_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("versions");
		VERSION_DIRECTORY_PATH = VERSIONS_DIRECTORY_PATH.resolve(selectedVersion);
		if (!Files.exists(VERSION_DIRECTORY_PATH)) {
			try {
				Files.createDirectories(VERSION_DIRECTORY_PATH);
			} catch (IOException ioe) {
				LOGGER.error("Cannot create {}", VERSION_DIRECTORY_PATH);
			}
		}
	}

	private MinecraftUpdate() {
	}

	public static boolean isGameCached() {
		return isLWJGLJarExistent() && isLWJGLNativeExistent() && isClientJarExistent();
	}

	private static boolean isLWJGLJarExistent() {
		List<String> lwjglJars = getListOfLWJGLJars();
		return lwjglJars.stream().allMatch(lwjglJar -> Files.exists(BIN_DIRECTORY_PATH.resolve(lwjglJar)));
	}

	private static boolean isLWJGLNativeExistent() {
		List<String> listOfLWJGLNatives = getListOfLWJGLNatives();
		return listOfLWJGLNatives.stream().findFirst()
				.filter(lwjglNative -> Files.exists(NATIVES_DIRECTORY_PATH.resolve(lwjglNative))).isPresent();
	}

	private static boolean isClientJarExistent() {
		String selectedVersion = (String) LauncherConfig.get(4);
		String clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
		return Files.exists(VERSION_DIRECTORY_PATH.resolve(clientJar));
	}

	private static List<String> getListOfLWJGLJars() {
		return Arrays.asList("jinput.jar", "jutils.jar", "lwjgl.jar", "lwjgl_util.jar");
	}

	private static List<String> getListOfLWJGLNatives() {
		if (EPlatform.isLinux()) {
			return EPlatform.isAARCH64() || EPlatform.isAMD64()
					? Arrays.asList("libjinput-linux64.so", "liblwjgl64.so", "libopenal64.so")
					: Arrays.asList("libjinput-linux.so", "liblwjgl.so", "libopenal.so");
		}
		if (EPlatform.isMacOS()) {
			return EPlatform.isAARCH64() ? Arrays.asList("liblwjgl.dylib", "openal.dylib")
					: Arrays.asList("libjinput-osx.jnilib", "liblwjgl.jnilib", "openal.dylib");
		}
		if (EPlatform.isWindows()) {
			return EPlatform.isAMD64()
					? Arrays.asList("jinput-dx8_64.dll", "jinput-raw_64.dll", "jinput-wintab.dll", "lwjgl64.dll",
							"OpenAL64.dll")
					: Arrays.asList("jinput-dx8.dll", "jinput-raw.dll", "jinput-wintab.dll", "lwjgl.dll",
							"OpenAL32.dll");
		}
		return new ArrayList<>();
	}

	private static GenericUrl[] getClientUrls() {
		GenericUrl[] urls = new GenericUrl[4];
		urls[0] = new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
				.append("AlphetaLauncher/").append("raw/").append("main/").append("bin/").append("client/")
				.append("legacy_release/").toString());
		urls[1] = new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
				.append("AlphetaLauncher/").append("raw/").append("main/").append("bin/").append("client/")
				.append("legacy_beta/").toString());
		urls[2] = new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
				.append("AlphetaLauncher/").append("raw/").append("main/").append("bin/").append("client/")
				.append("legacy_alpha/").toString());
		urls[3] = new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
				.append("AlphetaLauncher/").append("raw/").append("main/").append("bin/").append("client/")
				.append("legacy_infdev/").toString());
		return urls;
	}

	private static GenericUrl getLWJGLUrls() {
		if (EPlatform.isAARCH64()) {
			return new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
					.append("AlphetaLauncher/").append("raw/").append("main/").append("bin/").append("lwjgl/")
					.append("aarch64/").toString());
		}
		if (EPlatform.isAMD64()) {
			return new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
					.append("AlphetaLauncher/").append("raw/").append("main/").append("bin/").append("lwjgl/")
					.append("amd64/").toString());
		}
		if (EPlatform.isX86()) {
			return new GenericUrl(
					new StringBuilder().append("https://github.com/").append("KawaBaud/").append("AlphetaLauncher/")
							.append("raw/").append("main/").append("bin/").append("lwjgl/").append("x86/").toString());
		}
		return new GenericUrl("");
	}

	public static GenericUrl[] getGenericUrls() {
		String selectedVersion = (String) LauncherConfig.get(4);
		String clientJar = String.format("%s.jar", selectedVersion);
		String[] lwjglNativesZips = { "natives-linux.zip", "natives-macosx.zip", "natives-windows.zip" };

		GenericUrl[] clientUrls = getClientUrls();
		GenericUrl lwjglUrls = getLWJGLUrls();
		GenericUrl[] urls = new GenericUrl[6];
		GenericUrl clientUrl = getClientUrlByPrefix(selectedVersion, clientUrls);

		try {
			if (!isLWJGLJarExistent()) {
				for (int i = 0; i < getListOfLWJGLJars().size(); i++) {
					urls[i] = new GenericUrl(new URL(lwjglUrls.toURL(), getListOfLWJGLJars().get(i)));
				}
			}
			if (!isLWJGLNativeExistent()) {
				String lwjglNativesZip = getLwjglNativeZipByPlatform(lwjglNativesZips);

				if (Objects.nonNull(lwjglNativesZip)) {
					urls[4] = new GenericUrl(new URL(lwjglUrls.toURL(), lwjglNativesZip));
				}
			}
			if (!isClientJarExistent() && (Objects.nonNull(clientUrl))) {
				urls[5] = new GenericUrl(new URL(clientUrl.toURL(), clientJar));
			}
		} catch (MalformedURLException murle) {
			LOGGER.error("Cannot create generic URL(s)", murle);
		}
		return Arrays.stream(urls).filter(Objects::nonNull).toArray(GenericUrl[]::new);
	}

	private static GenericUrl getClientUrlByPrefix(String versionId, GenericUrl[] urls) {
		if (versionId.startsWith("i")) {
			return urls[3];
		}
		if (versionId.startsWith("a")) {
			return urls[2];
		}
		if (versionId.startsWith("b")) {
			return urls[1];
		}
		return urls[0];
	}

	private static String getLwjglNativeZipByPlatform(String[] zips) {
		if (EPlatform.isLinux()) {
			return zips[0];
		}
		if (EPlatform.isMacOS()) {
			return zips[1];
		}
		if (EPlatform.isWindows()) {
			return zips[2];
		}
		return null;
	}

	public static void downloadPackages(GenericUrl[] urls) {
		if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
			MinecraftAppletWrapper.getInstance().setTaskState(EState.DOWNLOAD_PACKAGES.ordinal());
			MinecraftAppletWrapper.getInstance().setTaskStateMessage(EState.DOWNLOAD_PACKAGES.getMessage());
			MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
			MinecraftAppletWrapper.getInstance().setTaskProgress(10);
		}

		String javaIoTmpdir = System.getProperty("java.io.tmpdir");
		Path javaIoTmpDirPath = Paths.get(javaIoTmpdir);

		AtomicInteger currentDownloadSize = new AtomicInteger(0);
		int totalDownloadSize = calculateTotalDownloadSize(urls);
		if (totalDownloadSize == 0) {
			return;
		}

		Arrays.stream(urls)
				.forEachOrdered(url -> download(javaIoTmpDirPath, currentDownloadSize, totalDownloadSize, url));
		move(javaIoTmpDirPath);
	}

	private static int calculateTotalDownloadSize(GenericUrl[] urls) {
		HttpTransport transport = new NetHttpTransport();

		HttpRequestFactory factory = transport.createRequestFactory();

		long size = 0L;
		for (GenericUrl url : urls) {
			try {
				HttpRequest request = factory.buildHeadRequest(url);
				HttpResponse response = request.execute();

				HttpHeaders headers = response.getHeaders();
				long contentLength = headers.getContentLength();
				size += contentLength;
			} catch (NumberFormatException nfe) {
				displayErrorMessage(nfe.getMessage());

				LOGGER.error("Cannot parse size for {}", url, nfe);
			} catch (IOException ioe) {
				displayErrorMessage(ioe.getMessage());

				LOGGER.error("Cannot calculate size for {}", url, ioe);
			}
		}
		return (int) size;
	}

	private static void download(Path p, AtomicInteger current, int total, GenericUrl url) {
		HttpTransport transport = new NetHttpTransport();

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildGetRequest(url);
			HttpResponse response = request.execute();

			int fileNameIndex = url.toString().lastIndexOf("/") + 1;
			String fileName = url.toString().substring(fileNameIndex);
			Path filePath = p.resolve(fileName);

			try (BufferedInputStream bis = new BufferedInputStream(response.getContent());
					OutputStream os = Files.newOutputStream(filePath)) {
				byte[] buffer = new byte[65536];
				int read;

				while ((read = bis.read(buffer)) != -1) {
					os.write(buffer, 0, read);
					current.addAndGet(read);

					int downloadProgress = (current.get() * 100) / total;
					int progress = 10 + ((current.get() * 45) / total);
					MinecraftAppletWrapper.getInstance().setTaskProgressMessage(LauncherLanguageUtils.getGAWKeys()[3],
							fileName, downloadProgress);
					MinecraftAppletWrapper.getInstance().setTaskProgress(progress);
				}
			}
		} catch (FileNotFoundException fnfe) {
			displayErrorMessage(fnfe.getMessage());

			LOGGER.error("Cannot find {}", url, fnfe);
		} catch (IOException ioe) {
			displayErrorMessage(ioe.getMessage());

			LOGGER.error("Cannot download {}", url, ioe);
		}
	}

	private static void move(Path p) {
		String selectedVersion = (String) LauncherConfig.get(4);
		String clientJar = String.format("%s.jar", selectedVersion);

		Consumer<String> move = fileName -> {
			Path srcFile = p.resolve(fileName);
			Path destFile;

			if (Objects.equals(fileName, clientJar)) {
				destFile = VERSION_DIRECTORY_PATH.resolve(fileName);
			} else
				destFile = fileName.startsWith("natives-") && fileName.endsWith(".zip")
						? NATIVES_DIRECTORY_PATH.resolve(fileName)
						: BIN_DIRECTORY_PATH.resolve(fileName);

			try {
				if (Files.exists(srcFile)) {
					Files.move(srcFile, destFile);
				}
			} catch (IOException ioe) {
				displayErrorMessage(ioe.getMessage());

				LOGGER.error("Cannot move {} to {}", srcFile, destFile, ioe);
			}
		};
		move.accept(clientJar);

		File[] zipFiles = p.toFile().listFiles((dir, name) -> name.startsWith("natives-") && name.endsWith(".zip"));
		if (Objects.nonNull(zipFiles)) {
			Arrays.stream(zipFiles).map(File::getName).forEach(move);
		}

		List<String> jarFiles = getListOfLWJGLJars();
		jarFiles.forEach(move);
	}

	public static void extractDownloadedPackages() {
		if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
			MinecraftAppletWrapper.getInstance().setTaskState(EState.EXTRACT_PACKAGES.ordinal());
			MinecraftAppletWrapper.getInstance().setTaskStateMessage(EState.EXTRACT_PACKAGES.getMessage());
			MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
			MinecraftAppletWrapper.getInstance().setTaskProgress(55);
		}

		File[] zipFiles = NATIVES_DIRECTORY_PATH.toFile()
				.listFiles((dir, name) -> name.startsWith("natives-") && name.endsWith(".zip"));
		if (Objects.isNull(zipFiles)) {
			return;
		}

		AtomicInteger currentExtractSize = new AtomicInteger(0);
		int totalExtractSize = calculateTotalExtractSize(zipFiles);
		if (totalExtractSize == 0) {
			return;
		}

		Arrays.stream(zipFiles).forEachOrdered(f -> extract(currentExtractSize, totalExtractSize, f.toPath()));
	}

	private static int calculateTotalExtractSize(File[] files) {
		long size = 0L;

		for (File file : files) {
			try (InputStream is = Files.newInputStream(file.toPath());
					BufferedInputStream bis = new BufferedInputStream(is);
					ZipInputStream zis = new ZipInputStream(bis)) {
				ZipEntry entry;

				while (Objects.nonNull(entry = zis.getNextEntry())) {
					size += entry.getSize();
				}
			} catch (FileNotFoundException fnfe) {
				displayErrorMessage(fnfe.getMessage());

				LOGGER.error("Cannot find {}", file, fnfe);
			} catch (IOException ioe) {
				displayErrorMessage(ioe.getMessage());

				LOGGER.error("Cannot calculate size for {}", file, ioe);
			}
		}
		return (int) size;
	}

	private static void extract(AtomicInteger current, int total, Path p) {
		try (InputStream is = Files.newInputStream(p);
				BufferedInputStream bis = new BufferedInputStream(is);
				ZipInputStream zis = new ZipInputStream(bis)) {
			ZipEntry entry;

			while (Objects.nonNull(entry = zis.getNextEntry())) {
				String name = entry.getName();
				if (!entry.isDirectory() && name.indexOf(47) != -1) {
					continue;
				}

				Path nativePath = NATIVES_DIRECTORY_PATH.resolve(name);
				if (!nativePath.startsWith(NATIVES_DIRECTORY_PATH)) {
					LOGGER.warn("{} is invalid path", nativePath);
					return;
				}

				try (OutputStream os = Files.newOutputStream(nativePath)) {
					byte[] buffer = new byte[65536];
					int read;

					while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
						os.write(buffer, 0, read);
						current.addAndGet(read);

						int extractProgress = (current.get() * 100) / total;
						int progress = 55 + ((current.get() * 30) / total);
						MinecraftAppletWrapper.getInstance()
								.setTaskProgressMessage(LauncherLanguageUtils.getGAWKeys()[3], name, extractProgress);
						MinecraftAppletWrapper.getInstance().setTaskProgress(progress);
					}
				}
			}
		} catch (FileNotFoundException fnfe) {
			displayErrorMessage(fnfe.getMessage());

			LOGGER.error("Cannot find {}", p, fnfe);
		} catch (IOException ioe) {
			displayErrorMessage(ioe.getMessage());

			LOGGER.error("Cannot extract {}", p, ioe);
		} finally {
			try {
				if (!Files.deleteIfExists(p)) {
					LOGGER.warn("Could not delete {}", p);
				}
			} catch (IOException ioe) {
				displayErrorMessage(ioe.getMessage());

				LOGGER.error("Cannot delete {}", p, ioe);
			}
		}
	}

	public static void updateClasspath() {
		if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
			MinecraftAppletWrapper.getInstance().setTaskState(EState.UPDATE_CLASSPATH.ordinal());
			MinecraftAppletWrapper.getInstance().setTaskStateMessage(EState.UPDATE_CLASSPATH.getMessage());
			MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
			MinecraftAppletWrapper.getInstance().setTaskProgress(90);
		}

		File[] jarFiles = BIN_DIRECTORY_PATH.toFile().listFiles((dir, name) -> name.endsWith(".jar"));
		String selectedVersion = (String) LauncherConfig.get(4);
		String clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
		File clientJarFile = VERSION_DIRECTORY_PATH.resolve(clientJar).toFile();

		if (Objects.nonNull(jarFiles)) {
			URL[] jarUrls = new URL[jarFiles.length + 1];
			try {
				for (int i = 0; i < jarFiles.length; i++) {
					jarUrls[i] = jarFiles[i].toURI().toURL();
				}

				jarUrls[jarFiles.length] = clientJarFile.toURI().toURL();
			} catch (MalformedURLException murle) {
				displayErrorMessage(murle.getMessage());

				LOGGER.error("Cannot parse {} as URL", jarUrls, murle);
			}

			MinecraftAppletWrapper.getInstance()
					.setMcAppletClassLoader(AccessController.doPrivileged((PrivilegedAction<URLClassLoader>) () -> {
						ClassLoader loader = Thread.currentThread().getContextClassLoader();
						URLClassLoader urlLoader = new URLClassLoader(jarUrls, loader);

						Thread.currentThread().setContextClassLoader(urlLoader);
						return urlLoader;
					}));

			String[] libraryPaths = new String[] { "org.lwjgl.librarypath", "net.java.games.input.librarypath" };
			Arrays.stream(libraryPaths)
					.forEachOrdered(libraryPath -> System.setProperty(libraryPath, NATIVES_DIRECTORY_PATH.toString()));
		}
	}

	private static void displayErrorMessage(String message) {
		MinecraftAppletWrapper.getInstance().setUpdaterTaskErrored(true);

		UTF8ResourceBundle bundle = LauncherLanguage.getBundle();

		int state = MinecraftAppletWrapper.getInstance().getTaskState();
		String fatalErrorMessage = MessageFormat.format(bundle.getString(LauncherLanguageUtils.getGAWKeys()[2]), state,
				message);
		MinecraftAppletWrapper.getInstance().setTaskStateMessage(fatalErrorMessage);
		MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
	}
}
