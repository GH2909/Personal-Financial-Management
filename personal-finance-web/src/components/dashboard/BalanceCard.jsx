function BalanceCard({ balance }) {
    return (
        <section className="balance-card">
            <div className="balance-card-top">
                <div>
                    <p className="dashboard-eyebrow">YOUR MONEY UNIVERSE ✦</p>
                    <p className="balance-label">Total balance</p>
                </div>

                <span className="balance-symbol">✧</span>
            </div>

            <h2 className="balance-amount">
                {balance.toLocaleString("vi-VN")} ₫
            </h2>

            <p className="balance-note">
                Small steps, big dreams.
            </p>
        </section>
    );
}

export default BalanceCard;