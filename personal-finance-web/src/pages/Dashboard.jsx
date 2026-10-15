
import BalanceCard from "../components/dashboard/BalanceCard";
import IncomeCard from "../components/dashboard/IncomeCard";
import ExpenseCard from "../components/dashboard/ExpenseCard";
import BudgetProgress from "../components/dashboard/BudgetProgress";
import SavingSummary from "../components/dashboard/SavingSummary";
import RecentTransactions from "../components/dashboard/RecentTransactions";

function Dashboard() {
    // Dữ liệu giả để dựng giao diện.
    // Chưa phải dữ liệu tài khoản thực tế.
    const balance = 24500000;
    const income = 18000000;
    const expense = 7500000;

    const transactions = [
        {
            id: 1,
            title: "Monthly salary",
            category: "Salary",
            date: "12 Oct 2026",
            amount: 15000000,                
            type: "INCOME",
        },
        {
            id: 2,
            title: "Coffee with friends",
            category: "Food & Drink",
            date: "11 Oct 2026",
            amount: 85000,
            type: "EXPENSE",
        },
        {
            id: 3,
            title: "Monthly savings",
            category: "Savings",
            date: "10 Oct 2026",
            amount: 2000000,
            type: "EXPENSE",
        },
    ];

    return (
        <div className="dashboard">
            <div className="dashboard-welcome page-hero">
                <div className="page-hero-content">
                    <span className="eyebrow">
                        YOUR PERSONAL FINANCE SPACE
                    </span>
            
                    <h1>
                        Chào bạn, hôm nay thế nào? <span>✦</span>
                    </h1>
            
                    <p>
                        Hiểu rõ dòng tiền của mình hôm nay để tự tin hơn
                        với những kế hoạch ngày mai.
                    </p>
                </div>
            
                    <div className="welcome-decoration" aria-hidden="true">
                        <span>✧</span>
                        <span>♡</span>
                        <span>✦</span>
                    </div>
                </div>

            <BalanceCard balance={balance} />

            <div className="summary-grid">
                <IncomeCard income={income} />
                <ExpenseCard expense={expense} />
            </div>

            <div className="dashboard-grid">
                <BudgetProgress
                    spent={7500000}
                    budget={12000000}
                />

                <SavingSummary
                    saved={6000000}
                    target={15000000}
                />
            </div>

            <RecentTransactions transactions={transactions} />
        </div>
    );
}

export default Dashboard;