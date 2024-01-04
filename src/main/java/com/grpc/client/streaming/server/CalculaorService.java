package com.grpc.client.streaming.server;

import com.example.sum.CalculatorGrpc.CalculatorImplBase;
import com.example.sum.Request;
import com.example.sum.Response;

import io.grpc.stub.StreamObserver;

public class CalculaorService extends CalculatorImplBase
{

	@Override
	public StreamObserver<Request> getSum(StreamObserver<Response> responseObserver) 
	{
		
		
		return new StreamObserver<Request>() {
			int sum=0;
			
			@Override
			public void onNext(Request request) {
				System.out.println("The sum of the Numbers is:::"+" "+request.getNumber());
				sum+=request.getNumber();
			}
			
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
			
			@Override
			public void onCompleted() {
				Response response=Response.newBuilder().setResult(sum).build();
				responseObserver.onNext(response);
			}
		};
	}

}
