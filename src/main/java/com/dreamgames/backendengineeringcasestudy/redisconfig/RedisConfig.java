package com.dreamgames.backendengineeringcasestudy.redisconfig;

import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean(name = "groupRedisTemplate")
    public RedisTemplate<String, TournamentGroup> groupRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TournamentGroup> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Anahtar ve değer serileştiricilerini ayarlıyoruz
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean(name = "customStringRedisTemplate")
    public RedisTemplate<String, String> customStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Anahtar ve değer serileştiricilerini ayarlıyoruz
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}

