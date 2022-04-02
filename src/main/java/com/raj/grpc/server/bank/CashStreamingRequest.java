package com.raj.grpc.server.bank;

import com.raj.models.Balance;
import com.raj.models.DepositRequest;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;

    public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver, int accountBalance) {
        this.balanceStreamObserver = balanceStreamObserver;
        this.accountBalance = accountBalance;
    }

    @Override
    public void onNext(DepositRequest value) {
        int accountNumber = value.getAccountNumber();
        int amount = value.getAmount();
        accountBalance = amount;
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder().setAmount(this.accountBalance).build();
        this.balanceStreamObserver.onNext(balance);
        this.balanceStreamObserver.onCompleted();
    }
}