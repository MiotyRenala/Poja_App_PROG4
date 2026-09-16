package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "submission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

  @Id private UUID id;

  private String email;

  /** null tant que le traitement asynchrone n'a pas mis à jour la soumission. */
  private String thumbnailKey;

  private Instant createdAt;
}
