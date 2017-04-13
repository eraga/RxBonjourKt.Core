package net.eraga.rxbonjour2.discovery

import io.reactivex.Emitter
import io.reactivex.ObservableEmitter
import net.eraga.rxbonjour2.model.BonjourEvent
import net.eraga.rxbonjour2.model.BonjourService
import java.net.InetAddress


abstract class AbstractBonjourDiscovery<in SERVICE>(val type: String) {

    lateinit var mEmitter: Emitter<BonjourEvent>

    protected var created = false


    @Synchronized
    open fun onCreate(
            emitter: ObservableEmitter<BonjourEvent>,
            address: InetAddress?) {
        if(created)
            throw IllegalStateException("onCreate must be called only once")

        mEmitter = emitter

        created = true
    }


    fun emitBonjourEvent(type: BonjourEvent.Type, service: SERVICE) {
        mEmitter.onNext(
                BonjourEvent(
                        type,
                        toBonjourService(service)
                )
        )
    }


//    protected abstract fun discoveryManager(address: InetAddress?): MANAGER

    abstract fun toBonjourService(service: SERVICE): BonjourService
}

