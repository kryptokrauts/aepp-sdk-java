package com.kryptokrauts.aeternity.sdk.config;

public class AEConfig {

    // vm version specification
    // https://github.com/aeternity/protocol/blob/master/contracts/contract_vms.md//virtual-machines-on-the-%C3%A6ternity-blockchain
    public static final int AEVM_NO_VM = 0; // NO_VM (Used in oracles)
    public static final int AEVM_01_SOPHIA_01 = 1; // For Sophia contracts on the AEVM
    public static final int AEVM_01_SOLIDITY_01 = 2; // For Solidity contracts on the AEVM
    public static final int FTWVM_01_SOPHIA_02 = 3; // For Sophia contracts on the FTWVM
    public static final int HLM_01_VARNA_01 = 4; // For Varna contracts on the HLM
    public static final int FAEVM_01_SOLIDITY_01 = 5; // For fast execution of Solidity contracts

    // fee calculation
    public static final int GAS_PER_BYTE = 20;
    public static final int BASE_GAS = 15000;

    // max number of block into the future that the name is going to be available
    // https://github.com/aeternity/protocol/blob/epoch-v0.22.0/AENS.md//update
    // https://github.com/aeternity/protocol/blob/44a93d3aab957ca820183c3520b9daf6b0fedff4/AENS.md//aens-entry
    public static final int NAME_MAX_TLL = 36000;
    public static final int NAME_CLIENT_TTL = 60000;
    public static final int DEFAULT_NAME_TTL = 500;

    // default relative ttl in number of blocks  for executing transaction on the chain
    public static final int MAX_TX_TTL = Integer.MAX_VALUE;
    public static final int DEFAULT_TX_TTL = 0;

    // default fee for posting transaction
    public static final int DEFAULT_FEE = 20000;

    // contracts
    public static final int CONTRACT_DEFAULT_GAS = 170000;
    public static final int CONTRACT_DEFAULT_GAS_PRICE = 1;
    public static final int CONTRACT_DEFAULT_DEPOSIT = 4;
    public static final int CONTRACT_DEFAULT_VM_VERSION = 1;
    public static final int CONTRACT_DEFAULT_AMOUNT = 1;

    // oracles
    // https://github.com/aeternity/protocol/blob/master/oracles/oracles.md//technical-aspects-of-oracle-operations
    public static final int ORACLE_DEFAULT_QUERY_FEE = 30000;
    public static final String ORACLE_DEFAULT_TTL_TYPE_DELTA = "delta";
    public static final String ORACLE_DEFAULT_TTL_TYPE_BLOCK = "block";
    public static final int ORACLE_DEFAULT_TTL_VALUE = 500;
    public static final int ORACLE_DEFAULT_QUERY_TTL_VALUE = 10;
    public static final int ORACLE_DEFAULT_RESPONSE_TTL_VALUE = 10;
    public static final int ORACLE_DEFAULT_VM_VERSION = AEVM_NO_VM;


    // network id
    public static final Network DEFAULT_NETWORK_ID = Network.MAINNET;

    // TUNING
    public static final int MAX_RETRIES = 8; // used in exponential backoff when checking a transaction
    public static final int POLLING_INTERVAL = 2; // in seconds

    private boolean nativeMode;
    private String apiBaseUrl;
    private Network network;

    // TODO initialize services with this config
    public AEConfig(final boolean nativeMode, final String apiBaseUrl, final Network network) {
        this.apiBaseUrl = apiBaseUrl;
        this.network = network;
        this.nativeMode = nativeMode;
    }
}
