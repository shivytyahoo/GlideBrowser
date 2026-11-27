package com.shivytyahoo.glidebrowser
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
/** S-AdShield's local-only VpnService — only DNS traffic goes through the tunnel, the rest stays outside.
 *  The full DNS packet engine (like DnsPacket.kt) comes from the S-AdShield repo — this file only has the TUN setup + lifecycle.
 *  Do NOT start this service when the VPN (IPChakra) is ON — Android allows only one VpnService at a time. */
class GlideVpnService : VpnService() {
    private var tun: ParcelFileDescriptor? = null
    companion object { const val STUB_IP = "10.10.10.3"; const val ACTION_STOP = "com.shivytyahoo.glidebrowser.STOP_VPN" }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) { stopSelf(); return START_NOT_STICKY }
        tun = Builder().addAddress("10.10.10.2", 32).addRoute("8.8.8.8", 32).addRoute("1.1.1.1", 32)
            .addDnsServer("8.8.8.8").setSession("GlideBrowser Shield").establish()
        return START_STICKY
    }
    override fun onDestroy() { tun?.close(); tun = null; super.onDestroy() }
}
