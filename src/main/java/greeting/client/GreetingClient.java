package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0){
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //we do not want to stuggle with ssl for now
                .build();

        switch (args[0]) {
            case "greet" -> doGreet(channel);
            case "greet_many_times" -> doGreetManyTimes(channel);
            case "long_greet" -> doLongGreet(channel);
            default -> System.out.println("Keyword invalid " + args[0]);
        }
        System.out.println("Shutting down");
        channel.shutdown();
    }

    //for client streaming
    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
            System.out.println("Enter doLongGreet");
        //creates asynchronous stub
        GreetingServiceGrpc.GreetingServiceStub greetingServiceStub = GreetingServiceGrpc.newStub(channel);
        List<String> names = new ArrayList<>();
        //CountDownLatch is initialized with a given count of threads which are required to be completed before the main thread.
        CountDownLatch latch = new CountDownLatch(1); //because of we're in asynchronous situation, creation of thread

        Collections.addAll(names, "cumali", "ahmet", "mehmet");
        StreamObserver<GreetingRequest> stream = greetingServiceStub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse response) {
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

        for (var name :
                names) {
            stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        }

        stream.onCompleted();
        // block the main thread execution until the current count reaches to zero, or timout reach,
        // or interrupted by other threads. the count is decremented using countDown() method
        latch.await(3, TimeUnit.SECONDS);

    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Enter doGreetManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        stub.greetManyTimes(GreetingRequest.newBuilder().setFirstName("Cumali").build()).forEachRemaining(response -> System.out.println(response.getResult()));
    }

    private static void doGreet(ManagedChannel channel) {
        //stub: it enables us calling a function directly on the server
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("cumali").build());

        System.out.println("Greeting: "+response.getResult());
    }

}
