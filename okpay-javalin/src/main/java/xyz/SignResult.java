package xyz;

/**
 * @author wujiawei
 * @see
 * @since 2022/3/31 14:12
 */
public class SignResult {
    private String merchantSign;
    private String merchantCert;
    
    public String getMerchantSign() {
        return merchantSign;
    }
    
    public void setMerchantSign(String merchantSign) {
        this.merchantSign = merchantSign;
    }
    
    public String getMerchantCert() {
        return merchantCert;
    }
    
    public void setMerchantCert(String merchantCert) {
        this.merchantCert = merchantCert;
    }
}
