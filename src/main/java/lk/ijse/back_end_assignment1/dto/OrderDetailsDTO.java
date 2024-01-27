package lk.ijse.back_end_assignment1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailsDTO {
    private String order_id;
    private String item_id;
    private double price;
    private int qty;
}