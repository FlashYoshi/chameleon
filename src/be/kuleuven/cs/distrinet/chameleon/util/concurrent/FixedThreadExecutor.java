package be.kuleuven.cs.distrinet.chameleon.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.kuleuven.cs.distrinet.chameleon.core.Config;

public class FixedThreadExecutor {

	public FixedThreadExecutor(ExecutorService executor) {
		_executor = executor;
		_availableProcessors = Runtime.getRuntime().availableProcessors();
	}
	public FixedThreadExecutor() {
		_availableProcessors = Runtime.getRuntime().availableProcessors();
		_executor = Executors.newFixedThreadPool(availableProcessors());
	}

	protected int _availableProcessors;

	public int availableProcessors() {
		boolean singleThreaded = Config.singleThreaded();
		if(singleThreaded) {
			return 1;
		} else {
			return _availableProcessors/2;
		}
	}

	protected ExecutorService _executor;

	public ExecutorService executor() {
		return _executor;
	}
	
}
