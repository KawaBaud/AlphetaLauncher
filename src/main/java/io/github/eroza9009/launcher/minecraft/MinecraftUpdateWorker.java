
package io.github.KawaBaud.launcher.minecraft;

import io.github.KawaBaud.launcher.ui.MinecraftAppletWrapper;
import com.google.api.client.http.GenericUrl;
import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftUpdateWorker extends SwingWorker<Applet, Void> {

	private static final Logger LOGGER;

	static {
		LOGGER = LoggerFactory.getLogger(MinecraftUpdateWorker.class);
	}

	private final GenericUrl[] urls;

	public MinecraftUpdateWorker(GenericUrl[] urls) {
		this.urls = urls;
	}

	@Override
	protected Applet doInBackground() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<?> future = service.submit(new MinecraftUpdateTask(urls));
		try {
			future.get();

			if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
				return (Applet) MinecraftAppletWrapper.getInstance().getMcAppletClassLoader()
						.loadClass("net.minecraft.client.MinecraftApplet").getDeclaredConstructor().newInstance();
			}
		} catch (ExecutionException ee) {
			LOGGER.error("Error while submitting updater task", ee.getCause());
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();

			LOGGER.error("Interrupted while submitting updater task", ie);
		} catch (ClassNotFoundException cnfe) {
			LOGGER.error("Cannot find MinecraftApplet", cnfe);
		} catch (InstantiationException ie) {
			LOGGER.error("Cannot instantiate MinecraftApplet", ie);
		} catch (IllegalAccessException iae) {
			LOGGER.error("Cannot access MinecraftApplet", iae);
		} catch (InvocationTargetException ite) {
			LOGGER.error("Cannot invoke MinecraftApplet", ite);
		} catch (NoSuchMethodException nsme) {
			LOGGER.error("Cannot find MinecraftApplet constructor", nsme);
		} finally {
			service.shutdown();
		}
		return null;
	}

	@Override
	protected void done() {
		try {
			Applet applet = this.get();
			if (Objects.nonNull(applet)) {
				MinecraftAppletWrapper.getInstance().replace(applet);
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();

			LOGGER.error("Interrupted while replacing applet", ie);
		} catch (ExecutionException ee) {
			LOGGER.error("Error while replacing applet", ee.getCause());
		}
	}
}
