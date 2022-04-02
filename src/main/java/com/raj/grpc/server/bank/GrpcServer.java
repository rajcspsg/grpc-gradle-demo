package com.raj.grpc.server.bank;

import com.raj.grpc.server.transfer.TransferService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6505).addService(new BankService()).addService(new TransferService()).build();
        server.start();
        server.awaitTermination();
    }
}

