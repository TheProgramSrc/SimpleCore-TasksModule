package xyz.theprogramsrc.tasksmodule.bungee

import net.md_5.bungee.api.scheduler.ScheduledTask
import xyz.theprogramsrc.simplecoreapi.bungee.BungeeLoader
import xyz.theprogramsrc.tasksmodule.objects.RecurringTask
import java.util.concurrent.TimeUnit

/**
 * Representation of the BungeeCord Task Manager
 */
class BungeeTasks {

    companion object {
        /**
         * The BungeeCord Task Manager
         */
        val instance = BungeeTasks()
    }

    /**
     * BungeeCord Scheduler
     */
    val scheduler = BungeeLoader.instance.proxy.scheduler

    /**
     * Instance of Bungee Loader
     */
    private val plugin = BungeeLoader.instance

    /**
     * Runs an async task after the specified delay in ticks
     * @param delay The delay in ticks. Defaults to 1 (1 tick = 0.05 seconds)
     * @param task The task to run
     * @return the [ScheduledTask]
     */
    fun runAsync(delay: Int = 1, task: Runnable): ScheduledTask = scheduler.schedule(plugin, task, delay.times(50).toLong(), TimeUnit.MILLISECONDS)

    /**
     * Runs a repeating task asynchronously every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param delay The delay in ticks. Defaults to 1
     * @param period The period in ticks. Defaults to 1
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runAsyncRepeating(delay: Int = 1, period: Int = 1, task: Runnable): RecurringTask {
        val bungeeTask = scheduler.schedule(plugin, task, delay.times(50).toLong(), period.times(50).toLong(), TimeUnit.MILLISECONDS)
        return object:RecurringTask(){
            var cancelled: Boolean = false

            override fun start(): RecurringTask = this.apply {
                if(cancelled) {
                    this@BungeeTasks.runAsyncRepeating(delay, period, task)
                }
            }

            override fun stop(): RecurringTask = this.apply {
                bungeeTask.cancel()
                cancelled = true
            }

        }
    }



}