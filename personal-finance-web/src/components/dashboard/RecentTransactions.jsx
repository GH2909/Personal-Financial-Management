function RecentTransactions({ transactions }) {
    return (
        <section className="dashboard-panel">
            <div className="panel-heading">
                <div>
                    <h3>Recent transactions</h3>
                    <p>Your latest money activities</p>
                </div>
            </div>

            {transactions.length === 0 ? (
                <p className="empty-state">
                    No transactions yet.
                </p>
            ) : (
                <div className="transaction-list">
                    {transactions.map((transaction) => (
                        <div
                            className="transaction-row"
                            key={transaction.id}
                        >
                            <div className="transaction-info">
                                <span
                                    className={`transaction-icon ${
                                        transaction.type === "INCOME"
                                            ? "income-icon"
                                            : "expense-icon"
                                    }`}
                                >
                                    {transaction.type === "INCOME" ? "↗" : "↘"}
                                </span>

                                <div>
                                    <p className="transaction-name">
                                        {transaction.title}
                                    </p>

                                    <p className="transaction-meta">
                                        {transaction.category} · {transaction.date}
                                    </p>
                                </div>
                            </div>

                            <strong
                                className={
                                    transaction.type === "INCOME"
                                        ? "income-text"
                                        : "expense-text"
                                }
                            >
                                {transaction.type === "INCOME" ? "+" : "-"}
                                {transaction.amount.toLocaleString("vi-VN")} ₫
                            </strong>
                        </div>
                    ))}
                </div>
            )}
        </section>
    );
}

export default RecentTransactions;