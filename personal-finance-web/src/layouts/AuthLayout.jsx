function AuthLayout({ children }) {
    return (
        <div className="auth-layout">
            <div className="auth-brand">
                <div className="auth-logo">GH</div>
                <h1>Personal Finance Manager</h1>
                <p>Quản lý tài chính, làm chủ tương lai.</p>
            </div>

            <div className="auth-content">
                {children}
            </div>
        </div>
    );
}

export default AuthLayout;