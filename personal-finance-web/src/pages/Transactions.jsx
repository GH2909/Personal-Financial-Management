// return (
//     <div className="transactions-page">
//         <section className="page-hero transaction-hero">
//             <div className="page-hero-content">
//                 <span className="eyebrow">
//                     YOUR MONEY, YOUR WAY
//                 </span>

//                 <h1>
//                     Sổ giao dịch <span className="hero-sparkle">✦</span>
//                 </h1>

//                 <p>
//                     Mỗi khoản thu chi là một bước nhỏ để bạn hiểu rõ
//                     và chủ động hơn với tài chính của mình.
//                 </p>
//             </div>

//             <button
//                 type="button"
//                 className="btn btn-primary add-transaction-btn"
//                 onClick={openCreateModal}
//             >
//                 <span aria-hidden="true">＋</span>
//                 Thêm giao dịch
//             </button>
//         </section>

//         <section className="personal-note">
//             <div className="personal-note-icon">✧</div>

//             <div>
//                 <strong>Một chút quan tâm dành cho chính bạn</strong>
//                 <p>
//                     Theo dõi hôm nay, lên kế hoạch cho ngày mai.
//                     Từng bước nhỏ cũng tạo nên thay đổi lớn.
//                 </p>
//             </div>
//         </section>

//         <section className="transaction-summary">
//             <article className="summary-card income-card">
//                 <div className="finance-card-top">
//                     <span className="summary-icon">↙</span>
//                     <span className="finance-card-label">INCOME</span>
//                 </div>

//                 <p>Tổng thu nhập</p>
//                 <h2>{formatMoney(summary.income)}</h2>
//                 <span className="summary-note">
//                     Những gì bạn đã nhận được
//                 </span>
//             </article>

//             <article className="summary-card expense-card">
//                 <div className="finance-card-top">
//                     <span className="summary-icon">↗</span>
//                     <span className="finance-card-label">EXPENSE</span>
//                 </div>

//                 <p>Tổng chi tiêu</p>
//                 <h2>{formatMoney(summary.expense)}</h2>
//                 <span className="summary-note">
//                     Những gì bạn đã chi trả
//                 </span>
//             </article>

//             <article className="summary-card saving-card">
//                 <div className="finance-card-top">
//                     <span className="summary-icon">♡</span>
//                     <span className="finance-card-label">SAVINGS</span>
//                 </div>

//                 <p>Tổng tiết kiệm</p>
//                 <h2>{formatMoney(summary.saving)}</h2>
//                 <span className="summary-note">
//                     Những gì bạn dành cho tương lai
//                 </span>
//             </article>
//         </section>

//         <section className="transaction-panel">
//             <div className="transaction-panel-heading">
//                 <div>
//                     <span className="eyebrow">YOUR FINANCIAL JOURNAL</span>
//                     <h2>Lịch sử giao dịch</h2>
//                     <p className="panel-description">
//                         Mọi khoản thu chi được ghi lại trong một nơi.
//                     </p>
//                 </div>

//                 <span className="transaction-count">
//                     {filteredTransactions.length} giao dịch
//                 </span>
//             </div>

//             <TransactionFilter
//                 search={search}
//                 onSearchChange={setSearch}
//                 type={type}
//                 onTypeChange={setType}
//                 categoryId={categoryId}
//                 onCategoryChange={setCategoryId}
//                 categories={categories}
//             />

//             {loading ? (
//                 <div className="transaction-status">
//                     <div className="campus-spinner" />
//                     <p>Đang tải giao dịch của bạn...</p>
//                 </div>
//             ) : error ? (
//                 <div className="transaction-status error">
//                     <p>{error}</p>
//                     <button
//                         type="button"
//                         className="btn btn-secondary"
//                         onClick={loadData}
//                     >
//                         Thử lại
//                     </button>
//                 </div>
//             ) : (
//                 <TransactionTable
//                     transactions={filteredTransactions}
//                     onEdit={openEditModal}
//                     onDelete={handleDelete}
//                 />
//             )}
//         </section>

