package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techacademy.entity.Reports;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer> {
}