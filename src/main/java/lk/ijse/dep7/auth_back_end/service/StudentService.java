package lk.ijse.dep7.auth_back_end.service;

import lk.ijse.dep7.auth_back_end.dto.StudentDTO;

import java.sql.*;

public class StudentService {

    private final Connection connection;

    public StudentService(Connection connection) {
        this.connection = connection;
    }

    public String saveStudent(StudentDTO student) {

        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO student (name, address, username) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, student.getName());
            stm.setString(2, student.getAddress());
//            TO Do:  Inject the user name here


            if (stm.executeUpdate() == 1) {
                ResultSet rst = stm.getGeneratedKeys();

                rst.next();
                return String.format("SID-%03d", rst.getInt(1));
            } else {
                throw new RuntimeException("Failed to save the student");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
