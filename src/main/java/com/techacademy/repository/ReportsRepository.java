package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer> {
    // 全件のうち削除されていない日報を取得（管理者用）
    List<Reports> findByDeleteFlgFalse();
    // 指定従業員で、削除されていない日報を取得
    List<Reports> findByEmployeeAndDeleteFlgFalse(Employee employee);
    List<Reports> findByEmployee(Employee employee);
    // 指定した従業員と日付で削除されていない日報が存在するか
    Optional<Reports> findByEmployeeAndReportDateAndDeleteFlgFalse(Employee employee, LocalDate reportDate);
    // 重複チェック（新規登録）
    List<Reports> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
    // 重複チェック（更新）
    List<Reports> findByEmployee_CodeAndReportDateAndDeleteFlgFalse(String employeeCode, LocalDate reportDate);
}