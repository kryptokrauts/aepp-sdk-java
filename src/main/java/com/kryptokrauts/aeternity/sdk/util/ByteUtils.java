package com.kryptokrauts.aeternity.sdk.util;

import java.nio.ByteBuffer;

import lombok.experimental.UtilityClass;

/**
 * this util class provides all byte related methods
 */
@UtilityClass
public class ByteUtils {

    /**
     * concatenate multiple bytearrays
     *
     * @param bytes
     * @return
     */
    public static final byte[] concatenate( byte[]... bytes ) {
        int size = 0;
        for ( byte[] b : bytes ) {
            size += b.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate( size );
        for ( byte[] b : bytes ) {
            buffer.put( b );
        }
        return buffer.array();
    }

    /**
     * add leading zeros to given byte array
     *
     * @param length
     * @param data
     * @return
     */
    public static final byte[] leftPad( final int length, final byte[] data ) {
        int fill = length - data.length;
        if ( fill > 0 ) {
            byte[] fillArray = new byte[fill];
            byte[] leftPadded = new byte[fillArray.length + data.length];
            System.arraycopy( fillArray, 0, leftPadded, 0, fillArray.length );
            System.arraycopy( data, 0, leftPadded, fillArray.length, data.length );
            return leftPadded;
        }
        return data;
    }

    /**
     * add trailing zeros to given byte array
     *
     * @param length
     * @param data
     * @return
     */
    public static final byte[] rightPad( final int length, final byte[] data ) {
        int fill = length - data.length;
        if ( fill > 0 ) {
            byte[] fillArray = new byte[fill];
            byte[] rightPadded = new byte[data.length + fillArray.length];
            System.arraycopy( data, 0, rightPadded, 0, data.length );
            System.arraycopy( fillArray, 0, rightPadded, data.length, fillArray.length );
            return rightPadded;
        }
        return data;
    }
}
