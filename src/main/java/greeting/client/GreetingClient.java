package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
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
            default -> System.out.println("Keyword invalid " + args[0]);
        }
        System.out.println("Shutting down");
        channel.shutdown();
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
