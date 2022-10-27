package xyz.card.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

/**
 * RSAHelper - 对RSA 签名&验签/分段加密&分段解密 的包装 签名算法: "SHA256withRSA", 私钥进行签名; 公钥进行验签.
 * 加密算法: "RSA/ECB/PKCS1Padding", 公钥进行加密; 私钥进行解密.
 *
 * [localPrivKey]是自己的私钥, 自己的公钥给通信对方. [peerPubKey]是对方的公钥, 对方的私钥在对方那边. 为了方便,
 * 这里假定双方的密钥长度一致, 签名和加密的规则也一致.
 *
 * 以`Base64Str`结尾的参数表示内容是Base64编码的字符串, 其他情况都是raw字符串.
 */

public class RSAUtils {
	/**
	 * 自己的私钥
	 */
	public static String privKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDY3sa/WrW7UvkbQn1YgdBBXC75/3Xkbo7U0YypaJiAm4I6Zw+XB1Y5tPC4tiXakJ9deUQOlAuRNDPAFP4JSEF8tnpxoTDcgrDcSjfOG5oRjOLwXfDXzIxmtPhMFSjQGZykj5OlowL1UJ5KLW2Lqe0726firC6lhFATFumQ9Krb/ZUXGQvYfDfvWz0fxtGgsp+8xKS3MobOvsDjt5q9zCbc7agwAWsQjgJNPLfQ/wB3ba+p9AOqmB48DFRH7hUR86D8ka+zKSxFfKWk/ZC0O7S6Xd0EB7908DIHQ2bpW5bF+LSN02E4FTZe5VEfnaKeiEwGh51rp3VHjvHJfAFQNQh/AgMBAAECggEAZO6Bpysrayw5nXM7bQa1isXBYz5+errtpKSGB44Tc79sQ4wVmF+kxKSuZ/j18xvd354Bi5thKsoVz661cFJ9j/JJWDsBx47Xix7LjBQSh1p/gAjofzbGnUJaUOaEIR3aZkggGjWcudk0Yid0VUzXiUU4hGlIKGZE8i12tKz6G/m0pdg+dWxXqyyz4sNLW+b2TD6W17tXTC7ycXSPRSsYUKN7hm1JB7ZTNBcftqy2nHiK5aiqYKfkp+ALdmG9CPhSYFKzMWNXdG6DyzXkSe774mMuCGwhof+FFZ1wl8eGjCPggsAqB1I8qJ3HlbOIZR/O3schdMvgp6+0XfAC16d5IQKBgQDtpoxIjnpv0YI4cmlK+FIzcL/ml5AqQ21TCtnCLH17KXVzuc+6LZE4dY7DnKfuCeoZmmAi9GvnkYYODVhSOUmqm+SfhzKrka0nGcQ+lQfKts2ZqukbcDOEhnz7abYNQPYQ5WtNsoa4p05yZokJqjPxMHPqHFHp25yF6otpc1JbMQKBgQDpnXq5L78Ulz5Od02tvjir207AvVLRt4GM3WponQmzMsmGY16srF7BE5h4FGAXnPwGaPw/G1vEQJ0Xr2ZAljqfx6jYcLtmPZn19tGNR0T8wtoG7/xJeNAw8FubUjAwh5Vc+UTgjefUAAkVIcArd+p3eAPEquabu7ZtMzIWQiNSrwKBgGDcC7sazw46DuAgyYdz1FUA9EXwqiJ/ntREzOIWLgJ/sE49gku4fMLNLyXM+KjNQ6uxvtMgWcZlCoU5rTC+BqdRqInYma9RzYftOY3kQeu+O+gQv7eeMHEoO1JhEIEwY9oZF3XweHZUQmXgIqRcyZTZEbVuW3h8OLxgDvgot31RAoGAV1nr/nyM7S0UPhC7+eArno71JnpkEkiUZ1CPLiix7NBrrBVR3CzjjTw/gX7U419umRL+ZrZbHMmwM6ABEttZdk8Yoj2cxMD55Y9xkWr5r61ICtoHMo27AuGMz0nu/OZlggsupuxIp+uPedhR268N+7fjaNEiQ07wCmrfKpV2Ub8CgYEAnV3OW3iX9UYuvyDr0ClP/7n1xTkXDfcgGaHWzmQ8OdfklAPPQKuWLaYKFfZLiz9nAKpCEutU4DcKTpKs+sPO9460+y2mBfapeCml8uuSR1oGGho2P8RAHM16OKZOQE1h5CJMUJ3BxlnrGVNhqF+MPVpobXhjLvcT9HSisaChDS4=";
	/**
	 * 对方公钥
	 */
	public static String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwTX0n60guOTHRXTAMTpgVxrHr2X1kBBIhOwtPevtiwdpb7x5zEk2+QUrVVUG+5fNHHYzPr2NMhWNrGGaNYdBq6yZfLr2bpodpPm3olR84uYU/VwrF3GEFYZO/vslht1L7FbfyQwpemVEJ+bMeOu2f8/kkQTS2q9AeKqzg0vD2LoG8c0UWKmbthdsMUawrbx+SJH4wAYqOIL3GSQwT9h8hccrZLT+AH4kw5o2kTUFEbhMx+yi1zbLyfuLeBMuBa9DPyHWNGCDzqfwFji6Ku8ljAnKMv2YyDCOJhoyzQxlSdkQrs6X64sRQLoQYVgxXPyzAd+ldVK8XdtoHoAMCRCccQIDAQAB";
	/*static  {
        Properties prop =  new  Properties();
        InputStream in = Object. class .getResourceAsStream( "/initKey.properties" );
         try  {
            prop.load(in);
            privKey = prop.getProperty("privKey").trim();
            pubKey = prop.getProperty("xlpubKey").trim();
        }  catch  (IOException e) {
            e.printStackTrace();
        }
    }  */
	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
	public static final int KEYBIT = 2048;
	public static final int RESERVEBYTES = 11;

