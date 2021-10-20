package lk.ijse.dep7.auth_back_end.api;

import jakarta.annotation.Resources;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.annotation.Resource;
import lk.ijse.dep7.auth_back_end.dto.StudentDTO;
import lk.ijse.dep7.auth_back_end.service.StudentService;
import lk.ijse.dep7.auth_back_end.service.UserService;
import security.SecurityContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentServlet", value = "/Students")
public class StudentServlet extends HttpServlet {

@Resource(name = "java:comp/env/jdbc/cp")
private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        try (Connection connection = dataSource.getConnection()) {
            /*TO Do: Remove in the future*/
            SecurityContext.setPrincipal(new UserService(connection).authenticate("admin","admin"));

            StudentService studentService = new StudentService(connection);
            List<StudentDTO> students = studentService.getAllStudents();

            response.setContentType("application/json");
            response.getWriter().println(JsonbBuilder.create().toJson(students));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getContentType()== null || !request.getContentType().startsWith("application/json")){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid request (Only accept JSON)");
            return;
        }

        try {
            StudentDTO studentDTO = JsonbBuilder.create().fromJson(request.getReader(), StudentDTO.class);

            if (studentDTO.getId() != null ){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID can't be specified when saving");
                return;
            }else if (studentDTO.getName() == null || !studentDTO.getName().trim().matches("^[A-Za-z ]+$")){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student name");
                return;
            }else if(studentDTO.getAddress() == null || studentDTO.getAddress().trim().length() < 3){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student address");
                return;
            }

            try (Connection connection = dataSource.getConnection()) {

                /* Todo: remove in the future */
                SecurityContext.setPrincipal(new UserService(connection).authenticate("admin", "admin"));

                StudentService studentService = new StudentService(connection);
                String id = studentService.saveStudent(studentDTO);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().println(JsonbBuilder.create().toJson(id));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }catch (JsonbException e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");;
            e.printStackTrace();
        }


    }
}
