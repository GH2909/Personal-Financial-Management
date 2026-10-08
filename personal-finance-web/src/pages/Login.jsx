import { useState } from "react";
import Button from "../components/common/Button";
import Input from "../components/common/Input";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    function handleSubmit(event) {
        event.preventDefault();

        console.log({
            email,
            password,
        });
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Đăng nhập</h1>

            <Input
                label="Email"
                name="email"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="Nhập email"
                required
            />

            <Input
                label="Mật khẩu"
                name="password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Nhập mật khẩu"
                required
            />

            <Button type="submit">
                Đăng nhập
            </Button>
        </form>
    );
}

export default Login;