	private PrivateKey localPrivKey;
	private PublicKey peerPubKey;

	public RSAUtils() {
	}

	/**
	 * 初始化自己的私钥,对方的公钥以及密钥长度.
	 *
	 * @param localPrivKeyBase64Str
	 *            Base64编码的私钥,PKCS#8编码. (去掉pem文件中的头尾标识)
	 * @param peerPubKeyBase64Str
	 *            Base64编码的公钥. (去掉pem文件中的头尾标识)
	 * @param keysize
	 *            密钥长度, 一般2048
	 */
	/*public void initKey(int keysize)  {
		
			try {
                localPrivKey = getPrivateKey(privKey);
                peerPubKey = getPublicKey(pubKey);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	}*/

	/**
	 * 从文件中输入流中加载公钥
	 *
	 * @param in
	 *            公钥输入流
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public RSAPublicKey getPublicKey(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return getPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入缓存流出错");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入流出错");
			}
		}
	}

	/**
	 * 从字符串中加载公钥
	 *
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public RSAPublicKey getPublicKey(String publicKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory
					.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从文件中加载私钥
	 *
	 * @param in
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public RSAPrivateKey getPrivateKey(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return getPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入缓存流出错");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入流出错");
			}
		}
	}

	/**
	 * 从字符串中加载私钥
	 *
	 * @param privateKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载私钥时产生的异常
	 */
	public RSAPrivateKey getPrivateKey(String privateKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory
					.generatePrivate(keySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * RAS加密
	 *
	 * @param plainBytes
	 *            公钥
	 * @param useBase64Code
	 *            待加密信息
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] encryptRSA(byte[] plainBytes, boolean useBase64Code, String charset, String publicKey)
			throws Exception {
        peerPubKey = getPublicKey(publicKey);
		String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
		int KEYBIT = 2048;
		int RESERVEBYTES = 11;
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		int decryptBlock = KEYBIT / 8; // 256 bytes
		int encryptBlock = decryptBlock - RESERVEBYTES; // 245 bytes
		// 计算分段加密的block数 (向上取整)
		int nBlock = (plainBytes.length / encryptBlock);
		if ((plainBytes.length % encryptBlock) != 0) { // 余数非0，block数再加1
			nBlock += 1;
		}
		// 输出buffer, 大小为nBlock个decryptBlock
		ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock
				* decryptBlock);
		cipher.init(Cipher.ENCRYPT_MODE, peerPubKey);
		// cryptedBase64Str =
		// Base64.encodeBase64String(cipher.doFinal(plaintext.getBytes()));
		// 分段加密
		for (int offset = 0; offset < plainBytes.length; offset += encryptBlock) {
			// block大小: encryptBlock 或 剩余字节数
			int inputLen = (plainBytes.length - offset);
			if (inputLen > encryptBlock) {
				inputLen = encryptBlock;
			}
			// 得到分段加密结果
			byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
			// 追加结果到输出buffer中
			outbuf.write(encryptedBlock);
		}
		// 如果是Base64编码，则返回Base64编码后的数组
		if (useBase64Code) {
			return Base64.encodeBase64String(outbuf.toByteArray()).getBytes(
					charset);
		} else {
			return outbuf.toByteArray(); // ciphertext
		}
	}

