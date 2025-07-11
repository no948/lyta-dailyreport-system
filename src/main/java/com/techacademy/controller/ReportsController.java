package com.techacademy.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportsService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportsController {

    private final ReportsService reportsService;
    private final EmployeeService employeeService;

    @Autowired
    public ReportsController(ReportsService reportsService, EmployeeService employeeService) {
        this.reportsService = reportsService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        List<Reports> reports = reportsService.findAll();
        model.addAttribute("listSize", reports.size());
        model.addAttribute("reportsList", reports);
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Reports report = reportsService.findById(id);
        model.addAttribute("report", report);
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Model model, Principal principal) {
        Reports report = new Reports();
        Employee loginUser = employeeService.findByCode(principal.getName());
        // ログイン中の従業員情報の氏名欄を表示
        report.setName(loginUser.getName());
        model.addAttribute("report", report);
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated @ModelAttribute("report") Reports report, BindingResult res, Principal principal, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            model.addAttribute("report", report);
            Employee loginUser = employeeService.findByCode(principal.getName());
            report.setName(loginUser.getName());
            report.setEmployeeCode(loginUser.getCode());
            return "reports/new";
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            Employee loginUser = employeeService.findByCode(principal.getName());
            // 氏名をセットする
            report.setName(loginUser.getName());
            report.setEmployee(loginUser);
            ErrorKinds result = reportsService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                model.addAttribute("report", report);
                model.addAttribute("employeeName", loginUser.getName());
                report.setName(loginUser.getName());
                return "reports/new";
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            model.addAttribute("report", report);
            Employee loginUser = employeeService.findByCode(principal.getName());
            model.addAttribute("employeeName", loginUser.getName());
            report.setName(loginUser.getName());
            return "reports/new";
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportsService.delete(id, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportsService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }


    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String update(@PathVariable Integer id, Model model, Principal principal) {
        Reports report = reportsService.findById(id);
        // 氏名が null または空ならログイン中の従業員情報から補完
        if (report.getName() == null || report.getName().isEmpty()) {
            Employee loginUser = employeeService.findByCode(principal.getName());
            report.setName(loginUser.getName());
        }
        model.addAttribute("report", report);
        return "reports/edit";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@Validated @ModelAttribute("report") Reports report, BindingResult res, Model model, Principal principal) {

        //入力チェック
        if (res.hasErrors()) {
             // ログイン中の従業員情報を取得して氏名をセット
            Employee loginUser = employeeService.findByCode(principal.getName());
            report.setName(loginUser.getName());
            model.addAttribute("report", report);
            return "reports/edit";
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            //ログインユーザーを取得してセットする
            Employee loginUser = employeeService.findByCode(principal.getName());
            report.setEmployee(loginUser);
            report.setName(loginUser.getName());
            ErrorKinds result = reportsService.update(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                model.addAttribute("report", report);
                return "reports/edit";
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            model.addAttribute("report", report);
            return "reports/edit";
        }

        return "redirect:/reports";
    }

    // ログイン成功時は日報一覧画面に遷移する
    @GetMapping("/login-success")
    public String loginSuccess() {
        return "redirect:/reports";
    }

}