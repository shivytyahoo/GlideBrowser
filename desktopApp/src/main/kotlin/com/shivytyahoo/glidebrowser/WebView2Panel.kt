package com.shivytyahoo.glidebrowser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.glidebrowser.adblock.StealthAdBlocker
import java.awt.Canvas
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import org.eclipse.swt.SWT
import org.eclipse.swt.awt.SWT_AWT
import org.eclipse.swt.browser.Browser
import org.eclipse.swt.browser.LocationAdapter
import org.eclipse.swt.browser.LocationEvent
import org.eclipse.swt.browser.ProgressAdapter
import org.eclipse.swt.browser.ProgressEvent
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

/**
 * Real Windows browser engine: Microsoft Edge WebView2 (system runtime,
 * no bundled Chromium) embedded through SWT's Browser widget (SWT.EDGE)
 * bridged into Compose Desktop via SwingPanel + SWT_AWT — the same pattern
 * production Compose Desktop apps use. The WebView2 runtime ships with
 * Windows 10/11, so the installer stays small.
 */

/** Controls the embedded browser from Compose (address bar, back, reload). */
class WebView2Controller internal constructor() {
    internal var navigateImpl: (String) -> Unit = {}
    internal var backImpl: () -> Unit = {}
    internal var reloadImpl: () -> Unit = {}
    fun navigateTo(url: String) = navigateImpl(url)
    fun goBack() = backImpl()
    fun reload() = reloadImpl()
}

/** Owns the single SWT Display on its own thread. SWT requires the display
 *  and its event loop to live on one dedicated thread. */
private object SwtDisplay {
    private val displayRef = AtomicReference<Display?>()
    private val ready = CountDownLatch(1)

    init {
        thread(name = "swt-display", isDaemon = true) {
            val display = Display()
            displayRef.set(display)
            ready.countDown()
            while (!display.isDisposed) {
                try {
                    if (!display.readAndDispatch()) display.sleep()
                } catch (e: Exception) {
                    // Never let the SWT loop die on one bad event.
                }
            }
        }
    }

    fun display(): Display {
        ready.await()
        return displayRef.get()!!
    }

    fun asyncExec(block: () -> Unit) = display().asyncExec(block)
}

@Composable
fun WebView2Panel(
    url: String,
    controller: WebView2Controller,
    adBlockEnabled: Boolean,
    blocker: StealthAdBlocker,
    onUrlChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val browserRef = remember { AtomicReference<Browser?>() }
    val shellRef = remember { AtomicReference<Shell?>() }
    val creating = remember { AtomicBoolean(false) }
    val adBlockRef = remember { AtomicReference(adBlockEnabled) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            SwtDisplay.asyncExec {
                try { browserRef.get()?.dispose() } catch (e: Exception) { /* already gone */ }
                try { shellRef.get()?.dispose() } catch (e: Exception) { /* already gone */ }
            }
        }
    }

    if (error != null) {
        Box(modifier.fillMaxSize()) {
            Text("WebView2 could not start. Microsoft Edge WebView2 Runtime is required on Windows 10 — it ships with Windows 11. ($error)")
        }
        return
    }

    SwingPanel(
        factory = {
            Canvas().also { canvas ->
                canvas.addComponentListener(object : ComponentAdapter() {
                    override fun componentResized(e: ComponentEvent) {
                        SwtDisplay.asyncExec {
                            val shell = shellRef.get() ?: return@asyncExec
                            if (!shell.isDisposed) {
                                shell.setBounds(0, 0, canvas.width, canvas.height)
                                shell.layout()
                            }
                        }
                    }
                })
                // The canvas needs a native peer before SWT can embed in it.
                canvas.addHierarchyListener {
                    if (canvas.isDisplayable && shellRef.get() == null && creating.compareAndSet(false, true)) {
                        SwtDisplay.asyncExec {
                            try {
                                createBrowser(canvas, url, controller, adBlockRef, blocker, onUrlChanged, browserRef, shellRef)
                            } catch (e: Exception) {
                                error = e.message ?: e.javaClass.simpleName
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize(),
        update = {
            adBlockRef.set(adBlockEnabled)
            SwtDisplay.asyncExec {
                val browser = browserRef.get() ?: return@asyncExec
                if (!browser.isDisposed && browser.url != url) browser.url = url
            }
        },
    )
}

private fun createBrowser(
    canvas: Canvas,
    initialUrl: String,
    controller: WebView2Controller,
    adBlockRef: AtomicReference<Boolean>,
    blocker: StealthAdBlocker,
    onUrlChanged: (String) -> Unit,
    browserRef: AtomicReference<Browser?>,
    shellRef: AtomicReference<Shell?>,
) {
    val display = SwtDisplay.display()
    val shell = SWT_AWT.new_Shell(display, canvas)
    shell.layout = FillLayout()
    val browser = Browser(shell, SWT.EDGE)
    browser.addLocationListener(object : LocationAdapter() {
        override fun changing(event: LocationEvent) {
            // Stealth adblock: veto navigation to a blocked ad/tracker host.
            if (adBlockRef.get() && blocker.shouldBlock(event.location).blocked) {
                event.doit = false
            }
        }

        override fun changed(event: LocationEvent) {
            if (event.top) onUrlChanged(browser.url)
        }
    })
    browser.addProgressListener(object : ProgressAdapter() {
        override fun completed(event: ProgressEvent) {
            onUrlChanged(browser.url)
        }
    })
    shellRef.set(shell)
    browserRef.set(browser)
    shell.setBounds(0, 0, canvas.width, canvas.height)
    shell.layout()
    browser.url = initialUrl
    controller.navigateImpl = { target ->
        SwtDisplay.asyncExec { if (!browser.isDisposed) browser.url = target }
    }
    controller.backImpl = {
        SwtDisplay.asyncExec { if (!browser.isDisposed && browser.isBackEnabled) browser.back() }
    }
    controller.reloadImpl = {
        SwtDisplay.asyncExec { if (!browser.isDisposed) browser.refresh() }
    }
}
