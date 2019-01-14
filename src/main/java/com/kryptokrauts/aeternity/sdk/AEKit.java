package com.kryptokrauts.aeternity.sdk;

import com.kryptokrauts.aeternity.generated.epoch.ApiClient;
import com.kryptokrauts.aeternity.generated.epoch.Configuration;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairService;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ChainService;
import com.kryptokrauts.aeternity.sdk.service.TransactionService;
import com.kryptokrauts.aeternity.sdk.signer.service.SignerService;
import com.kryptokrauts.aeternity.sdk.signer.service.SignerServiceFactory;
import com.kryptokrauts.aeternity.sdk.wallet.service.WalletService;
import com.kryptokrauts.aeternity.sdk.wallet.service.WalletServiceFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * base class to get the access the service providing classes
 */
public class AEKit {

    private static KeyPairServiceFactory keyPairServiceFactory = new KeyPairServiceFactory();

    private static SignerServiceFactory signerServiceFactory = new SignerServiceFactory();

    private static WalletServiceFactory walletServiceFactory = new WalletServiceFactory();

    /**
     * @return latest implementation of keypair related service
     */
    public static KeyPairService getKeyPairService() {
        return keyPairServiceFactory.getService();
    }

    /**
     * @return latest implementation of signing related service
     */
    public static SignerService getSignerService() {
        return signerServiceFactory.getService();
    }

    /**
     * @return latest implementation of wallet related service
     */
    public static WalletService getWalletService() {
        return walletServiceFactory.getService();
    }

    /**
     * @param nativeMode
     * @return interface to transaction related functions
     */
    public static TransactionService getTransactionService(final boolean nativeMode) {
        return TransactionService.getInstance(nativeMode);
    }

    /**
     * @return interface to chain related functions
     */
    public static ChainService getChainService() {
        return ChainService.getInstance();
    }

    public static ApiClient getApiClient(final String baseUrl) {
        JsonObject config = new JsonObject();
        config.put("basePath", baseUrl);
        return Configuration.setupDefaultApiClient(Vertx.vertx(), config);
    }
}
