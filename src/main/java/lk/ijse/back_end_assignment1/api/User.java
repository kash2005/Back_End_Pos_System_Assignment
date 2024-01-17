package lk.ijse.back_end_assignment1.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.back_end_assignment1.db.UserDB;
import lk.ijse.back_end_assignment1.dto.UserDTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "user",urlPatterns = "/user")
public class User extends HttpServlet {
    Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/pos");
            this.connection = pool.getConnection();

        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action.equals("checkUser")){
            String userName = req.getParameter("userName");
            checkUser(req,resp,userName);
        }
    }

    protected void checkUser(HttpServletRequest req, HttpServletResponse resp, String userName) {
        UserDB userDB = new UserDB();
        UserDTO user = userDB.getUser(userName, connection);

        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(user);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Do Post");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();

            UserDTO userDTO = jsonb.fromJson(req.getReader(),UserDTO.class);

            System.out.println(userDTO.getUserName());
            var userDB = new UserDB();
            boolean result = userDB.saveUser(connection,userDTO);

            if (result){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("User information saved successfully !");
            }else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to saved user information !");
            }

        }



    }
}
