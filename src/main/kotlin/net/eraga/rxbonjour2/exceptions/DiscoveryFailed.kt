package net.eraga.rxbonjour2.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when service discovery fails upon starting
 */
class DiscoveryFailed(message: String, cause: Throwable?) : Exception(message, cause) {
    constructor(implClass: KClass<*>, type: String, errorCode: Int) : this(
            implClass.simpleName + " discovery failed for type " + type + " with error code: " + errorCode,
            null
    )

    constructor(implClass: KClass<*>, type: String, cause: Exception) : this(
            implClass.simpleName + " discovery failed for type " + type + " with cause: " + cause.message,
            cause
    )
}
