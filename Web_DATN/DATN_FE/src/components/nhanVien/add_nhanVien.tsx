import React, { useState } from "react";
import { EyeIcon, EyeOffIcon } from "@heroicons/react/solid";

interface NhanVien {
  maNhanVien: string;
  hoTen: string;
  soDienThoai: string;
  ngaySinh: string;
  matKhau: string;
}

interface AddNhanVienProps {
  onAdd: (nhanVien: NhanVien) => void;
}

const AddNhanVien: React.FC<{ onAdd: (nv: NhanVien) => void }> = ({ onAdd }) => {
  const [maNhanVien, setMaNhanVien] = useState("");
  const [hoTen, setHoTen] = useState("");
  const [soDienThoai, setSoDienThoai] = useState("");
  const [ngaySinh, setNgaySinh] = useState("");
  const [matKhau, setMatKhau] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const handleSubmit = () => {
    const newErrors: { [key: string]: string } = {};

    // Validate Mã Nhân Viên
    if (maNhanVien.trim() === "") {
      newErrors.maNhanVien = "Mã nhân viên không được để trống.";
    } else if (maNhanVien.length < 4 || maNhanVien.length > 10) {
      newErrors.maNhanVien = "Mã nhân viên phải từ 4-10 ký tự.";
    }

    // Validate Họ và Tên
    if (hoTen.length < 6) {
      newErrors.hoTen = "Họ và tên phải có ít nhất 6 ký tự.";
    }

    // Validate Số Điện Thoại
    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(soDienThoai)) {
      newErrors.soDienThoai = "Số điện thoại phải là 10 chữ số và không có ký tự khác.";
    }

    // Validate Ngày Sinh
    if (ngaySinh.trim() === "") {
      newErrors.ngaySinh = "Ngày sinh không được để trống.";
    } else {
      const selectedDate = new Date(ngaySinh);
      const today = new Date();
      if (selectedDate > today) {
        newErrors.ngaySinh = "Ngày sinh không thể lớn hơn ngày hiện tại.";
      }
    }

    // Validate Mật Khẩu
    if (matKhau.trim() === "") {
      newErrors.matKhau = "Mật khẩu không được để trống.";
    }

    // If there are any errors, do not submit the form
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // If no errors, submit the form
    onAdd({
      maNhanVien,
      hoTen,
      soDienThoai,
      ngaySinh,
      matKhau,
    });

    // Clear errors and reset fields after successful submission
    setErrors({});
    setMaNhanVien("");
    setHoTen("");
    setSoDienThoai("");
    setNgaySinh("");
    setMatKhau("");
  };

  return (
    <form className="grid grid-cols-2 gap-6">
      {/* Mã Nhân Viên */}
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">
          Mã Nhân Viên
        </label>
        <input
          type="text"
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
          value={maNhanVien}
          onChange={(e) => setMaNhanVien(e.target.value)}
        />
        {errors.maNhanVien && <p className="text-red-500 text-sm">{errors.maNhanVien}</p>}
      </div>

      {/* Họ và Tên */}
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">
          Họ và Tên
        </label>
        <input
          type="text"
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
          value={hoTen}
          onChange={(e) => setHoTen(e.target.value)}
        />
        {errors.hoTen && <p className="text-red-500 text-sm">{errors.hoTen}</p>}
      </div>

      {/* Số Điện Thoại */}
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">
          Số Điện Thoại
        </label>
        <input
          type="text"
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
          value={soDienThoai}
          onChange={(e) => setSoDienThoai(e.target.value)}
        />
        {errors.soDienThoai && <p className="text-red-500 text-sm">{errors.soDienThoai}</p>}
      </div>

      {/* Ngày Sinh */}
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">
          Ngày Sinh
        </label>
        <input
          type="date"
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
          value={ngaySinh}
          onChange={(e) => setNgaySinh(e.target.value)}
        />
        {errors.ngaySinh && <p className="text-red-500 text-sm">{errors.ngaySinh}</p>}
      </div>

      {/* Mật Khẩu */}
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">
          Mật Khẩu
        </label>
        <div className="relative">
          <input
            type={showPassword ? "text" : "password"}
            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
            value={matKhau}
            onChange={(e) => setMatKhau(e.target.value)}
          />
          <button
            type="button"
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? (
              <EyeOffIcon className="h-6 w-6" />
            ) : (
              <EyeIcon className="h-6 w-6" />
            )}
          </button>
        </div>
        {errors.matKhau && <p className="text-red-500 text-sm">{errors.matKhau}</p>}
      </div>

      {/* Submit Button */}
      <div className="col-span-2 text-center mt-6">
        <button
          type="button"
          onClick={handleSubmit}
          className="bg-indigo-600 text-white py-2 px-6 rounded-xl shadow-md hover:bg-indigo-700 transition-all"
        >
          Thêm Nhân Viên
        </button>
      </div>
    </form>
  );
};

export default AddNhanVien;
