package com.naph.startup.service;

import com.naph.startup.dto.mpesa.*;

public interface DarajaApi {

//    Returns Daraja API Access Token Response

    AccessTokenResponse getAccessToken();

    RegisterUrlResponse registerUrl();

    SimulateTransactionResponse simulateC2BTransaction(SimulateTransactionRequest simulateTransactionRequest);

    StkPushSyncResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest);
}
