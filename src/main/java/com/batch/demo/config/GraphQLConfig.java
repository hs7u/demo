package com.batch.demo.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

@Configuration
public class GraphQLConfig {

    @Bean
    RuntimeWiringConfigurer configurer() {
        GraphQLScalarType scalarType = dateTimeScalarBean();
        return (builder) -> builder.scalar(scalarType);
    }

    @Bean
    public GraphQLScalarType dateTimeScalarBean() {
        return GraphQLScalarType
                .newScalar()
                .name("OffsetDateTime")
                .description("Java OffsetDateTime as scalar.")
                .coercing(new Coercing<OffsetDateTime, String>() {
                    @Override
                    public String serialize(final Object dataFetcherResult) {
                        if (dataFetcherResult instanceof OffsetDateTime) {
                            return ((OffsetDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        } else {
                            throw new CoercingSerializeException("Expected a LocalDateTime object.");
                        }
                    }

                    @Override
                    public OffsetDateTime parseValue(final Object input) {
                        try {
                            if (input instanceof String) {
                                return OffsetDateTime.parse((String) input, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            } else {
                                throw new CoercingParseValueException("Expected a String");
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(String.format("Not a valid dateTime: '%s'.", input), e);
                        }
                    }

                    @Override
                    public OffsetDateTime parseLiteral(final Object input) {
                        if (input instanceof StringValue) {
                            try {
                                return OffsetDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(e);
                            }
                        } else {
                            throw new CoercingParseLiteralException("Expected a StringValue.");
                        }
                    }
                })
                .build();
        
    }
}
