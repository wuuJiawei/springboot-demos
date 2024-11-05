import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

public class Main {
    private static final String CERTIFICATE_TYPE_NAME = "X.509";

    public static final String path = "/Users/wuu/Projects/sh-pan/那片云/coding/release.keystore";
    public static final String password = "123456";
    public static final String alias = "testalias";

    public static void main(String[] args) {
        System.out.println("path: " + path + ", password:" + password + ", alias:" + alias);
        try {
            FileInputStream is = new FileInputStream(path);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] passwd = password.toCharArray();
            keystore.load(is, passwd);
            Certificate cert = keystore.getCertificate(alias);
            String type = cert.getType();
            RSAPublicKey pub = (RSAPublicKey) cert.getPublicKey();
            String modulus = pub.getModulus().toString(10);
            System.out.println("modulus: " + modulus);
            if (CERTIFICATE_TYPE_NAME.equals(type) && cert instanceof X509Certificate) {
                X509Certificate x509cert = (X509Certificate) cert;
                String md5 = getThumbPrint(x509cert, "MD5");
                System.out.println("md5: " + md5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getThumbPrint(X509Certificate cert, String type)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance(type);
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        return hexify(digest);
    }

    public static String hexify(byte[] bytes) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder buf = new StringBuilder(bytes.length * 3);
        for (byte aByte : bytes) {
            buf.append(hexDigits[(aByte & 0xf0) >> 4]);
            buf.append(hexDigits[aByte & 0x0f]);
            buf.append(' ');
        }
        return buf.toString();
    }
}