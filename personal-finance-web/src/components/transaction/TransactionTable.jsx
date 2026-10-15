function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
        maximumFractionDigits: 0,
    }).format(Number(value) || 0);
}

function formatDate(value) {
    if (!value) return "—";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) return "—";

    return date.toLocaleDateString("vi-VN");
}

function TransactionTable({
    transactions,
    onEdit,
    onDelete,
}) {
    if (transactions.length === 0) {
        return (
            <div className="transaction-empty">
                <div className="empty-icon">♡</div>
                <h3>Chưa tìm thấy giao dịch</h3>
                <p>
                    Thử thay đổi bộ lọc hoặc thêm giao dịch mới nhé.
                </p>
            </div>
        );
    }

    return (
        <div className="transaction-table-wrapper">
            <table className="transaction-table">
                <thead>
                    <tr>
                        <th>Giao dịch</th>
                        <th>Danh mục</th>
                        <th>Ngày tạo</th>
                        <th>Số tiền</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>

                <tbody>
                    {transactions.map((transaction) => {
                        const isIncome =
                            transaction.type === "INCOME";

                        const isSaving =
                            transaction.type === "SAVING";

                        const amountClass = isIncome
                            ? "income"
                            : isSaving
                                ? "saving"
                                : "expense";

                        const amountPrefix = isIncome
                            ? "+"
                            : isSaving
                                ? ""
                                : "−";

                        return (
                            <tr key={transaction.transactionId}>
                                <td>
                                    <div className="transaction-name">
                                        <span
                                            className={`transaction-dot ${amountClass}`}
                                        />
                                        <div>
                                            <strong>
                                                {transaction.transactionName}
                                            </strong>

                                            {transaction.description && (
                                                <p>
                                                    {transaction.description}
                                                </p>
                                            )}
                                        </div>
                                    </div>
                                </td>

                                <td>
                                    <span className="category-chip">
                                        {transaction.categoryName || "Chưa phân loại"}
                                    </span>
                                </td>

                                <td>
                                    {formatDate(transaction.createdAt)}
                                </td>

                                <td>
                                    <strong
                                        className={`transaction-amount ${amountClass}`}
                                    >
                                        {amountPrefix}
                                        {formatMoney(
                                            transaction.spendingMoney
                                        )}
                                    </strong>
                                </td>

                                <td>
                                    <div className="transaction-actions">
                                        <button
                                            type="button"
                                            className="action-btn edit"
                                            onClick={() => onEdit(transaction)}
                                            aria-label="Sửa giao dịch"
                                            title="Sửa"
                                        >
                                            ✎
                                        </button>

                                        <button
                                            type="button"
                                            className="action-btn delete"
                                            onClick={() => onDelete(transaction)}
                                            aria-label="Xóa giao dịch"
                                            title="Xóa"
                                        >
                                            ×
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}

export default TransactionTable;