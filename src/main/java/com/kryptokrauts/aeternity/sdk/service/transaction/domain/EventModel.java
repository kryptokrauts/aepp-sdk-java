package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.Event;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class EventModel extends GenericResultObject<Event, EventModel> {
  private String address;

  @Default private List<BigInteger> topics = new ArrayList<>();

  private String data;

  @Override
  protected EventModel map(Event generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .address(generatedResultObject.getAddress())
          .data(generatedResultObject.getData())
          .topics(generatedResultObject.getTopics())
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
