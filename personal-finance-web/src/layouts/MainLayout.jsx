import Sidebar from "../components/layout/Sidebar";
import Header from "../components/layout/Header";

function MainLayout({ children }) {
    return (
        <div className="app-layout">
            <Sidebar />

            <div className="main-area">
                <Header />

                <main className="page-content">
                    {children}
                </main>
            </div>
        </div>
    );
}

export default MainLayout;