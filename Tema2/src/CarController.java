import com.mysql.cj.jdbc.exceptions.NotUpdatable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarController {

    public void create(String brand, String model, int year) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement("insert into cars(brand, model, year) values(?, ?, ?)")) {
            pstmt.setString(1, brand);
            pstmt.setString(2, model);
            pstmt.setInt(3, year);
            pstmt.executeUpdate();
        }
    }

    public void createOnID(int ID, String brand, String model, int year) throws ConflictException, SQLException {
        Car car = null;
        try{
            car = this.findById(ID);

        }catch(SQLException ignored){ }
        if(car != null) throw new ConflictException("conflict");

        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement("insert into cars(ID, brand, model, year) values(?, ?, ?, ?)")) {
            pstmt.setString(1, Integer.toString(ID));
            pstmt.setString(2, brand);
            pstmt.setString(3, model);
            pstmt.setInt(4, year);
            pstmt.executeUpdate();
        }
    }



    public Car findById(int id) throws SQLException {
        Connection con = Database.getConnection();
        try(Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * from cars where id = " + id)){
            if(!resultSet.next()) return null;
            int carId = resultSet.getInt(1);
            String brand = resultSet.getString(2);
            String model = resultSet.getString(3);
            int year = resultSet.getInt(4);
            return new Car(carId, brand, model, year);

        }
    }

    public List<Car> findAll() throws SQLException {
        Connection con = Database.getConnection();
        List<Car> carList = new ArrayList<>();
        try(Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * from cars")){

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String brand = resultSet.getString(2);
                String model = resultSet.getString(3);
                int year = resultSet.getInt(4);
                carList.add(new Car(id,brand,model,year));
            }
        }
        return carList;
    }

    public void update(int ID, String brand, String model, int year) throws SQLException, NotFoundException {
        Connection con = Database.getConnection();
        Car car;
        car = findById(ID);

        if(car == null) throw new NotFoundException("not found");

        Statement stmt = con.createStatement();
        String sql ="UPDATE cars SET " +
                "brand = '" + (brand == null ? car.getBrand() : brand) + "'" +
                ", model = '" + (model == null ? car.getModel() : model) + "'" +
                ", year = " + (year == -1 ? car.getYear() : year) +
                " WHERE id = " + ID;
        stmt.executeUpdate(sql
        );
    }

    public void delete(int ID) throws SQLException{
        Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
        String sql = "DELETE FROM cars WHERE id = " + ID;
        stmt.executeUpdate(sql);
    }
}
