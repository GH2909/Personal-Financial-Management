import Button from "./Button";
import Modal from "./Modal";

function ConfirmDialog({
    isOpen,
    title = "Xác nhận thao tác",
    message = "Bạn có chắc chắn muốn thực hiện thao tác này?",
    confirmText = "Xác nhận",
    cancelText = "Hủy",
    onConfirm,
    onCancel,
    isLoading = false,
}) {
    function handleConfirm() {
        if (!isLoading) {
            onConfirm();
        }
    }

    return (
        <Modal
            isOpen={isOpen}
            title={title}
            onClose={onCancel}
        >
            <p className="confirm-message">{message}</p>

            <div className="confirm-actions">
                <Button
                    variant="secondary"
                    onClick={onCancel}
                    disabled={isLoading}
                >
                    {cancelText}
                </Button>

                <Button
                    variant="danger"
                    onClick={handleConfirm}
                    disabled={isLoading}
                >
                    {isLoading ? "Đang xử lý..." : confirmText}
                </Button>
            </div>
        </Modal>
    );
}

export default ConfirmDialog;