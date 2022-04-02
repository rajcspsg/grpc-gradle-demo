package client;

import com.raj.models.Money;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class WithdrawMoneyStreamingResponse implements StreamObserver<Money> {
    private CountDownLatch latch;

    public WithdrawMoneyStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Money value) {
        System.out.println("Received async: " + value.getValue());
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Error: " +t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Successfully withdrawn");
        latch.countDown();
    }
}
