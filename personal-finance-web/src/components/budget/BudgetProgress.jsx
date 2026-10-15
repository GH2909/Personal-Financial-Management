function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
        maximumFractionDigits: 0,
    }).format(Number(value) || 0);
}

function BudgetProgress({ category, spent = 0 }) {
    const allocated = Number(category.allocatedAmount) || 0;
    const actualSpent = Number(spent) || 0;

    const percentage =
        allocated > 0 ? (actualSpent / allocated) * 100 : actualSpent > 0 ? 100 : 0;

    const barWidth = Math.min(percentage, 100);
    const exceeded = actualSpent > allocated;
    const nearLimit = percentage >= 80 && !exceeded;

    let status = "Trong hạn mức";
    if (exceeded) status = "Vượt ngân sách";
    else if (nearLimit) status = "Sắp hết ngân sách";

    return (
        <div className="budget-progress">
            <div className="budget-progress-heading">
                <div>
                    <strong>{category.categoryName}</strong>
                    <p>
                        Đã chi {formatMoney(actualSpent)} / {formatMoney(allocated)}
                    </p>
                </div>

                <span
                    className={`budget-status ${
                        exceeded
                            ? "budget-status-danger"
                            : nearLimit
                              ? "budget-status-warning"
                              : "budget-status-normal"
                    }`}
                >
                    {status}
                </span>
            </div>

            <div
                className="budget-progress-track"
                role="progressbar"
                aria-label={`Mức chi tiêu ${category.categoryName}`}
                aria-valuemin={0}
                aria-valuemax={100}
                aria-valuenow={Math.min(percentage, 100)}
            >
                <div
                    className={`budget-progress-fill ${
                        exceeded
                            ? "is-danger"
                            : nearLimit
                              ? "is-warning"
                              : ""
                    }`}
                    style={{ width: `${barWidth}%` }}
                />
            </div>

            <div className="budget-progress-footer">
                <span>{percentage.toFixed(1)}% đã sử dụng</span>
                <span>
                    {exceeded
                        ? `Vượt ${formatMoney(actualSpent - allocated)}`
                        : `Còn ${formatMoney(allocated - actualSpent)}`}
                </span>
            </div>
        </div>
    );
}

export default BudgetProgress;