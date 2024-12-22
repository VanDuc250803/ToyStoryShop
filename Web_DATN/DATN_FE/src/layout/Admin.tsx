import React from 'react';
import Navbar from '../components/admin/Navbar';
import { Navigate, Outlet, useNavigate } from 'react-router-dom';

type Props = {};

const Admin = (props: Props) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    const mess = window.confirm('Bạn chắc chắn muốn logout?');
    if (mess) {
      try {
        sessionStorage.removeItem('user');
        navigate('/');
      } catch (error) {
        console.log(error);
      }
    }
  };

  return (
    <div className="flex h-screen">
      <div className="sticky top-0 h-full bg-gray-800 text-white">
      <Navbar handleLogout={handleLogout}  />
      </div>
      
      <div className="flex-1 overflow-y-auto p-6">
        <Outlet />
      </div>
    </div>
  );
};

export default Admin;
