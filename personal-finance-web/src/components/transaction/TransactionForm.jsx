import { useState } from "react";
import CustomSelect from "./CustomSelect";

function TransactionForm({
    transaction,
    categories,
    onSubmit,
    onCancel,
    saving,
}) {
    const [form, setForm] = useState({
        transactionName: transaction?.transactionName || "",
        categoryId: transaction?.categoryId
            ? String(transaction.categoryId)
            : "",
        spendingMoney: transaction?.spendingMoney ?? "",
        description: transaction?.description || "",
    });

    const [error, setError] = useState("");

    function handleChange(event) {
        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");

        if (!form.categoryId) {
            setError("Vui lòng chọn danh mục.");
            return;
        }

        if (
            !form.spendingMoney ||
            !Number.isFinite(Number(form.spendingMoney)) ||
            Number(form.spendingMoney) <= 0
        ) {
            setError("Số tiền phải lớn hơn 0.");
            return;
        }

        try {
            await onSubmit({
                transactionName: form.transactionName.trim(),
                categoryId: Number(form.categoryId),
                spendingMoney: Number(form.spendingMoney),
                description: form.description.trim(),
            });
        } catch (err) {
            setError(err.message || "Không thể lưu giao dịch.");
        }
    }

    return (
        <form className="transaction-form" onSubmit={handleSubmit}>
            <div className="form-heading">
                <span className="campus-stamp">B.U.</span>

                <div>
                    <p className="campus-eyebrow">
                        BTS UNIVERSITY · FINANCE CLASS
                    </p>

                    <h3>
                        {transaction
                            ? "Chỉnh sửa giao dịch"
                            : "Ghi chép giao dịch mới"}
                    </h3>
                </div>
            </div>

            <div className="transaction-field">
                <label htmlFor="transactionName">
                    Tên giao dịch *
                </label>

                <input
                    id="transactionName"
                    name="transactionName"
                    value={form.transactionName}
                    onChange={handleChange}
                    placeholder="Ví dụ: Ăn trưa ở trường"
                    maxLength={100}
                    required
                />
            </div>

            <div className="transaction-field">
                <label htmlFor="categoryId">Danh mục *</label>

                <div className="transaction-field">
                    <label>Danh mục *</label>
                
                    <CustomSelect
                        value={form.categoryId}
                        onChange={(value) =>
                            setForm((previous) => ({
                                ...previous,
                                categoryId: value,
                            }))
                        }
                        placeholder="Chọn danh mục"
                        options={categories.map((category) => ({
                            value: String(category.categoryId),
                            label: category.categoryName,
                        }))}
                        searchable
                    />
                </div>
            </div>

            <div className="transaction-field">
                <label htmlFor="spendingMoney">
                    Số tiền (VND) *
                </label>

                <input
                    id="spendingMoney"
                    name="spendingMoney"
                    type="number"
                    min="0.01"
                    step="any"
                    value={form.spendingMoney}
                    onChange={handleChange}
                    placeholder="0"
                    required
                />
            </div>

            <div className="transaction-field">
                <label htmlFor="description">Ghi chú</label>

                <textarea
                    id="description"
                    name="description"
                    value={form.description}
                    onChange={handleChange}
                    placeholder="Ghi chú thêm về giao dịch..."
                    maxLength={225}
                    rows={3}
                />
            </div>

            {error && (
                <p className="transaction-form-error">
                    {error}
                </p>
            )}

            <div className="transaction-form-actions">
                <button
                    type="button"
                    className="campus-btn secondary"
                    onClick={onCancel}
                    disabled={saving}
                >
                    Hủy
                </button>

                <button
                    type="submit"
                    className="campus-btn primary"
                    disabled={saving}
                >
                    {saving
                        ? "Đang lưu..."
                        : transaction
                            ? "Lưu thay đổi"
                            : "Thêm giao dịch"}
                </button>
            </div>
        </form>
    );
}

export default TransactionForm;