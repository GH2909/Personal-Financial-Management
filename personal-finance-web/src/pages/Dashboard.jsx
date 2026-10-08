import { useState } from "react";
import Button from "../components/common/Button";
import Modal from "../components/common/Modal";

function Dashboard() {
    const [isModalOpen, setIsModalOpen] = useState(false);

    return (
        <div>
            <h1>Dashboard</h1>

            <Button onClick={() => setIsModalOpen(true)}>
                Thêm giao dịch
            </Button>

            <Modal
                isOpen={isModalOpen}
                title="Thêm giao dịch"
                onClose={() => setIsModalOpen(false)}
            >
                <p>Form thêm giao dịch sẽ được xây dựng ở giai đoạn sau.</p>

                <Button
                    variant="secondary"
                    onClick={() => setIsModalOpen(false)}
                >
                    Đóng
                </Button>
            </Modal>
        </div>
    );
}

export default Dashboard;