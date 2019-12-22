package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Slf4j
public abstract class AbstractTransactionModel<GeneratedTxModel> {

  /** the fee is automatically calculated but can also be set manually */
  @Mandatory protected BigInteger fee;

  /**
   * this method needs to be implemented for testing purposes (non native mode) and returns the
   * generated tx model from the transaction fields
   *
   * @return one of {@link com.kryptokrauts.aeternity.generated.model}
   */
  public abstract GeneratedTxModel toApiModel();

  /**
   * validate, that all mandatory fields are set - if not we throw an {@link
   * com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException}
   */
  public String checkMandatoryFields() {
    for (Field field : this.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(Mandatory.class)) {
        try {
          Object value = field.get(this);
          if (value == null) {
            return field.getName();
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    return null;
  }

  /** this method can be used to perform transaction specific validations that will */
  public abstract void validateInput();

  /** builds the necessary transaction object */
  public abstract AbstractTransaction<?> buildTransaction(
      ExternalApi externalApi, DefaultApi compilerApi);

  /**
   * remap the given genericTx to a model
   *
   * @return
   */
  public abstract Function<GenericTx, ?> getApiToModelFunction();
}