	/**
	 * RSA解密
	 *
	 * @param privateKey
	 *            私钥
	 * @param cryptedBytes
	 *            待解密信息
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] decryptRSA(byte[] cryptedBytes, boolean useBase64Code,
			String charset,String privateKey) throws Exception {
		localPrivKey = getPrivateKey(privateKey);
		String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
		byte[] data = null;

		// 如果是Base64编码的话，则要Base64解码
		if (useBase64Code) {
			data = Base64.decodeBase64(new String(cryptedBytes, charset));
		} else {
			data = cryptedBytes;
		}

		int KEYBIT = 2048;
		int RESERVEBYTES = 11;
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		int decryptBlock = KEYBIT / 8; // 256 bytes
		int encryptBlock = decryptBlock - RESERVEBYTES; // 245 bytes
		// 计算分段解密的block数 (理论上应该能整除)
		int nBlock = (data.length / decryptBlock);
		// 输出buffer, , 大小为nBlock个encryptBlock
		ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock
				* encryptBlock);
		cipher.init(Cipher.DECRYPT_MODE, localPrivKey);
		// plaintext = new
		// String(cipher.doFinal(Base64.decodeBase64(cryptedBase64Str)));
		// 分段解密
		for (int offset = 0; offset < data.length; offset += decryptBlock) {
			// block大小: decryptBlock 或 剩余字节数
			int inputLen = (data.length - offset);
			if (inputLen > decryptBlock) {
				inputLen = decryptBlock;
			}

			// 得到分段解密结果
			byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
			// 追加结果到输出buffer中
			outbuf.write(decryptedBlock);
		}
		outbuf.flush();
		outbuf.close();
		return outbuf.toByteArray();
	}

	/**
	 * RSA签名
	 *
	 * @param privateKey
	 *            私钥
	 * @param plainBytes
	 *            需要签名的信息
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] signRSA(byte[] plainBytes, boolean useBase64Code,
			String charset,String privateKey) throws Exception {
		localPrivKey = getPrivateKey(privateKey);
		String SIGNATURE_ALGORITHM = "SHA256withRSA";
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(localPrivKey);
		signature.update(plainBytes);

		// 如果是Base64编码的话，需要对签名后的数组以Base64编码
		if (useBase64Code) {
			return Base64.encodeBase64String(signature.sign())
					.getBytes(charset);
		} else {
			return signature.sign();
		}
	}



	/**
	 * 验签操作
	 *
	 * @param publicKey
	 *            公钥
	 * @param plainBytes
	 *            需要验签的信息
	 * @param signBytes
	 *            签名信息
	 * @return boolean
	 */
	public boolean verifyRSA(byte[] plainBytes, byte[] signBytes,
			boolean useBase64Code, String charset,String publicKey) throws Exception {
        peerPubKey = getPublicKey(publicKey);
		boolean isValid = false;
		String SIGNATURE_ALGORITHM = "SHA256withRSA";
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(peerPubKey);
		signature.update(plainBytes);

		// 如果是Base64编码的话，需要对验签的数组以Base64解码
		if (useBase64Code) {
			isValid = signature.verify(Base64.decodeBase64(new String(signBytes)));
		} else {
			isValid = signature.verify(signBytes);
		}
		return isValid;
	}

