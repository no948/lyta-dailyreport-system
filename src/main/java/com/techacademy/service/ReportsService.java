package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;

    @Autowired
    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Reports reports) {

        reports.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        reports.setCreatedAt(now);
        reports.setUpdatedAt(now);

        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {
        Reports report = findById(id);

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (report.getEmployee().getCode().equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }

        report.setUpdatedAt(LocalDateTime.now());
        report.setDeleteFlg(true);
        reportsRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

        /* 削除対象の従業員に紐づいている日報情報の削除：ここから */
        // 削除対象の従業員（employee）に紐づいている、日報のリスト（reportList）を取得
    public List<Reports> findByEmployee(Employee employee) {
        return reportsRepository.findByEmployee(employee);
    }
    //  日報（report）のIDを指定して、日報情報を削除
    @Transactional
    public void delete(Integer id) {
        Reports report = findById(id);
        report.setUpdatedAt(LocalDateTime.now());
        report.setDeleteFlg(true);
        reportsRepository.save(report);
    }
        /* 削除対象の従業員に紐づいている日報情報の削除：ここまで */

    // 日報一覧表示処理
    public List<Reports> findAll() {
        return reportsRepository.findAll();
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Reports reports) {
        // DBから既存データを取得
        Reports dbReports = findById(reports.getId());

        reports.setCreatedAt(dbReports.getCreatedAt()); // 元の作成日を保持
        reports.setUpdatedAt(LocalDateTime.now());
        reports.setDeleteFlg(false);

        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    // 1件を検索
    public Reports findById(Integer id) {
        // findByIdで検索
        Optional<Reports> option = reportsRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Reports reports = option.orElse(null);
        return reports;
    }

}