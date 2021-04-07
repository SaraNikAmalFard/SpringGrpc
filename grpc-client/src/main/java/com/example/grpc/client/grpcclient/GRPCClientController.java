package com.example.grpc.client.grpcclient;

import com.google.gson.JsonArray;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class GRPCClientController {

	GRPCClientService grpcClientService;    
	@Autowired
    	public GRPCClientController(GRPCClientService grpcClientService) {
        	this.grpcClientService = grpcClientService;
    	}

	@Autowired
	public FileStorageService fileStorageService;


	@PostMapping("/uploadFile")
	public String uploadFile(@RequestParam("file") MultipartFile file)
	{
		fileStorageService.storeFile(file);
		return "File successfully uploaded";
	}

	@PostMapping("/uploadMultipleFiles")
	public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files)
	{
		Arrays.asList(files).stream().map(file->uploadFile(file)).collect(Collectors.toList());
		if(grpcClientService.checkFiles() == true)
		{
			return "File successfully uploaded";
		}
		else
		{
			grpcClientService.delete();
			return "The matrices or the file you uploaded was not accepted!";
		}
	}
	@GetMapping("/ping")
    	public String ping() {
        	return grpcClientService.ping();
    	}

    @GetMapping("/getFootprint")
      public double getFootprint()
	  {
	  	System.out.println("Creating 2D array from the file");
	  	grpcClientService.create2DArrayFromFile();
	  	return grpcClientService.getFootprint();
	  }

	@GetMapping("/getRequestNumOf")
	public String getRequestNumOf(@RequestParam(value = "bs" , defaultValue = "2") int bs, @RequestParam(value = "reqNum" , defaultValue = "4") int reqNum)
	{
		System.out.println("Getting Request number of...");
		grpcClientService.getRequestNumOf(bs,reqNum);
		return "Method successfully finished";
	}

	@GetMapping("/multiplyMatrixBlock")
	public String multiplyMatrixBlock(@RequestParam(value = "deadline" , defaultValue = "10") int deadline)
	{
		//System.out.println("Creating 2D array from the file...");
		grpcClientService.create2DArrayFromFile();
		//System.out.println("2D arrays create finished.");
		//System.out.println("In controller, calling multiply block...");
		//System.out.println("Deadline = " + deadline);
		//grpcClientService.multiplyMatrixBlock(deadline)
		System.out.println("Generating results...");
		 grpcClientService.replyMatrixToJson(grpcClientService.multiplyMatrixBlock(deadline));
		return "Multiplication successfully done!";
	}
}
