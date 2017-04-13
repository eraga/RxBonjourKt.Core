package net.eraga.rxbonjour2.discovery


import io.reactivex.ObservableEmitter
import net.eraga.rxbonjour2.exceptions.DiscoveryFailed
import net.eraga.rxbonjour2.model.BonjourEvent
import net.eraga.rxbonjour2.model.BonjourService
import java.io.IOException
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import javax.jmdns.impl.DNSStatefulObject


/**
 * JmDNS Implementation of bonjour discovery
 */
@Suppress("unused")
class JmDNSDiscovery(type: String) : AbstractBonjourDiscovery<ServiceEvent>(type) {

    companion object {
        private var jmdnsInstance: JmDNS? = null

        /** Number of subscribers using JmDNS  */
        private val jmdnsSubscriberCount = AtomicInteger(0)
    }

    override fun onCreate(
            emitter: ObservableEmitter<BonjourEvent>,
            address: InetAddress?) {
        super.onCreate(emitter, address)


        val listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                event.dns.requestServiceInfo(event.type, event.name)
            }

            override fun serviceRemoved(event: ServiceEvent) {
                emitBonjourEvent(BonjourEvent.Type.REMOVED, event)
            }

            override fun serviceResolved(event: ServiceEvent) {
                emitBonjourEvent(BonjourEvent.Type.ADDED, event)
            }
        }


        // Obtain the current IP address and initialize JmDNS' discovery service with that
        try {
            if(jmdnsInstance == null) {
                jmdnsSubscriberCount.set(0)
                //TODO: If we want to listen to multiple services at different interfaces this won't work
                jmdnsInstance = JmDNS.create(address, address?.hostName)
            }


            emitter.setCancellable {
                Companion.jmdnsInstance?.removeServiceListener(type, listener)
                decrementSubscriberCount()
                closeIfNecessary()
            }

            // Start discovery
            jmdnsInstance?.addServiceListener(type, listener)
            incrementSubscriberCount()


        } catch (e: IOException) {
            emitter.onError(DiscoveryFailed(JmDNSDiscovery::class, type, e))
        }

    }

    override fun toBonjourService(service: ServiceEvent): BonjourService {
        val info = service.info
        val serviceBuilder = BonjourService.Builder(service.name, service.type)

        // Prepare TXT record Bundle
        val keys = info.propertyNames
        while (keys.hasMoreElements()) {
            val key = keys.nextElement()
            serviceBuilder.addTxtRecord(key, info.getPropertyString(key))
        }

        // Add non-null host addresses and port
        val addresses = info.inetAddresses
        for (address in addresses) {
            if (address == null) continue
            serviceBuilder.addAddress(address)
        }
        serviceBuilder.setPort(info.port)

        return serviceBuilder.build()
    }


    /**
     * Returns whether the JmDNS instance is not closing or closed.
     */
    private fun isAvailable(): Boolean {
        val dso = jmdnsInstance as DNSStatefulObject
        return !(dso.isClosing || dso.isClosed)
    }


    /**
     * Increments the count of JmDNS subscribers.
     *
     * @return The updated subscriber count
     */
    fun incrementSubscriberCount(): Int {
        if (isAvailable()) {
            return jmdnsSubscriberCount.incrementAndGet()
        }
        return 0
    }

    /**
     * Decrements the count of JmDNS subscribers.
     *
     * @return The updated subscriber count
     */
    fun decrementSubscriberCount(): Int {
        if (isAvailable()) {
            return jmdnsSubscriberCount.decrementAndGet()
        }
        return 0
    }

    /**
     * Closes the JmDNS instance if there are no longer any subscribers.
     */
    fun closeIfNecessary() {
        if (jmdnsSubscriberCount.get() <= 0) {
            close()
        }
    }

    /**
     * Closes the JmDNS instance.
     */
    fun close() {
        try {
            jmdnsInstance?.close()
            jmdnsInstance = null
        } catch (ignored: IOException) {
        } finally {
            jmdnsSubscriberCount.set(0)
        }
    }
}

