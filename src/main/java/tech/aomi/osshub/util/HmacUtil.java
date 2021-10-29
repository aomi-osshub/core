package tech.aomi.osshub.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Sean createAt 2021/10/27
 */
public class HmacUtil {

    /**
     * Returns HMacSHA256 digest of given key and data.
     */
    public static byte[] sumHmac(byte[] key, byte[] data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        mac.update(data);
        return mac.doFinal();
    }
}
