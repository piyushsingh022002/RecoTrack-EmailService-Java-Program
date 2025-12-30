package com.reco.emailservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "RecoTrack Email Service API",
        version = "1.0.0",
        description = "Endpoints for sending templated RecoTrack emails and monitoring delivery status."
))
public class OpenApiConfig {

    private static final List<HeaderMetadata> GLOBAL_HEADERS = List.of(
            new HeaderMetadata("Authorization", "Bearer token obtained from the authentication service.", false,
                    "Bearer eyJhbGciOiJIUzI1NiIs..."),
            new HeaderMetadata("X-CLIENT-ID", "Client identifier assigned to the caller.", false, "reco-client-001"),
            new HeaderMetadata("X-SIGNATURE", "HMAC signature that proves payload integrity.", false,
                    "sha256=abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890")
    );

    @Bean
    public OpenApiCustomizer globalHeaderDocumentation() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values()
                    .forEach(pathItem -> pathItem.readOperations().forEach(this::appendHeadersIfMissing));
        };
    }

    //private void appendHeadersIfMissing(Operation operation) {
      //  List<Parameter> parameters = operation.getParameters();
      //  if (parameters == null) {
      //      parameters = new ArrayList<>();
       //     operation.setParameters(parameters);
       // }
      //  GLOBAL_HEADERS.stream()
      //          .map(this::toParameter)
       //         .filter(parameter -> parameters.stream().noneMatch(existing -> existing.getName().equals(parameter.getName())))
       //         .forEach(parameters::add);
    //}

    private void appendHeadersIfMissing(Operation operation) {
    List<Parameter> existingParams = operation.getParameters();

    if (existingParams == null) {
        existingParams = new ArrayList<>();
        operation.setParameters(existingParams);
    }

    final List<Parameter> parameters = existingParams;

    GLOBAL_HEADERS.stream()
            .map(this::toParameter)
            .filter(parameter ->
                    parameters.stream()
                            .noneMatch(existing ->
                                    existing.getName().equals(parameter.getName())
                            )
            )
            .forEach(parameters::add);
}


    private Parameter toParameter(HeaderMetadata metadata) {
        return new Parameter()
                .name(metadata.name())
                .in("header")
                .description(metadata.description())
                .required(metadata.required())
                .schema(new StringSchema().example(metadata.example()));
    }

    private record HeaderMetadata(String name, String description, boolean required, String example) {
    }
}
