package com.payflow.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}