package com.nikola.userhandlerbe2.services;


import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import com.nikola.userhandlerbe2.utils.Logger;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VertexAiPrompterService {
  private final ArticleScraperService articleScraperService;
  @org.springframework.beans.factory.annotation.Value("${vertex-ai.project.name}")
  private String vertexAiProjectName;

  public String getSentiments(String cryptoCurrencyName, String articles) throws Exception {
     String instance =
        "{ \"prompt\": " + "\" This are my article(s):  \n"  + articles +
                " \n What is the sentiment on buying in on " + cryptoCurrencyName + " in this article/these articles? " +
                "Give a sentiment for each one and then combined if there are more than one. " +
                "When talking about the sentiment of an article, provide the link for it." +
                "Also, provide reasoning for your choice." +
                "\" }";
     String parameters =
        "{\n"
            + "  \"temperature\": 0.2,\n"
            + "  \"maxOutputTokens\": 1024,\n"
            + "  \"topP\": 0.95,\n"
            + "  \"topK\": 40\n"
            + "}";
     String project = vertexAiProjectName;
     String location = "us-central1";
     String publisher = "google";
     String model = "text-bison@002";
     PredictResponse predictResponse = null;
     try {
        predictResponse = predictTextPrompt(instance, parameters, project, location, publisher, model);
     }
     catch (StatusRuntimeException e) {
        String[] articlesArray = articles.split("ARTICLE");
        StringBuilder newArticles = new StringBuilder();
        for (int i = 0; i < articlesArray.length - 1; i++) {
          newArticles.append(articlesArray[i]);
        }
        Logger.log("Failed to get sentiment, trying again with less articles");
        return getSentiments(cryptoCurrencyName, newArticles.toString());
     }
     catch (InvalidProtocolBufferException e) {
        String[] articlesArray = articles.split("ARTICLE");
        StringBuilder newArticles = new StringBuilder();
        for (int i = 0; i < articlesArray.length - 1; i++) {
          newArticles.append(articlesArray[i]);
        }
        Logger.log("Failed to get sentiment, trying again with less articles");
        return getSentiments(cryptoCurrencyName, newArticles.toString());
     }
     if (predictResponse == null) {
        throw new Exception("Failed to get sentiment");
     }
     Value prediction = predictResponse.getPredictions(0);

     // Get the 'content' field from the prediction
     Value content = prediction.getStructValue().getFieldsMap().get("content");

     return content.getStringValue();
  }

  // Get a text prompt from a supported text model
  public PredictResponse predictTextPrompt(
      String instance,
      String parameters,
      String project,
      String location,
      String publisher,
      String model)
      throws IOException {
    String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
    PredictionServiceSettings predictionServiceSettings =
            PredictionServiceSettings.newBuilder().setEndpoint(endpoint).build();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests.
    try (PredictionServiceClient predictionServiceClient =
        PredictionServiceClient.create(predictionServiceSettings)) {
      final EndpointName endpointName =
          EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);

      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      Value.Builder instanceValue = Value.newBuilder();
      JsonFormat.parser().merge(instance, instanceValue);
      List<Value> instances = new ArrayList<>();
      instances.add(instanceValue.build());

      // Use Value.Builder to convert instance to a dynamically typed value that can be
      // processed by the service.
      Value.Builder parameterValueBuilder = Value.newBuilder();
      JsonFormat.parser().merge(parameters, parameterValueBuilder);
      Value parameterValue = parameterValueBuilder.build();

      return predictionServiceClient.predict(endpointName, instances, parameterValue);
    }
  }
}