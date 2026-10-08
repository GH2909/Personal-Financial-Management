import { Link, useLocation } from "react-router-dom";
import {
    LayoutDashboard,
    ArrowLeftRight,
    Tags,
    Wallet,
    PiggyBank,
    ChartPie,
    UserRound,
} from "lucide-react";

const menuItems = [
    {
        label: "Dashboard",
        path: "/dashboard",
        icon: LayoutDashboard,
    },
    {
        label: "Transactions",
        path: "/transactions",
        icon: ArrowLeftRight,
    },
    {
        label: "Categories",
        path: "/categories",
        icon: Tags,
    },
    {
        label: "Budgets",
        path: "/budgets",
        icon: Wallet,
    },
    {
        label: "Savings",
        path: "/savings",
        icon: PiggyBank,
    },
    {
        label: "Reports",
        path: "/reports",
        icon: ChartPie,
    },
    {
        label: "Profile",
        path: "/profile",
        icon: UserRound,
    },
];

function Sidebar() {
    const location = useLocation();

    return (
        <aside className="sidebar">
            <div className="sidebar-brand">
                <div className="brand-logo">GH</div>

                <div>
                    <h2>Personal Finance</h2>
                    <p>Manager</p>
                </div>
            </div>

            {/* <p className="sidebar-label">MENU</p> */}

            <nav className="sidebar-nav">
                {menuItems.map((item) => {
                    const isActive =
                        location.pathname === item.path;

                    const Icon = item.icon;

                    return (
                        <Link
                            key={item.path}
                            to={item.path}
                            className={
                                isActive
                                    ? "sidebar-link active"
                                    : "sidebar-link"
                            }
                        >
                            <span className="menu-icon">
                                <Icon size={21} strokeWidth={1.8} />
                            </span>

                            <span>{item.label}</span>
                        </Link>
                    );
                })}
            </nav>

            <div className="sidebar-footer">
                Personal Finance Manager
                <p>Manage your money better.</p>
            </div>
        </aside>
    );
}

export default Sidebar;