package com.payflow.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}