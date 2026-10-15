import { useCallback, useEffect, useMemo, useState } from "react";

import {
    getBudgets,
    createBudget,
    updateBudget,
    deleteBudget,
} from "../api/budgetApi";

import { getCategories } from "../api/categoryApi";
import { getTransactions } from "../api/transactionApi";

import BudgetCard from "../components/budget/BudgetCard";
import BudgetForm from "../components/budget/BudgetForm";
import BudgetCategoryList from "../components/budget/BudgetCategoryList";

function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
        maximumFractionDigits: 0,
    }).format(Number(value) || 0);
}

function Budgets() {
    const now = new Date();

    const [budgets, setBudgets] = useState([]);
    const [categories, setCategories] = useState([]);
    const [transactions, setTransactions] = useState([]);

    const [selectedBudgetId, setSelectedBudgetId] = useState(null);
    const [modalOpen, setModalOpen] = useState(false);
    const [editingBudget, setEditingBudget] = useState(null);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadData = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const [budgetData, categoryData, transactionData] =
                await Promise.all([
                    getBudgets(),
                    getCategories(),
                    getTransactions(),
                ]);

            const budgetList = Array.isArray(budgetData) ? budgetData : [];

            setBudgets(budgetList);
            setCategories(Array.isArray(categoryData) ? categoryData : []);
            setTransactions(
                Array.isArray(transactionData) ? transactionData : []
            );

            setSelectedBudgetId((currentId) => {
                if (
                    currentId != null &&
                    budgetList.some(
                        (item) => item.budgetId === currentId
                    )
                ) {
                    return currentId;
                }

                const currentMonthBudget = budgetList.find(
                    (item) =>
                        Number(item.month) === now.getMonth() + 1 &&
                        Number(item.year) === now.getFullYear()
                );

                return (
                    currentMonthBudget?.budgetId ??
                    budgetList[0]?.budgetId ??
                    null
                );
            });
        } catch (err) {
            setError(err.message || "Không thể tải dữ liệu ngân sách.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const selectedBudget = useMemo(
        () =>
            budgets.find(
                (item) => item.budgetId === selectedBudgetId
            ) || null,
        [budgets, selectedBudgetId]
    );

    const selectedMonthTransactions = useMemo(() => {
        if (!selectedBudget) return [];

        return transactions.filter((transaction) => {
            if (transaction.type !== "EXPENSE" || !transaction.createdAt) {
                return false;
            }

            const date = new Date(transaction.createdAt);

            return (
                !Number.isNaN(date.getTime()) &&
                date.getMonth() + 1 === Number(selectedBudget.month) &&
                date.getFullYear() === Number(selectedBudget.year)
            );
        });
    }, [transactions, selectedBudget]);

    const actualExpense = useMemo(
        () =>
            selectedMonthTransactions.reduce(
                (sum, transaction) =>
                    sum + (Number(transaction.spendingMoney) || 0),
                0
            ),
        [selectedMonthTransactions]
    );

    const summary = useMemo(() => {
        if (!selectedBudget) {
            return {
                total: 0,
                expenseBudget: 0,
                allocated: 0,
                remaining: 0,
            };
        }

        const total = Number(selectedBudget.totalBudget) || 0;
        const expenseBudget =
            selectedBudget.expenseBudget != null
                ? Number(selectedBudget.expenseBudget)
                : total +
                  (Number(selectedBudget.rolloverAmount) || 0) -
                  (Number(selectedBudget.savingAmount) || 0);

        const allocated = Number(selectedBudget.allocatedAmount) || 0;

        return {
            total,
            expenseBudget,
            allocated,
            remaining:
                selectedBudget.remainingAmount != null
                    ? Number(selectedBudget.remainingAmount)
                    : expenseBudget - allocated,
        };
    }, [selectedBudget]);

    function openCreateModal() {
        setEditingBudget(null);
        setModalOpen(true);
    }

    function openEditModal(budget) {
        setEditingBudget(budget);
        setModalOpen(true);
    }

    async function handleSave(formData) {
        setSaving(true);

        try {
            if (editingBudget) {
                await updateBudget(editingBudget.budgetId, formData);
            } else {
                await createBudget(formData);
            }

            setModalOpen(false);
            setEditingBudget(null);
            await loadData();
        } finally {
            setSaving(false);
        }
    }

    async function handleDelete(budget) {
        const confirmed = window.confirm(
            `Bạn có chắc muốn xóa ngân sách tháng ${budget.month}/${budget.year}?`
        );

        if (!confirmed) return;

        try {
            await deleteBudget(budget.budgetId);

            setBudgets((previous) =>
                previous.filter(
                    (item) => item.budgetId !== budget.budgetId
                )
            );

            setSelectedBudgetId((currentId) =>
                currentId === budget.budgetId ? null : currentId
            );
        } catch (err) {
            window.alert(
                err.message ||
                    "Không thể xóa ngân sách. Kiểm tra lại API backend."
            );
        }
    }

    return (
        <div className="budgets-page">
            <header className="budgets-heading">
                <div>
                    <p className="budget-eyebrow">YOUR PERSONAL FINANCE</p>
                    <h1>Ngân sách</h1>
                    <p>
                        Lập kế hoạch chi tiêu, theo dõi từng danh mục và kiểm
                        soát giới hạn hàng tháng.
                    </p>
                </div>

                <button
                    type="button"
                    className="budget-btn budget-btn-primary"
                    onClick={openCreateModal}
                >
                    <span>＋</span> Tạo ngân sách
                </button>
            </header>

            {loading ? (
                <div className="budget-state">Đang tải ngân sách...</div>
            ) : error ? (
                <div className="budget-state budget-state-error">
                    <p>{error}</p>
                    <button
                        type="button"
                        className="budget-btn budget-btn-secondary"
                        onClick={loadData}
                    >
                        Thử lại
                    </button>
                </div>
            ) : (
                <>
                    <section className="budget-overview">
                        <article className="budget-overview-card">
                            <span className="budget-overview-icon">◷</span>
                            <p>Tổng ngân sách</p>
                            <h2>{formatMoney(summary.total)}</h2>
                            <span>Ngân sách tháng được chọn</span>
                        </article>

                        <article className="budget-overview-card">
                            <span className="budget-overview-icon">↗</span>
                            <p>Ngân sách chi tiêu</p>
                            <h2>{formatMoney(summary.expenseBudget)}</h2>
                            <span>
                                Sau khi trừ tiền tiết kiệm và cộng tiền chuyển dư
                            </span>
                        </article>

                        <article className="budget-overview-card">
                            <span className="budget-overview-icon">✦</span>
                            <p>Chi tiêu thực tế</p>
                            <h2>{formatMoney(actualExpense)}</h2>
                            <span>
                                {selectedBudget
                                    ? `Tháng ${selectedBudget.month}/${selectedBudget.year}`
                                    : "Chưa chọn ngân sách"}
                            </span>
                        </article>

                        <article className="budget-overview-card">
                            <span className="budget-overview-icon">♡</span>
                            <p>Chưa phân bổ</p>
                            <h2>{formatMoney(summary.remaining)}</h2>
                            <span>
                                Ngân sách chi tiêu trừ tổng hạn mức đã phân bổ
                            </span>
                        </article>
                    </section>

                    <section className="budget-layout">
                        <div className="budget-list-panel">
                            <div className="budget-section-heading">
                                <div>
                                    <p className="budget-eyebrow">MONTHLY PLAN</p>
                                    <h2>Kế hoạch ngân sách</h2>
                                </div>
                                <span>{budgets.length} ngân sách</span>
                            </div>

                            {budgets.length === 0 ? (
                                <div className="budget-empty">
                                    <span>✦</span>
                                    <h3>Bắt đầu lập kế hoạch</h3>
                                    <p>
                                        Tạo ngân sách đầu tiên để phân bổ hạn mức
                                        cho các danh mục chi tiêu.
                                    </p>
                                    <button
                                        type="button"
                                        className="budget-btn budget-btn-primary"
                                        onClick={openCreateModal}
                                    >
                                        Tạo ngân sách
                                    </button>
                                </div>
                            ) : (
                                <div className="budget-cards">
                                    {budgets.map((budget) => (
                                        <BudgetCard
                                            key={budget.budgetId}
                                            budget={budget}
                                            selected={
                                                budget.budgetId === selectedBudgetId
                                            }
                                            onSelect={(item) =>
                                                setSelectedBudgetId(item.budgetId)
                                            }
                                            onEdit={openEditModal}
                                            onDelete={handleDelete}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>

                        <section className="budget-detail-panel">
                            {!selectedBudget ? (
                                <div className="budget-empty">
                                    <h3>Chưa có ngân sách được chọn</h3>
                                    <p>
                                        Tạo ngân sách hoặc chọn một ngân sách
                                        bên trái để quản lý danh mục.
                                    </p>
                                </div>
                            ) : (
                                <>
                                    <div className="budget-detail-heading">
                                        <div>
                                            <p className="budget-eyebrow">
                                                BUDGET DETAILS
                                            </p>
                                            <h2>
                                                Tháng {selectedBudget.month}/
                                                {selectedBudget.year}
                                            </h2>
                                            <p>
                                                {selectedBudget.allocationMode ===
                                                "PERCENTAGE"
                                                    ? "Phân bổ theo phần trăm"
                                                    : "Phân bổ theo số tiền"}
                                            </p>
                                        </div>
                                    </div>

                                    <div className="budget-actual-summary">
                                        <div>
                                            <span>Chi tiêu thực tế</span>
                                            <strong>
                                                {formatMoney(actualExpense)}
                                            </strong>
                                        </div>
                                        <div>
                                            <span>Ngân sách chi tiêu</span>
                                            <strong>
                                                {formatMoney(
                                                    summary.expenseBudget
                                                )}
                                            </strong>
                                        </div>
                                    </div>

                                    {actualExpense > summary.expenseBudget && (
                                        <div className="budget-warning">
                                            <strong>Đã vượt ngân sách tháng</strong>
                                            <p>
                                                Bạn đã chi vượt{" "}
                                                {formatMoney(
                                                    actualExpense -
                                                        summary.expenseBudget
                                                )}
                                                . Hãy kiểm tra các giao dịch và
                                                hạn mức đã phân bổ.
                                            </p>
                                        </div>
                                    )}

                                    <BudgetCategoryList
                                        budget={selectedBudget}
                                        categories={categories}
                                        transactions={transactions}
                                        onRefresh={loadData}
                                    />
                                </>
                            )}
                        </section>
                    </section>
                </>
            )}

            {modalOpen && (
                <div
                    className="budget-modal-overlay"
                    onMouseDown={(event) => {
                        if (
                            event.target === event.currentTarget &&
                            !saving
                        ) {
                            setModalOpen(false);
                        }
                    }}
                >
                    <section
                        className="budget-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="budget-modal-title"
                    >
                        <div className="budget-modal-heading">
                            <div>
                                <p className="budget-eyebrow">
                                    PERSONAL FINANCE MANAGER
                                </p>
                                <h2 id="budget-modal-title">
                                    {editingBudget
                                        ? "Chỉnh sửa ngân sách"
                                        : "Tạo ngân sách tháng"}
                                </h2>
                            </div>

                            <button
                                type="button"
                                className="budget-close-btn"
                                onClick={() => setModalOpen(false)}
                                disabled={saving}
                                aria-label="Đóng"
                            >
                                ×
                            </button>
                        </div>

                        <BudgetForm
                            budget={editingBudget}
                            onSubmit={handleSave}
                            onCancel={() => setModalOpen(false)}
                            saving={saving}
                        />
                    </section>
                </div>
            )}
        </div>
    );
}

export default Budgets;