import React, { useEffect, useState } from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';

type Props = {
  handleLogout: () => void;
};

type User = {
  info: string;
};

const Navbar = ({ handleLogout }: Props) => {
  const [user, setUser] = useState<User | null>(null);
  const location = useLocation();
  const [active, setActive] = useState(location.pathname);
  const navigate = useNavigate();

  useEffect(() => {
    const userData = sessionStorage.getItem("user");
    if (userData) {
      try {
        setUser(JSON.parse(userData));
      } catch (error) {
        console.error("Failed to parse user data:", error);
      }
    }
    setActive(location.pathname);
  }, [location]);

  const logout = () => {
    sessionStorage.removeItem("user");
    setUser(null);
    navigate('/');
  };

  return (
    <nav className="bg-[#121e31] h-screen top-0 left-0 min-w-[250px] py-6 px-4 font-[sans-serif] tracking-wide overflow-auto">
      <div className="flex flex-wrap items-center gap-4 cursor-pointer">
        <p className="px-4 py-2 bg-white text-sm rounded-full font-bold text-gray-700 border-2 border-[#007bff]">
          {user?.info}
        </p>
      </div>
      <hr className="my-6 border-gray-400" />
      <ul className="space-y-3">
        <li>
          <NavLink
            to="/admin/dashboard"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/dashboard' ? 'bg-gray-700' : ''
              }`}
          >
            <DashboardIcon />
            <span>Danh sách sản phẩm</span>
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/admin/thongke"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/thongke' ? 'bg-gray-700' : ''
              }`}
          >
            <DashboardIcon />
            <span>Thống kê</span>
          </NavLink>
        </li>
        
        <li>
          <NavLink
            to="/admin/transaction"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/transaction' ? 'bg-gray-700' : ''
              }`}
          >
            <DashboardIcon />
            <span>Giao dịch</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/admin/category"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/category' ? 'bg-gray-700' : ''
              }`}
          >
            <CategoryIcon />
            <span>Loại sản phẩm</span>
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/admin/add"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/add' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span>Thêm sản phẩm</span>
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/admin/donhang"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/donhang' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span> Đơn hàng</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/admin/hoanHang"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/hoanHang' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span> Hoàn hàng</span>
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/admin/tro_chuyen"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/tro_chuyen' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span>Chăm sóc khách hàng</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/admin/voucher"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/voucher' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span>Mã giảm giá</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/admin/feedback"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/feedback' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span>Đánh giá</span>
          </NavLink>
        </li>

        <li>
          <NavLink
            to="/admin/news"
            className={`text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all ${active === '/admin/news' ? 'bg-gray-700' : ''
              }`}
          >
            <AddIcon />
            <span>Tin tức</span>
          </NavLink>
        </li>


        <li>
          <button
            onClick={logout}
            className="text-white text-sm flex items-center hover:bg-gray-700 rounded px-4 py-3 transition-all"
          >
            <LogoutIcon />
            <span>Đăng xuất</span>
          </button>
        </li>
      </ul>
    </nav>
  );
};

export default Navbar;

// SVG Icon Components
const DashboardIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="w-[18px] h-[18px] mr-4" viewBox="0 0 512 512">
    {/* Add SVG path here for Dashboard icon */}
    <path d="M…" />
  </svg>
);

const CategoryIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="w-[18px] h-[18px] mr-4" viewBox="0 0 511.414 511.414">
    {/* Add SVG path here for Category icon */}
    <path d="M…" />
  </svg>
);

const AddIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="w-[18px] h-[18px] mr-4" viewBox="0 0 511.414 511.414">
    {/* Add SVG path here for Add icon */}
    <path d="M…" />
  </svg>
);

const LogoutIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="w-[18px] h-[18px] mr-4" viewBox="0 0 193.769 193.769">
    {/* Add SVG path here for Logout icon */}
    <path d="M…" />
  </svg>
);
