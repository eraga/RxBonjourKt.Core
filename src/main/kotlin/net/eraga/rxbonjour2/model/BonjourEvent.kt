package net.eraga.rxbonjour2.model

/**
 * Event container class broadcast by [net.eraga.rxbonjour2.discovery.AbstractBonjourDiscovery] implementations.
 * Contains Bonjour service data, as well as information on whether the service was just discovered
 * or lost.
 */
data class BonjourEvent(val type: Type, val service: BonjourService) {

    enum class Type {
        ADDED,
        REMOVED
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BonjourEvent) return false

        if (type != other.type) return false
        if (service != other.service) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + service.hashCode()
        return result
    }

    override fun toString(): String {
        return "BonjourEvent(type=$type, service=$service)"
    }
}

