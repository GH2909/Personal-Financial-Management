function ExpenseCard({ expense }) {
    return (
        <section className="summary-card">
            <div className="summary-card-heading">
                <span className="summary-icon expense-icon">↘</span>
                <span className="summary-label">Expenses</span>
            </div>

            <h3 className="summary-amount expense-text">
                -{expense.toLocaleString("vi-VN")} ₫
            </h3>

            <p className="summary-description">
                Total expenses this month
            </p>
        </section>
    );
}

export default ExpenseCard;