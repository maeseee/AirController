package org.air_controller;

class TrackableRunnable implements Runnable {
    private final Runnable delegate;
    private volatile Thread executingThread;

    TrackableRunnable(Runnable delegate) { this.delegate = delegate; }

    @Override
    public void run() {
        executingThread = Thread.currentThread();
        try {
            delegate.run();
        } finally {
            executingThread = null;
        }
    }

    public String getCurrentThreadCallStack() {
        if (executingThread != null) {
            final StringBuilder callStack = new StringBuilder();
            for (StackTraceElement ste : executingThread.getStackTrace()) {
                callStack.append("at ").append(ste).append("\n");
            }
            return callStack.toString();
        } else {
            return "Task not currently executing (maybe finished or not started)";
        }
    }
}
