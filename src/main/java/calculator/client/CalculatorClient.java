package calculator.client;

import com.proto.calculator.*;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0){
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("Localhost", 5003)
                .usePlaintext() //we do not want to stuggle with ssl for now
                .build();

        switch (args[0]) {
            case "sum" -> doCalculate(channel);
            case "find_prime_numbers" -> doPrimeNumbers(channel);
            case "find_average" -> doFindAverage(channel);
            default -> System.out.println("Keyword invalid " + args[0]);
        }
        System.out.println("Shutting down");
        channel.shutdown();

    }

    private static void doFindAverage(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doFindAverage");
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);
        List<Integer> numbers = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(1);

        Collections.addAll(numbers, 1,2,3,4,5,6);

        StreamObserver<AvgRequest> stream = stub.findAverage(new StreamObserver<AvgResponse>() {
            @Override
            public void onNext(AvgResponse response) {
                System.out.println(response.getResult());
            }


            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();//to finish the program
            }

        });

        for (var num :
                numbers) {
            stream.onNext(AvgRequest.newBuilder().setFirstNumber(num).build());
        }

        stream.onCompleted();
        // block the main thread execution until the current count reaches to zero, or timout reach,
        // or interrupted by other threads. the count is decremented using countDown() method
        latch.await(3, TimeUnit.SECONDS);


    }

    private static void doPrimeNumbers(ManagedChannel channel) {
        System.out.println("Enter doCalculate");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.findPrimeNumbers(PrimeRequest.newBuilder().setNumber(26).build())
                .forEachRemaining(res -> System.out.println(res.getResult()));

    }

    private static void doCalculate(ManagedChannel channel) {
        //stub: it enables us calling a function directly on the server
        System.out.println("Enter doCalculate");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse response = stub.calculate(SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(2).build());

        System.out.println("1 + 2: "+response.getResult());
    }
}
