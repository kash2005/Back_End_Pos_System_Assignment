package lk.ijse.back_end_assignment1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDTO {
    private String order_date;
    private String order_id;
    private String customer_id;
    private double total;
    private double discount;
    private double cash;
}
