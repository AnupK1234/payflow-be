package com.payflow.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @NotNull
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String companyName;  

    @NotNull
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String contactPersonName; 

    @NotNull
    @Email
    @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String contactEmail;

    @NotNull
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @Column(nullable = false, length = 10)
    private String contactPhone;

    
    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 50)
    private String country;

    @Column(length = 10)
    private String postalCode;


    @Column(length = 20)
    private String status = "Active"; 

    
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Optional login/user details if needed in future
    
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private User user;  
}
