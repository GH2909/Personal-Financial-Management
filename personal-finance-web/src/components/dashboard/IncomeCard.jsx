function IncomeCard({ income }) {
    return (
        <section className="summary-card">
            <div className="summary-card-heading">
                <span className="summary-icon income-icon">↗</span>
                <span className="summary-label">Income</span>
            </div>

            <h3 className="summary-amount income-text">
                +{income.toLocaleString("vi-VN")} ₫
            </h3>

            <p className="summary-description">
                Total income this month
            </p>
        </section>
    );
}

export default IncomeCard;