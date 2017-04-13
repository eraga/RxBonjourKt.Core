package net.eraga.rxbonjour2.model

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

/**
 * Resolved Bonjour service detected within the device's local network.
 */
class BonjourService private constructor(
        /**
         * The service's display name, e.g. 'Office Printer'
         */
        val name: String,
        /**
         * The service's type, e.g. '_http._tcp.local.'
         */
        val type: String,
        /**
         * The IPv4 host address of the service, or null if it doesn't provide any
         */
        val v4Host: Inet4Address?,
        /**
         * The IPv6 host address of the service, or null if it doesn't provide any
         */
        val v6Host: Inet6Address?,
        /**
         * The port on which the service is being broadcast
         */
        val port: Int,
        /**
         * Map containing all TXT records associated with the service, stored as &lt;String, String&gt;
         * key-value pairs.
         * If the service doesn't have any TXT records, or none could be resolved, this returns an empty Map
         */
        val txtRecords: Map<String, String>) {

    /**
     * Obtains the host address of the service.
     * For services with both an IPv4 **and** an IPv6 address, the former address takes precedence over the latter,
     * so that it always favors the v4 address over the v6 one.

     * If you need to access specific addresses, consider using [v4Host] and [v6Host], respectively.

     * @return A host address of the service
     */
    @Suppress("unused")
    val host: InetAddress
        get() = v4Host ?: v6Host as InetAddress

    /**
     * @return The number of TXT records associated with the service
     */
    @Suppress("unused")
    val txtRecordCount: Int
        get() = txtRecords.size

    /**
     * Returns the specific TXT record with the provided key,
     * falling back to the default value if this TXT record doesn't exist.
     *
     * @param key          Key of the TXT record
     * *
     * @param defaultValue Value to return if the TXT record isn't contained in the service's records
     * *
     * @return The associated value for the provided key, or the default value if absent
     */
    @Suppress("unused")
    fun getTxtRecord(key: String, defaultValue: String): String {
        return txtRecords[key] ?: defaultValue
    }

    /**
     * Returns the specific TXT record with the provided key, or null if no such mapping exists.
     *
     * @param key Key of the TXT record
     * *
     * @return The associated value for the provided key, or null if absent
     */
    @Suppress("unused")
    fun getTxtRecord(key: String): String? {
        return txtRecords[key]
    }




    /* Begin static */

    class Builder(private val mName: String, private val mType: String) {
        private var mHost4: Inet4Address? = null
        private var mHost6: Inet6Address? = null
        private var mPort: Int = 0
        private var mTxtRecords: MutableMap <String, String> = HashMap()

        fun addAddress(address: InetAddress): Builder {
            if (address is Inet4Address) {
                mHost4 = address

            } else if (address is Inet6Address) {
                mHost6 = address
            }
            return this
        }

        fun setPort(port: Int): Builder {
            mPort = port
            return this
        }

        fun addTxtRecord(key: String, value: String): Builder {
            mTxtRecords.put(key, value)
            return this
        }

        fun build(): BonjourService {
            return BonjourService(mName, mType, mHost4, mHost6, mPort, mTxtRecords)
        }
    }

    override fun toString(): String {
        return "BonjourService(name='$name', type='$type', v4Host=$v4Host, v6Host=$v6Host, port=$port, txtRecords=$txtRecords)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BonjourService) return false

        if (name != other.name) return false
        if (type != other.type) return false
        if (v4Host != other.v4Host) return false
        if (v6Host != other.v6Host) return false
        if (port != other.port) return false
        if (txtRecords != other.txtRecords) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (v4Host?.hashCode() ?: 0)
        result = 31 * result + (v6Host?.hashCode() ?: 0)
        result = 31 * result + port
        result = 31 * result + txtRecords.hashCode()
        return result
    }
}
