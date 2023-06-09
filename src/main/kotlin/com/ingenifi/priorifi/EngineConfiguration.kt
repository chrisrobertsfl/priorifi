package com.ingenifi.priorifi

import com.ingenifi.engine.ClasspathResource
import com.ingenifi.engine.Engine
import com.ingenifi.engine.Option
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EngineConfiguration {

    @Bean
    fun validationEngine(): Engine {
        return Engine(
            ruleResources = listOf(ClasspathResource("task-service-validations.drl")),
            options = listOf(Option.TRACK_RULES, Option.SHOW_FACTS)
        )
    }
}