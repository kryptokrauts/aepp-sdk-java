package com.kryptokrauts.aeternity.sdk.constants;

public interface SerializationTags {

    // RLP version number
    // https://github.com/aeternity/protocol/blob/master/serializations.md#binary-serialization
    int VSN = 1;

    // Tag constant for ids (type uint8)
    // see
    // https://github.com/aeternity/protocol/blob/master/serializations.md#the-id-type
    // <<Tag:1/unsigned-integer-unit:8, Hash:32/binary-unit:8>>
    int ID_TAG_ACCOUNT = 1;

    int ID_TAG_NAME = 2;

    int ID_TAG_COMMITMENT = 3;

    int ID_TAG_ORACLE = 4;

    int ID_TAG_CONTRACT = 5;

    int ID_TAG_CHANNEL = 6;

    // Object tags
    // see
    // https://github.com/aeternity/protocol/blob/master/serializations.md#binary-serialization

    int OBJECT_TAG_ACCOUNT = 10;

    int OBJECT_TAG_SIGNED_TRANSACTION = 11;

    int OBJECT_TAG_SPEND_TRANSACTION = 12;

    int OBJECT_TAG_ORACLE = 20;

    int OBJECT_TAG_ORACLE_QUERY = 21;

    int OBJECT_TAG_ORACLE_REGISTER_TRANSACTION = 22;

    int OBJECT_TAG_ORACLE_QUERY_TRANSACTION = 23;

    int OBJECT_TAG_ORACLE_RESPONSE_TRANSACTION = 24;

    int OBJECT_TAG_ORACLE_EXTEND_TRANSACTION = 25;

    int OBJECT_TAG_NAME_SERVICE_NAME = 30;

    int OBJECT_TAG_NAME_SERVICE_COMMITMENT = 31;

    int OBJECT_TAG_NAME_SERVICE_CLAIM_TRANSACTION = 32;

    int OBJECT_TAG_NAME_SERVICE_PRECLAIM_TRANSACTION = 33;

    int OBJECT_TAG_NAME_SERVICE_UPDATE_TRANSACTION = 34;

    int OBJECT_TAG_NAME_SERVICE_REVOKE_TRANSACTION = 35;

    int OBJECT_TAG_NAME_SERVICE_TRANSFER_TRANSACTION = 36;

    int OBJECT_TAG_CONTRACT = 40;

    int OBJECT_TAG_CONTRACT_CALL = 41;

    int OBJECT_TAG_CONTRACT_CREATE_TRANSACTION = 42;

    int OBJECT_TAG_CONTRACT_CALL_TRANSACTION = 43;

    int OBJECT_TAG_CHANNEL_CREATE_TRANSACTION = 50;

    int OBJECT_TAG_CHANNEL_DEPOSIT_TRANSACTION = 51;

    int OBJECT_TAG_CHANNEL_WITHDRAW_TRANSACTION = 52;

    int OBJECT_TAG_CHANNEL_FORCE_PROGRESS_TRANSACTION = 521;

    int OBJECT_TAG_CHANNEL_CLOSE_MUTUAL_TRANSACTION = 53;

    int OBJECT_TAG_CHANNEL_CLOSE_SOLO_TRANSACTION = 54;

    int OBJECT_TAG_CHANNEL_SLASH_TRANSACTION = 55;

    int OBJECT_TAG_CHANNEL_SETTLE_TRANSACTION = 56;

    int OBJECT_TAG_CHANNEL_OFF_CHAIN_TRANSACTION = 57;

    int OBJECT_TAG_CHANNEL_OFF_CHAIN_UPDATE_TRANSFER = 570;

    int OBJECT_TAG_CHANNEL_OFF_CHAIN_UPDATE_DEPOSIT = 571;

    int OBJECT_TAG_CHANNEL_OFF_CHAIN_UPDATE_WITHDRAWAL = 572;

    int OBJECT_TAG_CHANNEL_OFF_CHAIN_UPDATE_CREATE_CONTRACT = 573;

    int OBJECT_TAG_CHANNEL_OFF_CHAIN_UPDATE_CALL_CONTRACT = 574;

    int OBJECT_TAG_CHANNEL = 58;

    int OBJECT_TAG_CHANNEL_SNAPSHOT_TRANSACTION = 59;

    int OBJECT_TAG_POI = 60;

    int OBJECT_TAG_MICRO_BODY = 101;

    int OBJECT_TAG_LIGHT_MICRO_BLOCK = 102;
}
