package carlease;

public enum BlockchainInvocationType {
    /**
     * Invoke ensures that a Fabric transaction is set up around a call, permitting modifications during the execution.
     */
    INVOKE("invoke"),
    
    /**
     * Query doesn't permit modifications but is very lightweight, executing much faster and without involving other 
     * nodes.
     */
    QUERY("query");

    private String pathParamValue;

    BlockchainInvocationType( String pathParamValue ) {
       this.pathParamValue = pathParamValue;
    }

    public String getPathParamValue() {
       return pathParamValue;
    }
    
    public void setPathParamValue(String pathParamValue) {
        this.pathParamValue = pathParamValue;
    }
}
