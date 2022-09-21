package ru.egartech.sickday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.egartech.sickday.domain.remain.SickDayRemainResolver;
import ru.egartech.sickday.domain.remain.aggregator.SickDayRemainAggregator;

import java.util.List;

@ComponentScan("ru.egartech.sickday.domain.remain")
public class SickDayRemainResolverTestConfig extends AbstractBaseConfig {
    @Bean
    public SickDayRemainResolver sickDayRemainResolver(List<SickDayRemainAggregator> sickDayRemainAggregators) {
        return new SickDayRemainResolver(sickDayRemainAggregators);
    }
}
