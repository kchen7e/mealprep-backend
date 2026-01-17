package com.mealprep.MealPrep.service;

import java.io.InputStream;

public interface StorageService {

  /**
   * Upload a file to storage
   *
   * @param bucket bucket/container name
   * @param key object key (path)
   * @param data file data
   * @param contentType MIME type
   * @return the object key
   */
  String upload(String bucket, String key, InputStream data, long size, String contentType);

  /**
   * Download a file from storage
   *
   * @param bucket bucket/container name
   * @param key object key (path)
   * @return file data as stream
   */
  InputStream download(String bucket, String key);

  /**
   * Delete a file from storage
   *
   * @param bucket bucket/container name
   * @param key object key (path)
   */
  void delete(String bucket, String key);

  /**
   * Get the public URL for an object
   *
   * @param bucket bucket/container name
   * @param key object key (path)
   * @return public URL
   */
  String getPublicUrl(String bucket, String key);
}
