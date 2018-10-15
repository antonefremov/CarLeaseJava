package carlease;

//import com.google.common.base.Strings;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

@WebServlet("/")
public class FabricSimpleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = CloudLoggerFactory.getLogger(FabricSimpleServlet.class);
    public static final String CARLEASE_CHAINCODE_ID = "CARLEASE_CHAINCODE_ID";

    @Override
    protected void doGet(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            final FabricService fabricService = FabricService.createByGettingTokenViaCfServicesConfig();
            String result = "";
            String pathInfo = request.getRequestURI(); // /vehicles/{id}
            String[] pathParts = pathInfo.split("/");
            // String part0 = pathParts[0];
            String part1 = pathParts[1]; // carlease or owners
            String part2 = "";
            String part3 = "";
            String part4 = "";

            if (pathParts.length > 2) {
                part2 = pathParts[2]; // vehicles or owner
            }

            if (pathParts.length > 3) {
                part3 = pathParts[3]; // {id}
            }

            if (pathParts.length > 4) {
                part4 = pathParts[4]; // {id} of the vehicle in the
                                      // /vehicles/owner/id path
            }

            // logger.info("Lets see the pathParts...");

            if (part1.contains("carlease")) {
                if (part2.contains("vehicles")) {
                    // /vehicles
                    if (part3.isEmpty()) {
                        if (request.getMethod().contains("GET")) {
                            BlockchainInvocationType invType = BlockchainInvocationType.QUERY;
                            invType.setPathParamValue(part2 + "/");
                            result = fabricService.invokeOrQuery(invType, getChaincodeId(), "read_vehicles");
                        }
                    }
                    // /vehicles/owner/id
                    else if (part3.contains("owner")) {
                        BlockchainInvocationType invType = BlockchainInvocationType.QUERY;
                        invType.setPathParamValue(part3 + "/" + part4);
                        if (request.getMethod().contains("GET")) {
                            result = fabricService.invokeOrQuery(invType, getChaincodeId(), "get_owner");
                        }
//                        if (request.getMethod().contains("PUT")) {
//                            setAccessControlHeaders(request, response);
//                            result = fabricService.put(invType, getChaincodeId(), "set_owner");
//                        }
                    }
                    // /vehicles/id
                    else {
                        BlockchainInvocationType invType = BlockchainInvocationType.QUERY;
                        invType.setPathParamValue(part2 + "/" + part3);
                        if (request.getMethod().contains("GET")) {
                            result = fabricService.invokeOrQuery(invType, getChaincodeId(), "read_vehicle");
                        }
//                        if (request.getMethod().contains("POST")) {
//                            result = fabricService.post(invType, getChaincodeId(), "write");
//                        }
                    }
                }
            }
            if (part1.contains("owners")) {
                // /owners
                if (request.getMethod().contains("GET")) {
                    BlockchainInvocationType invType = BlockchainInvocationType.QUERY;
                    invType.setPathParamValue(part1 + "/");
                    result = fabricService.invokeOrQuery(invType, getChaincodeId(), "read_owners");
                }
            }

            setAccessControlHeaders(request, response);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("Failure: " + e.getMessage(), e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            final FabricService fabricService = FabricService.createByGettingTokenViaCfServicesConfig();
            String result = "";
            String pathInfo = request.getRequestURI(); // carlease/vehicles/owner/{id}
            String[] pathParts = pathInfo.split("/");
//            String part0 = pathParts[0]; // carlease
            String part1 = pathParts[1]; //  carlease
            String part2 = ""; // vehicles
            String part3 = ""; // owner
            String part4 = ""; // id

            if (pathParts.length > 2) {
                part2 = pathParts[2]; // vehicles
            }

            if (pathParts.length > 3) {
                part3 = pathParts[3]; // owner
            }

            if (pathParts.length > 4) {
                part4 = pathParts[4]; // {id} of the vehicle in the
                                      // /vehicles/owner/id path
            }

            // logger.info("Lets see the pathParts...");

            if (part1.contains("carlease")) {
                if (part2.contains("vehicles")) {
                    // /vehicles/owner/id
                    if (part3.contains("owner")) {
                        BlockchainInvocationType invType = BlockchainInvocationType.INVOKE;
                        invType.setPathParamValue(part2 + "/" + part3 + "/" + part4);
                        setAccessControlHeaders(request, response);
                        result = fabricService.put(invType, getChaincodeId(), "set_owner", inputStreamToString(request.getInputStream()));
                    }
                }
            }
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("Failure: " + e.getMessage(), e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    // for Preflight
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(req, resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public String getChaincodeId() {
        try {
            final Optional<String> carlease_chaincode_id = ((ScpCfCloudPlatform) CloudPlatformAccessor
                    .getCloudPlatform()).getEnvironmentVariable(CARLEASE_CHAINCODE_ID);
            if (carlease_chaincode_id.isPresent()) {
                return carlease_chaincode_id.get();
            }
            throw new ShouldNotHappenException(CARLEASE_CHAINCODE_ID + " environment variable not set.");

        } catch (ClassCastException cce) {
            throw new ShouldNotHappenException("Not part of a SAP Cloud Platform Cloud Foundry application, "
                    + "please only use in that environment.");
        }
    }

    private void setAccessControlHeaders(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");// https://carleasedemo-i300455trial.dispatcher.hanatrial.ondemand.com/
        resp.setHeader("Access-Control-Allow-Methods", "GET, HEAD, OPTIONS, POST, PUT");
        if (!req.getMethod().contains("OPTIONS")) {
            resp.setHeader("Content-Type", "application/json");
            resp.setHeader("X-Content-Type-Options", "nosniff");
        }
    }
    
    private static String inputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        return scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
    }
}
