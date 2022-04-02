package com.raj.grpc.server.bank;

import com.raj.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
       responseObserver.onNext(Balance.newBuilder().setAmount(request.getAccountNumber() * 51).build());
        System.out.println("received the balance request for " + request.getAccountNumber());
       responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accNo = request.getAccountNumber();
        System.out.println("request received for accNo");
        int amount = request.getAmount();
        int balance = accNo * 56;
        if(balance < amount) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Not enough Money. Please check Balance");
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        for (int i = 0; i < amount/10; i++) {
            Money money = Money.newBuilder().setValue(10).build();
            responseObserver.onNext(money);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> deposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver, 0);
    }
}
