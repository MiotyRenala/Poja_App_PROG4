package com.example.demo.service;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.ThumbnailRequested;
import com.example.demo.entity.Submission;
import com.example.demo.handler.exception.InvalidSubmissionException;
import com.example.demo.repository.SubmissionRepository;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class SubmissionService {

  private final SubmissionRepository submissionRepository;
  private final EventProducer<ThumbnailRequested> eventProducer;

  @SneakyThrows
  public Submission createSubmission(MultipartFile file, String email) {
    validate(file, email);

    var submission =
        Submission.builder()
            .id(UUID.randomUUID())
            .email(email)
            .thumbnailKey(null)
            .createdAt(Instant.now())
            .build();
    submissionRepository.save(submission);

    var event =
        ThumbnailRequested.builder()
            .submissionId(submission.getId())
            .email(email)
            .originalFileName(file.getOriginalFilename())
            .base64Content(Base64.getEncoder().encodeToString(file.getBytes()))
            .build();
    eventProducer.accept(List.of(event));

    return submission;
  }

  public List<Submission> listSubmissions() {
    return submissionRepository.findAll();
  }

  private void validate(MultipartFile file, String email) {
    if (file == null || file.isEmpty()) {
      throw new InvalidSubmissionException("File is required.");
    }
    if (email == null || email.isBlank()) {
      throw new InvalidSubmissionException("Email is required");
    }
  }
}
