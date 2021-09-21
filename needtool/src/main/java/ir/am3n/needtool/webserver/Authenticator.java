// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

public abstract class Authenticator
{
    public abstract Result authenticate(final HttpExchange p0);
    
    public abstract static class Result
    {
    }
    
    public static class Failure extends Result
    {
        private int responseCode;
        
        public Failure(final int responseCode) {
            this.responseCode = responseCode;
        }
        
        public int getResponseCode() {
            return this.responseCode;
        }
    }
    
    public static class Success extends Result
    {
        private HttpPrincipal principal;
        
        public Success(final HttpPrincipal principal) {
            this.principal = principal;
        }
        
        public HttpPrincipal getPrincipal() {
            return this.principal;
        }
    }
    
    public static class Retry extends Result
    {
        private int responseCode;
        
        public Retry(final int responseCode) {
            this.responseCode = responseCode;
        }
        
        public int getResponseCode() {
            return this.responseCode;
        }
    }
}
