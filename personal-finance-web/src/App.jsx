import {
    BrowserRouter,
    Routes,
    Route,
    Navigate,
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import Dashboard from "./pages/Dashboard";
import Transactions from "./pages/Transactions";
import Categories from "./pages/Categories";
import Budgets from "./pages/Budgets";

import AuthLayout from "./layouts/AuthLayout";
import MainLayout from "./layouts/MainLayout";

function App() {
    return (
        <BrowserRouter>
            <Routes>

                <Route
                    path="/"
                    element={
                        <Navigate
                            to="/login"
                            replace
                        />
                    }
                />

                <Route
                    path="/login"
                    element={
                        <AuthLayout>
                            <Login />
                        </AuthLayout>
                    }
                />

                <Route
                    path="/register"
                    element={
                        <AuthLayout>
                            <Register />
                        </AuthLayout>
                    }
                />

                <Route
                    path="/forgot-password"
                    element={
                        <AuthLayout>
                            <ForgotPassword />
                        </AuthLayout>
                    }
                />

                <Route
                    path="/dashboard"
                    element={
                        <MainLayout>
                            <Dashboard />
                        </MainLayout>
                    }
                />

                <Route
                    path="/transactions"
                    element={
                        <MainLayout>
                            <Transactions />
                        </MainLayout>
                    }
                />

                <Route 
                    path="/categories" 
                    element={
                        <MainLayout>
                            <Categories />
                        </MainLayout>
                    } />

                <Route 
                    path="/budgets" 
                    element={
                        <MainLayout>
                            <Budgets />
                        </MainLayout>
                    } />

            </Routes>
        </BrowserRouter>
    );
}

export default App;