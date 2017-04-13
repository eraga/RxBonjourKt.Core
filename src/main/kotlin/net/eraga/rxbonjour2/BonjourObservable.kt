package net.eraga.rxbonjour2

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import net.eraga.rxbonjour2.discovery.AbstractBonjourDiscovery
import net.eraga.rxbonjour2.model.BonjourEvent
import java.net.InetAddress
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 *
 */
@Suppress("unused")
object BonjourObservable {

    /**
     * Creates Bonjour discovery observable listening on default network interface
     *
     * @param type DNS-SD service type to discover
     * @param discoveryImpl Kotlin class implementing [AbstractBonjourDiscovery]
     */
    @Suppress("unused")
    fun create(
            type: String,
            discoveryImpl: KClass<out AbstractBonjourDiscovery<*>>):
            Observable<BonjourEvent> {

        val discovery = discoveryImpl.primaryConstructor!!.call(type)

        return constructObservable(discovery, null)
    }

    /**
     * Creates Bonjour discovery observable listening on specific network interface
     *
     * @param type DNS-SD service type to discover
     * @param listenAddress [InetAddress] to listen for services
     * @param discoveryImpl Kotlin class implementing [AbstractBonjourDiscovery]
     *
     */
    @Suppress("unused")
    fun create(
            type: String,
            listenAddress: InetAddress,
            discoveryImpl: KClass<out AbstractBonjourDiscovery<*>>):
            Observable<BonjourEvent> {

        val discovery = discoveryImpl.primaryConstructor!!.call(type)

        return constructObservable(discovery, listenAddress)
    }


    private fun constructObservable(
            discovery: AbstractBonjourDiscovery<*>,
            address: InetAddress?): Observable<BonjourEvent> {
        return Observable.create<BonjourEvent> { emitter ->
            discovery.onCreate(emitter, address)
        }.subscribeOn(Schedulers.io())
    }


}
