package client;
import com.raj.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferClientTest {
    private TransferServiceGrpc.TransferServiceStub ts;
//    private TransferServiceGrpc.

    @BeforeAll
    public void setUp() {
        ManagedChannel mc = ManagedChannelBuilder.forAddress("localhost", 6505)
                .usePlaintext()
                .build();
    //    this.ts = BankServiceGrpc.newBlockingStub(mc);
        this.ts = TransferServiceGrpc.newStub(mc);
    }

    @Test
    public void transfer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TransferStreamingResponse response = new TransferStreamingResponse(latch);
        StreamObserver<TransferRequest> requestStreamObserver = this.ts.transfer(response);
        for (int i = 0; i < 100; i++) {
            TransferRequest transferRequest = TransferRequest.newBuilder()
                    .setFromAccount(ThreadLocalRandom.current().nextInt(1,11))
                    .setToAccount( ThreadLocalRandom.current().nextInt(1,11))
                    .setAmount(ThreadLocalRandom.current().nextInt(1, 21))
        .build();
        requestStreamObserver.onNext(transferRequest);
        }
        requestStreamObserver.onCompleted();
        latch.await();
    }
}