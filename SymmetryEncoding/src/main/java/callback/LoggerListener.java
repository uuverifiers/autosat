package callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerListener implements Listener {

	private static final Logger LOGGER = LogManager.getLogger();

	public void inform(String message) {
		LOGGER.info(message);
	}

}
