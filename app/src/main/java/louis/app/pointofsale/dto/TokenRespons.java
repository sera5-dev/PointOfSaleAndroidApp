package louis.app.pointofsale.dto;

public class TokenRespons {
    private String mXCSRFHeader;
    private String mXCSRFParam;
    private String mXCSRFToken;

    public String getXCSRFHeader() {
        return mXCSRFHeader;
    }

    public void setXCSRFHeader(String pXCSRFHeader) {
        this.mXCSRFHeader = pXCSRFHeader;
    }

    public String getXCSRFParam() {
        return mXCSRFParam;
    }

    public void setXCSRFParam(String pXCSRFParam) {
        this.mXCSRFParam = pXCSRFParam;
    }

    public String getXCSRFToken() {
        return mXCSRFToken;
    }

    public void setXCSRFToken(String pXCSRFToken) {
        this.mXCSRFToken = pXCSRFToken;
    }
}
