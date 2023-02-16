package xyz.theprogramsrc.tasksmodule.velocity

import com.velocitypowered.api.scheduler.ScheduledTask
import xyz.theprogramsrc.simplecoreapi.velocity.VelocityLoader
import xyz.theprogramsrc.tasksmodule.objects.RecurringTask
import java.util.concurrent.TimeUnit

class VelocityTasks {

    companion object {
        /**
         * The Velocity Task Manager
         */
        val instance = VelocityTasks()
    }

    /**
     * Velocity Scheduler
     */
    val scheduler = VelocityLoader.instance.server.scheduler

    /**
     * Instance of the Velocity Loader
     */
    private val plugin = VelocityLoader.instance

    /**
     * Runs an async task after the specified delay in ticks
     * @param delay The delay in ticks. Defaults to 1 (1 tick = 0.05 seconds)
     * @param task The task to run
     * @return the [ScheduledTask]
     */
    fun runAsync(delay: Int = 1, task: () -> Unit): ScheduledTask =
        scheduler.buildTask(plugin, task)
            .delay(delay.times(50).toLong(), TimeUnit.MILLISECONDS)
            .schedule()

    /**
     * Runs a repeating task asynchronously every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param delay The delay in ticks. Defaults to 1
     * @param period The period in ticks. Defaults to 1
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runAsyncRepeating(delay: Int = 1, period: Int = 1, task: () -> Unit): RecurringTask {
        val velocityTask = scheduler.buildTask(plugin, task)
            .delay(delay.times(50).toLong(), TimeUnit.MILLISECONDS)
            .repeat(period.times(50).toLong(), TimeUnit.MILLISECONDS)
            .schedule()
        return object:RecurringTask(){
            var cancelled: Boolean = false

            override fun start(): RecurringTask = this.apply {
                if(cancelled) {
                    this@VelocityTasks.runAsyncRepeating(delay, period, task)
                    cancelled = false
                }
            }

            override fun stop(): RecurringTask = this.apply {
                velocityTask.cancel()
                cancelled = true
            }

        }
    }

}