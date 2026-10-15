import { useCallback, useEffect, useMemo, useState } from "react";

import {
    getCategories,
    createCategory,
    updateCategory,
    deleteCategory,
} from "../api/categoryApi";

import CategoryList from "../components/category/CategoryList";
import CategoryModal from "../components/category/CategoryModal";

const FILTER_OPTIONS = [
    { value: "ALL", label: "Tất cả" },
    { value: "INCOME", label: "Thu nhập" },
    { value: "EXPENSE", label: "Chi tiêu" },
    { value: "SAVING", label: "Tiết kiệm" },
];

function Categories() {
    const [categories, setCategories] = useState([]);
    const [filter, setFilter] = useState("ALL");
    const [search, setSearch] = useState("");

    const [modalOpen, setModalOpen] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState(null);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadCategories = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const data = await getCategories();
            setCategories(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message || "Không thể tải danh mục.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadCategories();
    }, [loadCategories]);

    const filteredCategories = useMemo(() => {
        const keyword = search.trim().toLowerCase();

        return categories.filter((category) => {
            const matchesType = filter === "ALL" || category.type === filter;
            const matchesSearch = category.categoryName
                ?.toLowerCase()
                .includes(keyword);

            return matchesType && matchesSearch;
        });
    }, [categories, filter, search]);

    function openCreateModal() {
        setSelectedCategory(null);
        setModalOpen(true);
    }

    function openEditModal(category) {
        setSelectedCategory(category);
        setModalOpen(true);
    }

    async function handleSave(formData) {
        setSaving(true);

        try {
            if (selectedCategory) {
                await updateCategory(
                    selectedCategory.categoryId,
                    formData
                );
            } else {
                await createCategory(formData);
            }

            setModalOpen(false);
            setSelectedCategory(null);
            await loadCategories();
        } finally {
            setSaving(false);
        }
    }

    async function handleDelete(category) {
        const confirmed = window.confirm(
            `Bạn có chắc muốn xóa danh mục "${category.categoryName}"?`
        );

        if (!confirmed) return;

        try {
            await deleteCategory(category.categoryId);
            setCategories((previous) =>
                previous.filter(
                    (item) => item.categoryId !== category.categoryId
                )
            );
        } catch (err) {
            window.alert(err.message || "Không thể xóa danh mục.");
        }
    }

    const incomeCount = categories.filter((item) => item.type === "INCOME").length;
    const expenseCount = categories.filter((item) => item.type === "EXPENSE").length;
    const savingCount = categories.filter((item) => item.type === "SAVING").length;

    return (
        <div className="categories-page">
            <header className="categories-heading">
                <div>
                    <p className="category-eyebrow">YOUR PERSONAL FINANCE</p>
                    <h1>Danh mục</h1>
                    <p>Phân loại thu nhập, chi tiêu và các khoản tiết kiệm.</p>
                </div>

                <button
                    type="button"
                    className="category-btn category-btn-primary"
                    onClick={openCreateModal}
                >
                    <span>＋</span> Thêm danh mục
                </button>
            </header>

            <div className="category-stats">
                <article className="category-stat">
                    <span className="category-stat-icon">✦</span>
                    <p>Tổng danh mục</p>
                    <strong>{categories.length}</strong>
                </article>

                <article className="category-stat category-stat-income">
                    <span className="category-stat-icon">↙</span>
                    <p>Thu nhập</p>
                    <strong>{incomeCount}</strong>
                </article>

                <article className="category-stat category-stat-expense">
                    <span className="category-stat-icon">↗</span>
                    <p>Chi tiêu</p>
                    <strong>{expenseCount}</strong>
                </article>

                <article className="category-stat category-stat-saving">
                    <span className="category-stat-icon">♡</span>
                    <p>Tiết kiệm</p>
                    <strong>{savingCount}</strong>
                </article>
            </div>

            <section className="category-panel">
                <div className="category-panel-heading">
                    <div>
                        <h2>Danh mục của bạn</h2>
                        <p>Quản lý các nhóm dùng trong sổ giao dịch.</p>
                    </div>

                    <span className="category-count">
                        {filteredCategories.length} danh mục
                    </span>
                </div>

                <div className="category-toolbar">
                    <input
                        type="search"
                        value={search}
                        onChange={(event) => setSearch(event.target.value)}
                        placeholder="Tìm danh mục..."
                        aria-label="Tìm danh mục"
                    />

                    <div className="category-filter-tabs">
                        {FILTER_OPTIONS.map((option) => (
                            <button
                                key={option.value}
                                type="button"
                                className={filter === option.value ? "active" : ""}
                                onClick={() => setFilter(option.value)}
                            >
                                {option.label}
                            </button>
                        ))}
                    </div>
                </div>

                {loading ? (
                    <div className="category-state">Đang tải danh mục...</div>
                ) : error ? (
                    <div className="category-state category-state-error">
                        <p>{error}</p>
                        <button
                            type="button"
                            className="category-btn category-btn-secondary"
                            onClick={loadCategories}
                        >
                            Thử lại
                        </button>
                    </div>
                ) : (
                    <CategoryList
                        categories={filteredCategories}
                        onEdit={openEditModal}
                        onDelete={handleDelete}
                    />
                )}
            </section>

            <CategoryModal
                open={modalOpen}
                category={selectedCategory}
                onSubmit={handleSave}
                onClose={() => {
                    if (!saving) {
                        setModalOpen(false);
                        setSelectedCategory(null);
                    }
                }}
                saving={saving}
            />
        </div>
    );
}

export default Categories;