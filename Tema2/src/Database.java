import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String USERNAME="root";
    private static final String PASSWORD="root";
    private static final String CONN_STRING="jdbc:mysql://localhost:3306/cars?serverTimezone=UTC";
    private static Connection connection = null;

    private Database(){}

    public static Connection getConnection(){
        if(connection==null) createConnection();
        return connection;
    }
    public static void init() {
        try {
//            createConnection();
//            Statement stmt = connection.createStatement();
//            stmt.executeUpdate("create table cars(" +
//                    "    id integer not null AUTO_INCREMENT," +
//                    "brand varchar(100) not null," +
//                    "model varchar(100) not null," +
//                    "year integer not null," +
//                    " primary key (id)" +
//                    ");");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void createConnection(){

        try {
//            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        }
        catch(SQLException e){
            System.err.println("Create error");
            System.err.println(e);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void closeConnection(){
        try {
            connection.close();
        }
        catch(SQLException e){
            System.err.println("Close error");
            System.err.println(e);
        }
    }

    public static void commit(){
        try{
            connection.setAutoCommit(false);
            connection.commit();
        }
        catch(SQLException e){
            System.err.println("Commit error");
            System.err.println(e);
        }
    }

    public static void rollback(){
        try{
            connection.rollback();
        }
        catch(SQLException e){
            System.err.println("Rollback error");
            System.err.println(e);
        }
    }

}
