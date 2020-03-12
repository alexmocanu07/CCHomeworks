import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MyHttpHandler implements HttpHandler {

    private CarController carController;

    public MyHttpHandler() {
        this.carController = new CarController();
        Database.init();
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        if(Util.HTTP_GET.equals(httpExchange.getRequestMethod())){
            handleGetRequest(httpExchange);
        }
        else if(Util.HTTP_POST.equals(httpExchange.getRequestMethod())){
            handlePostRequest(httpExchange);
        }
        else if(Util.HTTP_PATCH.equals(httpExchange.getRequestMethod())){
            handlePatchRequest(httpExchange);
        }
        else if(Util.HTTP_PUT.equals(httpExchange.getRequestMethod())){
            handlePutRequest(httpExchange);
        }
        else if(Util.HTTP_DELETE.equals(httpExchange.getRequestMethod())){
            handleDeleteRequest(httpExchange);
        }
    }

    private void handleGetRequest(HttpExchange httpExchange){
        String param = Util.getURIParams(httpExchange.getRequestURI().toString());
        if(param.equals(Util.invalidRequest)){
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        String ID;
        if(Arrays.asList(Util.routes).contains(param)) ID = null;
        else {
            try {
                ID = Integer.toString(Integer.parseInt(param));
            } catch (NumberFormatException nfe) {
                sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
                return;
            }
        }
        Gson g = new Gson();
        String json;
        if(ID == null){
            try{
                json = g.toJson(this.carController.findAll());
            }
            catch(SQLException e){
                e.printStackTrace();
                sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
                return;
            }
        }
        else{
            try {
                json = g.toJson(this.carController.findById(Integer.parseInt(ID)));
            }
            catch(SQLException e){
                json = "";
            }
        }
        if(json.equals("") || json.equals("null")) sendResponse(httpExchange, Util.STATUS_NOT_FOUND, false);
        else sendResponse(httpExchange, json, true);

    }

    private void handlePostRequest(HttpExchange httpExchange){
        String param = Util.getURIParams(httpExchange.getRequestURI().toString());

        if(param.equals(Util.invalidRequest)){
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }

        String data = Util.getRequestBody(httpExchange);
        if(data == null) {
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }
        if(data.equals("")){
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }

        String ID;
        if(Arrays.asList(Util.routes).contains(param)) ID = null;
        else {
            try {
                ID = Integer.toString(Integer.parseInt(param));
            } catch (NumberFormatException nfe) {
                sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
                return;
            }
        }
        Gson g = new Gson();
        Car[] carArray;

        try {
            carArray = g.fromJson(data, Car[].class);
        }catch(Exception e){
            carArray = new Car[]{g.fromJson(data, Car.class)};
        }
        if(Util.isBodyIncorrect(carArray, ID == null)){
            sendResponse(httpExchange, Util.STATUS_UNPROCESSABLE, false);
            return;
        }

        try{
            if(ID == null) {
                for(Car c : carArray){
                    this.carController.create(c.getBrand(), c.getModel(), c.getYear());
                }
            }
            else{
                Car c = carArray[0];
                this.carController.createOnID(Integer.parseInt(ID),c.getBrand(), c.getModel(), c.getYear());
            }
        }catch(ConflictException ce){
            sendResponse(httpExchange, Util.STATUS_CONFLICT, false);
            return;
        } catch(SQLException e){
            e.printStackTrace();
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }
        sendResponse(httpExchange, Util.STATUS_CREATED, false);
    }

    private void handlePutRequest(HttpExchange httpExchange){
        String param = Util.getURIParams(httpExchange.getRequestURI().toString());

        if(param.equals(Util.invalidRequest)){
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        if(Arrays.asList(Util.routes).contains(param)){
            sendResponse(httpExchange, Util.STATUS_NOT_ALLOWED, false);
            return;
        }
        try {
            int ID = Integer.parseInt(param);
        } catch (NumberFormatException nfe) {
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        String data = Util.getRequestBody(httpExchange);
        if(data == null) {
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }
        Gson g = new Gson();

        Car c;
        try{
            c = g.fromJson(data, Car.class);
        }catch(Exception e){
            sendResponse(httpExchange, Util.STATUS_UNPROCESSABLE, false);
            return;
        }
        if(Util.isBodyIncorrect(new Car[]{c}, false)){
            sendResponse(httpExchange, Util.STATUS_UNPROCESSABLE, false);
            return;
        }

        try{
            this.carController.update(Integer.parseInt(param), c.getBrand(), c.getModel(), c.getYear());
        }catch(SQLException e){
            e.printStackTrace();
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }catch(NotFoundException nfe){
            sendResponse(httpExchange, Util.STATUS_NOT_FOUND, false);
            return;
        }
        catch (NumberFormatException n){
//            n.printStackTrace();
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        sendResponse(httpExchange, Util.STATUS_CREATED, false);
    }

    private void handlePatchRequest(HttpExchange httpExchange){
        String param = Util.getURIParams(httpExchange.getRequestURI().toString());

        if(param.equals(Util.invalidRequest)){
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        if(Arrays.asList(Util.routes).contains(param)){
            sendResponse(httpExchange, Util.STATUS_NOT_ALLOWED, false);
            return;
        }
        try {
            int ID = Integer.parseInt(param);
        } catch (NumberFormatException nfe) {
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        String data = Util.getRequestBody(httpExchange);
        if(data == null) {
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }
        Gson g = new Gson();
        Map obj = new HashMap<>();
        try{
            obj = g.fromJson(data, obj.getClass());
        }catch(Exception e){
            sendResponse(httpExchange, Util.STATUS_UNPROCESSABLE, false);
            return;
        }

        try{
            String brand = obj.get("brand") == null ? null: obj.get("brand").toString();
            String model = obj.get("model") == null ? null: obj.get("model").toString();
            int year = obj.get("year") == null ? -1: Integer.parseInt(obj.get("year").toString());
            this.carController.update(Integer.parseInt(param), brand, model, year);
        }catch(SQLException e){
            e.printStackTrace();
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }catch(NotFoundException nfe){
            sendResponse(httpExchange, Util.STATUS_NOT_FOUND, false);
            return;
        }
        catch (NumberFormatException n){
            n.printStackTrace();
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        sendResponse(httpExchange, Util.STATUS_CREATED, false);
    }

    private void handleDeleteRequest(HttpExchange httpExchange){
        String param = Util.getURIParams(httpExchange.getRequestURI().toString());

        if(param.equals(Util.invalidRequest)){
            sendResponse(httpExchange, Util.STATUS_BAD_REQUEST, false);
            return;
        }
        if(Arrays.asList(Util.routes).contains(param)){
            sendResponse(httpExchange, Util.STATUS_NOT_ALLOWED, false);
            return;
        }
        String ID;
        try {
            ID = Integer.toString(Integer.parseInt(param));
        }
        catch(NumberFormatException nfe){
            sendResponse(httpExchange,Util.STATUS_BAD_REQUEST, false);
            return;
        }

        try{
            Car c = carController.findById(Integer.parseInt(ID));
            if(c == null){
                sendResponse(httpExchange, Util.STATUS_NOT_FOUND, false);
                return;
            }
            carController.delete(Integer.parseInt(ID));
        }catch(SQLException e){
            e.printStackTrace();
            sendResponse(httpExchange, Util.STATUS_SERVER_ERROR, false);
            return;
        }
        sendResponse(httpExchange, Util.STATUS_OK, false);

    }

    private void sendResponse(HttpExchange httpExchange, String response, boolean bodyRequired){
        OutputStream outputStream = httpExchange.getResponseBody();
        if(bodyRequired){
            try {
                httpExchange.sendResponseHeaders(Util.HTTP_STATUS.get(Util.STATUS_OK), response.length());
                outputStream.write(response.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        else{
            try {
                httpExchange.sendResponseHeaders(Util.HTTP_STATUS.get(response), 0);
                outputStream.write("".getBytes());
                outputStream.flush();
                outputStream.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
