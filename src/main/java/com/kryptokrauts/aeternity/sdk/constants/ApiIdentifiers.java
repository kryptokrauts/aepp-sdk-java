package com.kryptokrauts.aeternity.sdk.constants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * List of <a
 * href=https://github.com/aeternity/protocol/blob/master/node/api/api_encoding.md#encoding-scheme-for-api-identifiers-and-byte-arrays>encoding
 * identifiers </a>
 */
public interface ApiIdentifiers {

  // base58
  String ACCOUNT_PUBKEY = "ak"; // base58 Account pubkey

  String BLOCK_PROOF_OF_FRAUD_HASH = "bf"; // base58 Block Proof of Fraud hash

  String BLOCK_STATE_HASH = "bs"; // base58 Block State hash

  String BLOCK_TRANSACTION_HASH = "bx"; // base58 Block transaction hash

  String CHANNEL = "ch"; // base58 Channel

  String COMMITMENT = "cm"; // base58 Commitment

  String CONTRACT_PUBKEY = "ct"; // base58 Contract pubkey

  String KEY_BLOCK_HASH = "kh"; // base58 Key block hash

  String MICRO_BLOCK_HASH = "mh"; // base58 Micro block hash

  String NAME = "nm"; // base58 Name

  String ORACLE_PUBKEY = "ok"; // base58 Oracle pubkey

  String ORACLE_QUERY_ID = "oq"; // base58 Oracle query id

  String PEER_PUBKEY = "pp"; // base58 Peer pubkey

  String SIGNATURE = "sg"; // base58 Signature

  String TRANSACTION_HASH = "th"; // base58 Transaction hash

  // base 64
  String CONTRACT_BYTE_ARRAY = "cb"; // base64 Contract byte array

  String ORACLE_RESPONSE = "or"; // base64 Oracle response

  String ORACLE_QUERY = "ov"; // base64 Oracle query

  String PROOF_OF_INCLUSION = "pi"; // base64 Proof of Inclusion

  String STATE_TREES = "ss"; // base64 State trees

  String STATE = "st"; // base64 State

  String TRANSACTION = "tx"; // base64 Transaction

  String BYTE_ARRAY = "ba"; // base64 byte array

  // Indentifiers with base58
  String[] IDENTIFIERS_B58 = {
    ACCOUNT_PUBKEY,
    BLOCK_PROOF_OF_FRAUD_HASH,
    BLOCK_STATE_HASH,
    BLOCK_TRANSACTION_HASH,
    CHANNEL,
    COMMITMENT,
    CONTRACT_PUBKEY,
    KEY_BLOCK_HASH,
    MICRO_BLOCK_HASH,
    NAME,
    ORACLE_PUBKEY,
    ORACLE_QUERY_ID,
    PEER_PUBKEY,
    SIGNATURE,
    TRANSACTION_HASH
  };

  List<String> IDENTIFIERS_B58_LIST = new LinkedList<String>(Arrays.asList(IDENTIFIERS_B58));

  // Indentifiers with base64
  String[] IDENTIFIERS_B64 = {
    BYTE_ARRAY,
    CONTRACT_BYTE_ARRAY,
    ORACLE_RESPONSE,
    ORACLE_QUERY,
    PROOF_OF_INCLUSION,
    STATE_TREES,
    STATE,
    TRANSACTION,
  };

  List<String> IDENTIFIERS_B64_LIST = new LinkedList<String>(Arrays.asList(IDENTIFIERS_B64));
}
