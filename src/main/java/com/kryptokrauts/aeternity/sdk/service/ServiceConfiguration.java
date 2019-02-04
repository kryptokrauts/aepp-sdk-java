package com.kryptokrauts.aeternity.sdk.service;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.ApiClient;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.wallet.WalletServiceConfiguration;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Builder.Default;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * a lomboked class with service parameters should extend this configuration
 * class like
 * 
 * <pre>
 *   
 * {@literal @}Getter
 * {@literal @}Builder( builderMethodName = "configure", buildMethodName =
 * "compile" )
 * public class <Servicename>ServiceConfiguration implements
 * ServiceConfiguration
 * </pre>
 * 
 * parameters should provide default values in the following way
 * 
 * <pre>
 * {@literal @}Builder.Default
 * <type> paramName = "value";
 * </pre>
 * 
 * examples see {@link WalletServiceConfiguration}
 */
@SuperBuilder( builderMethodName = "configure", buildMethodName = "compile" )
@NoArgsConstructor
public class ServiceConfiguration {

    private static final Logger _logger = LoggerFactory.getLogger( ServiceConfiguration.class );

    @Default
    @Nonnull
    protected String baseUrl = BaseConstants.DEFAULT_TESTNET_URL;

    /**
     * the vertx instance
     */
    protected Vertx vertx;

    /**
     * 
     * @return apiClient initalized with default or given values of vertx and
     *         baseURL
     */
    public ApiClient getApiClient() {
        if ( vertx == null ) {
            _logger.debug( "Vertx entry point not initialized, creating default" );
            vertx = Vertx.vertx();
        }
        if ( vertx != null && baseUrl != null ) {
            _logger.debug( String.format( "Initializing Vertx ApiClient using baseUrl", baseUrl ) );
            return new ApiClient( vertx, new JsonObject( new HashMap<String, Object>( ImmutableMap.of( BaseConstants.VERTX_BASE_PATH, baseUrl ) ) ) );
        }
        else
            throw new RuntimeException( "Cannot intantiate ApiClient due to missing params vertx and or baseUrl" );
    }

}
