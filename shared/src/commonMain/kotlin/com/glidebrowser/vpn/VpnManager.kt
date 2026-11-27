package com.glidebrowser.vpn

/** The IPChakra (github.com/shivytyahoo/IPChakra) contract.
 *  Android impl = VpnService, Windows impl = wg/openvpn/xray sidecar process.
 *  “Connected” only after a verified connect — never a false Connected.
 */
enum class VpnEngine { WARP_WIREGUARD, OPENVPN, XRAY_STEALTH }
enum class VpnState { DISCONNECTED, CONNECTING, VERIFYING, CONNECTED, RECONNECTING, FAILED }
data class VpnServer(val id: String, val name: String, val country: String, val engine: VpnEngine, val latencyMs: Int? = null, val verified: Boolean = false)
data class VpnStatus(val state: VpnState, val server: VpnServer? = null, val exitIp: String? = null, val dnsInUse: String? = null, val log: String = "")
interface VpnManager {
    suspend fun connect(server: VpnServer, dnsProviderId: String, adShieldOn: Boolean): VpnStatus
    suspend fun disconnect()
    suspend fun verifyTunnel(): Boolean // DNS probe + exit-IP probe
    fun status(): VpnStatus
}
