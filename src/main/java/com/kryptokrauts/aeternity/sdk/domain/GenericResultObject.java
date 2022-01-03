package com.kryptokrauts.aeternity.sdk.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kryptokrauts.aeternity.generated.ApiException;
import com.kryptokrauts.aeternity.sdk.exception.DebugModeNotEnabledException;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.DryRunTransactionResult;
import io.reactivex.Single;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsules the generated result objects to keep the SDK stable in terms of changes
 * within the underlying AE protocol
 *
 * @param <T> the generated ae result object class
 */
@Getter
@SuperBuilder(toBuilder = true)
@ToString
public abstract class GenericResultObject<T, V extends GenericResultObject<?, ?>> {

  protected String rootErrorMessage;

  protected String aeAPIErrorMessage;

  protected Throwable throwable;

  protected static final Logger _logger = LoggerFactory.getLogger(GenericResultObject.class);

  /**
   * execute a blocking call and return mapped result
   *
   * @param generatedResultObjectSingle the {@link Single} of a result object of a generated api
   *     model class
   * @return the mapped result object
   */
  public V blockingGet(Single<T> generatedResultObjectSingle) {
    try {
      return map(generatedResultObjectSingle.blockingGet());
    } catch (Exception e) {
      V result = createErrorResult(e);
      /**
       * if expected result is of type {@link DryRunTransactionResult} and the exception contains
       * "Not Found" or "Forbidden" it indicates, that debug resp internal endpoint is not
       * available, thus throw a meaningful exception
       */
      String rootCauseMessage = e.getCause().getMessage();
      if (this.getClass().equals(DryRunTransactionResult.class)
          && e.getCause() instanceof ApiException
          && ("Not Found".equals(rootCauseMessage) || "Forbidden".equals(rootCauseMessage))
          && result.getRootErrorMessage() == null) {
        throw new DebugModeNotEnabledException("Debug endpoint not enabled, check environment");
      }
      return result;
    }
  }

  /**
   * execute an async call and return a mapped single
   *
   * @param generatedResultObjectSingle the {@link Single} of a result object of a generated api
   *     model class
   * @return a {@link Single} of the mapped result object
   */
  public Single<V> asyncGet(Single<T> generatedResultObjectSingle) {
    return generatedResultObjectSingle.map(
        single -> {
          return map(single);
        });
  }

  protected abstract V map(T generatedResultObject);

  protected abstract String getResultObjectClassName();

  private V createErrorResult(Exception e) {
    V result = this.map(null);
    result.rootErrorMessage = determineRootErrorMessage(e);
    result.aeAPIErrorMessage = e.getMessage();
    String prettyErrorMessage = result.getRootErrorMessage();
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      prettyErrorMessage =
          mapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(mapper.readValue(result.getRootErrorMessage(), Object.class));
    } catch (Exception e1) {
    }
    _logger.warn(
        String.format(
            "\nError mapping GenericResultObject to class %s\ncaused by: %s, root cause was: %s",
            getResultObjectClassName(), result.aeAPIErrorMessage, prettyErrorMessage));
    if (_logger.isDebugEnabled()) {
      result.throwable = e;
    }
    return result;
  }

  private String determineRootErrorMessage(Throwable e) {
    if (e != null) {
      if (e instanceof ApiException) {
        ApiException ex = (ApiException) e;
        return Optional.ofNullable(ex.getResponseBody()).orElse(ex.getLocalizedMessage());
      }
      if (e instanceof com.kryptokrauts.sophia.compiler.generated.ApiException) {
        com.kryptokrauts.sophia.compiler.generated.ApiException ex =
            (com.kryptokrauts.sophia.compiler.generated.ApiException) e;
        return Optional.ofNullable(ex.getResponseBody()).orElse(ex.getLocalizedMessage());
      }
      if (e instanceof com.kryptokrauts.mdw.generated.ApiException) {
        com.kryptokrauts.mdw.generated.ApiException ex =
            (com.kryptokrauts.mdw.generated.ApiException) e;
        return Optional.ofNullable(ex.getResponseBody()).orElse(ex.getLocalizedMessage());
      } else return determineRootErrorMessage(e.getCause());
    }
    return "Root cause error message cannot be determined";
  }
}