//         <TransactionModal
//             open={modalOpen}
//             transaction={selectedTransaction}
//             categories={categories}
//             onSubmit={handleSave}
//             onClose={() => {
//                 if (!saving) {
//                     setModalOpen(false);
//                     setSelectedTransaction(null);
//                 }
//             }}
//             saving={saving}
//         />
//     </div>
// );

import { useCallback, useEffect, useMemo, useState } from "react";

import { apiRequest } from "../api/apiClient";
import {
    getTransactions,
    createTransaction,
    updateTransaction,
    deleteTransaction,
} from "../api/transactionApi";

import TransactionFilter from "../components/transaction/TransactionFilter";
import TransactionTable from "../components/transaction/TransactionTable";
import TransactionModal from "../components/transaction/TransactionModal";

function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
        maximumFractionDigits: 0,
    }).format(Number(value) || 0);
}

function Transactions() {
    const [transactions, setTransactions] = useState([]);
    const [categories, setCategories] = useState([]);

    const [search, setSearch] = useState("");
    const [type, setType] = useState("ALL");
    const [categoryId, setCategoryId] = useState("ALL");

    const [modalOpen, setModalOpen] = useState(false);
    const [selectedTransaction, setSelectedTransaction] = useState(null);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadData = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const [transactionData, categoryData] = await Promise.all([
                getTransactions(),
                apiRequest("/categories"),
            ]);

            setTransactions(
                Array.isArray(transactionData) ? transactionData : []
            );

            setCategories(
                Array.isArray(categoryData) ? categoryData : []
            );
        } catch (err) {
            setError(err.message || "Không thể tải dữ liệu.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const filteredTransactions = useMemo(() => {
        const keyword = search.trim().toLowerCase();

        return transactions.filter((transaction) => {
            const matchesSearch =
                !keyword ||
                transaction.transactionName
                    ?.toLowerCase()
                    .includes(keyword) ||
                transaction.categoryName
                    ?.toLowerCase()
                    .includes(keyword) ||
                transaction.description
                    ?.toLowerCase()
                    .includes(keyword);

            const matchesType =
                type === "ALL" || transaction.type === type;

            const matchesCategory =
                categoryId === "ALL" ||
                String(transaction.categoryId) === categoryId;

            return (
                matchesSearch &&
                matchesType &&
                matchesCategory
            );
        });
    }, [transactions, search, type, categoryId]);

    const summary = useMemo(() => {
        return transactions.reduce(
            (result, transaction) => {
                const amount = Number(transaction.spendingMoney) || 0;

                if (transaction.type === "INCOME") {
                    result.income += amount;
                } else if (transaction.type === "EXPENSE") {
                    result.expense += amount;
                } else if (transaction.type === "SAVING") {
                    result.saving += amount;
                }

                return result;
            },
            { income: 0, expense: 0, saving: 0 }
        );
    }, [transactions]);

    function openCreateModal() {
        setSelectedTransaction(null);
        setModalOpen(true);
    }

    function openEditModal(transaction) {
        setSelectedTransaction(transaction);
        setModalOpen(true);
    }

    async function handleSave(formData) {
        setSaving(true);

        try {
            if (selectedTransaction) {
                await updateTransaction(
                    selectedTransaction.transactionId,
                    formData
                );
            } else {
                await createTransaction(formData);
            }

            setModalOpen(false);
            setSelectedTransaction(null);
            await loadData();
        } finally {
            setSaving(false);
        }
    }

    async function handleDelete(transaction) {
        const confirmed = window.confirm(
            `Bạn có chắc muốn xóa giao dịch "${transaction.transactionName}"?`
        );

        if (!confirmed) return;

        try {
            await deleteTransaction(transaction.transactionId);

            setTransactions((previous) =>
                previous.filter(
                    (item) =>
                        item.transactionId !== transaction.transactionId
                )
            );
        } catch (err) {
            window.alert(err.message || "Không thể xóa giao dịch.");
        }
    }

    return (
        <div className="transactions-page">
            <section className="page-hero transaction-hero">
                <div className="page-hero-content">
                    <span className="eyebrow">
                        YOUR MONEY, YOUR WAY
                    </span>
    
                    <h1>
                        Sổ giao dịch <span className="hero-sparkle">✦</span>
                    </h1>
    
                    <p>
                        Mỗi khoản thu chi là một bước nhỏ để bạn hiểu rõ
                        và chủ động hơn với tài chính của mình.
                    </p>
                </div>
    
                <button
                    type="button"
                    className="btn btn-primary add-transaction-btn"
                    onClick={openCreateModal}
                >
                    <span aria-hidden="true">＋</span>
                    Thêm giao dịch
                </button>
            </section>
    
            <section className="personal-note">
                <div className="personal-note-icon">✧</div>
    
                <div>
                    <strong>Một chút quan tâm dành cho chính bạn</strong>
                    <p>
                        Theo dõi hôm nay, lên kế hoạch cho ngày mai.
                        Từng bước nhỏ cũng tạo nên thay đổi lớn.
                    </p>
                </div>
            </section>
    
            <section className="transaction-summary">
                <article className="summary-card income-card">
                    <div className="finance-card-top">
                        <span className="summary-icon">↙</span>
                        <span className="finance-card-label">INCOME</span>
                    </div>
    
                    <p>Tổng thu nhập</p>
                    <h2>{formatMoney(summary.income)}</h2>
                    <span className="summary-note">
                        Những gì bạn đã nhận được
                    </span>
                </article>
    
                <article className="summary-card expense-card">
                    <div className="finance-card-top">
                        <span className="summary-icon">↗</span>
                        <span className="finance-card-label">EXPENSE</span>
                    </div>
    
                    <p>Tổng chi tiêu</p>
                    <h2>{formatMoney(summary.expense)}</h2>
                    <span className="summary-note">
                        Những gì bạn đã chi trả
                    </span>
                </article>
    
                <article className="summary-card saving-card">
                    <div className="finance-card-top">
                        <span className="summary-icon">♡</span>
                        <span className="finance-card-label">SAVINGS</span>
                    </div>
    
                    <p>Tổng tiết kiệm</p>
                    <h2>{formatMoney(summary.saving)}</h2>
                    <span className="summary-note">
                        Những gì bạn dành cho tương lai
                    </span>
                </article>
            </section>
    
            <section className="transaction-panel">
                <div className="transaction-panel-heading">
                    <div>
                        <span className="eyebrow">YOUR FINANCIAL JOURNAL</span>
                        <h2>Lịch sử giao dịch</h2>
                        <p className="panel-description">
                            Mọi khoản thu chi được ghi lại trong một nơi.
                        </p>
                    </div>
    
                    <span className="transaction-count">
                        {filteredTransactions.length} giao dịch
                    </span>
                </div>
    
                <TransactionFilter
                    search={search}
                    onSearchChange={setSearch}
                    type={type}
                    onTypeChange={setType}
                    categoryId={categoryId}
                    onCategoryChange={setCategoryId}
                    categories={categories}
                />
    
                {loading ? (
                    <div className="transaction-status">
                        <div className="campus-spinner" />
                        <p>Đang tải giao dịch của bạn...</p>
                    </div>
                ) : error ? (
                    <div className="transaction-status error">
                        <p>{error}</p>
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={loadData}
                        >
                            Thử lại
                        </button>
                    </div>
                ) : (
                    <TransactionTable
                        transactions={filteredTransactions}
                        onEdit={openEditModal}
                        onDelete={handleDelete}
                    />
                )}
            </section>
    
            <TransactionModal
                open={modalOpen}
                transaction={selectedTransaction}
                categories={categories}
                onSubmit={handleSave}
                onClose={() => {
                    if (!saving) {
                        setModalOpen(false);
                        setSelectedTransaction(null);
                    }
                }}
                saving={saving}
            />
        </div>
    );
}

export default Transactions;