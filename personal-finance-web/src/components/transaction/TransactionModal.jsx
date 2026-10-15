import { useEffect } from "react";
import TransactionForm from "./TransactionForm";

function TransactionModal({
    open,
    transaction,
    categories,
    onSubmit,
    onClose,
    saving,
}) {
    useEffect(() => {
        if (!open) return;

        function handleKeyDown(event) {
            if (event.key === "Escape" && !saving) {
                onClose();
            }
        }

        window.addEventListener("keydown", handleKeyDown);

        return () => {
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, [open, saving, onClose]);

    if (!open) return null;

    return (
        <div
            className="transaction-modal-backdrop"
            onMouseDown={(event) => {
                if (
                    event.target === event.currentTarget &&
                    !saving
                ) {
                    onClose();
                }
            }}
        >
            <section
                className="transaction-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="transaction-modal-title"
            >
                <button
                    type="button"
                    className="transaction-modal-close"
                    onClick={onClose}
                    disabled={saving}
                    aria-label="Đóng"
                >
                    ×
                </button>

                <div id="transaction-modal-title">
                    <TransactionForm
                        key={transaction?.transactionId ?? "new"}
                        transaction={transaction}
                        categories={categories}
                        onSubmit={onSubmit}
                        onCancel={onClose}
                        saving={saving}
                    />
                </div>
            </section>
        </div>
    );
}

export default TransactionModal;