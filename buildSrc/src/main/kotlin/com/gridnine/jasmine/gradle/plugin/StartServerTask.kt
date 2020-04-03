package com.gridnine.jasmine.gradle.plugin

import org.apache.tools.ant.types.Commandline
import org.gradle.api.Incubating
import org.gradle.api.JavaVersion
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.internal.jvm.inspection.JvmVersionDetector
import org.gradle.process.CommandLineArgumentProvider
import org.gradle.process.JavaExecSpec
import org.gradle.process.JavaForkOptions
import org.gradle.process.ProcessForkOptions
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.DefaultJavaExecAction
import org.gradle.process.internal.ExecActionFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.inject.Inject

open class StartServerTask : ConventionTask(), JavaExecSpec{
    private var javaExecHandleBuilder = getExecActionFactory().newJavaExecAction() as DefaultJavaExecAction;

    @Inject
    open protected fun getExecActionFactory(): ExecActionFactory {
        throw UnsupportedOperationException()
    }

    @TaskAction
    fun exec() {
        setMain(getMain()) // make convention mapping work (at least for 'main'...
        setJvmArgs(getJvmArgs()) // ...and for 'jvmArgs')
        setIgnoreExitValue(true)
        javaExecHandleBuilder.build().start()
    }


    /**
     * {@inheritDoc}
     */
    override fun getAllJvmArgs(): List<String?>? {
        return javaExecHandleBuilder.allJvmArgs
    }

    /**
     * {@inheritDoc}
     */
    override  fun setAllJvmArgs(arguments: List<String?>?) {
        javaExecHandleBuilder.allJvmArgs = arguments
    }

    /**
     * {@inheritDoc}
     */
    override  fun setAllJvmArgs(arguments: Iterable<*>?) {
        javaExecHandleBuilder.setAllJvmArgs(arguments)
    }

    /**
     * {@inheritDoc}
     */
    override  fun getJvmArgs(): List<String?>? {
        return javaExecHandleBuilder.jvmArgs
    }

    /**
     * {@inheritDoc}
     */
    override  fun setJvmArgs(arguments: List<String?>?) {
        javaExecHandleBuilder.jvmArgs = arguments
    }

    /**
     * {@inheritDoc}
     */
    override  fun setJvmArgs(arguments: Iterable<*>?) {
        javaExecHandleBuilder.setJvmArgs(arguments)
    }

