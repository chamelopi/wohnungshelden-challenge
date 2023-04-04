package searchrequests.configuration;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * Sets up (de-)serialization of ISO 8601 timestamps to zoned date time and vice versa
 */
@Configuration
public class DateFormatConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.deserializers(InstantDeserializer.ZONED_DATE_TIME)
                .serializers(new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
    }
}
