import { useEffect, useState } from "react";

const INITIAL_FORM = {
    categoryName: "",
    type: "EXPENSE",
    icon: "",
};

function CategoryForm({ category, onSubmit, onCancel, saving }) {
    const [form, setForm] = useState(INITIAL_FORM);
    const [error, setError] = useState("");

    useEffect(() => {
        setForm(
            category
                ? {
                      categoryName: category.categoryName || "",
                      type: category.type || "EXPENSE",
                      icon: category.icon || "",
                  }
                : INITIAL_FORM
        );
        setError("");
    }, [category]);

    function handleChange(event) {
        const { name, value } = event.target;
        setForm((previous) => ({ ...previous, [name]: value }));
    }

    async function handleSubmit(event) {
        event.preventDefault();

        if (!form.categoryName.trim()) {
            setError("Vui lòng nhập tên danh mục.");
            return;
        }

        setError("");

        try {
            await onSubmit({
                categoryName: form.categoryName.trim(),
                type: form.type,
                icon: form.icon.trim() || null,
            });
        } catch (err) {
            setError(err.message || "Không thể lưu danh mục.");
        }
    }

    return (
        <form className="category-form" onSubmit={handleSubmit}>
            <div className="category-field">
                <label htmlFor="categoryName">Tên danh mục</label>
                <input
                    id="categoryName"
                    name="categoryName"
                    value={form.categoryName}
                    onChange={handleChange}
                    maxLength={100}
                    placeholder="Ví dụ: Ăn uống"
                    required
                />
            </div>

            <div className="category-field">
                <label htmlFor="categoryType">Loại danh mục</label>
                <select
                    id="categoryType"
                    name="type"
                    value={form.type}
                    onChange={handleChange}
                    required
                >
                    <option value="INCOME">Thu nhập</option>
                    <option value="EXPENSE">Chi tiêu</option>
                    <option value="SAVING">Tiết kiệm</option>
                </select>
            </div>

            <div className="category-field">
                <label htmlFor="categoryIcon">Biểu tượng (không bắt buộc)</label>
                <input
                    id="categoryIcon"
                    name="icon"
                    value={form.icon}
                    onChange={handleChange}
                    placeholder="Ví dụ: 🍜"
                    maxLength={30}
                />
            </div>

            {error && <p className="category-form-error">{error}</p>}

            <div className="category-form-actions">
                <button
                    type="button"
                    className="category-btn category-btn-secondary"
                    onClick={onCancel}
                    disabled={saving}
                >
                    Hủy
                </button>

                <button
                    type="submit"
                    className="category-btn category-btn-primary"
                    disabled={saving}
                >
                    {saving ? "Đang lưu..." : category ? "Lưu thay đổi" : "Thêm danh mục"}
                </button>
            </div>
        </form>
    );
}

export default CategoryForm;