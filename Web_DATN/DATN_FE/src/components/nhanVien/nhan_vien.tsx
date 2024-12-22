import React, { useState, useEffect } from "react";
import AddNhanVien from "./add_nhanVien";
import UpdateNhanVien from "./updateNhanVien";
import DeleteNhanVien from "./DeleteNhanVien";
import { IoArrowBackCircle } from "react-icons/io5";  // Thêm import cho mũi tên

interface NhanVien {
  maNhanVien: string;
  hoTen: string;
  soDienThoai: string;
  ngaySinh: string;
  matKhau: string;
}

const NhanVien: React.FC = () => {
  const [nhanVienList, setNhanVienList] = useState<NhanVien[]>([]);
  const [selectedNhanVien, setSelectedNhanVien] = useState<NhanVien | null>(null);
  const [currentView, setCurrentView] = useState<"list" | "add" | "update" | null>("list");
  // Hàm tải dữ liệu từ Local Storage
  useEffect(() => {
    const storedNhanVienList = localStorage.getItem("nhanVienList");
    if (storedNhanVienList) {
      setNhanVienList(JSON.parse(storedNhanVienList));
    }
  }, []);

   // Hàm lưu dữ liệu vào Local Storage
   const saveToLocalStorage = (list: NhanVien[]) => {
    localStorage.setItem("nhanVienList", JSON.stringify(list));
  };

  const handleAddNhanVien = (newNhanVien: NhanVien) => {
    const updatedList = [...nhanVienList, newNhanVien];
    setNhanVienList(updatedList);
    saveToLocalStorage(updatedList); // Lưu vào Local Storage
    setCurrentView("list");
  };

  const handleUpdateNhanVien = (updatedNhanVien: NhanVien) => {
    const updatedList = nhanVienList.map((nv) =>
      nv.maNhanVien === updatedNhanVien.maNhanVien ? updatedNhanVien : nv
    );
    setNhanVienList(updatedList);
    saveToLocalStorage(updatedList); // Lưu vào Local Storage
    setSelectedNhanVien(null);
    setCurrentView("list");
  };

  const handleDeleteNhanVien = (maNhanVien: string) => {
    const updatedList = nhanVienList.filter((nv) => nv.maNhanVien !== maNhanVien);
    setNhanVienList(updatedList);
    saveToLocalStorage(updatedList); // Lưu vào Local Storage
  };
  const handleGoBack = () => {
    const confirmExit = window.confirm("Bạn có chắc muốn thoát không?");
    if (confirmExit) {
      setCurrentView("list");
    }
  };

  return (
    <div className="p-6 max-w-full mx-auto bg-white shadow-xl rounded-lg">
      <h1 className="text-4xl font-semibold text-center mb-8 text-indigo-700">
        Quản Lý Nhân Viên
      </h1>

      {currentView === "list" && (
        <div>
          <div className="flex justify-between items-center mb-6">
            <button
              className="bg-indigo-600 text-white py-2 px-6 rounded-xl shadow-md hover:bg-indigo-700 transition-all"
              onClick={() => setCurrentView("add")}
            >
              + Thêm Nhân Viên
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full text-left border-collapse shadow-md rounded-lg">
              <thead className="bg-indigo-600 text-white">
                <tr>
                  <th className="px-6 py-3 text-sm font-semibold">Mã NV</th>
                  <th className="px-6 py-3 text-sm font-semibold">Họ và Tên</th>
                  <th className="px-6 py-3 text-sm font-semibold">Số Điện Thoại</th>
                  <th className="px-6 py-3 text-sm font-semibold">Ngày Sinh</th>
                  <th className="px-6 py-3 text-sm font-semibold">Hành Động</th>
                </tr>
              </thead>
              <tbody>
                {nhanVienList.length > 0 ? (
                  nhanVienList.map((nv) => (
                    <tr
                      key={nv.maNhanVien}
                      className="hover:bg-gray-100 border-b border-gray-200 transition-all"
                    >
                      <td className="px-6 py-4">{nv.maNhanVien}</td>
                      <td className="px-6 py-4">{nv.hoTen}</td>
                      <td className="px-6 py-4">{nv.soDienThoai}</td>
                      <td className="px-6 py-4">{nv.ngaySinh}</td>
                      <td className="px-6 py-4 flex space-x-4">
                        <button
                          className="bg-green-500 text-white py-2 px-4 rounded-xl shadow-md hover:bg-green-600 transition-all"
                          onClick={() => {
                            setSelectedNhanVien(nv);
                            setCurrentView("update");
                          }}
                        >
                          Edit
                        </button>
                        <DeleteNhanVien
                          onDelete={() => handleDeleteNhanVien(nv.maNhanVien)}
                        />
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={5} className="text-center py-6 text-gray-500">
                      Không có nhân viên nào.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {currentView === "add" && (
        <div className="bg-gray-100 p-6 rounded-lg shadow-lg">
          <div className="flex items-center mb-4">
            <button
              className="text-indigo-600 hover:text-indigo-800 text-3xl mr-4"
              onClick={handleGoBack}
            >
              <IoArrowBackCircle size={30} /> {/* Mũi tên to ra */}
            </button>
            <h2 className="text-2xl font-semibold">Thêm Nhân Viên</h2>
          </div>
          <AddNhanVien onAdd={handleAddNhanVien} />
        </div>
      )}

      {currentView === "update" && selectedNhanVien && (
        <div className="bg-gray-100 p-6 rounded-lg shadow-lg">
          <div className="flex items-center mb-4">
            <button
              className="text-indigo-600 hover:text-indigo-800 text-3xl mr-4"
              onClick={handleGoBack}
            >
              <IoArrowBackCircle size={30} /> {/* Mũi tên to ra */}
            </button>
            <h2 className="text-2xl font-semibold">Cập Nhật Nhân Viên</h2>
          </div>
          <UpdateNhanVien
            nhanVien={selectedNhanVien}
            onUpdate={handleUpdateNhanVien}
          />
        </div>
      )}
    </div>
  );
};

export default NhanVien;
