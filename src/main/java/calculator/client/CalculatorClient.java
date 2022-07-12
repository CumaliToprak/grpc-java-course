package calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
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
            default -> System.out.println("Keyword invalid " + args[0]);
        }
        System.out.println("Shutting down");
        channel.shutdown();

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
