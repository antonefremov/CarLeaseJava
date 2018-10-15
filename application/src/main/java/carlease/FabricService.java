package carlease;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpEntityUtil;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;

import java.net.URI;
import java.util.Arrays;

public class FabricService
{

    private static final Logger logger = CloudLoggerFactory.getLogger(FabricService.class);

    private ScpCfService cfService;

    public FabricService( ScpCfService cfService ) {
        this.cfService = cfService;
    }

    public static FabricService createByGettingTokenViaCfServicesConfig() throws ShouldNotHappenException 
    {
        return getFabricService("hyperledger-fabric");
    }

    /**
     * Run a blockchain chaincode using the GET method...
     */
    public String invokeOrQuery(BlockchainInvocationType invocationType, String chaincodeId,
                                String function, String... args) throws Exception 
    {
        logger.trace("{} {}:{}({})", invocationType, chaincodeId, function, Arrays.asList(args));

        final HttpGet chaincodeEndpointRequest = new HttpGet(
                new URI(getServiceUrl() + "/chaincodes/" + chaincodeId
                        + "/latest/" + invocationType.getPathParamValue()).normalize());

        cfService.addBearerTokenHeader(chaincodeEndpointRequest);
        chaincodeEndpointRequest.setHeader("Accept", "application/json;charset=UTF-8");

        final JsonObject requestBody = new JsonObject();
        requestBody.addProperty("function", function);
        requestBody.add("arguments", new Gson().toJsonTree(args));
        requestBody.addProperty("async", false);

        final String json = new Gson().toJson(requestBody);
        logger.trace("Request: {}", json);
//        chaincodeEndpointRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        chaincodeEndpointRequest.setHeader("Content-Type", "application/json");

        try {
            final HttpResponse response = HttpClientAccessor.getHttpClient().execute(chaincodeEndpointRequest);
            final StatusLine statusLine = response.getStatusLine();

            final String responsePayload = HttpEntityUtil.getResponseBody(response);
            logger.debug("Response status: {}", statusLine);
            logger.trace("Response status: {}, content: {}", statusLine, responsePayload);

            switch (statusLine.getStatusCode()) {
                case HttpStatus.SC_OK:
                    return responsePayload;

                case HttpStatus.SC_UNAUTHORIZED:
                case HttpStatus.SC_FORBIDDEN:
                    throw new AuthenticationException("Access token already expired or simply wrong: "
                            + "Service request denied with status: " + statusLine);
                default:
                    throw new Exception("Failed to execute request to function " + function + ": "
                            + "Service request failed with status: " + statusLine);
            }
        } finally {
            chaincodeEndpointRequest.releaseConnection();
        }
    }

    /**
     * Run a blockchain chaincode using the POST method...
     */
    public String post(BlockchainInvocationType invocationType, String chaincodeId,
                                String function, String... args) throws Exception 
    {
        logger.trace("{} {}:{}({})", invocationType, chaincodeId, function, Arrays.asList(args));

        final HttpPost chaincodeEndpointRequest = new HttpPost(
                new URI(getServiceUrl() + "/chaincodes/" + chaincodeId
                        + "/latest/" + invocationType.getPathParamValue()).normalize());

        cfService.addBearerTokenHeader(chaincodeEndpointRequest);
        chaincodeEndpointRequest.setHeader("Accept", "application/json;charset=UTF-8");

        final JsonObject requestBody = new JsonObject();
        requestBody.addProperty("function", function);
        requestBody.add("arguments", new Gson().toJsonTree(args));
        requestBody.addProperty("async", false);

        final String json = new Gson().toJson(requestBody);
        logger.trace("Request: {}", json);
        chaincodeEndpointRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        chaincodeEndpointRequest.setHeader("Content-Type", "application/json");

        try {
            final HttpResponse response = HttpClientAccessor.getHttpClient().execute(chaincodeEndpointRequest);
            final StatusLine statusLine = response.getStatusLine();

            final String responsePayload = HttpEntityUtil.getResponseBody(response);
            logger.debug("Response status: {}", statusLine);
            logger.trace("Response status: {}, content: {}", statusLine, responsePayload);

            switch (statusLine.getStatusCode()) {
                case HttpStatus.SC_OK:
                    return responsePayload;

                case HttpStatus.SC_UNAUTHORIZED:
                case HttpStatus.SC_FORBIDDEN:
                    throw new AuthenticationException("Access token already expired or simply wrong: "
                            + "Service request denied with status: " + statusLine);
                default:
                    throw new Exception("Failed to execute request to function " + function + ": "
                            + "Service request failed with status: " + statusLine);
            }
        } finally {
            chaincodeEndpointRequest.releaseConnection();
        }
    }
    
    /**
     * Run a blockchain chaincode using the PUT method...
     */
    public String put(BlockchainInvocationType invocationType, String chaincodeId,
                                String function, String... args) throws Exception 
    {
        logger.info("Calling PUT with args " + Arrays.asList(args));
        logger.trace("{} {}:{}({})", invocationType, chaincodeId, function, Arrays.asList(args));

        final HttpPut chaincodeEndpointRequest = new HttpPut(
                new URI(getServiceUrl() + "/chaincodes/" + chaincodeId
                        + "/latest/" + invocationType.getPathParamValue()).normalize());

        cfService.addBearerTokenHeader(chaincodeEndpointRequest);
        chaincodeEndpointRequest.setHeader("Accept", "application/json;charset=UTF-8");

        final JsonObject requestBody = new JsonObject();
        requestBody.addProperty("function", function);
        requestBody.add("arguments", new Gson().toJsonTree(args[0]));
        requestBody.addProperty("async", false);

        final String json = new Gson().toJson(requestBody);
        logger.trace("Request: {}", json);
        chaincodeEndpointRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        chaincodeEndpointRequest.setHeader("Content-Type", "application/json");

        try {
            final HttpResponse response = HttpClientAccessor.getHttpClient().execute(chaincodeEndpointRequest);
            final StatusLine statusLine = response.getStatusLine();

            final String responsePayload = HttpEntityUtil.getResponseBody(response);
            logger.debug("Response status: {}", statusLine);
            logger.trace("Response status: {}, content: {}", statusLine, responsePayload);

            switch (statusLine.getStatusCode()) {
                case HttpStatus.SC_OK:
                    return responsePayload;

                case HttpStatus.SC_UNAUTHORIZED:
                case HttpStatus.SC_FORBIDDEN:
                    throw new AuthenticationException("Access token already expired or simply wrong: "
                            + "Service request denied with status: " + statusLine);
                default:
                    throw new Exception("Failed to execute request to function " + function + ": "
                            + "Service request failed with status: " + statusLine);
            }
        } finally {
            chaincodeEndpointRequest.releaseConnection();
        }
    }
    
    static FabricService getFabricService( final String serviceType )
            throws ShouldNotHappenException
    {
        try {
            final ScpCfService cfService = ScpCfService.of(serviceType, null, "credentials/oAuth/url",
                    "credentials/oAuth/clientId", "credentials/oAuth/clientSecret", "credentials/serviceUrl");

            return new FabricService(cfService);

        } catch (ShouldNotHappenException e) {
            throw e;
        } catch (final Exception e) {
            throw new ShouldNotHappenException("Failed to setup Blockchain service: " + e.getMessage(), e);
        }
    }

    public String getServiceUrl() {
        return cfService.getServiceLocationInfo();
    }

    @Override
    public String toString()
    {
        return "FabricService{" +
                "cfService=" + cfService +
                '}';
    }
}
