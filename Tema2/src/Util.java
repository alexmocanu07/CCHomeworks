import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Util {
    static final String HTTP_GET = "GET";
    static final String HTTP_POST = "POST";
    static final String HTTP_PUT = "PUT";
    static final String HTTP_PATCH = "PATCH";
    static final String HTTP_DELETE = "DELETE";

    static final String STATUS_OK = "OK";
    static final String STATUS_CREATED = "CREATED";
    static final String STATUS_BAD_REQUEST = "BAD_REQUEST";
    static final String STATUS_UNAUTHORIZED = "UNAUTHORIZED";
    static final String STATUS_FORBIDDEN = "FORBIDDEN";
    static final String STATUS_NOT_FOUND = "NOT_FOUND";
    static final String STATUS_NOT_ALLOWED = "NOT_ALLOWED";
    static final String STATUS_CONFLICT = "CONFLICT";
    static final String STATUS_UNPROCESSABLE = "UNPROCESSABLE";
    static final String STATUS_SERVER_ERROR = "SERVER_ERROR";


    static final Map<String, Integer> HTTP_STATUS = new HashMap<>(){{
       put(STATUS_OK, 200);
       put(STATUS_CREATED, 201);
       put(STATUS_BAD_REQUEST, 400);
       put(STATUS_UNAUTHORIZED, 401);
       put(STATUS_FORBIDDEN, 403);
       put(STATUS_NOT_FOUND, 404);
       put(STATUS_NOT_ALLOWED, 405);
       put(STATUS_CONFLICT, 409);
       put(STATUS_UNPROCESSABLE, 422);
       put(STATUS_SERVER_ERROR, 500);
    }};

    static final String invalidRequest = "invalidRequest";
    static final String[] routes = { "cars"} ;

    private Util(){}

    public static String getURIParams(String URI){

        String[] parts = URI.split("/");

        if(parts.length > 5 || parts.length < 4) return invalidRequest;
        if(!Arrays.asList(routes).contains(parts[3])) return invalidRequest;
        if(parts.length == 4) return parts[3];
        return parts[4];
    }

    public static String getRequestBody(HttpExchange httpExchange){
        InputStream is = httpExchange.getRequestBody();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        try {
            while ((len = is.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }


    public static boolean isBodyIncorrect(Car[] carArray, boolean moreAllowed){
        if(!moreAllowed && carArray.length > 1) return true;
        for(Car c : carArray){
            if(c.getID() != 0 || c.getYear() == 0 || c.getBrand() == null || c.getModel() == null) return true;
        }
        return false;
    }
}
