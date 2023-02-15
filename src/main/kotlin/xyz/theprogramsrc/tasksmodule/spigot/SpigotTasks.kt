package xyz.theprogramsrc.tasksmodule.spigot

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.tasksmodule.objects.RecurringTask

/**
 * Representation of the Spigot Task Manager
 */
class SpigotTasks {

    companion object {
        /**
         * The Spigot Task Manager
         */
        val instance = SpigotTasks()
    }

    /**
     * Bukkit Scheduler
     */
    val scheduler = Bukkit.getScheduler()

    /**
     * Instance of Spigot Loader
     */
    private val plugin = SpigotLoader.instance

    /**
     * Runs a task after 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTask(task: () -> Unit): BukkitTask = runTaskLater(task = task)

    /**
     * Runs an async task after 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTaskAsynchronously(task: () -> Unit): BukkitTask = runTaskLaterAsynchronously(task = task)

    /**
     * Runs a task after the given ticks (1 tick = 0.05 seconds)
     * @param ticks The ticks to run after. Defaults to 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTaskLater(ticks: Long = 1, task: () -> Unit): BukkitTask = scheduler.runTaskLater(plugin, task, ticks)

    /**
     * Runs an async task after the given ticks (1 tick = 0.05 seconds)
     * @param ticks The ticks to run after. Defaults to 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [BukkitTask]
     */
    fun runTaskLaterAsynchronously(ticks: Long = 1, task: () -> Unit): BukkitTask = scheduler.runTaskLaterAsynchronously(plugin, task, ticks)

    /**
     * Runs a repeating task every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param period The ticks to wait between each run. Defaults to 1 tick (0.05 seconds)
     * @param delay The ticks to wait before the first run. Defaults to 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runTaskTimer(period: Long = 1, delay: Long = 1, task: () -> Unit): RecurringTask = createRecurringTask {
        scheduler.runTaskTimer(plugin, task, delay, period)
    }

    /**
     * Runs a repeating task asynchronously every given ticks (1 tick = 0.05 seconds) after the given ticks (1 tick = 0.05 seconds)
     * @param period The ticks to wait between each run. Defaults to 1 tick (0.05 seconds)
     * @param delay The ticks to wait before the first run. Defaults to 1 tick (0.05 seconds)
     * @param task The task to run
     * @return the [RecurringTask]
     */
    fun runTaskTimerAsynchronously(period: Long = 1, delay: Long = 1, task: () -> Unit): RecurringTask = createRecurringTask {
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, period)
    }

    private fun createRecurringTask(bukkitTask: () -> BukkitTask): RecurringTask {
        return object:RecurringTask() {
            private var task: BukkitTask? = null

            init {
                task = bukkitTask.invoke()
            }

            override fun start(): RecurringTask {
                if(task == null){
                    task = bukkitTask.invoke()
                    return this
                }

                if(!scheduler.isCurrentlyRunning(task!!.taskId)) {
                    task = bukkitTask.invoke()
                }
                return this
            }

            override fun stop(): RecurringTask {
                task?.cancel()
                task = null
                return this
            }
        }
    }
}