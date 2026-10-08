function Input({
    label,
    type = "text",
    name,
    value,
    onChange,
    placeholder = "",
    required = false,
    error = "",
}) {
    return (
        <div className="form-group">
            {label && (
                <label htmlFor={name} className="form-label">
                    {label}
                </label>
            )}

            <input
                id={name}
                name={name}
                type={type}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                required={required}
                className={`form-input ${error ? "input-error" : ""}`}
            />

            {error && (
                <p className="form-error">{error}</p>
            )}
        </div>
    );
}

export default Input;