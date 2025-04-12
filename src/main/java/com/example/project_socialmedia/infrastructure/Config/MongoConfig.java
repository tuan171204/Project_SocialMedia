package com.example.project_socialmedia.infrastructure.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.project_socialmedia.domain.Repository")
public class MongoConfig {
}
