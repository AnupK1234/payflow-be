package com.payflow.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payflow.app.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_payment_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonIgnore
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @NotNull
    private Double amount;

    @Column(length = 255)
    private String reason;

    @Column(length = 500)
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING; 

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;   

    private LocalDateTime acceptedAt;  

    private LocalDateTime rejectedAt;   
}
