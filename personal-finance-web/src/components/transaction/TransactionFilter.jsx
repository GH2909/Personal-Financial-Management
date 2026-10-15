import CustomSelect from "./CustomSelect";

function TransactionFilter({
    search,
    onSearchChange,
    type,
    onTypeChange,
    categoryId,
    onCategoryChange,
    categories,
}) {
    const typeOptions = [
        { value: "ALL", label: "Tất cả loại" },
        { value: "INCOME", label: "Thu nhập" },
        { value: "EXPENSE", label: "Chi tiêu" },
        { value: "SAVING", label: "Tiết kiệm" },
    ];

    const categoryOptions = [
        { value: "ALL", label: "Tất cả danh mục" },
        ...categories.map((category) => ({
            value: String(category.categoryId),
            label: category.categoryName,
        })),
    ];

    return (
        <div className="transaction-filters">
            <div className="transaction-search">
                <span>⌕</span>

                <input
                    type="text"
                    value={search}
                    onChange={(event) =>
                        onSearchChange(event.target.value)
                    }
                    placeholder="Tìm giao dịch..."
                />
            </div>

            <CustomSelect
                value={type}
                onChange={onTypeChange}
                options={typeOptions}
            />

            <CustomSelect
                value={categoryId}
                onChange={onCategoryChange}
                options={categoryOptions}
                searchable
            />
        </div>
    );
}

export default TransactionFilter;