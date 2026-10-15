function SavingSummary({ saved, target }) {
    const percentage =
        target > 0
            ? Math.min((saved / target) * 100, 100)
            : 0;

    return (
        <section className="dashboard-panel">
            <div className="panel-heading">
                <div>
                    <h3>Saving goals</h3>
                    <p>Small steps, big dreams.</p>
                </div>

                <span className="saving-decoration">✧</span>
            </div>

            <div className="saving-amount">
                <strong>{saved.toLocaleString("vi-VN")} ₫</strong>
                <span> / {target.toLocaleString("vi-VN")} ₫</span>
            </div>

            <div className="progress-track">
                <div
                    className="progress-fill saving-fill"
                    style={{ width: `${percentage}%` }}
                />
            </div>

            <div className="budget-footer">
                <span>{percentage.toFixed(0)}% completed</span>
                <span>Keep going ✦</span>
            </div>
        </section>
    );
}

export default SavingSummary;