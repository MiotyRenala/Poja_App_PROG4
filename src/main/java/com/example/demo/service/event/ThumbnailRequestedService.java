package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.ThumbnailRequested;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.repository.SubmissionRepository;
import jakarta.mail.internet.InternetAddress;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ThumbnailRequestedService implements Consumer<ThumbnailRequested> {

  private static final int THUMBNAIL_SIZE = 256;

  private final SubmissionRepository submissionRepository;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(ThumbnailRequested event) {
    var originalBytes = Base64.getDecoder().decode(event.getBase64Content());
    var originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));

    var resized = new BufferedImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = resized.createGraphics();
    graphics.drawImage(originalImage, 0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE, null);
    graphics.dispose();

    var format = extractFormat(event.getOriginalFileName());
    var tempFile = File.createTempFile("thumbnail-" + event.getSubmissionId(), "." + format);
    ImageIO.write(resized, format, tempFile);

    var bucketKey = "thumbnails/" + event.getSubmissionId() + "." + format;
    bucketComponent.upload(tempFile, bucketKey);

    var submission = submissionRepository.findById(event.getSubmissionId()).orElseThrow();
    submission.setThumbnailKey(bucketKey);
    submissionRepository.save(submission);

    try {
      var downloadUri = bucketComponent.presign(bucketKey, Duration.ofDays(7));
      mailer.accept(
          new Email(
              new InternetAddress(event.getEmail()),
              List.of(),
              List.of(),
              "Sticker done",
              "Find and download your sticker here : " + downloadUri,
              List.of()));
    } catch (Exception e) {
      log.warn("Fail to send email {} : {}", event.getSubmissionId(), e.getMessage());
    }
  }

  private String extractFormat(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "png";
    }
    var extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    return switch (extension) {
      case "jpg", "jpeg" -> "jpg";
      default -> "png";
    };
  }
}
