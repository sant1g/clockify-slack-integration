package com.sant1g.horas.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    return Executors.newSingleThreadExecutor();
  }
}
