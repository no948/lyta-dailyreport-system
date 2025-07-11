package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Reports {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 氏名
    private String name;

    // 日付
    @NotNull(message = "値を入力してください")
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    // タイトル
    @Column(length = 100, nullable = false)
    @NotBlank(message = "値を入力してください")
    @Length(max = 100)
    private String title;

    // 内容
    @Column(length = 600, nullable = false)
    @NotBlank(message = "値を入力してください")
    @Length(max = 600)
    private String content;

    // 社員番号
    @Transient
    private String employeeCode;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code")
    private Employee employee;

    // 削除フラグ(論理削除を行うため)
    @Column(name = "delete_flg", columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}