    /**
     * {@inheritDoc}
     */
    override  fun jvmArgs(arguments: Iterable<*>?): StartServerTask? {
        javaExecHandleBuilder.jvmArgs(arguments)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override  fun jvmArgs(vararg arguments: Any?): StartServerTask? {
        javaExecHandleBuilder.jvmArgs(*arguments)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override  fun getSystemProperties(): Map<String?, Any?>? {
        return javaExecHandleBuilder.systemProperties
    }

    /**
     * {@inheritDoc}
     */
    override  fun setSystemProperties(properties: Map<String?, *>?) {
        javaExecHandleBuilder.systemProperties = properties
    }

    /**
     * {@inheritDoc}
     */
    override  fun systemProperties(properties: Map<String?, *>?): StartServerTask? {
        javaExecHandleBuilder.systemProperties(hashMapOf<String?, Any>())
        return this
    }

    /**
     * {@inheritDoc}
     */
    override  fun systemProperty(name: String?, value: Any?): StartServerTask? {
        javaExecHandleBuilder.systemProperty(name, value)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override  fun getBootstrapClasspath(): FileCollection? {
        return javaExecHandleBuilder.bootstrapClasspath
    }

    /**
     * {@inheritDoc}
     */
    override  fun setBootstrapClasspath(classpath: FileCollection?) {
        javaExecHandleBuilder.bootstrapClasspath = classpath
    }

    /**
     * {@inheritDoc}
     */
    override  fun bootstrapClasspath(vararg classpath: Any?): StartServerTask? {
        javaExecHandleBuilder.bootstrapClasspath(*classpath)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun getMinHeapSize(): String? {
        return javaExecHandleBuilder.minHeapSize
    }

    /**
     * {@inheritDoc}
     */
    override fun setMinHeapSize(heapSize: String?) {
        javaExecHandleBuilder.minHeapSize = heapSize
    }

    /**
     * {@inheritDoc}
     */
    override fun getDefaultCharacterEncoding(): String? {
        return javaExecHandleBuilder.defaultCharacterEncoding
    }

    /**
     * {@inheritDoc}
     */
    override fun setDefaultCharacterEncoding(defaultCharacterEncoding: String?) {
        javaExecHandleBuilder.defaultCharacterEncoding = defaultCharacterEncoding
    }

    /**
     * {@inheritDoc}
     */
    override fun getMaxHeapSize(): String? {
        return javaExecHandleBuilder.maxHeapSize
    }

    /**
     * {@inheritDoc}
     */
    override fun setMaxHeapSize(heapSize: String?) {
        javaExecHandleBuilder.maxHeapSize = heapSize
    }

    /**
     * {@inheritDoc}
     */
    override fun getEnableAssertions(): Boolean {
        return javaExecHandleBuilder.enableAssertions
    }

    /**
     * {@inheritDoc}
     */
    override fun setEnableAssertions(enabled: Boolean) {
        javaExecHandleBuilder.enableAssertions = enabled
    }

    /**
     * {@inheritDoc}
     */
    override fun getDebug(): Boolean {
        return javaExecHandleBuilder.debug
    }

    /**
     * {@inheritDoc}
     */
    @Option(option = "debug-jvm", description = "Enable debugging for the process. The process is started suspended and listening on port 5005.")
    override fun setDebug(enabled: Boolean) {
        javaExecHandleBuilder.debug = enabled
    }

    /**
     * {@inheritDoc}
     */
    override fun getMain(): String? {
        return javaExecHandleBuilder.main
    }

    /**
     * {@inheritDoc}
     */
    override fun setMain(mainClassName: String?): StartServerTask? {
        javaExecHandleBuilder.main = mainClassName
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun getArgs(): List<String?>? {
        return javaExecHandleBuilder.args
    }

    /**
     * Parses an argument list from `args` and passes it to [.setArgs].
     *
     *
     *
     * The parser supports both single quote (`'`) and double quote (`"`) as quote delimiters.
     * For example, to pass the argument `foo bar`, use `"foo bar"`.
     *
     *
     *
     * Note: the parser does **not** support using backslash to escape quotes. If this is needed,
     * use the other quote delimiter around it.
     * For example, to pass the argument `'singly quoted'`, use `"'singly quoted'"`.
     *
     *
     * @param args Args for the main class. Will be parsed into an argument list.
     * @return this
     * @since 4.9
     */
    @Incubating
    @Option(option = "args", description = "Command line arguments passed to the main class. [INCUBATING]")
    fun setArgsString(args: String?): StartServerTask? {
        return setArgs(Arrays.asList(*Commandline.translateCommandline(args)))
    }

    /**
     * {@inheritDoc}
     */
    override fun setArgs(applicationArgs: List<String?>?): StartServerTask? {
        javaExecHandleBuilder.args = applicationArgs
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun setArgs(applicationArgs: Iterable<*>?): StartServerTask? {
        javaExecHandleBuilder.setArgs(applicationArgs)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun args(vararg args: Any?): StartServerTask? {
        javaExecHandleBuilder.args(*args)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun args(args: Iterable<*>?): JavaExecSpec? {
        javaExecHandleBuilder.args(args)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun getArgumentProviders(): List<CommandLineArgumentProvider?>? {
        return javaExecHandleBuilder.argumentProviders
    }

    /**
     * {@inheritDoc}
     */
    override fun setClasspath(classpath: FileCollection?): StartServerTask? {
        javaExecHandleBuilder.classpath = classpath
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun classpath(vararg paths: Any?): StartServerTask? {
        javaExecHandleBuilder.classpath(*paths)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun getClasspath(): FileCollection? {
        return javaExecHandleBuilder.classpath
    }

    /**
     * {@inheritDoc}
     */
    override fun copyTo(options: JavaForkOptions?): StartServerTask? {
        javaExecHandleBuilder.copyTo(options)
        return this
    }

    /**
     * Returns the version of the Java executable specified by [.getExecutable].
     *
     * @since 5.2
     */
    @Input
    @Incubating
    fun getJavaVersion(): JavaVersion? {
        return services.get(JvmVersionDetector::class.java).getJavaVersion(getExecutable())
    }

    /**
     * {@inheritDoc}
     */
    @Internal("covered by getJavaVersion")
    override fun getExecutable(): String? {
        return javaExecHandleBuilder.executable
    }

    /**
     * {@inheritDoc}
     */
    override fun setExecutable(executable: String?) {
        javaExecHandleBuilder.executable = executable
    }

    /**
     * {@inheritDoc}
     */
    override fun setExecutable(executable: Any?) {
        javaExecHandleBuilder.setExecutable(executable)
    }

    /**
     * {@inheritDoc}
     */
    override fun executable(executable: Any?): StartServerTask? {
        javaExecHandleBuilder.executable(executable)
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    override fun getWorkingDir(): File? {
        return javaExecHandleBuilder.workingDir
    }

    /**
     * {@inheritDoc}
     */
    override fun setWorkingDir(dir: File?) {
        javaExecHandleBuilder.workingDir = dir
    }

    /**
     * {@inheritDoc}
     */
    override fun setWorkingDir(dir: Any?) {
        javaExecHandleBuilder.setWorkingDir(dir)
    }

    /**
     * {@inheritDoc}
     */
    override fun workingDir(dir: Any?): StartServerTask? {
        javaExecHandleBuilder.workingDir(dir)
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    override fun getEnvironment(): Map<String?, Any?>? {
        return javaExecHandleBuilder.environment
    }

    /**
     * {@inheritDoc}
     */
    override fun setEnvironment(environmentVariables: Map<String?, *>?) {
        javaExecHandleBuilder.environment = environmentVariables
    }

    /**
     * {@inheritDoc}
     */
    override fun environment(name: String?, value: Any?): StartServerTask? {
        javaExecHandleBuilder.environment(name, value)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun environment(environmentVariables: Map<String?, *>?): StartServerTask? {
        javaExecHandleBuilder.environment(environmentVariables)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun copyTo(target: ProcessForkOptions?): StartServerTask? {
        javaExecHandleBuilder.copyTo(target)
        return this
    }

    /**
     * {@inheritDoc}
     */
    override fun setStandardInput(inputStream: InputStream?): StartServerTask? {
        javaExecHandleBuilder.standardInput = inputStream
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    override fun getStandardInput(): InputStream? {
        return javaExecHandleBuilder.standardInput
    }

    /**
     * {@inheritDoc}
     */
    override fun setStandardOutput(outputStream: OutputStream?): StartServerTask? {
        javaExecHandleBuilder.standardOutput = outputStream
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    override fun getStandardOutput(): OutputStream? {
        return javaExecHandleBuilder.standardOutput
    }

    /**
     * {@inheritDoc}
     */
    override fun setErrorOutput(outputStream: OutputStream?): StartServerTask? {
        javaExecHandleBuilder.errorOutput = outputStream
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    override fun getErrorOutput(): OutputStream? {
        return javaExecHandleBuilder.errorOutput
    }

    /**
     * {@inheritDoc}
     */
    override fun setIgnoreExitValue(ignoreExitValue: Boolean): JavaExecSpec? {
        javaExecHandleBuilder.isIgnoreExitValue = ignoreExitValue
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Input
    override fun isIgnoreExitValue(): Boolean {
        return javaExecHandleBuilder.isIgnoreExitValue
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    override fun getCommandLine(): List<String?>? {
        return javaExecHandleBuilder.commandLine
    }

    /**
     * {@inheritDoc}
     */
    override fun getJvmArgumentProviders(): List<CommandLineArgumentProvider?>? {
        return javaExecHandleBuilder.jvmArgumentProviders
    }
}