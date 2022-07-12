package greeting.server;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    //unary streaming
    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello"+request.getFirstName()).build());
        responseObserver.onCompleted();
    }

    //server streaming
    @Override
    public void greetManyTimes(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver)
    {
        GreetingResponse response = GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build();
        for (int i = 0; i < 10; i++)
        {
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();//says connection between client and server completed
    }

    //client streaming
    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responseObserver)
    {
        StringBuilder sb = new StringBuilder();
        return new StreamObserver<GreetingRequest>(){

            @Override
            public void onNext(GreetingRequest request) {
                sb.append("Hello ");
                sb.append(request.getFirstName());
                sb.append("\n");
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);//return the error the client back
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };
    }

}
