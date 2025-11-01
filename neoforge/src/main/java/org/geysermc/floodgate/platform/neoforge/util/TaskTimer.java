package org.geysermc.floodgate.platform.neoforge.util;

import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class TaskTimer {
    public static final TaskTimer INSTANCE = new TaskTimer();
    private final List<Task> tasks = new LinkedList<>();

    private static class Task {
        long ticks;
        Runnable run;

        Task(long ticks, Runnable run) {
            this.ticks = ticks;
            this.run = run;
        }
    }

    public void runLater(Runnable runnable, int delay) {
        tasks.add(new Task(delay, runnable));
    }

    public void onEndTick(ServerTickEvent.Post event) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (--task.ticks <= 0) {
                try {
                    task.run.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iterator.remove();
            }
        }
    }
}
