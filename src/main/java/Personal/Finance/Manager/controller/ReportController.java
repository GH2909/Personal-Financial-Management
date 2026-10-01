package Personal.Finance.Manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import Personal.Finance.Manager.dto.response.CategoryReportResponse;
import Personal.Finance.Manager.dto.response.DashboardReportResponse;
import Personal.Finance.Manager.dto.response.MonthlyReportResponse;
import Personal.Finance.Manager.dto.response.SavingReportResponse;

import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.service.ReportService;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    @GetMapping("/dashboard")
    public ResponseEntity<DashboardReportResponse> getDashboardReport(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                reportService.getDashboardReport(user)
        );
    }


    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                reportService.getMonthlyReport(
                        month,
                        year,
                        user
                )
        );
    }


    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportResponse>> getCategoryReport(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                reportService.getCategoryReport(
                        month,
                        year,
                        user
                )
        );
    }


    @GetMapping("/savings")
    public ResponseEntity<SavingReportResponse> getSavingReport(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                reportService.getSavingReport(user)
        );
    }
}