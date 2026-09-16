package com.example.demo.endpoint.event.model;

import java.time.Duration;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "base64Content")
public class ThumbnailRequested extends PojaEvent {

  private UUID submissionId;
  private String email;
  private String originalFileName;

  private String base64Content;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(60);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
