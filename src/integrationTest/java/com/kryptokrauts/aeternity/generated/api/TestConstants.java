package com.kryptokrauts.aeternity.generated.api;

import java.util.Arrays;
import java.util.List;

public interface TestConstants {

  /** @see https://testnet.contracts.aepps.com/ */
  String testnetAccountPrivateKey =
      "a7a695f999b1872acb13d5b63a830a8ee060ba688a478a08c6e65dfad8a01cd70bb4ed7927f97b51e1bcb5e1340d12335b2a2b12c8bc5221d63c4bcb39d41e61";

  String testnetURL = "https://sdk-testnet.aepps.com/v2";

  String testContractSourceCode = "contract Identity =\n  entrypoint main(z : int) = z";

  String testContractFunction = "main";

  String testContractFunctionSophiaType = "int";

  String testContractFuntionParam = "42";

  List<String> testContractFunctionParams = Arrays.asList(testContractFuntionParam);

  String testContractByteCode =
      "cb_+GZGA6D67IdhchygjVXTQp0u4OvuaH4qeIPoQPsYthv9OxpwtcC4OZ7+RNZEHwA3ADcAGg6CPwEDP/64F37sADcBBwcBAQCWLwIRRNZEHxFpbml0EbgXfuwRbWFpboIvAIU0LjAuMAD99JbV";

  String testContractCallData = "cb_KxFE1kQfP4oEp9E=";

  String encodedServiceCall = "cb_KxG4F37sG1Q/+F7e";

  String encodedServiceCallAnswer = "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACr8s/aY";

  String serviceCallAnswerJSON = "{\"type\":\"word\",\"value\":42}";

  /** PublicKey: ak_twR4h7dEcUtc2iSEDv8kB7UFJJDGiEDQCXr85C3fYF8FdVdyo */
  String BENEFICIARY_PRIVATE_KEY =
      "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

  int NUM_TRIALS_DEFAULT = 60;

  String DOMAIN = "kryptokrauts";

  String NAMESPACE = ".chain";

  String paymentSplitterACI =
      "{contract={event={variant=[{AddingInitialRecipients=[]}, {RecipientAdded=[address, int]}, {AddressUpdated=[address, address]}, {UpdatingAllRecipients=[]}, {PaymentReceivedAndSplitted=[address, int, int]}]}, functions=[{arguments=[{name=recipientConditions', type={map=[address, int]}}], name=init, payable=false, returns=PaymentSplitter.state, stateful=false}, {arguments=[], name=getOwner, payable=false, returns=address, stateful=false}, {arguments=[], name=getRecipientsCount, payable=false, returns=int, stateful=false}, {arguments=[{name=who', type=address}], name=isRecipient, payable=false, returns=bool, stateful=false}, {arguments=[{name=who', type=address}], name=getWeight, payable=false, returns=int, stateful=false}, {arguments=[], name=getTotalAmountSplitted, payable=false, returns=int, stateful=false}, {arguments=[], name=payAndSplit, payable=true, returns={tuple=[]}, stateful=true}, {arguments=[{name=newOwner', type=address}], name=transferOwnership, payable=false, returns={tuple=[]}, stateful=true}, {arguments=[{name=oldAddress', type=address}, {name=newAddress', type=address}], name=updateAddress, payable=false, returns={tuple=[]}, stateful=true}, {arguments=[{name=recipients', type={map=[address, int]}}], name=updateRecipientConditions, payable=false, returns={tuple=[]}, stateful=true}], name=PaymentSplitter, payable=true, state={record=[{name=owner, type=address}, {name=recipientConditions, type={map=[address, int]}}, {name=totalAmountSplitted, type=int}]}, type_defs=[]}}";
}
