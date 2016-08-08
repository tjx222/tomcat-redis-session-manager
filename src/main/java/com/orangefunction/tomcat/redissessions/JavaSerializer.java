package com.orangefunction.tomcat.redissessions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class JavaSerializer implements Serializer {
  private ClassLoader loader;

  private final Log log = LogFactory.getLog(JavaSerializer.class);

  @Override
  public void setClassLoader(ClassLoader loader) {
    this.loader = loader;
  }

  @Override
public byte[] attributesHashFrom(RedisSession session) throws IOException {
    HashMap<String,Object> attributes = new HashMap<String,Object>();
    for (Enumeration<String> enumerator = session.getAttributeNames(); enumerator.hasMoreElements();) {
      String key = enumerator.nextElement();
      attributes.put(key, session.getAttribute(key));
    }

    byte[] serialized = null;

    try (
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
    ) {
      oos.writeUnshared(attributes);
      oos.flush();
      serialized = bos.toByteArray();
    }

    MessageDigest digester = null;
    try {
      digester = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      log.error("Unable to get MessageDigest instance for MD5");
    }
    return digester.digest(serialized);
  }

  @Override
  public byte[] serializeFrom(RedisSession session, SessionSerializationMetadata metadata) throws IOException {
    byte[] serialized = null;

    try (
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
    ) {
      oos.writeObject(metadata);
      session.writeObjectData(oos);
      oos.flush();
      serialized = bos.toByteArray();
    }

    return serialized;
  }

  @Override
  public void deserializeInto(byte[] data, RedisSession session, SessionSerializationMetadata metadata) throws IOException, ClassNotFoundException {
    try(
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
        ObjectInputStream ois = new CustomObjectInputStream(bis, loader);
    ) {
      SessionSerializationMetadata serializedMetadata = (SessionSerializationMetadata)ois.readObject();
      metadata.copyFieldsFrom(serializedMetadata);
      session.readObjectData(ois);
     /* // Deserialize the scalar instance variables (except Manager)
      Long  creationTime = ((Long) ois.readObject()).longValue();
      Long lastAccessedTime = ((Long) ois.readObject()).longValue();
      Integer maxInactiveInterval = ((Integer) ois.readObject()).intValue();
      Boolean isNew = ((Boolean) ois.readObject()).booleanValue();
      Boolean isValid = ((Boolean) ois.readObject()).booleanValue();
      Long thisAccessedTime = ((Long) ois.readObject()).longValue();
      //        setId((String) stream.readObject());
      String id = (String) ois.readObject();

      // Deserialize the attribute count and attribute values
      Map<String,Object> attributes = new ConcurrentHashMap<String, Object>();
      int n = ((Integer) ois.readObject()).intValue();
      boolean isValidSave = isValid;
      isValid = true;
      for (int i = 0; i < n; i++) {
          String name = (String) ois.readObject();
          final Object value;
          try {
              value = ois.readObject();
          } catch (WriteAbortedException wae) {
              if (wae.getCause() instanceof NotSerializableException) {
                  // Skip non serializable attributes
                  continue;
              }
              throw wae;
          }
      }*/
    }
  }
}
