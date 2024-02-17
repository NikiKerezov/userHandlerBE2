package com.nikola.userhandlerbe2.services;


import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import com.nikola.userhandlerbe2.entities.ArticlesAndSentiments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VertexAiPrompterService {
  private final ArticleScraperService articleScraperService;
  private final GetLatestNewsService getLatestNewsService;

  public ArticlesAndSentiments getNewsOnCryptoCurrency(String name) throws Exception {
    List<String> articleLinks = getLatestNewsService.getArticleLinks(name);
    StringBuilder articles = new StringBuilder();
    for (String articleLink : articleLinks) {
      articles.append("\nARTICLE\n").append(articleScraperService.scrapeArticle(articleLink)).append("\n");
    }
     String instance =
        "{ \"prompt\": " + "\" This are my articles:  \n"  + articles +
                " \n What is the sentiment on buying in on " + name + " in these articles? Give a sentiment for each one and then combined.\" }";
    String parameters =
        "{\n"
            + "  \"temperature\": 0.2,\n"
            + "  \"maxOutputTokens\": 1024,\n"
            + "  \"topP\": 0.95,\n"
            + "  \"topK\": 40\n"
            + "}";
    String project = "cryptoprophet";
    String location = "us-central1";
    String publisher = "google";
    String model = "text-bison@002";

    PredictResponse predictResponse = predictTextPrompt(instance, parameters, project, location, publisher, model);

    Value prediction = predictResponse.getPredictions(0);

    // Get the 'content' field from the prediction
    Value content = prediction.getStructValue().getFieldsMap().get("content");

    // Extract the string value of the 'content' field
    ArticlesAndSentiments articlesAndSentiments = new ArticlesAndSentiments();
    articlesAndSentiments.setArticles(articleLinks);
    articlesAndSentiments.setSentiments(content.getStringValue());
    return articlesAndSentiments;
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

      PredictResponse predictResponse =
          predictionServiceClient.predict(endpointName, instances, parameterValue);
      System.out.println("Predict Response");
      System.out.println(predictResponse);

        return predictResponse;
    }
  }
}