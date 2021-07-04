package com.kryptokrauts.aeternity.sdk.service.delegation;

public interface DelegationService {

  /**
   * creates a delegation signature over "network id + account address + contract address"
   * (concatenated as byte arrays)
   *
   * @param contractId the address of the contract (ct_...)
   * @return the delegation signature that allows a contract to perform a preclaim for an AENS name
   *     on behalf of the configured keypair
   */
  String createAensDelegationSignature(String contractId);

  /**
   * creates a delegation signature over "network id + account address + name hash + contract
   * address" (concatenated as byte arrays)
   *
   * @param contractId the address of the contract (ct_...)
   * @param name the AENS name (e.g. kryptokrauts.chain)
   * @return the delegation signature that allows a contract to perform AENS related actions for a
   *     specific name on behalf of the configured keypair
   */
  String createAensDelegationSignature(String contractId, String name);

  /**
   * creates a delegation signature over "network id + account address + contract" (concatenated as
   * byte arrays)
   *
   * @param contractId the address of the contract (ct_...)
   * @return the delegation signature that allows a contract to create and extend an oracle on
   *     behalf of the configured keypair
   */
  String createOracleDelegationSignature(String contractId);

  /**
   * creates a delegation signature over "network id + oracle query id + contract address"
   * (concatenated as byte arrays)
   *
   * @param contractId the address of the contract (ct_...)
   * @param queryId the query id (oq_...)
   * @return the delegation signature that allows a contract to respond to a specific query on
   *     behalf of the configured (oracle) keypair
   */
  String createOracleDelegationSignature(String contractId, String queryId);
}
