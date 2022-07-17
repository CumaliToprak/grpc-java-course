package blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogService {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 5003;

        MongoClient client = MongoClients.create("mongodb://root:root@localhost:27017/");

        Server server = ServerBuilder
                .forPort(port)
                .addService(new BlogServiceImpl(client))//registering the class in the grpc server instance
                .build();
        server.start();
        System.out.println("Server started");
        System.out.println("Listening on port: "+port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            client.close();
            System.out.println("Server stopped");
        }));

        server.awaitTermination();
    }
}
