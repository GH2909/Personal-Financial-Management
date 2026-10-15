import { useEffect, useState } from "react";

function BudgetForm({ budget, onSubmit, onCancel, saving }) {
    const now = new Date();

    const [form, setForm] = useState({
        totalBudget: "",
        month: String(now.getMonth() + 1),
        year: String(now.getFullYear()),
        allocationMode: "AMOUNT",
    });

    const [error, setError] = useState("");

    useEffect(() => {
        if (budget) {
            setForm({
                totalBudget: String(budget.totalBudget ?? ""),
                month: String(budget.month),
                year: String(budget.year),
                allocationMode: budget.allocationMode || "AMOUNT",
            });
        } else {
            setForm({
                totalBudget: "",
                month: String(now.getMonth() + 1),
                year: String(now.getFullYear()),
                allocationMode: "AMOUNT",
            });
        }

        setError("");
    }, [budget]);

    function handleChange(event) {
        const { name, value } = event.target;
        setForm((previous) => ({ ...previous, [name]: value }));
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const amount = Number(form.totalBudget);
        const month = Number(form.month);
        const year = Number(form.year);

        if (!Number.isFinite(amount) || amount <= 0) {
            setError("Tổng ngân sách phải lớn hơn 0.");
            return;
        }

        if (!Number.isInteger(month) || month < 1 || month > 12) {
            setError("Tháng phải nằm trong khoảng 1–12.");
            return;
        }

        if (!Number.isInteger(year) || year < 2000) {
            setError("Vui lòng nhập năm hợp lệ.");
            return;
        }

        setError("");

        try {
            await onSubmit({
                totalBudget: amount,
                month,
                year,
                allocationMode: form.allocationMode,
            });
        } catch (err) {
            setError(err.message || "Không thể lưu ngân sách.");
        }
    }

    return (
        <form className="budget-form" onSubmit={handleSubmit}>
            <div className="budget-field">
                <label htmlFor="totalBudget">Tổng ngân sách (VND)</label>
                <input
                    id="totalBudget"
                    name="totalBudget"
                    type="number"
                    min="1"
                    step="1000"
                    value={form.totalBudget}
                    onChange={handleChange}
                    placeholder="Ví dụ: 12000000"
                    required
                />
            </div>

            <div className="budget-form-row">
                <div className="budget-field">
                    <label htmlFor="budgetMonth">Tháng</label>
                    <select
                        id="budgetMonth"
                        name="month"
                        value={form.month}
                        onChange={handleChange}
                        disabled={Boolean(budget)}
                    >
                        {Array.from({ length: 12 }, (_, index) => (
                            <option key={index + 1} value={index + 1}>
                                Tháng {index + 1}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="budget-field">
                    <label htmlFor="budgetYear">Năm</label>
                    <input
                        id="budgetYear"
                        name="year"
                        type="number"
                        min="2000"
                        value={form.year}
                        onChange={handleChange}
                        disabled={Boolean(budget)}
                        required
                    />
                </div>
            </div>

            <div className="budget-field">
                <label htmlFor="allocationMode">Cách phân bổ</label>
                <select
                    id="allocationMode"
                    name="allocationMode"
                    value={form.allocationMode}
                    onChange={handleChange}
                    disabled={Boolean(budget)}
                >
                    <option value="AMOUNT">Theo số tiền</option>
                    <option value="PERCENTAGE">Theo phần trăm</option>
                </select>
                <p className="budget-field-hint">
                    {form.allocationMode === "AMOUNT"
                        ? "Nhập số tiền tối đa dành cho từng danh mục."
                        : "Nhập tỷ lệ phần trăm của ngân sách chi tiêu cho từng danh mục."}
                </p>
            </div>

            {budget && (
                <p className="budget-field-hint">
                    Theo quy tắc backend, tháng, năm và cách phân bổ không thể
                    thay đổi sau khi tạo ngân sách.
                </p>
            )}

            {error && <p className="budget-form-error">{error}</p>}

            <div className="budget-form-actions">
                <button
                    type="button"
                    className="budget-btn budget-btn-secondary"
                    onClick={onCancel}
                    disabled={saving}
                >
                    Hủy
                </button>

                <button
                    type="submit"
                    className="budget-btn budget-btn-primary"
                    disabled={saving}
                >
                    {saving ? "Đang lưu..." : budget ? "Lưu thay đổi" : "Tạo ngân sách"}
                </button>
            </div>
        </form>
    );
}

export default BudgetForm;