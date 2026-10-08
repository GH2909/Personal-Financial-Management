import { useState } from "react";
import { Link } from "react-router-dom";

import Button from "../components/common/Button";
import Input from "../components/common/Input";

function Register() {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    function handleSubmit(event) {
        event.preventDefault();

        console.log({
            fullName,
            email,
            password,
            confirmPassword,
        });
    }

    return (
        <div className="auth-form">
            <div className="auth-form-header">
                <h2>Create an account</h2>
                <p>
                    Tạo tài khoản để bắt đầu quản lý tài chính.
                </p>
            </div>

            <form onSubmit={handleSubmit}>
                <Input
                    label="Họ và tên"
                    name="fullName"
                    value={fullName}
                    onChange={(event) =>
                        setFullName(event.target.value)
                    }
                    placeholder="Nhập họ và tên"
                    required
                />

                <Input
                    label="Email"
                    name="email"
                    type="email"
                    value={email}
                    onChange={(event) =>
                        setEmail(event.target.value)
                    }
                    placeholder="Nhập email"
                    required
                />

                <Input
                    label="Mật khẩu"
                    name="password"
                    type="password"
                    value={password}
                    onChange={(event) =>
                        setPassword(event.target.value)
                    }
                    placeholder="Nhập mật khẩu"
                    required
                />

                <Input
                    label="Xác nhận mật khẩu"
                    name="confirmPassword"
                    type="password"
                    value={confirmPassword}
                    onChange={(event) =>
                        setConfirmPassword(event.target.value)
                    }
                    placeholder="Nhập lại mật khẩu"
                    required
                />

                <Button type="submit">
                    Đăng ký
                </Button>
            </form>

            <div className="auth-form-footer">
                <p>
                    Đã có tài khoản?{" "}
                    <Link to="/login">
                        Đăng nhập
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default Register;