package com.kryptokrauts.aeternity.sdk.service;

import java.util.HashMap;
import java.util.Map;

/**
 * This abstract factory defines the process of retrieving service
 * implementations of different versions in a uniform way
 * by default it returns the current version
 * <p>
 * versions should only be increased when new methods are added to the service
 * interface or existing methods are changed
 * that do not depend on domain object changes
 * 
 * @param <T>
 *            the service interface to be retrieved
 * @param <C>
 *            a pojo containing a set of service parameters with defaults, also
 *            see {@link ServiceConfiguration}
 */
public abstract class AbstractServiceFactory<T, C extends ServiceConfiguration> {
    private Map<C, T> instanceList = new HashMap<>();

    /**
     * @return a singleton instance of the service class with default
     *         configuration
     */
    public abstract T getService();

    /**
     * 
     * @param config
     *            the configuration pojo
     * @return a singleton instance of the service class with the given
     *         configuration. all further calls to this method with the same
     *         config object will return the same instance
     */
    public T getService( final C config ) {
        T instanceToReturn = instanceList.get( config );

        if ( instanceToReturn == null ) {
            instanceToReturn = getServiceWithConfig( config );

            instanceList.put( config, instanceToReturn );
        }
        return instanceToReturn;
    }

    /**
     * 
     * @param config
     *            the configuration pojo
     * @return the concrete factory implementation
     */
    protected abstract T getServiceWithConfig( final C config );
}
