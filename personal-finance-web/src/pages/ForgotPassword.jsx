import { useState } from "react";
import { Link } from "react-router-dom";

import Button from "../components/common/Button";
import Input from "../components/common/Input";

function ForgotPassword() {
    const [email, setEmail] = useState("");

    function handleSubmit(event) {
        event.preventDefault();

        console.log({
            email,
        });
    }

    return (
        <div className="auth-form">
            <div className="auth-form-header">
                <h2>Forgot password?</h2>
                <p>
                    Nhập email của bạn để nhận hướng dẫn
                    đặt lại mật khẩu.
                </p>
            </div>

            <form onSubmit={handleSubmit}>
                <Input
                    label="Email"
                    name="email"
                    type="email"
                    value={email}
                    onChange={(event) =>
                        setEmail(event.target.value)
                    }
                    placeholder="Nhập email của bạn"
                    required
                />

                <Button type="submit">
                    Gửi yêu cầu
                </Button>
            </form>

            <div className="auth-form-footer">
                <Link to="/login">
                    ← Quay lại đăng nhập
                </Link>
            </div>
        </div>
    );
}

export default ForgotPassword;