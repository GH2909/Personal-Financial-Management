package Personal.Finance.Manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Personal.Finance.Manager.dto.request.SavingDepositRequest;
import Personal.Finance.Manager.dto.request.SavingGoalRequest;
import Personal.Finance.Manager.dto.request.SavingWithdrawRequest;
import Personal.Finance.Manager.dto.response.SavingGoalResponse;
import Personal.Finance.Manager.dto.response.SavingRecordResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.service.SavingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/savings")
@RequiredArgsConstructor
public class SavingController {

    private final SavingService savingService;

    // ==============================
    // CREATE SAVING GOAL
    // ==============================

    @PostMapping
    public ResponseEntity<SavingGoalResponse> createSaving(
            @RequestAttribute("user") User user,
            @Valid @RequestBody SavingGoalRequest request) {

        return ResponseEntity.ok(
                savingService.createSaving(request, user));
    }

    // ==============================
    // GET ALL SAVING GOALS
    // ==============================

    @GetMapping
    public ResponseEntity<List<SavingGoalResponse>> getAllSavings(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                savingService.getAllSavings(user));
    }

    // ==============================
    // GET SAVING GOAL BY ID
    // ==============================

    @GetMapping("/{savingGoalId}")
    public ResponseEntity<SavingGoalResponse> getSavingById(
            @PathVariable Long savingGoalId,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                savingService.getSavingById(
                        savingGoalId,
                        user));
    }

    // ==============================
    // UPDATE SAVING GOAL
    // ==============================

    @PutMapping("/{savingGoalId}")
    public ResponseEntity<SavingGoalResponse> updateSaving(
            @PathVariable Long savingGoalId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody SavingGoalRequest request) {

        return ResponseEntity.ok(
                savingService.updateSaving(
                        savingGoalId,
                        request,
                        user));
    }

    // ==============================
    // DELETE SAVING GOAL
    // ==============================

    @DeleteMapping("/{savingGoalId}")
    public ResponseEntity<?> deleteSaving(
            @PathVariable Long savingGoalId,
            @RequestAttribute("user") User user) {

        savingService.deleteSaving(
                savingGoalId,
                user);

        return ResponseEntity.ok(
                "Saving goal deleted successfully");
    }

    // ==============================
    // DEPOSIT
    // ==============================

    @PostMapping("/{savingGoalId}/deposit")
    public ResponseEntity<SavingGoalResponse> deposit(
            @PathVariable Long savingGoalId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody SavingDepositRequest request) {

        return ResponseEntity.ok(
                savingService.deposit(
                        savingGoalId,
                        request,
                        user));
    }

    // ==============================
    // WITHDRAW
    // ==============================

    @PostMapping("/{savingGoalId}/withdraw")
    public ResponseEntity<SavingGoalResponse> withdraw(
            @PathVariable Long savingGoalId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody SavingWithdrawRequest request) {

        return ResponseEntity.ok(
                savingService.withdraw(
                        savingGoalId,
                        request,
                        user));
    }

    // ==============================
    // GET SAVING RECORDS
    // ==============================
    @GetMapping("/{savingGoalId}/records")
    public ResponseEntity<List<SavingRecordResponse>> getSavingRecords(
          @PathVariable Long savingGoalId,
           @RequestAttribute("user") User user) {
    
       return ResponseEntity.ok(
               savingService.getSavingRecords(savingGoalId, user)
       );
    }
}