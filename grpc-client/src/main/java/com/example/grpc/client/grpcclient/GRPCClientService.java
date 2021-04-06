package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

	public static int matrixA[][];
	public static int matrixB[][];

	public boolean isPowerOfTwo(int n)
	{
		return (int) (Math.ceil((Math.log(n) / Math.log(2))))
				== (int) (Math.floor(((Math.log(n) / Math.log(2)))));
	}


	public boolean checkFiles()
	{
		File directoryPath = new File("./uploads");
		String[] contents = directoryPath.list();
		String filePath = null;
		List<String> lines = new ArrayList();

		System.out.println("Checking the files path ... ");

		int commonLineSize = 0;

		if (contents != null)
		{
			System.out.println("File path is not empty!");
			filePath = "./uploads" + "/" + contents[0];
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(filePath);
				Scanner scanner = new Scanner(fileInputStream);
				while (scanner.hasNextLine()) {
					lines.add(scanner.nextLine());
				}
				scanner.close();

				if (isPowerOfTwo(lines.size()) == true) {
					String[] elements;
					for (String line : lines) {
						elements = line.split(" ");
						if (elements.length != lines.size()) {
							return false;
						}
					}
				} else {
					return false;
				}
			} catch (java.io.FileNotFoundException e) {
				e.printStackTrace();
			}

			filePath = "./uploads" + "/" + contents[1];
			try {
				commonLineSize = lines.size();
				lines.clear();
				fileInputStream = new FileInputStream(filePath);
				Scanner scanner = new Scanner(fileInputStream);
				while (scanner.hasNextLine()) {
					lines.add(scanner.nextLine());
				}
				scanner.close();

				if (lines.size() == commonLineSize) {
					String[] elements;
					for (String line : lines) {
						elements = line.split(" ");
						if (elements.length != commonLineSize) {
							return false;
						}
					}
				} else {
					return false;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

	public static void delete()
	{
		File directory = new File("./uploads");
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
	}

	public void create2DArrayFromFile() {
		File directoryPath = new File("./uploads");
		String[] contents = directoryPath.list();
		String lineA;
		String lineB;
		int row = 0;
		int size = 0;
		System.out.println("Creating 2D matrix A and B");
		try {
			BufferedReader bufferA = new BufferedReader(new FileReader("./uploads" + "/" + contents[0]));
			BufferedReader bufferB = new BufferedReader(new FileReader("./uploads" + "/" + contents[1]));
			while ((lineA = bufferA.readLine()) != null) {
				lineB = bufferB.readLine();
				String[] valsA = lineA.trim().split("\\s+");
				String[] valsB = lineB.trim().split("\\s+");
				if (matrixA == null) {
					size = valsA.length;
					matrixA = new int[size][size];
					matrixB = new int[size][size];
				}

				for (int col = 0; col < size; col++) {
					matrixA[row][col] = Integer.parseInt(valsA[col]);
					matrixB[row][col] = Integer.parseInt(valsB[col]);
				}
				row++;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}


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
		System.out.println("Matrix A size = "+ matrixA.length);
		System.out.println("First element of matrix B is =" + matrixB[0][0]);
		int[][] matrixOne = {{1,2} , {3,4}};
		int[][] matrixTwo = {{5,6} , {7,8}};
		long startTime = System.nanoTime();

		ManagedChannel channel = ManagedChannelBuilder.forAddress("172.31.74.179", 9090)
		.usePlaintext()
			.build();

		MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceBlockingStub stub =
				MatrixMultiplicationServiceGrpc.newBlockingStub(channel);

		stub.multiplyBlock(requestBuilder(matrixOne,matrixTwo));

		System.out.println("Foot print is called ... ");

		long endTime = System.nanoTime();
		long footPrint = endTime - startTime;
		double ftprnt = (double) footPrint / 1_000_000_000;
		System.out.println("Foot print is = " + ftprnt);
		return ftprnt;
	}
}
