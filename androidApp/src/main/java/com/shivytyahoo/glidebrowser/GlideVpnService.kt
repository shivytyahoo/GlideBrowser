package com.shivytyahoo.glidebrowser
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
/** S-AdShield ka local-only VpnService — sirf DNS traffic tunnel me jata hai, baaki traffic bahar.
 *  Poora DNS packet engine (DnsPacket.kt jaisa) S-AdShield repo se lena hai — yahan TUN setup + lifecycle hai.
 *  VPN (IPChakra) ON ho to ye service start MAT karo — Android pe ek hi VpnService chal sakta hai. */
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
