package client;

import com.raj.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class DepositStreamObserver implements StreamObserver<Balance> {

    private CountDownLatch latch;

    public DepositStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Balance value) {
        System.out.println("Final Balance " + value.getAmount());
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("error occurred : " + t.getMessage());
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server is done!!");
        this.latch.countDown();
    }
}
