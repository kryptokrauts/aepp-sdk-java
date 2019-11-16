package com.kryptokrauts.aeternity.sdk.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kryptokrauts.aeternity.generated.ApiException;
import io.reactivex.Single;
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
   * @param generatedResultObjectSingle
   * @return
   */
  public V blockingGet(Single<T> generatedResultObjectSingle) {
    try {
      return map(generatedResultObjectSingle.blockingGet());
    } catch (Exception e) {
      return createErrorResult(e);
    }
  }

  /**
   * execute an async call and return a mapped single
   *
   * @param generatedResultObjectSingle
   * @return
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
            "Error mapping GenericResultObject to class %s\ncause: %s\nroot cause:\n%s",
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
        return ex.getResponseBody();
      }
      if (e instanceof com.kryptokrauts.sophia.compiler.generated.ApiException) {
        com.kryptokrauts.sophia.compiler.generated.ApiException ex =
            (com.kryptokrauts.sophia.compiler.generated.ApiException) e;
        return ex.getResponseBody();
      }
      if (e instanceof com.kryptokrauts.aeternal.generated.ApiException) {
        com.kryptokrauts.aeternal.generated.ApiException ex =
            (com.kryptokrauts.aeternal.generated.ApiException) e;
        return ex.getResponseBody();
      } else return determineRootErrorMessage(e.getCause());
    }
    return "Root cause error message cannot be determined";
  }
}
