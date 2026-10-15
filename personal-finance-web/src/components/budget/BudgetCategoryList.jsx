import { useEffect, useMemo, useState } from "react";

import {
    addBudgetCategory,
    updateBudgetCategory,
    deleteBudgetCategory,
} from "../../api/budgetApi";

import BudgetProgress from "./BudgetProgress";

const CATEGORY_TYPES = {
    INCOME: "Thu nhập",
    EXPENSE: "Chi tiêu",
    SAVING: "Tiết kiệm",
};

function getTransactionDate(transaction) {
    if (!transaction.createdAt) return null;

    const date = new Date(transaction.createdAt);
    return Number.isNaN(date.getTime()) ? null : date;
}

function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
        maximumFractionDigits: 0,
    }).format(Number(value) || 0);
}

function BudgetCategoryList({ budget, categories, transactions, onRefresh }) {
    const [categoryId, setCategoryId] = useState("");
    const [amount, setAmount] = useState("");
    const [percentage, setPercentage] = useState("");
    const [editingId, setEditingId] = useState(null);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState("");

    const isPercentage = budget.allocationMode === "PERCENTAGE";

    useEffect(() => {
        setCategoryId("");
        setAmount("");
        setPercentage("");
        setEditingId(null);
        setError("");
    }, [budget.budgetId]);

    const expenseCategories = useMemo(
        () => categories.filter((item) => item.type === "EXPENSE"),
        [categories]
    );

    const existingIds = new Set(
        (budget.categories || []).map((item) => String(item.categoryId))
    );

    const availableCategories = expenseCategories.filter(
        (item) =>
            !existingIds.has(String(item.categoryId)) ||
            String(item.categoryId) === String(editingId)
    );

    const spendingByCategory = useMemo(() => {
        const result = {};

        transactions.forEach((transaction) => {
            if (transaction.type !== "EXPENSE") return;

            const date = getTransactionDate(transaction);
            if (!date) return;

            if (
                date.getMonth() + 1 !== Number(budget.month) ||
                date.getFullYear() !== Number(budget.year)
            ) {
                return;
            }

            const id = String(transaction.categoryId);
            result[id] = (result[id] || 0) + (Number(transaction.spendingMoney) || 0);
        });

        return result;
    }, [transactions, budget.month, budget.year]);

    function startEdit(item) {
        setEditingId(item.categoryId);
        setCategoryId(String(item.categoryId));
        setAmount(String(item.allocatedAmount ?? ""));
        setPercentage(String(item.allocationPercentage ?? ""));
        setError("");
    }

    function resetForm() {
        setEditingId(null);
        setCategoryId("");
        setAmount("");
        setPercentage("");
        setError("");
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const id = Number(categoryId);
        const value = Number(isPercentage ? percentage : amount);

        if (!id) {
            setError("Vui lòng chọn danh mục.");
            return;
        }

        if (!Number.isFinite(value) || value < 0) {
            setError("Giá trị phân bổ phải lớn hơn hoặc bằng 0.");
            return;
        }

        if (isPercentage && value > 100) {
            setError("Phần trăm không được vượt quá 100%.");
            return;
        }

        setBusy(true);
        setError("");

        try {
            const payload = {
                categoryId: id,
                ...(isPercentage
                    ? { allocationPercentage: value }
                    : { allocatedAmount: value }),
            };

            if (editingId != null) {
                await updateBudgetCategory(
                    budget.budgetId,
                    editingId,
                    payload
                );
            } else {
                await addBudgetCategory(budget.budgetId, payload);
            }

            resetForm();
            await onRefresh();
        } catch (err) {
            setError(err.message || "Không thể lưu phân bổ danh mục.");
        } finally {
            setBusy(false);
        }
    }

    async function handleDelete(item) {
        const confirmed = window.confirm(
            `Xóa phân bổ cho danh mục "${item.categoryName}"?`
        );

        if (!confirmed) return;

        setBusy(true);
        setError("");

        try {
            await deleteBudgetCategory(budget.budgetId, item.categoryId);
            if (String(editingId) === String(item.categoryId)) resetForm();
            await onRefresh();
        } catch (err) {
            setError(err.message || "Không thể xóa danh mục khỏi ngân sách.");
        } finally {
            setBusy(false);
        }
    }

    const budgetCategories = budget.categories || [];

    return (
        <div className="budget-category-section">
            <div className="budget-section-heading">
                <div>
                    <p className="budget-eyebrow">CATEGORY ALLOCATION</p>
                    <h3>Phân bổ theo danh mục</h3>
                    <p>Chỉ dùng danh mục chi tiêu để lập hạn mức.</p>
                </div>
            </div>

            <form className="budget-category-form" onSubmit={handleSubmit}>
                <div className="budget-field">
                    <label htmlFor="budgetCategory">Danh mục</label>
                    <select
                        id="budgetCategory"
                        value={categoryId}
                        onChange={(event) => setCategoryId(event.target.value)}
                        disabled={busy}
                        required
                    >
                        <option value="">Chọn danh mục chi tiêu</option>
                        {availableCategories.map((item) => (
                            <option key={item.categoryId} value={item.categoryId}>
                                {item.icon ? `${item.icon} ` : ""}
                                {item.categoryName}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="budget-field">
                    <label htmlFor="allocationValue">
                        {isPercentage ? "Phân bổ (%)" : "Số tiền phân bổ (VND)"}
                    </label>
                    <input
                        id="allocationValue"
                        type="number"
                        min="0"
                        max={isPercentage ? 100 : undefined}
                        step={isPercentage ? "0.01" : "1000"}
                        value={isPercentage ? percentage : amount}
                        onChange={(event) =>
                            isPercentage
                                ? setPercentage(event.target.value)
                                : setAmount(event.target.value)
                        }
                        placeholder={isPercentage ? "Ví dụ: 25" : "Ví dụ: 2000000"}
                        disabled={busy}
                        required
                    />
                </div>

                <div className="budget-category-form-actions">
                    <button
                        type="submit"
                        className="budget-btn budget-btn-primary"
                        disabled={busy}
                    >
                        {busy
                            ? "Đang lưu..."
                            : editingId != null
                              ? "Cập nhật"
                              : "Thêm danh mục"}
                    </button>

                    {editingId != null && (
                        <button
                            type="button"
                            className="budget-btn budget-btn-secondary"
                            onClick={resetForm}
                            disabled={busy}
                        >
                            Hủy sửa
                        </button>
                    )}
                </div>
            </form>

            {error && <p className="budget-form-error">{error}</p>}

            {budgetCategories.length === 0 ? (
                <div className="budget-empty-category">
                    Chưa có danh mục nào được phân bổ cho ngân sách này.
                </div>
            ) : (
                <div className="budget-category-list">
                    {budgetCategories.map((item) => (
                        <article
                            className="budget-category-item"
                            key={item.budgetCategoryId ?? item.categoryId}
                        >
                            <BudgetProgress
                                category={item}
                                spent={spendingByCategory[String(item.categoryId)] || 0}
                            />

                            <div className="budget-category-meta">
                                <span>
                                    Phân bổ:{" "}
                                    {isPercentage
                                        ? `${Number(item.allocationPercentage || 0).toFixed(2)}%`
                                        : formatMoney(item.allocatedAmount)}
                                </span>

                                <span>
                                    Loại: {CATEGORY_TYPES.EXPENSE}
                                </span>
                            </div>

                            <div className="budget-category-actions">
                                <button
                                    type="button"
                                    className="budget-btn budget-btn-secondary"
                                    onClick={() => startEdit(item)}
                                    disabled={busy}
                                >
                                    Chỉnh sửa
                                </button>

                                <button
                                    type="button"
                                    className="budget-btn budget-btn-danger"
                                    onClick={() => handleDelete(item)}
                                    disabled={busy}
                                >
                                    Xóa
                                </button>
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </div>
    );
}

export default BudgetCategoryList;