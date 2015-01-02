package org.notmysock.profiler;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class Lajja {
	
	public static final class BacktraceTimer extends TimerTask {
		
		private final ThreadMXBean watcher = ManagementFactory.getThreadMXBean();
		private final StringBuilder sb = new StringBuilder();
		
		private String getBackTrace(ThreadInfo thread) {
			sb.setLength(0);
			StackTraceElement[] stacktrace = thread.getStackTrace();
			for (StackTraceElement se : stacktrace) {
				sb.append(se.getClassName());
				sb.append("::");
				sb.append(se.getMethodName());
				sb.append("->");
			}
			return sb.toString();
		}

		@Override
		public void run() {
			final long id = Thread.currentThread().getId();
			ThreadInfo[] threads = watcher.dumpAllThreads(false, false);
			long t1 = System.currentTimeMillis();
			for (ThreadInfo t : threads) {
				if (t.getThreadId() == id) {
					continue;
				}
				System.err.println("[" + t.getThreadName() + "]:" + getBackTrace(t));
			}
		}
	};
	
	public static void premain(String options, Instrumentation instrumentation) {
		Timer t = new Timer("Lajja", true);
		t.scheduleAtFixedRate(new BacktraceTimer(), 1000, 100);
	}
}
