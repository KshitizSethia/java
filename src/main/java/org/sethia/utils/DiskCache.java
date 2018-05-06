package org.sethia.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO make generic if needed
// WARN possible bottleneck as it uses a BIG lock
public enum DiskCache {
  EXPIRING_6_HOURS(6 * 60 * 60);

  // todo take from command line/config?
  private static final String FOLDER_FOR_CACHE = "./diskCache/";

  // TODO try making these static
  private final Logger log = LoggerFactory.getLogger(DiskCache.class);
  private final File indexFile = Paths.get(FOLDER_FOR_CACHE, "index.bin").toFile();
  private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Charset CHARSET = Charset.forName("UTF-8");
  private final MessageDigest HASHER;

  // instance vars
  private final long expiryTimeInSeconds;
  // for multi threaded protection
  private final Object lock;

  // TODO use for avoiding multiple reads from disk for same file
  //private final Map<String, String> inMemoryStore;


  DiskCache(long expirySeconds) {
    lock = new Object();
    expiryTimeInSeconds = expirySeconds;

    try {
      HASHER = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      log.error("Hashing algorithm not found", e);
      throw new RuntimeException(e);
    }

    File folder = new File(FOLDER_FOR_CACHE);
    if (!folder.exists()) {
      folder.mkdirs();
    }
  }

  private boolean contains(String key) {
    synchronized (lock) {
      File cachedFile = getFileForCaching(key);
      long lastAcceptedTimestamp = System.currentTimeMillis() - (expiryTimeInSeconds * 1000);
      if (cachedFile.exists()
          && lastAcceptedTimestamp <= cachedFile.lastModified()) {
        return true;
      }

    }
    return false;
  }

  public Optional<String> get(String key) {
    synchronized (lock) {
      if (contains(key)) {
        File cachedFile = getFileForCaching(key);
        try {
          return Optional
              .of(String.join(System.lineSeparator(), Files.readLines(cachedFile, CHARSET)));
        } catch (IOException e) {
          log.error("Problem in accessing a valid cache file", e);
          throw new RuntimeException(e);
        }
      }
    }
    return Optional.empty();
  }

  private File getFileForCaching(String key) {
    String md5HashOfkey = DatatypeConverter.printHexBinary(HASHER.digest(key.getBytes()));
    return Paths.get(FOLDER_FOR_CACHE, md5HashOfkey).toFile();
  }

  public void put(String key, String value) {
    synchronized (lock) {
      File cachedFile = getFileForCaching(key);
      try (BufferedWriter writer = Files.newWriter(cachedFile, CHARSET)) {
        writer.write(value);
      } catch (Exception e) {
        log.error("error writing to cache for {} with hash path {}", key, cachedFile.getName(), e);
        throw new RuntimeException(e);
      }
    }
  }
}
