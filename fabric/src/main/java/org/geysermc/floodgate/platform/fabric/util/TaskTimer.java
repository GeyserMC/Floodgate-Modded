package org.geysermc.floodgate.platform.fabric.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TaskTimer implements ServerTickEvents.EndTick {
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

    @Override
    public void onEndTick(MinecraftServer server) {
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

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(INSTANCE);
    }
}
