function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
        maximumFractionDigits: 0,
    }).format(Number(value) || 0);
}

function BudgetCard({ budget, onEdit, onDelete, selected, onSelect }) {
    const total = Number(budget.totalBudget) || 0;
    const savings = Number(budget.savingAmount) || 0;
    const rollover = Number(budget.rolloverAmount) || 0;
    const expenseBudget =
        budget.expenseBudget != null
            ? Number(budget.expenseBudget)
            : total + rollover - savings;

    const allocated = Number(budget.allocatedAmount) || 0;
    const remaining =
        budget.remainingAmount != null
            ? Number(budget.remainingAmount)
            : expenseBudget - allocated;

    const percentage =
        expenseBudget > 0 ? (allocated / expenseBudget) * 100 : 0;

    return (
        <article
            className={`budget-card ${selected ? "budget-card-selected" : ""}`}
        >
            <button
                type="button"
                className="budget-card-main"
                onClick={() => onSelect(budget)}
            >
                <div className="budget-card-heading">
                    <span className="budget-card-icon">◷</span>
                    <span className="budget-card-month">
                        Tháng {budget.month}/{budget.year}
                    </span>
                    <span className="budget-card-mode">
                        {budget.allocationMode === "PERCENTAGE"
                            ? "Theo %"
                            : "Theo số tiền"}
                    </span>
                </div>

                <p className="budget-card-label">Tổng ngân sách</p>
                <h3>{formatMoney(total)}</h3>

                <div className="budget-card-details">
                    <div>
                        <span>Ngân sách chi tiêu</span>
                        <strong>{formatMoney(expenseBudget)}</strong>
                    </div>
                    <div>
                        <span>Đã phân bổ</span>
                        <strong>{formatMoney(allocated)}</strong>
                    </div>
                    <div>
                        <span>Chưa phân bổ</span>
                        <strong className={remaining < 0 ? "text-danger" : ""}>
                            {formatMoney(remaining)}
                        </strong>
                    </div>
                </div>

                <div className="budget-progress-track">
                    <div
                        className="budget-progress-fill"
                        style={{ width: `${Math.min(percentage, 100)}%` }}
                    />
                </div>

                <p className="budget-card-footnote">
                    Đã phân bổ {percentage.toFixed(1)}% ngân sách chi tiêu
                </p>
            </button>

            <div className="budget-card-actions">
                <button
                    type="button"
                    className="budget-btn budget-btn-secondary"
                    onClick={() => onEdit(budget)}
                >
                    Chỉnh sửa
                </button>

                <button
                    type="button"
                    className="budget-btn budget-btn-danger"
                    onClick={() => onDelete(budget)}
                >
                    Xóa
                </button>
            </div>
        </article>
    );
}

export default BudgetCard;