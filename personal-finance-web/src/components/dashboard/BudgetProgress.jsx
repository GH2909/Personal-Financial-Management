function BudgetProgress({ spent, budget }) {
    const percentage =
        budget > 0
            ? Math.min((spent / budget) * 100, 100)
            : 0;

    const isWarning = budget > 0 && spent / budget >= 0.8;

    return (
        <section className="dashboard-panel">
            <div className="panel-heading">
                <div>
                    <h3>Budget overview</h3>
                    <p>Your monthly spending limit</p>
                </div>

                <span className="panel-decoration">✦</span>
            </div>

            <div className="budget-amounts">
                <strong>{spent.toLocaleString("vi-VN")} ₫</strong>
                <span>of {budget.toLocaleString("vi-VN")} ₫</span>
            </div>

            <div
                className="progress-track"
                role="progressbar"
                aria-valuenow={Math.round(percentage)}
                aria-valuemin={0}
                aria-valuemax={100}
                aria-label="Monthly budget used"
            >
                <div
                    className={`progress-fill ${isWarning ? "warning" : ""}`}
                    style={{ width: `${percentage}%` }}
                />
            </div>

            <div className="budget-footer">
                <span>{percentage.toFixed(0)}% used</span>
                <span>
                    {Math.max(budget - spent, 0).toLocaleString("vi-VN")} ₫ left
                </span>
            </div>

            {isWarning && (
                <p className="budget-warning">
                    You have used at least 80% of your budget.
                </p>
            )}
        </section>
    );
}

export default BudgetProgress;