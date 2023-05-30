package me.nogari.nogari.api.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;

import java.nio.charset.StandardCharsets;

public class LambdaInvokeFunction {
	private String functionName = "notion-parser";
	private String notionToken;
	private String url;
	private String type;

	public LambdaInvokeFunction(String notionToken, String url, String type){
		this.notionToken = notionToken;
		this.url = url;
		this.type = type;
	}

	public void post() {
		InvokeRequest invokeRequest = new InvokeRequest()
			.withFunctionName(functionName)
			.withPayload("{\n"
				+ "  \"notion\": {\n"
				+ "    \"notionToken\": \""
				+ notionToken
				+ "\",\n"
				+ "    \"url\": \""
				+ url
				+ "\"\n"
				+ "  },\n"
				+ "  \"type\": \""
				+ type
				+ "\"\n"
				+ "}");

		InvokeResult invokeResult = null;

		try {
			AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider())
				.withRegion(Regions.AP_NORTHEAST_2).build();
			invokeResult = awsLambda.invoke(invokeRequest);
			String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
			System.out.println(ans);
		} catch (ServiceException e) {
			System.out.println(e);
		}
		System.out.println(invokeResult.getStatusCode());
	}
}
