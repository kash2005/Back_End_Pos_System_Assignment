package lk.ijse.back_end_assignment1.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.ijse.back_end_assignment1.db.OrderDB;
import lk.ijse.back_end_assignment1.dto.CombinedOrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "order", urlPatterns = "/order")
public class Order extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(Order.class);
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/shopPoss");
            this.connection = pool.getConnection();
            logger.info("Initialized with connection pool: {}", pool);
        } catch (NamingException | SQLException e) {
            logger.error("Error during initialization", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling POST request");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CombinedOrderDTO combinedOrderDTO = jsonb.fromJson(req.getReader(), CombinedOrderDTO.class);
            OrderDB orderDB = new OrderDB();
            boolean result = orderDB.saveOrder(combinedOrderDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order information saved successfully.");
                logger.info("Order information saved successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save Order information.");
                logger.error("Failed to save Order information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Invalid request format for POST");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling DELETE request");

        String orderId = req.getParameter("orderId");
        if (orderId != null) {
            OrderDB orderDB = new OrderDB();
            boolean result = orderDB.delete(orderId, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order deleted successfully.");
                logger.info("Order deleted successfully: {}", orderId);
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete order.");
                logger.error("Failed to delete order: {}", orderId);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId parameter.");
            logger.warn("Missing orderId parameter for DELETE");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling GET request");

        var action = req.getParameter("action");

        if ("getAllOrders".equals(action)) {
            getAllOrders(req, resp);
        } else if ("getOrderId".equals(action)) {
            generateOrderId(req, resp);
        } else if ("getOrder".equals(action)) {
            var orderId = req.getParameter("orderId");
            getOrder(req, resp, orderId);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
            logger.warn("Invalid action parameter: {}", action);
        }
    }

    private void getOrder(HttpServletRequest req, HttpServletResponse resp, String orderId) {
        logger.debug("Handling getOrder");

        try {
            if (orderId != null) {
                OrderDB orderDB = new OrderDB();
                CombinedOrderDTO order = orderDB.getOrder(orderId, connection);
                if (order != null) {
                    Jsonb jsonb = JsonbBuilder.create();
                    String json = jsonb.toJson(order);
                    resp.setContentType("application/json");
                    resp.getWriter().write(json);
                    logger.debug("Returned order successfully: {}", orderId);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    logger.warn("Order not found: {}", orderId);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId parameter.");
                logger.warn("Missing orderId parameter for getOrder");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateOrderId(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling generateOrderId");

        OrderDB orderDB = new OrderDB();
        var orderId = orderDB.generateOrderId(connection);
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String json = jsonb.toJson(orderId);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned generated order ID successfully: {}", orderId);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getAllOrders(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling getAllOrders");

        try {
            OrderDB orderDB = new OrderDB();
            List<CombinedOrderDTO> allOrders = orderDB.getAllOrders(connection);
            Jsonb jsonb = JsonbBuilder.create();
            String json = jsonb.toJson(allOrders);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned all orders successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling PUT request");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CombinedOrderDTO combinedOrderDTO = jsonb.fromJson(req.getReader(), CombinedOrderDTO.class);
            OrderDB orderDB = new OrderDB();
            boolean result = orderDB.updateOrder(combinedOrderDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order information updated successfully.");
                logger.info("Order information updated successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update Order information.");
                logger.error("Failed to update Order information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Invalid request format for PUT");
        }
    }
}