package com.kryptokrauts.aeternity.test.integration;

import java.util.Arrays;
import java.util.List;

public interface TestConstants {

  String testContractSourceCode = "contract Identity =\n  entrypoint test(z : int) = z";

  String testContractFunction = "test";

  String testContractFuntionParam = "42";

  List<String> testContractFunctionParams = Arrays.asList(testContractFuntionParam);

  String testContractByteCode =
      "cb_+GZGA6BF4fveIg9pvKjCcuxiZg0e4xDbdN7xLVUBxVY9dRwt38C4OZ7+RNZEHwA3ADcAGg6CPwEDP/6SiyA2ADcBBwcBAQCWLwIRRNZEHxFpbml0EZKLIDYRdGVzdIIvAIU2LjAuMAC/zsAS";

  String testContractCallData = "cb_KxFE1kQfP4oEp9E=";

  String encodedServiceCall = "cb_KxGSiyA2G1RGzEK7";

  String encodedServiceCallAnswer = "cb_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACr8s/aY";

  String serviceCallAnswerJSON = "{\"type\":\"word\",\"value\":42}";

  /** PublicKey: ak_twR4h7dEcUtc2iSEDv8kB7UFJJDGiEDQCXr85C3fYF8FdVdyo */
  String BENEFICIARY_PRIVATE_KEY =
      "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

  int NUM_TRIALS_DEFAULT = 60;

  String NAME = "kryptokrauts";

  String NAMESPACE = ".chain";

  String paymentSplitterACI =
      "{contract={event={variant=[{AddingInitialRecipients=[]}, {RecipientAdded=[address, int]}, {AddressUpdated=[address, address]}, {UpdatingAllRecipients=[]}, {PaymentReceivedAndSplitted=[address, int, int]}]}, functions=[{arguments=[{name=recipientConditions', type={map=[address, int]}}], name=init, payable=false, returns=PaymentSplitter.state, stateful=false}, {arguments=[], name=getOwner, payable=false, returns=address, stateful=false}, {arguments=[], name=getRecipientsCount, payable=false, returns=int, stateful=false}, {arguments=[{name=who', type=address}], name=isRecipient, payable=false, returns=bool, stateful=false}, {arguments=[{name=who', type=address}], name=getWeight, payable=false, returns=int, stateful=false}, {arguments=[], name=getTotalAmountSplitted, payable=false, returns=int, stateful=false}, {arguments=[], name=payAndSplit, payable=true, returns={tuple=[]}, stateful=true}, {arguments=[{name=newOwner', type=address}], name=transferOwnership, payable=false, returns={tuple=[]}, stateful=true}, {arguments=[{name=oldAddress', type=address}, {name=newAddress', type=address}], name=updateAddress, payable=false, returns={tuple=[]}, stateful=true}, {arguments=[{name=recipients', type={map=[address, int]}}], name=updateRecipientConditions, payable=false, returns={tuple=[]}, stateful=true}], kind=contract_main, name=PaymentSplitter, payable=true, state={record=[{name=owner, type=address}, {name=recipientConditions, type={map=[address, int]}}, {name=totalAmountSplitted, type=int}]}, type_defs=[]}}";

  String testGABlindAuthContract =
      "contract BlindAuth =\n    record state = { owner : address }\n    entrypoint init(owner' : address) = { owner = owner' }\n    stateful entrypoint authorize(r: int) : bool =\n        switch(Auth.tx_hash)\n            None => abort(\"Not in Auth context\")\n            Some(tx_hash) => true\n    entrypoint to_sign(h : hash, n : int) : hash =\n        Crypto.blake2b((h, n))";
}
