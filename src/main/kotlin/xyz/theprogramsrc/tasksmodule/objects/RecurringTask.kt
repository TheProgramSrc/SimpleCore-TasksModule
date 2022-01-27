package xyz.theprogramsrc.tasksmodule.objects

abstract class RecurringTask {

    abstract fun start(): RecurringTask

    abstract fun stop(): RecurringTask
}