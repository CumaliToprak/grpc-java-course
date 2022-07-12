package calculator.server;

import greeting.server.GreetingServerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorService {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 5003;

        Server server = ServerBuilder
                .forPort(port)
                .addService(new CalculatorServiceImpl())//registering the class in the grpc server instance
                .build();
        server.start();
        System.out.println("Server started");
        System.out.println("Listening on port: "+port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Server stopped");
        }));

        server.awaitTermination();
    }
}
