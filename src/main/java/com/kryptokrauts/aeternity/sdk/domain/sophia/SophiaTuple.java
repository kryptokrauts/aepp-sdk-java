package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class SophiaTuple extends SophiaType {

  private List<Object> tupleValues;

  public SophiaTuple(List<Object> tupleValues) {
    if (tupleValues == null || tupleValues.size() < 2) {
      throw new InvalidParameterException("Must not be null and size must be >= 2.");
    }
    this.tupleValues = tupleValues;
  }

  public List<Object> getTupleValues() {
    return this.tupleValues;
  }

  @Override
  public String getCompilerValue() {
    return "("
        + tupleValues.stream()
            .map(o -> o instanceof SophiaType ? ((SophiaType) o).getCompilerValue() : o.toString())
            .collect(Collectors.joining(","))
        + ")";
  }
}
