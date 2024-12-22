import React from 'react';

interface DeleteNhanVienProps {
  onDelete: () => void;
}

const DeleteNhanVien: React.FC<DeleteNhanVienProps> = ({ onDelete }) => {
  const handleDeleteNhanVien = () => {
    if (window.confirm('Bạn có chắc muốn xóa nhân viên này?')) {
      onDelete();
      alert('Xóa nhân viên thành công!');
    }
  };

  return (
    <button
      className="bg-red-600 text-white py-3 px-6 rounded-lg w-full hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
      onClick={handleDeleteNhanVien}
    >
      Delete
    </button>
  );
};

export default DeleteNhanVien;
