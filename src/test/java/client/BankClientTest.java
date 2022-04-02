package client;

import com.google.common.util.concurrent.Uninterruptibles;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

   private BankServiceGrpc.BankServiceBlockingStub bs;
   private BankServiceGrpc.BankServiceStub bss;
   @BeforeAll
   public void setUp() {
        ManagedChannel mc = ManagedChannelBuilder.forAddress("localhost", 6505)
                .usePlaintext()
                .build();
         this.bs = BankServiceGrpc.newBlockingStub(mc);
         this.bss = BankServiceGrpc.newStub(mc);
   }

   @Test
   public void balanceTest() {
       BalanceCheckRequest bcr = BalanceCheckRequest.newBuilder().setAccountNumber(5).build();
       Balance b = this.bs.getBalance(bcr);
       System.out.println("balacne received " + b.getAmount());
   }

   @Test
    public void withdrawTest() {
       WithdrawRequest wr = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(100).build();
       Iterator<Money> iter = this.bs.withdraw(wr);
       while(iter.hasNext()) {
           System.out.println(iter.next());
       }
   }

    @Test
    public void withdrawErrorTest() {
        StatusRuntimeException re = assertThrows(StatusRuntimeException.class, () -> {
            WithdrawRequest wr = WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(100).build();
            Iterator<Money> iter = this.bs.withdraw(wr);
            iter.forEachRemaining( money -> System.out.println(money));
        });
        assertEquals(re.getMessage(), "FAILED_PRECONDITION: Not enough Money. Please check Balance");
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest wr = WithdrawRequest.newBuilder().setAccountNumber(100).setAmount(100).build();
        this.bss.withdraw(wr, new WithdrawMoneyStreamingResponse(latch));
        latch.await();
//        Uninterruptibles.sleepUninterruptibly(88888888, TimeUnit.SECONDS);
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