const TYPE_LABELS = {
    INCOME: "Thu nhập",
    EXPENSE: "Chi tiêu",
    SAVING: "Tiết kiệm",
};

function CategoryList({ categories, onEdit, onDelete }) {
    if (categories.length === 0) {
        return (
            <div className="category-empty">
                <span className="category-empty-icon">✦</span>
                <h3>Chưa có danh mục</h3>
                <p>Thêm danh mục để phân loại các giao dịch của bạn.</p>
            </div>
        );
    }

    return (
        <div className="category-grid">
            {categories.map((category) => (
                <article className="category-card" key={category.categoryId}>
                    <div className="category-card-top">
                        <span className="category-icon">
                            {category.icon || "✦"}
                        </span>

                        <span className={`category-type type-${category.type?.toLowerCase()}`}>
                            {TYPE_LABELS[category.type] || category.type}
                        </span>
                    </div>

                    <h3>{category.categoryName}</h3>

                    <div className="category-card-actions">
                        <button
                            type="button"
                            className="category-btn category-btn-secondary"
                            onClick={() => onEdit(category)}
                        >
                            Chỉnh sửa
                        </button>

                        <button
                            type="button"
                            className="category-btn category-btn-danger"
                            onClick={() => onDelete(category)}
                        >
                            Xóa
                        </button>
                    </div>
                </article>
            ))}
        </div>
    );
}

export default CategoryList;