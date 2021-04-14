package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import com.google.gson.Gson;

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

    Gson gson = new Gson();

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


	public static MatrixMultiplicationRequest requestBuilder(int[][] matrixA, int[][] matrixB)
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

	public static long getFootprint()
	{
		int[][] matrixOne = {{1,2} , {3,4}};
		int[][] matrixTwo = {{5,6} , {7,8}};
		long startTime = System.nanoTime();

		ManagedChannel channel = ManagedChannelBuilder.forAddress("172.31.74.179", 9090)
		.usePlaintext()
			.build();

		MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceBlockingStub stub =
				MatrixMultiplicationServiceGrpc.newBlockingStub(channel);

		stub.multiplyBlock(requestBuilder(matrixOne,matrixTwo));

		long endTime = System.nanoTime();
		long footPrint = endTime - startTime;
		//double ftprnt = (double) footPrint / 1_000_000_000; // converting into seconds
		System.out.println("Foot print is = " + footPrint);
        System.out.println("Integer foot print is = " + (int)footPrint);
		return footPrint;
	}

	public static int[][] arrayReplyBuilder(MatrixMultiplicationReply reply)
	{

		int size = reply.getMatrixC(0).getColumnCount();
		int[][] C = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < C[i].length; j++)
			{
				C[i][j] = reply.getMatrixC(i).getColumn(j);
				System.out.println("C["+i+"]["+j+"]=" + C[i][j] );
			}
		}
		return C;
	}

	public static MatrixMultiplicationRequest getRequestNumOf(/*int[][] matrixA, int[][] matrixB,*/ int bs, int reqNum)
	{
		System.out.println("Entered request num method ," + " reqNum is = "+ reqNum);
		int size = matrixA.length / bs;
		int rowA = reqNum / (size * size);
		int colArowB = (reqNum % (size * size)) / size;
		int colB = (reqNum % (size * size)) % size;
		int[][] ma = new int[bs][bs];
		int[][] mb = new int[bs][bs];
		for (int ki = 0; ki < bs; ki++)
		{
			for (int kj = 0; kj < bs; kj++)
			{
				ma[ki][kj] = matrixA[rowA * bs + ki][colArowB * bs + kj];
				mb[ki][kj] = matrixB[colArowB * bs + ki][colB * bs + kj];
			}
		}
		return requestBuilder(ma, mb);
	}


	public static int[][] multiplyMatrixBlock(/*int[][] matrixA, int[][] matrixB ,*/ int deadline)
	{
		System.out.println("Entered the multiplication method");
		//int footPrint = (int)getFootprint();
		//long ftPrint = getFootprint();
		int footPrint = (int)getFootprint();
        System.out.println("Foot print is: " + footPrint);
		System.out.println("Deadline is = " + deadline);
		//int footPrint = 10;

		int bSize = 2;
		int newSize = matrixA.length / bSize;
		int multiplyNum = (newSize) * (newSize) * (newSize);
		System.out.println("Required multiply number  is: " + multiplyNum);
		int SERVER_NUM = (footPrint* multiplyNum)/deadline; // from the formula
		System.out.println("Number of servers required is: " + SERVER_NUM);
		if(SERVER_NUM > 4)
		{SERVER_NUM = 4;}
		System.out.println("Number of servers are set to: " + SERVER_NUM);
		int workPerServer = multiplyNum / SERVER_NUM;
		System.out.println("Work per server is:" + workPerServer);
		ArrayList<StreamObserver<MatrixMultiplicationRequest>> requestObserverList = new ArrayList<>();
		ArrayList<int[][]> resultMatrixArrayList = new ArrayList<>(multiplyNum);
		ArrayList<CountDownLatch> latchList = new ArrayList<>(SERVER_NUM);
		int[] resNum = new int[SERVER_NUM];
		int[] reqNum = new int[SERVER_NUM];
		System.out.println("Channels are being created...");
		ManagedChannel channel1 = ManagedChannelBuilder.forAddress("172.31.74.179", 9090).usePlaintext().build();
		ManagedChannel channel2 = ManagedChannelBuilder.forAddress("172.31.74.213", 9090).usePlaintext().build();
        ManagedChannel channel3 = ManagedChannelBuilder.forAddress("172.31.76.162", 9090).usePlaintext().build();
        ManagedChannel channel4 = ManagedChannelBuilder.forAddress("172.31.77.230", 9090).usePlaintext().build();
        /*ManagedChannel channel5 = ManagedChannelBuilder.forAddress("172.31.70.55", 8081).usePlaintext().build();
        ManagedChannel channel6 = ManagedChannelBuilder.forAddress("172.31.67.0", 8081).usePlaintext().build();
        ManagedChannel channel7 = ManagedChannelBuilder.forAddress("172.31.76.213", 8081).usePlaintext().build();
        ManagedChannel channel8 = ManagedChannelBuilder.forAddress("172.31.68.246", 8081).usePlaintext().build();*/

		System.out.println("Stubs are being created...");
		MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub1  =MatrixMultiplicationServiceGrpc.newStub(channel1);
		MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub2  =MatrixMultiplicationServiceGrpc.newStub(channel2);
        MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub3  =MatrixMultiplicationServiceGrpc.newStub(channel3);
        MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub4  =MatrixMultiplicationServiceGrpc.newStub(channel4);
       /* MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub5  =MatrixMultiplicationServiceGrpc.newStub(channel5);
        MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub6  =MatrixMultiplicationServiceGrpc.newStub(channel6);
        MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub7  =MatrixMultiplicationServiceGrpc.newStub(channel7);
        MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub asyncStub8  =MatrixMultiplicationServiceGrpc.newStub(channel8);*/

		//System.out.println("Adding stubs to list ... ");
		ArrayList<MatrixMultiplicationServiceGrpc.MatrixMultiplicationServiceStub> asyncStubList = new ArrayList<>(SERVER_NUM);
		asyncStubList.add(asyncStub1);
		asyncStubList.add(asyncStub2);
        asyncStubList.add(asyncStub3);
        asyncStubList.add(asyncStub4);
        /*asyncStubList.add(asyncStub5);
        asyncStubList.add(asyncStub6);
        asyncStubList.add(asyncStub7);
        asyncStubList.add(asyncStub8);*/
        for(int i=0; i<multiplyNum; i++)
		{
			resultMatrixArrayList.add(null);
		}
		for (int i = 0; i < SERVER_NUM; i++)
		{
			System.out.println("Stub " +i +" is called " + asyncStubList.get(i).toString());
			latchList.add(i , new CountDownLatch(1));
			System.out.println("Latch list size is" + latchList.size());
			System.out.println("Latch list " + i + "is set");
			int currentServer = i;
			System.out.println("Dealing with stub number " + i);
			System.out.println("Stub size is: " + asyncStubList.size());
			StreamObserver<MatrixMultiplicationRequest> temp =	asyncStubList.get(i).multiplyStreamBlock(new StreamObserver<MatrixMultiplicationReply>()
			{
				@Override
				public void onNext(MatrixMultiplicationReply matrixResult)
				{
					System.out.println("On next for result is being called...");

					System.out.println("current server: " + currentServer + " And re quest number is = " + reqNum[currentServer]);
					//this is called to get Result of Mult / server calls this to give us result
					//resultMatrixArrayList.toArray()[currentServer * workPerServer + (resNum[currentServer]++)] = arrayReplyBuilder(matrixResult);
					//resultMatrixArrayList.add(currentServer * workPerServer + (resNum[currentServer]++),arrayReplyBuilder(matrixResult));
					resultMatrixArrayList.set(currentServer * workPerServer + (resNum[currentServer]++),arrayReplyBuilder(matrixResult));
					//System.out.println("Request number = " + reqNum[currentServer]);
					if (reqNum[currentServer] < workPerServer)
					{
						System.out.println("current server in if: " + currentServer);
						System.out.println("Work is not done yet...");

						requestObserverList.get(currentServer).onNext(getRequestNumOf(/*matrixA,matrixB, */bSize ,currentServer * workPerServer + (reqNum[currentServer]++)));

					}// we call this on next to give server the next work load

					else
					{
						if(currentServer == 1 )
							System.out.println("Server " + currentServer + " finished its job");
						requestObserverList.get(currentServer).onCompleted();
					}

				}
				@Override
				public void onError(Throwable t) {
					latchList.get(currentServer).countDown(); }

				@Override
				public void onCompleted()
				{//info("Finished RecordRoute");
					latchList.get(currentServer).countDown();
				}
			});
			//requestObserverList.set(i,temp);
			requestObserverList.add(i,temp);

			System.out.println("Initial stream is calling ... ");
			//initial call to start stream
			requestObserverList.get(i).onNext(getRequestNumOf(/*matrixA,matrixB,*/ bSize ,i * workPerServer + (reqNum[i]++)));
		}


		for(int z=0 ;z<SERVER_NUM;z++)
		{
			try
			{
				latchList.get(z).await(1, TimeUnit.MINUTES);
			}catch (Exception e)
			{ }

		}

		System.out.println("Multiplication is ended and adding is starting here...");
		//add method
		int[][] matrix;
		int[][] bMatRes;
		ArrayList<int[][]> matrixArrayToAdd = new ArrayList<>(newSize);
		System.out.println("Result Matrix arraylist size is = " + resultMatrixArrayList.size());
		for (int k = 0; k < newSize /*matrixArrayToAdd.toArray().length*/; k++)
		{
			matrix = new int[matrixA.length][matrixB.length];
			//matrixArrayToAdd.set(k, matrix);

			for (int colIndex = 0; colIndex < newSize; colIndex++)
			{
				for (int rowIndex = 0; rowIndex < newSize; rowIndex++)
				{
					System.out.println("index = " + colIndex * (newSize * newSize) + k * newSize + rowIndex);
					bMatRes = resultMatrixArrayList.get(colIndex * (newSize * newSize) + k * newSize + rowIndex);
					for (int ki = 0; ki < bSize; ki++)
					{
						for (int kj = 0; kj < bSize; kj++)
						{
							matrix[rowIndex * bSize + ki][colIndex * bSize + kj] = bMatRes[ki][kj];
						}
					}
				}
			}
			matrixArrayToAdd.add(k,matrix);
		}


		//client
		System.out.println("Generating Result ... ");
		int[][] matrixResultFinal = new int[matrixA.length][matrixA.length];
		for (int r = 0; r < matrixResultFinal.length; r++)
		{
			for (int c = 0; c < matrixResultFinal[r].length; c++)
			{
				for (int[][] matrixx : matrixArrayToAdd)
					matrixResultFinal[r][c] += matrixx[r][c];
			}
		}

		for (int i=0;i<matrixResultFinal.length; i++)
		{
			for(int j=0;j<matrixResultFinal.length;j++)
			{
				System.out.println("result["+i+"]["+j+"] = " + matrixResultFinal[i][j]);
			}
		}
		return matrixResultFinal;
	}

	/*public JSONArray replyMatrixToJson(int[][] matrix)
	{
		JSONArray jsonArray = new JSONArray();
		for (int[] row : matrix)
		{
			JSONArray arr = new JSONArray();
			for (int element : row)
			{
				arr.put(String.valueOf(element));
				//	arr.add(Character.toString(el));
			}
			jsonArray.put(arr);
			//jsonArray.add(arr);
		}
		return jsonArray;
		//Another way
		*//*for(int[] row: matrix)
			jsonArray.put(Arrays.toString(row));*//*
	}*/

    public String  resultToJson(int[][] matrix)
    {
        /*int[][] data = {{1, 2, 3}, {3, 4, 5}, {4, 5, 6}};
        String json = gson.toJson(data);
        System.out.println("Data = " + json);*/

        String json = gson.toJson(matrix);
        System.out.println("Result = " + json);
        return json;
    }

}


