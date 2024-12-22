import React, { useEffect, useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { Icategory } from '../../interface/category';
import { Popconfirm, Pagination } from 'antd'; // Import Pagination component
import { delCategory, getAllCategories } from '../../service/category';
import LoadingComponent from '../Loading';

type Props = {};

const Listcategory = (props: Props) => {
  const [categories, setCategory] = useState<Icategory[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>(""); // Từ khóa tìm kiếm
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(false);
  const [currentPage, setCurrentPage] = useState<number>(1); // Trang hiện tại
  const [itemsPerPage] = useState<number>(9); // Số lượng sản phẩm mỗi trang

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const data = await getAllCategories();
        setCategory(data);
      } catch (error) {
        console.log(error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleDeleteCategory = async (id: string) => {
    try {
      await delCategory(id);
      const updatedCategories = categories.filter((category) => category._id !== id);
      setCategory(updatedCategories);
    } catch (error) {
      console.log('Error deleting category:', error);
    }
  };

  const updateCategory = (id: string) => {
    navigate(`updatecategory/${id}`);
  };

  // Lọc danh mục theo từ khóa
  const filteredCategories = categories.filter((category) =>
    category.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Tính toán các sản phẩm trong trang hiện tại
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = filteredCategories.slice(indexOfFirstItem, indexOfLastItem);

  // Thêm nút phân trang
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <>
      {loading && <LoadingComponent />}

      {/* Nút thêm mới danh mục */}
      <div className="mb-6 flex justify-start">
        <NavLink to={'/admin/addcategory'}>
          <button className="text-white bg-indigo-600 hover:bg-indigo-700 font-semibold rounded-lg text-base px-6 py-3 shadow-lg transition duration-300 ease-in-out">
            Thêm mới danh mục
          </button>
        </NavLink>
      </div>

      {/* Thanh tìm kiếm */}
      <div className="flex mb-6 justify-center w-full">
        <input
          type="text"
          placeholder="Tìm kiếm danh mục..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border border-gray-300 rounded-lg px-4 py-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 w-full max-w-lg"
        />
      </div>

      <div className="overflow-x-auto w-full bg-white shadow-lg rounded-lg">
        <table className="min-w-full table-auto text-center">
          <thead className="bg-indigo-600 text-white">
            <tr>
              <th className="px-6 py-4 text-sm font-semibold">Stt</th>
              <th className="px-6 py-4 text-sm font-semibold">Tên danh mục</th>
              <th className="px-6 py-4 text-sm font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody>
            {currentItems.map((category: Icategory, index: number) => (
              <tr
                className="bg-gray-50 hover:bg-gray-100 transition duration-300 ease-in-out"
                key={category._id}
              >
                <td className="px-6 py-4">{indexOfFirstItem + index + 1}</td>
                <td className="px-6 py-4 text-gray-700">{category.name}</td>
                <td className="px-6 py-4 flex justify-center items-center gap-4">
                  <button
                    onClick={() => updateCategory(category._id)}
                    className="text-white bg-blue-600 hover:bg-blue-700 font-semibold rounded-lg px-4 py-2 transition duration-200 ease-in-out"
                  >
                    Edit
                  </button>
                  {/* <Popconfirm
                    title="Xóa danh mục"
                    description="Bạn có chắc chắn muốn xóa danh mục này?"
                    onConfirm={() => handleDeleteCategory(category._id)}
                    okText="Có"
                    cancelText="Không"
                  >
                    <button className="text-white bg-red-600 hover:bg-red-700 font-semibold rounded-lg px-4 py-2 transition duration-200 ease-in-out">
                      Delete
                    </button>
                  </Popconfirm> */}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Phân trang đặt dưới cùng */}
      <div className="mt-6 w-full flex justify-center">
        <Pagination
          current={currentPage}
          pageSize={itemsPerPage}
          total={filteredCategories.length} // Tổng số danh mục đã lọc
          onChange={handlePageChange}
          showSizeChanger={false} // Ẩn chọn số lượng sản phẩm mỗi trang
          className="pagination-style"
        />
      </div>
    </>
  );
};

export default Listcategory;