	public boolean verifyRSA(String srcStr, String signStr,String publicKey) throws Exception {
		peerPubKey = getPublicKey(publicKey);

		String SIGNATURE_ALGORITHM = "SHA256withRSA";
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(peerPubKey);
		signature.update(srcStr.getBytes("utf-8"));
		return signature.verify(Base64.decodeBase64(signStr));

	}

	public static void main(String [] args) {
		RSAUtils rsa = new RSAUtils();
		String say = "{\"batchNo\":\"20190722\",\"busCnl\":\"POS\",\"busId\":\"680015611\",\"bussJnlNo\":\"2019072257639139729101\",\"crdNo\":\"680015611\",\"crdPwd\":\"FBC13C8246179B44FA498B7AEF29A031\",\"fullAmtFlg\":\"0\",\"nonceStr\":\"bdebe6eb98c44a99aa18953a27a65760\",\"reqJnlNo\":\"bdebe6eb98c44a99aa18953a27a65760\",\"reqTxnDt\":\"20190724\",\"reqTxnTm\":\"090454\",\"serviceCode\":\"singleConsume\",\"sign\":\"tqF/AfynOLvIL8qMf61R48+BPRzBpQF9y3us7R7jrAISpxiYe9S6Zm07ucGq4saDugsmskDSnJ3Pn6JcRbOIKjkSf4mddoosE5C8lY3PF8kWzT41+xN5dl4jZEwTL6Xp8kIvyKckZsQiSZ/JwumhBycZ6knabfgbjI/XgaJYLBY091ebrb79xEkcTnMPWs/tML4iWc/x9Fn5TcZTwWyJ1oYa5wU7tFeAs65Y3hOoWnETBq+7RLu0vFpgqZxtKPfvtLITbqQug2Om/aUKe000FcaczM4ZK2R3cz/TM910Mh+U7ONAzsfX2A8O4DFUBb0D1up2ljXaSwliOQoj2++Hog==\",\"storeId\":\"09999\",\"sysCnl\":\"OKPOS\",\"tellerNo\":\"11111\",\"termId\":\"001\",\"txnAmt\":\"0.01\"}";
		JSONObject json = JSONUtil.parseObj(say);
		try {
			String str = SignUtils.getParamStr(json);
			String sign = new String(rsa.signRSA(str.getBytes(), true, "utf-8",privKey));
			System.out.println(sign);
			//boolean flag = rsa.verifyRSA(say.getBytes(), sign.getBytes(), true, "utf-8",pubKey);
			System.out.println("tqF/AfynOLvIL8qMf61R48+BPRzBpQF9y3us7R7jrAISpxiYe9S6Zm07ucGq4saDugsmskDSnJ3Pn6JcRbOIKjkSf4mddoosE5C8lY3PF8kWzT41+xN5dl4jZEwTL6Xp8kIvyKckZsQiSZ/JwumhBycZ6knabfgbjI/XgaJYLBY091ebrb79xEkcTnMPWs/tML4iWc/x9Fn5TcZTwWyJ1oYa5wU7tFeAs65Y3hOoWnETBq+7RLu0vFpgqZxtKPfvtLITbqQug2Om/aUKe000FcaczM4ZK2R3cz/TM910Mh+U7ONAzsfX2A8O4DFUBb0D1up2ljXaSwliOQoj2++Hog==");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
