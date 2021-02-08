/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet

import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Sessions
import org.zkoss.zk.ui.WebApps
import org.zkoss.zk.ui.sys.DesktopCache
import org.zkoss.zk.ui.sys.DesktopCtrl
import org.zkoss.zk.ui.sys.WebAppCtrl
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


abstract class LongOperation : Runnable {
    private var desktopId: String? = null
    private var desktopCache: DesktopCache? = null
    private var thread: Thread? = null
    private val cancelled = AtomicBoolean(false)

    /**
     * asynchronous callback for your long operation code
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    protected abstract fun execute()

    /**
     * optional callback method when the task has completed successfully
     */
    open protected fun onFinish() {}

    /**
     * optional callback method when the task has been cancelled or was interrupted otherwise
     */
    protected fun onCancel() {}

    /**
     * optional callback method when the task has completed with an uncaught RuntimeException
     * @param exception
     */
    protected fun onException(exception: RuntimeException) {
        exception.printStackTrace()
    }

    /**
     * optional callback method when the task has completed (always called)
     */
    open protected fun onCleanup() {}

    /**
     * set the cancelled flag and try to interrupt the thread
     */
    fun cancel() {
        cancelled.set(true)
        thread!!.interrupt()
    }

    /**
     * check the cancelled flag
     * @return
     */
    fun isCancelled(): Boolean {
        return cancelled.get()
    }

    /**
     * activate the thread (and cached desktop) for UI updates
     * call [.deactivate] once done updating the UI
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    protected fun activate() {
        Executions.activate(desktop)
    }

    /**
     * deactivate the current active (see: [.activate]) thread/desktop after updates are done
     */
    protected fun deactivate() {
        Executions.deactivate(desktop)
    }

    /**
     * Checks if the task thread has been interrupted. Use this to check whether or not to exit a busy operation in case.
     * @throws InterruptedException when the current task has been cancelled/interrupted
     */
    @Throws(InterruptedException::class)
    protected fun checkCancelled() {
        check(!(Thread.currentThread() !== thread)) { "this method can only be called in the worker thread (i.e. during execute)" }
        val interrupted = Thread.interrupted()
        if (interrupted || cancelled.get()) {
            cancelled.set(true)
            throw InterruptedException()
        }
    }

    /**
     * launch the long operation
     */
    fun start() {
        //not caching the desktop directly to enable garbage collection, in case the desktop destroyed during the long operation
        desktopId = Executions.getCurrent().desktop.id
        desktopCache = (WebApps.getCurrent() as WebAppCtrl).getDesktopCache(Sessions.getCurrent())
        enableServerPushForThisTask()
        thread = Thread(this)
        thread!!.start()
    }

    override fun run() {
        try {
            try {
                checkCancelled() //avoid unnecessary execution
                execute()
                checkCancelled() //final cancelled check before calling onFinish
                activate()
                onFinish()
            } catch (e: InterruptedException) {
                try {
                    cancelled.set(true)
                    activate()
                    onCancel()
                } catch (e1: InterruptedException) {
                    throw RuntimeException("interrupted onCancel handling", e1)
                } finally {
                    deactivate()
                }
            } catch (rte: RuntimeException) {
                try {
                    activate()
                    onException(rte)
                } catch (e1: InterruptedException) {
                    throw RuntimeException("interrupted onException handling", e1)
                } finally {
                    deactivate()
                }
                throw rte
            } finally {
                deactivate()
            }
        } finally {
            try {
                activate()
                onCleanup()
            } catch (e1: InterruptedException) {
                throw RuntimeException("interrupted onCleanup handling", e1)
            } finally {
                deactivate()
                disableServerPushForThisTask()
            }
        }
    }

    private val taskId = UUID.randomUUID()
    private fun enableServerPushForThisTask() {
        (desktop as DesktopCtrl).enableServerPush(true, taskId)
    }

    private fun disableServerPushForThisTask() {
        (desktop as DesktopCtrl).enableServerPush(false, taskId)
    }

    private val desktop: Desktop
        private get() = desktopCache!!.getDesktop(desktopId)
}