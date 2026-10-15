import CategoryForm from "./CategoryForm";

function CategoryModal({
    open,
    category,
    onSubmit,
    onClose,
    saving,
}) {
    if (!open) return null;

    return (
        <div
            className="category-modal-overlay"
            onMouseDown={(event) => {
                if (event.target === event.currentTarget && !saving) {
                    onClose();
                }
            }}
        >
            <section
                className="category-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="category-modal-title"
            >
                <div className="category-modal-heading">
                    <div>
                        <p className="category-eyebrow">PERSONAL FINANCE MANAGER</p>
                        <h2 id="category-modal-title">
                            {category ? "Chỉnh sửa danh mục" : "Danh mục mới"}
                        </h2>
                    </div>

                    <button
                        type="button"
                        className="category-close-btn"
                        onClick={onClose}
                        disabled={saving}
                        aria-label="Đóng"
                    >
                        ×
                    </button>
                </div>

                <CategoryForm
                    category={category}
                    onSubmit={onSubmit}
                    onCancel={onClose}
                    saving={saving}
                />
            </section>
        </div>
    );
}

export default CategoryModal;