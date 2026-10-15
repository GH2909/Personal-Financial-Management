import { useEffect, useRef, useState } from "react";

function CustomSelect({
    value,
    onChange,
    options,
    placeholder = "Vui lòng chọn",
    searchable = false,
}) {
    const [open, setOpen] = useState(false);
    const [search, setSearch] = useState("");
    const wrapperRef = useRef(null);

    const selectedOption = options.find(
        (option) => String(option.value) === String(value)
    );

    const filteredOptions = options.filter((option) =>
        option.label.toLowerCase().includes(search.toLowerCase())
    );

    useEffect(() => {
        function handleOutsideClick(event) {
            if (
                wrapperRef.current &&
                !wrapperRef.current.contains(event.target)
            ) {
                setOpen(false);
                setSearch("");
            }
        }

        function handleKeyDown(event) {
            if (event.key === "Escape") {
                setOpen(false);
                setSearch("");
            }
        }

        document.addEventListener("mousedown", handleOutsideClick);
        document.addEventListener("keydown", handleKeyDown);

        return () => {
            document.removeEventListener("mousedown", handleOutsideClick);
            document.removeEventListener("keydown", handleKeyDown);
        };
    }, []);

    function handleSelect(option) {
        onChange(String(option.value));
        setOpen(false);
        setSearch("");
    }

    return (
        <div
            className={`custom-select ${open ? "is-open" : ""}`}
            ref={wrapperRef}
        >
            <button
                type="button"
                className={`custom-select-trigger ${
                    selectedOption ? "has-value" : ""
                }`}
                onClick={() => setOpen((previous) => !previous)}
                aria-haspopup="listbox"
                aria-expanded={open}
            >
                <span className="custom-select-value">
                    {selectedOption?.label || placeholder}
                </span>

                <span className="custom-select-arrow" aria-hidden="true">
                    <svg
                        viewBox="0 0 20 20"
                        fill="none"
                        width="18"
                        height="18"
                    >
                        <path
                            d="m5.5 7.5 4.5 4.5 4.5-4.5"
                            stroke="currentColor"
                            strokeWidth="1.7"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        />
                    </svg>
                </span>
            </button>

            {open && (
                <div className="custom-select-menu">
                    {searchable && (
                        <div className="custom-select-search">
                            <span aria-hidden="true">⌕</span>

                            <input
                                autoFocus
                                value={search}
                                onChange={(event) =>
                                    setSearch(event.target.value)
                                }
                                placeholder="Tìm danh mục..."
                                aria-label="Tìm lựa chọn"
                            />
                        </div>
                    )}

                    <div
                        className="custom-select-options"
                        role="listbox"
                    >
                        {filteredOptions.length > 0 ? (
                            filteredOptions.map((option) => {
                                const isSelected =
                                    String(option.value) === String(value);

                                return (
                                    <button
                                        key={option.value}
                                        type="button"
                                        role="option"
                                        aria-selected={isSelected}
                                        className={`custom-select-option ${
                                            isSelected ? "selected" : ""
                                        }`}
                                        onClick={() => handleSelect(option)}
                                    >
                                        <span>{option.label}</span>

                                        {isSelected && (
                                            <svg
                                                viewBox="0 0 20 20"
                                                fill="none"
                                                width="17"
                                                height="17"
                                                aria-hidden="true"
                                            >
                                                <path
                                                    d="m4 10 4 4 8-8"
                                                    stroke="currentColor"
                                                    strokeWidth="2"
                                                    strokeLinecap="round"
                                                    strokeLinejoin="round"
                                                />
                                            </svg>
                                        )}
                                    </button>
                                );
                            })
                        ) : (
                            <p className="custom-select-empty">
                                Không tìm thấy lựa chọn
                            </p>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default CustomSelect;