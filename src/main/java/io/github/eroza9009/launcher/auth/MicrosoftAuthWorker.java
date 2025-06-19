
package io.github.KawaBaud.launcher.auth;

import io.github.KawaBaud.launcher.ui.LauncherNoNetworkPanel;
import io.github.KawaBaud.launcher.ui.LauncherPanel;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicrosoftAuthWorker extends SwingWorker<Object, Void> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String clientId;
	private final String deviceCode;
	private final int expiresIn;
	private final int interval;

	public MicrosoftAuthWorker(String clientId, String deviceCode, String expiresIn, String interval) {
		this.clientId = clientId;
		this.deviceCode = deviceCode;
		this.expiresIn = Integer.parseInt(expiresIn);
		this.interval = Integer.parseInt(interval);
	}

	@Override
	protected Object doInBackground() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		Future<?>[] future = new Future<?>[1];
		future[0] = service.scheduleAtFixedRate(new MicrosoftAuthTask(service, clientId, deviceCode), 0,
				interval * 200L, TimeUnit.MILLISECONDS);

		try {
			return future[0].get(expiresIn * 1000L, TimeUnit.MILLISECONDS);
		} catch (ExecutionException ee) {
			this.logger.error("Error while scheduling authentication task", ee.getCause());
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();

			this.logger.error("Interrupted while scheduling authentication task", ie);
		} catch (TimeoutException te) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));

			this.logger.error("Timeout while scheduling authentication task", te);
		} finally {
			service.shutdown();
		}
		return null;
	}
}
