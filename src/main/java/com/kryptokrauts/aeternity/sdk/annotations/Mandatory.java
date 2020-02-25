package com.kryptokrauts.aeternity.sdk.annotations;

import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this annotation indicates, that a field of a transaction model is mandatory and must contain a
 * value. the annotation is validated before constructing the RLP array in {@link
 * AbstractTransaction#createUnsignedTransaction(boolean, long)}
 *
 * @author mitch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mandatory {}
