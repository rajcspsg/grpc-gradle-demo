package com.raj.grpc.server.bank.lb;

import com.raj.grpc.server.bank.BankService;
import com.raj.grpc.server.transfer.TransferService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(7575).addService(new BankService()).addService(new TransferService()).build();
        server.start();
        server.awaitTermination();
    }
}

