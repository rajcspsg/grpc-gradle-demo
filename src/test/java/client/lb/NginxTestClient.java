package client.lb;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NginxTestClient {

    private BankServiceGrpc.BankServiceBlockingStub bs;

    @BeforeAll
    public void setUp() {
        ManagedChannel mc = ManagedChannelBuilder.forAddress("localhost", 8585)
                .usePlaintext()
                .build();
        this.bs = BankServiceGrpc.newBlockingStub(mc);
    }
    @Test
    public void balanceTest() {
        for (int i = 0; i < 10; i++) {
            BalanceCheckRequest bcr = BalanceCheckRequest.newBuilder().setAccountNumber(i).build();
            Balance b = this.bs.getBalance(bcr);
            System.out.println("balacne received " + b.getAmount());
        }
    }


}
