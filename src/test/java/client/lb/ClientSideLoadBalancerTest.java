package client.lb;

import client.DepositStreamObserver;
import com.raj.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientSideLoadBalancerTest {

    private BankServiceGrpc.BankServiceBlockingStub bs;
    private BankServiceGrpc.BankServiceStub bss;

    @BeforeAll
    public void setUp() {
        ServiceRegistry.register("bank-service", Arrays.asList("localhost:6565", "localhost:7575"));
        NameResolverRegistry.getDefaultRegistry().register(new TempNameResolverProvider());
        ManagedChannel mc = ManagedChannelBuilder
                //.forAddress("localhost", 8585)
                .forTarget("bank-service")
                .usePlaintext()
                .build();
        this.bs = BankServiceGrpc.newBlockingStub(mc);
        this.bss = BankServiceGrpc.newStub(mc);
    }
    @Test
    public void balanceTest() {
        for (int i = 0; i < 10; i++) {
            BalanceCheckRequest bcr = BalanceCheckRequest.newBuilder().setAccountNumber(i).build();
            Balance b = this.bs.getBalance(bcr);
            System.out.println("balacne received " + b.getAmount());
        }
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> streamObserver = this.bss.deposit(new DepositStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            DepositRequest dr = DepositRequest.newBuilder().setAccountNumber(8).setAmount(200).build();
            streamObserver.onNext(dr);
        }
        streamObserver.onCompleted();
        latch.await();
    }
}