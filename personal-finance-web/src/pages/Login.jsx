import { useState } from "react";
import { useNavigate } from "react-router-dom";

import Button from "../components/common/Button";
import Input from "../components/common/Input";

import { authApi } from "../api/authApi";
import { saveToken } from "../api/authService";

function Login() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();

        setError("");
        setLoading(true);

        try {
            const response = await authApi.login({
                email,
                password,
            });

            console.log("Login response:", response);

            saveToken(response.token);

            navigate("/dashboard");
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Đăng nhập</h1>

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

            {error && (
                <p className="form-error">
                    {error}
                </p>
            )}

            <Button
                type="submit"
                disabled={loading}
            >
                {loading ? "Đang đăng nhập..." : "Đăng nhập"}
            </Button>
        </form>
    );
}

export default Login;