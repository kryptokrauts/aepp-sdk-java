package com.kryptokrauts.aeternity.generated.api;

import java.util.Arrays;
import java.util.List;

public interface TestConstants {

  /** @see https://testnet.contracts.aepps.com/ */
  String testnetAccountPrivateKey =
      "a7a695f999b1872acb13d5b63a830a8ee060ba688a478a08c6e65dfad8a01cd70bb4ed7927f97b51e1bcb5e1340d12335b2a2b12c8bc5221d63c4bcb39d41e61";

  String testnetURL = "https://sdk-testnet.aepps.com/v2";

  String testContractSourceCode =
      "contract Identity =\n  type state = ()\n  entrypoint main(z : int) = z";

  String testContractFunction = "main";

  String testContractFunctionSophiaType = "int";

  String testContractFuntionParam = "42";

  List<String> testContractFunctionParams = Arrays.asList(testContractFuntionParam);

  String testContractByteCode =
      "cb_+QP1RgKgrAqzy8P9OGgz6wFSvRD4mtGDnvst1Wq0RUDBbMQm9w/5Avv5ASqgaPJnYzj/UIg5q6R3Se/6i+h+8oTyB/s9mZhwHNU4h8WEbWFpbrjAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKD//////////////////////////////////////////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAuEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+QHLoLnJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqhGluaXS4YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////////////////////////////////////7kBQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEA//////////////////////////////////////////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///////////////////////////////////////////uMxiAABkYgAAhJGAgIBRf7nJVvKLMUmp9Zh6pQXz2hsiCcxXOSNABiu2wb2fn5nqFGIAAMBXUIBRf2jyZ2M4/1CIOaukd0nv+ovofvKE8gf7PZmYcBzVOIfFFGIAAK9XUGABGVEAW2AAGVlgIAGQgVJgIJADYAOBUpBZYABRWVJgAFJgAPNbYACAUmAA81tZWWAgAZCBUmAgkANgABlZYCABkIFSYCCQA2ADgVKBUpBWW2AgAVFRWVCAkVBQgJBQkFZbUFCCkVBQYgAAjFaFMy4yLjAR0UPb";

  String testContractCallData =
      "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACC5yVbyizFJqfWYeqUF89obIgnMVzkjQAYrtsG9n5+Z6gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAnHQYrA==";

  String encodedServiceCall =
      "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBo8mdjOP9QiDmrpHdJ7/qL6H7yhPIH+z2ZmHAc1TiHxQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACo7dbVl";

  String encodedServiceCallAnswer = "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACr8s/aY";

  String serviceCallAnswerJSON = "{type=word, value=42}";

  /** PublicKey: ak_twR4h7dEcUtc2iSEDv8kB7UFJJDGiEDQCXr85C3fYF8FdVdyo */
  String BENEFICIARY_PRIVATE_KEY =
      "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

  int NUM_TRIALS_DEFAULT = 30;

  String DOMAIN = "kryptokrauts";

  String NAMESPACE = ".test";

  String paymentSplitterACI =
      "{contract={event={variant=[{AddingInitialRecipients=[]}, {RecipientAdded=[address, int]}, {AddressUpdated=[address, address]}, {UpdatingAllRecipients=[]}, {PaymentReceivedAndSplitted=[address, int, int]}]}, functions=[{arguments=[{name=recipientConditions', type={map=[address, int]}}], name=init, returns=PaymentSplitter.state, stateful=false}, {arguments=[], name=getOwner, returns=address, stateful=false}, {arguments=[], name=getRecipientsCount, returns=int, stateful=false}, {arguments=[{name=who', type=address}], name=isRecipient, returns=bool, stateful=false}, {arguments=[{name=who', type=address}], name=getWeight, returns=int, stateful=false}, {arguments=[], name=getTotalAmountSplitted, returns=int, stateful=false}, {arguments=[], name=payAndSplit, returns={tuple=[]}, stateful=true}, {arguments=[{name=newOwner', type=address}], name=transferOwnership, returns={tuple=[]}, stateful=true}, {arguments=[{name=oldAddress', type=address}, {name=newAddress', type=address}], name=updateAddress, returns={tuple=[]}, stateful=true}, {arguments=[{name=recipients', type={map=[address, int]}}], name=updateRecipientConditions, returns={tuple=[]}, stateful=true}], name=PaymentSplitter, state={record=[{name=owner, type=address}, {name=recipientConditions, type={map=[address, int]}}, {name=totalAmountSplitted, type=int}]}, type_defs=[]}}";
}
