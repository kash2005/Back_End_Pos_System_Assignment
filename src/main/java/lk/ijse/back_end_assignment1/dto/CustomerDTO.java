package lk.ijse.back_end_assignment1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String customerId;
    private String customerName;
    private String customerAddress;
    private String customerContact;
    private String customerEmail;
}
