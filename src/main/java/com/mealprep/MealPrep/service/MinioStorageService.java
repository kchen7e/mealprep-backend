package com.mealprep.MealPrep.service;

import io.minio.*;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MinioStorageService implements StorageService {

  @Autowired private MinioClient minioClient;

  @Value("${minio.public-url}")
  private String publicUrl;

  @Override
  public String upload(String bucket, String key, InputStream data, long size, String contentType) {
    try {
      ensureBucketExists(bucket);
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucket).object(key).stream(data, size, -1)
              .contentType(contentType)
              .build());
      return key;
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  @Override
  public InputStream download(String bucket, String key) {
    try {
      return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build());
    } catch (Exception e) {
      throw new RuntimeException("Failed to download file from MinIO", e);
    }
  }

  @Override
  public void delete(String bucket, String key) {
    try {
      minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete file from MinIO", e);
    }
  }

  @Override
  public String getPublicUrl(String bucket, String key) {
    if (key == null) {
      return null;
    }
    return String.format("%s/%s/%s", publicUrl, bucket, key);
  }

  private void ensureBucketExists(String bucket) {
    try {
      boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!exists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to ensure bucket exists", e);
    }
  }
}
