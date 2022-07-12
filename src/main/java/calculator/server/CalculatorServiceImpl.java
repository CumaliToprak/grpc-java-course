package calculator.server;


import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

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
}
