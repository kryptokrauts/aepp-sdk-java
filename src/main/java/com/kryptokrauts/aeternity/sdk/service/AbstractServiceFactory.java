package com.kryptokrauts.aeternity.sdk.service;

import java.util.HashMap;
import java.util.Map;

/**
 * This abstract factory defines the process of retrieving service implementations of different versions in a uniform way
 * by default it returns the current version
 * <p>
 * versions should only be increased when new methods are added to the service interface or existing methods are changed
 * that do not depend on domain object changes
 *
 * @param <T> the service interface to be retrieved
 * @param <E> an enum which defines the available service versions
 */
public abstract class AbstractServiceFactory<T, E> {

    protected Map<E, T> instanceList = new HashMap<>();

    public abstract T getService(E serviceVersion);

    public abstract T getService();
}
