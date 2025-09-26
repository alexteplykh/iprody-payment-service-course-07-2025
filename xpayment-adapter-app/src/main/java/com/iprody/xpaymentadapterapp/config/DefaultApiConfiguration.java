package com.iprody.xpaymentadapterapp.config;

import com.iprody.xpayment.app.api.client.DefaultApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultApiConfiguration {

    @Bean
    public DefaultApi apiClient() {
        return new DefaultApi();
    }
}
