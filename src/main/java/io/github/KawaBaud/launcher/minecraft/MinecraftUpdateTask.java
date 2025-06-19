
package io.github.KawaBaud.launcher.minecraft;

import io.github.KawaBaud.launcher.ui.MinecraftAppletWrapper;
import com.google.api.client.http.GenericUrl;

public class MinecraftUpdateTask implements Runnable {

	private final GenericUrl[] urls;

	public MinecraftUpdateTask(GenericUrl[] urls) {
		this.urls = urls;
	}

	@Override
	public void run() {
		MinecraftAppletWrapper.getInstance().setTaskState(EState.CHECK_CACHE.ordinal());
		MinecraftAppletWrapper.getInstance().setTaskStateMessage(EState.CHECK_CACHE.getMessage());
		MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
		MinecraftAppletWrapper.getInstance().setTaskProgress(5);
		if (!MinecraftUpdate.isGameCached()) {
			MinecraftUpdate.downloadPackages(urls);
			MinecraftUpdate.extractDownloadedPackages();
		}

		if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
			MinecraftUpdate.updateClasspath();

			MinecraftAppletWrapper.getInstance().setTaskState(EState.DONE.ordinal());
			MinecraftAppletWrapper.getInstance().setTaskStateMessage(EState.DONE.getMessage());
			MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
			MinecraftAppletWrapper.getInstance().setTaskProgress(95);
		}
	}
}
