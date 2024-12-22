import React, { useState } from "react";
import { EyeIcon, EyeOffIcon } from "@heroicons/react/solid";

interface NhanVien {
  maNhanVien: string;
  hoTen: string;
  soDienThoai: string;
  ngaySinh: string;
  matKhau: string;
}

interface UpdateNhanVienProps {
  nhanVien: NhanVien;
  onUpdate: (updatedNhanVien: NhanVien) => void;
}

const UpdateNhanVien: React.FC<UpdateNhanVienProps> = ({ nhanVien, onUpdate }) => {
  const [formData, setFormData] = useState<NhanVien>(nhanVien);
  const [errors, setErrors] = useState<Partial<NhanVien>>({});
  const [showPassword, setShowPassword] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: "" }); // Xóa lỗi khi người dùng nhập
  };

  const handleUpdateNhanVien = () => {
    const { maNhanVien, hoTen, soDienThoai, ngaySinh, matKhau } = formData;
    const newErrors: Partial<NhanVien> = {};

    // Validate Mã nhân viên
    if (!maNhanVien) {
      newErrors.maNhanVien = "Mã nhân viên không được để trống.";
    }

    // Validate Họ và tên
    const hoTenRegex = /^[a-zA-Z\s]{6,}$/;
    if (!hoTen) {
      newErrors.hoTen = "Họ và tên không được để trống.";
    } else if (!hoTenRegex.test(hoTen)) {
      newErrors.hoTen = "Họ và tên phải có ít nhất 6 ký tự và không chứa số.";
    }

    // Validate Số điện thoại
    const phoneRegex = /^[0-9]{10}$/;
    if (!soDienThoai) {
      newErrors.soDienThoai = "Số điện thoại không được để trống.";
    } else if (!phoneRegex.test(soDienThoai)) {
      newErrors.soDienThoai = "Số điện thoại phải gồm 10 chữ số.";
    }

    // Validate Ngày sinh
    if (!ngaySinh) {
      newErrors.ngaySinh = "Ngày sinh không được để trống.";
    } else if (new Date(ngaySinh) >= new Date()) {
      newErrors.ngaySinh = "Ngày sinh phải là một ngày trong quá khứ.";
    }

    // Validate Mật khẩu
    if (!matKhau) {
      newErrors.matKhau = "Mật khẩu không được để trống.";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors); // Cập nhật lỗi
      return;
    }

    // Reset lỗi và cập nhật
    setErrors({});
    onUpdate(formData);
    alert("Cập nhật nhân viên thành công!");
  };

  return (
    <div className="max-w-4xl mx-auto p-6 bg-white rounded-lg shadow-md">
      <h2 className="font-semibold text-2xl text-center text-gray-700 mb-6">Cập Nhật Thông Tin Nhân Viên</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {["maNhanVien", "hoTen", "soDienThoai", "ngaySinh", "matKhau"].map((field) => (
          <div key={field} className="flex flex-col">
            <label className="block text-gray-700 font-medium mb-2">
              {field === "maNhanVien"
                ? "Mã Nhân Viên"
                : field === "hoTen"
                ? "Họ và Tên"
                : field === "soDienThoai"
                ? "Số Điện Thoại"
                : field === "ngaySinh"
                ? "Ngày Sinh"
                : "Mật Khẩu"}
            </label>
            <div className="relative">
              <input
                type={
                  field === "matKhau"
                    ? showPassword
                      ? "text"
                      : "password"
                    : field === "ngaySinh"
                    ? "date"
                    : "text"
                }
                name={field}
                className={`w-full h-12 border ${
                  errors[field as keyof NhanVien] ? "border-red-500" : "border-gray-300"
                } rounded px-4 focus:outline-none focus:ring-2 ${
                  errors[field as keyof NhanVien] ? "focus:ring-red-500" : "focus:ring-blue-500"
                }`}
                value={(formData as any)[field]}
                onChange={handleInputChange}
              />
              {field === "matKhau" && (
                <button
                  type="button"
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOffIcon className="h-5 w-5 text-gray-500" />
                  ) : (
                    <EyeIcon className="h-5 w-5 text-gray-500" />
                  )}
                </button>
              )}
            </div>
            {errors[field as keyof NhanVien] && (
              <p className="text-red-500 text-sm mt-2">{errors[field as keyof NhanVien]}</p>
            )}
          </div>
        ))}

        <button
          className="bg-blue-600 text-white py-3 px-6 rounded-lg w-full hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 col-span-2"
          onClick={handleUpdateNhanVien}
        >
          Cập Nhật Nhân Viên
        </button>
      </div>
    </div>
  );
};

export default UpdateNhanVien;
