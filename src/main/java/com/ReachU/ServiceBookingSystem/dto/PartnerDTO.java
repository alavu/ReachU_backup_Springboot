package com.ReachU.ServiceBookingSystem.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//public class PartnerDTO {
//    private Long id;
//    private String name;
//    private String lastname;
//    private String email;
//    private String password;
//    private String phone;
//    private byte[] img;
//    private String service;
//    private Double rating;
//}

// PartnerDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private byte[] img;
    private String service;
    private Double rating;

    public PartnerDTO(Long id, String name, String lastname, String email,
                      String password, String phone, byte[] img, String service) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.img = img;
        this.service = service;
        this.rating = 0.0; // Default value
    }
}