package es.caib.plugins.arxiu.filesystem;

import java.util.concurrent.Semaphore;

public class MySemaphore extends Semaphore {
	
	private static MySemaphore semaphore = null;
	
	public static MySemaphore get() {
		
		if(semaphore == null) semaphore = new MySemaphore(1);
		
		return semaphore;
	}

	public MySemaphore(int permits) {
		super(permits);
	}
	
	
	private static final long serialVersionUID = 1L;
	
}
