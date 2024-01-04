package com.grpc.client.streaming.client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.example.sum.CalculatorGrpc;
import com.example.sum.Request;
import com.example.sum.Response;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class CalculatorClient 
{
	public static void main(String[] args)
	{
		String address="localhost";
		int portNumber=8081;
		ManagedChannel channel=ManagedChannelBuilder
				.forAddress(address, portNumber)
				.usePlaintext()
				.build();
		CountDownLatch countDownLatch=new CountDownLatch(1);
		CalculatorGrpc.CalculatorStub stub=CalculatorGrpc.newStub(channel);
		
		StreamObserver<Request> streamObserver=stub.getSum(new StreamObserver<Response>() {
			
			@Override
			public void onNext(Response value) {
				System.out.println("The Total Sum of the Given Numbers is"+" "+value.getResult());
			}
			
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				countDownLatch.countDown();
			}
			
			@Override
			public void onCompleted() 
			{
				countDownLatch.countDown();
				
			}
		});
		
		List<Integer> list=Arrays.asList(1,2,3,4,5);
		for(Integer i:list)
		{
			Request request=Request.newBuilder().setNumber(i).build();
			streamObserver.onNext(request);
		}
		
		streamObserver.onCompleted();

        try {
            countDownLatch.await();
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException | RuntimeException e) {
            e.printStackTrace();
        }
	}

}
