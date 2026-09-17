package Personal.Finance.Manager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Personal.Finance.Manager.dto.request.SavingDepositRequest;
import Personal.Finance.Manager.dto.request.SavingGoalRequest;
import Personal.Finance.Manager.dto.request.SavingWithdrawRequest;
import Personal.Finance.Manager.dto.response.SavingGoalResponse;
import Personal.Finance.Manager.model.GoalType;
import Personal.Finance.Manager.model.SavingGoal;
import Personal.Finance.Manager.model.SavingMethod;
import Personal.Finance.Manager.model.SavingRecord;
import Personal.Finance.Manager.model.SavingRecordType;
import Personal.Finance.Manager.model.SavingStatus;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.SavingGoalRepository;
import Personal.Finance.Manager.repository.SavingRecordRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingService {

    private final SavingGoalRepository savingGoalRepository;
    private final SavingRecordRepository savingRecordRepository;
    // =========================
    // CREATE SAVING GOAL
    // =========================
    @Transactional
    public SavingGoalResponse createSaving(
            SavingGoalRequest request,
            User user
    ) {

        SavingGoal savingGoal = new SavingGoal();

        savingGoal.setUser(user);
        savingGoal.setName(request.getName());
        savingGoal.setGoalType(request.getGoalType());
        savingGoal.setSavingMethod(request.getSavingMethod());

        // Saving luôn bắt đầu từ 0
        savingGoal.setCurrentAmount(BigDecimal.ZERO);

        savingGoal.setStartDate(LocalDate.now());
        savingGoal.setStatus(SavingStatus.ACTIVE);

        // =========================
        // TARGET
        // =========================
        if (request.getGoalType() == GoalType.TARGET) {

            validateTargetRequest(request);

            BigDecimal monthlySaving = request.getTargetAmount()
                    .divide(
                            BigDecimal.valueOf(request.getDurationMonths()),
                            2,
                            RoundingMode.HALF_UP
                    );

            savingGoal.setTargetAmount(request.getTargetAmount());
            savingGoal.setDurationMonths(request.getDurationMonths());

            // Tự tính savingValue
            savingGoal.setSavingValue(monthlySaving);

            savingGoal.setTargetDate(
                    LocalDate.now().plusMonths(request.getDurationMonths())
            );
        }

        // =========================
        // UNLIMITED
        // =========================
        else if (request.getGoalType() == GoalType.UNLIMITED) {

            validateUnlimitedRequest(request);

            savingGoal.setTargetAmount(null);
            savingGoal.setDurationMonths(null);
            savingGoal.setTargetDate(null);

            // User nhập giá trị cố định hoặc %
            savingGoal.setSavingValue(request.getSavingValue());
        }

        SavingGoal savedGoal = savingGoalRepository.save(savingGoal);

        return mapToResponse(savedGoal);
    }

    private void validateTargetRequest(SavingGoalRequest request) {

        if (request.getTargetAmount() == null
                || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Target amount must be greater than 0"
            );
        }

        if (request.getDurationMonths() == null
                || request.getDurationMonths() <= 0) {

            throw new IllegalArgumentException(
                    "Duration must be greater than 0"
            );
        }

        if (request.getSavingMethod() != SavingMethod.FIXED) {

            throw new IllegalArgumentException(
                    "TARGET saving must use FIXED method"
            );
        }
    }

    private void validateUnlimitedRequest(SavingGoalRequest request) {

        if (request.getTargetAmount() != null) {

            throw new IllegalArgumentException(
                    "UNLIMITED goal must not have target amount"
            );
        }

        if (request.getDurationMonths() != null) {

            throw new IllegalArgumentException(
                    "UNLIMITED goal must not have duration"
            );
        }

        if (request.getSavingValue() == null
                || request.getSavingValue().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Saving value must be greater than 0"
            );
        }
    }

    private SavingGoalResponse mapToResponse(SavingGoal savingGoal) {

        SavingGoalResponse response = new SavingGoalResponse();

        response.setSavingGoalId(savingGoal.getSavingGoalId());
        response.setName(savingGoal.getName());
        response.setGoalType(savingGoal.getGoalType());
        response.setTargetAmount(savingGoal.getTargetAmount());
        response.setCurrentAmount(savingGoal.getCurrentAmount());
        response.setSavingMethod(savingGoal.getSavingMethod());
        response.setSavingValue(savingGoal.getSavingValue());
        response.setDurationMonths(savingGoal.getDurationMonths());
        response.setStartDate(savingGoal.getStartDate());
        response.setTargetDate(savingGoal.getTargetDate());
        response.setStatus(savingGoal.getStatus());

        return response;
    }

    // =========================
    // GET ALL SAVING GOALS
    // =========================
    @Transactional(readOnly = true)
    public List<SavingGoalResponse> getAllSavings(User user) {

    return savingGoalRepository.findByUser(user)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    // ========================
    // GET SAVING GOAL BY ID
    // ========================
    @Transactional(readOnly = true)
    public SavingGoalResponse getSavingById(
            Long savingGoalId,
            User user
    ) {

        SavingGoal savingGoal = savingGoalRepository
               .findBySavingGoalIdAndUser(savingGoalId, user)
                .orElseThrow(() ->
                         new IllegalArgumentException(
                                "Saving goal not found"
                        )
               );

        return mapToResponse(savingGoal);
    }

    // ========================
    // UPDATE SAVING GOAL
    // ========================
    @Transactional
    public SavingGoalResponse updateSaving(
            Long savingGoalId,
            SavingGoalRequest request,
            User user
    ) {
    
        SavingGoal savingGoal = savingGoalRepository
                .findBySavingGoalIdAndUser(savingGoalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saving goal not found"
                        )
                );
    
        // Không cho đổi Goal Type
        if (request.getGoalType() != savingGoal.getGoalType()) {
            throw new IllegalArgumentException(
                    "Goal type cannot be changed"
            );
        }
    
        // =========================
        // TARGET
        // =========================
        if (savingGoal.getGoalType() == GoalType.TARGET) {
    
            // TARGET chỉ được đổi tên
            savingGoal.setName(request.getName());
        }
    
        // =========================
        // UNLIMITED
        // =========================
        else if (savingGoal.getGoalType() == GoalType.UNLIMITED) {
    
            validateUnlimitedRequest(request);
    
            savingGoal.setName(request.getName());
    
            // Cho phép FIXED <-> PERCENTAGE
            savingGoal.setSavingMethod(
                    request.getSavingMethod()
            );
    
            savingGoal.setSavingValue(
                    request.getSavingValue()
            );
        }
    
        SavingGoal updatedGoal =
                savingGoalRepository.save(savingGoal);
    
        return mapToResponse(updatedGoal);
    }

    // ========================
    // DELETE SAVING GOAL
    // ========================
    @Transactional
    public void deleteSaving(
            Long savingGoalId,
            User user
    ) {
    
        SavingGoal savingGoal = savingGoalRepository
                .findBySavingGoalIdAndUser(savingGoalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saving goal not found"
                        )
                );
    
        savingGoalRepository.delete(savingGoal);
    }

    // ========================
    // DEPOSIT SAVING STATUS
    // ========================
    @Transactional
    public SavingGoalResponse deposit(
            Long savingGoalId,
            SavingDepositRequest request,
            User user
    ) {
        // 1. Tìm SavingGoal + kiểm tra ownership
        SavingGoal savingGoal = savingGoalRepository
                .findBySavingGoalIdAndUser(savingGoalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saving goal not found"
                        )
                );
    
        // 2. Kiểm tra ACTIVE
        if (savingGoal.getStatus() != SavingStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Saving goal is not active"
            );
        }
    
        // 3. Kiểm tra amount > 0
        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than 0"
            );
        }
    
        // 4. Tạo SavingRecord
        SavingRecord record = new SavingRecord();
    
        record.setSavingGoal(savingGoal);
        record.setAmount(request.getAmount());
        record.setType(SavingRecordType.DEPOSIT);
        record.setCreatedAt(LocalDateTime.now());
        record.setDescription(request.getDescription());
    
        savingRecordRepository.save(record);
    
        // 5. currentAmount += amount
        BigDecimal newAmount = savingGoal.getCurrentAmount()
                .add(request.getAmount());
    
        savingGoal.setCurrentAmount(newAmount);
    
        // 6. TARGET và đạt mục tiêu → COMPLETED
        if (savingGoal.getGoalType() == GoalType.TARGET
                && newAmount.compareTo(savingGoal.getTargetAmount()) >= 0) {
    
            savingGoal.setStatus(SavingStatus.COMPLETED);
        }
    
        // 7. Save
        SavingGoal savedGoal =
                savingGoalRepository.save(savingGoal);
    
        return mapToResponse(savedGoal);
    }

    // ========================
    // WITHDRAW SAVING STATUS
    // ========================
    @Transactional
    public SavingGoalResponse withdraw(
            Long savingGoalId,
            SavingWithdrawRequest request,
            User user
    ) {
        // 1. Tìm SavingGoal + kiểm tra ownership
        SavingGoal savingGoal = savingGoalRepository
                .findBySavingGoalIdAndUser(savingGoalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saving goal not found"
                        )
                );
    
        // 2. Kiểm tra amount > 0
        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Withdraw amount must be greater than 0"
            );
        }
    
        // 3. Kiểm tra amount <= currentAmount
        if (request.getAmount()
                .compareTo(savingGoal.getCurrentAmount()) > 0) {
    
            throw new IllegalArgumentException(
                    "Withdraw amount exceeds current saving amount"
            );
        }
    
        // 4. Tạo SavingRecord
        SavingRecord record = new SavingRecord();
    
        record.setSavingGoal(savingGoal);
        record.setAmount(request.getAmount());
        record.setType(SavingRecordType.WITHDRAW);
        record.setCreatedAt(LocalDateTime.now());
        record.setDescription(request.getDescription());
    
        savingRecordRepository.save(record);
    
        // 5. currentAmount -= amount
        BigDecimal newAmount = savingGoal.getCurrentAmount()
                .subtract(request.getAmount());
    
        savingGoal.setCurrentAmount(newAmount);
    
        // 6. Save
        SavingGoal savedGoal =
                savingGoalRepository.save(savingGoal);
    
        return mapToResponse(savedGoal);
    }
}