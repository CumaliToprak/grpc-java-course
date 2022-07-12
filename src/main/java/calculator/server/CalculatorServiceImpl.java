package calculator.server;


import com.proto.calculator.*;
import com.proto.greeting.GreetingResponse;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void calculate(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        responseObserver.onNext(SumResponse.newBuilder().setResult(request.getFirstNumber() + request.getSecondNumber()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findPrimeNumbers(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver)
    {
        int number = request.getNumber();
        int divider = 2;
        while (number > 1)
        {
            if(number % divider == 0)
            {
                responseObserver.onNext(PrimeResponse.newBuilder().setResult(divider).build());
                number /= divider;
            }
            else
                divider++;
        }
        responseObserver.onCompleted();
    }

    //client streaming
    @Override
    public StreamObserver<AvgRequest> findAverage(StreamObserver<AvgResponse>  responseObserver)
    {
        List<Integer> numbers = new ArrayList<>();
        return new StreamObserver<AvgRequest>() {
            @Override
            public void onNext(AvgRequest request) {
                numbers.add(request.getFirstNumber());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AvgResponse.newBuilder()
                        .setResult(numbers.stream().mapToInt(p -> p.intValue()).average().orElseThrow()).build());
                //says connection between client and server is done
                responseObserver.onCompleted();

            }
        };
    }
}
