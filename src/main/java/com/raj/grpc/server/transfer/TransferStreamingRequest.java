package com.raj.grpc.server.transfer;

import com.raj.grpc.server.dao.AccountDao;
import com.raj.models.Account;
import com.raj.models.TransferRequest;
import com.raj.models.TransferResponse;
import com.raj.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

    private StreamObserver<TransferResponse> transferResponseStreamObserver;

    public TransferStreamingRequest(StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
       int fromAccount = transferRequest.getFromAccount();
       int toAccount = transferRequest.getToAccount();
       int amount = transferRequest.getAmount();
       int balance = AccountDao.getBalance(fromAccount);
       TransferStatus status = TransferStatus.FAILED;

       if(balance >= amount && fromAccount != toAccount) {
           AccountDao.deductBalance(fromAccount, amount);
           AccountDao.addBalance(toAccount, amount);
           status = TransferStatus.SUCCESS;
       }
       Account fromAccountInfo =  Account.newBuilder().setAccountNumber(fromAccount).setAmount(AccountDao.getBalance(fromAccount)).build();
       Account toAccountInfo = Account.newBuilder().setAccountNumber(toAccount).setAmount(AccountDao.getBalance(toAccount)).build();
       TransferResponse response = TransferResponse.newBuilder().setStatus(status).addAccounts(fromAccountInfo).addAccounts(toAccountInfo).build();
       this.transferResponseStreamObserver.onNext(response);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println(t);
    }

    @Override
    public void onCompleted() {
        AccountDao.printAccountDetails();
        this.transferResponseStreamObserver.onCompleted();
    }
}
