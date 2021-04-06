package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
@Service
public class GRPCClientService {
    public String ping() {
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("172.31.74.179", 9090)
                .usePlaintext()
                .build();        
		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);        
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());        
		channel.shutdown();        
		return helloResponse.getPong();
    }


	/*public static double getFootprint()
	{
		int[][] matrixOne = {{1,2} , {3,4}};
		int[][] matrixTwo = {{5,6} , {7,8}};
		long startTime = System.nanoTime();

		ManagedChannel channel = ManagedChannelBuilder.forAddress("172.31.74.179", 8081)
				.usePlaintext()
				.build();
		MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceBlockingStub stub
				= MatrixMultiplicationServiceGrpc.newBlockingStub(channel);

		stub.multiplyBlock(requestBuilder(matrixOne,matrixTwo));
		System.out.println(" Foot print is called");

		long endTime = System.nanoTime();
		long footPrint = endTime - startTime;
		double ftprnt = (double) footPrint / 1_000_000_000;
		return ftprnt;
	}*/


	public /*static*/ MatrixMultiplicationRequest requestBuilder(int[][] matrixA, int[][] matrixB)
	{
		MatrixMultiplicationRequest.Builder request = MatrixMultiplicationRequest.newBuilder();
		int MAX = matrixA.length;
		for(int i = 0; i < MAX; i++)
		{
			Row.Builder row = Row.newBuilder();
			for (int col : matrixA[i]) {
				row.addColumn(col);
			}
			request.addMatrixA(row.build());
		}
		for (int i = 0; i < MAX; i++) {
			Row.Builder row = Row.newBuilder();
			for (int col : matrixB[i]) {
				row.addColumn(col);
			}
			request.addMatrixB(row.build());
		}
		return request.build();
	}

	public /*static*/ double getFootprint()
	{
		System.out.println("Entered footprint method");
		int[][] matrixOne = {{1,2} , {3,4}};
		int[][] matrixTwo = {{5,6} , {7,8}};
		long startTime = System.nanoTime();

		ManagedChannel channel = ManagedChannelBuilder.forAddress("172.31.74.179", 9090)
		.usePlaintext()
			.build();

		MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceBlockingStub stub =
				MatrixMultiplicationServiceGrpc.newBlockingStub(channel);

		stub.multiplyBlock(requestBuilder(matrixOne,matrixTwo));

		System.out.println(" Foot print is called ... ");

		long endTime = System.nanoTime();
		long footPrint = endTime - startTime;
		double ftprnt = (double) footPrint / 1_000_000_000;
		System.out.println("Foor print is = " + ftprnt);
		return ftprnt;
	}
}
