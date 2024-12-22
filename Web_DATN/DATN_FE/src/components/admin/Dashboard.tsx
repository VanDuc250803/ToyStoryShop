import React, { useEffect, useState } from "react";
import { DeleteProduct, getAllProducts, hideProduct } from "../../service/products";
import { Iproduct } from "../../interface/products";
import { Popconfirm, Pagination, Input, Button } from "antd";
import { useNavigate } from "react-router-dom";

const { Search } = Input;

const Dashboard = () => {
  const [products, setProducts] = useState<Iproduct[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 7;

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await getAllProducts();
        setProducts(data);
      } catch (error) {
        console.error("Lỗi khi lấy danh sách sản phẩm:", error);
      }
    };
    fetchData();
  }, []);

  const delProduct = async (id: string, e?: React.MouseEvent<HTMLElement>) => {
    if (e) {
      e.stopPropagation();
    }
    try {
      await DeleteProduct(id);
      const newProducts = products.filter((product) => product._id !== id);
      setProducts(newProducts);
    } catch (error) {
      console.error("Lỗi khi xóa sản phẩm:", error);
    }
  };

  const hideProductHandler = async (id: string, e?: React.MouseEvent<HTMLElement>) => {
    if (e) {
      e.stopPropagation();
    }
    try {
      await hideProduct(id);
      const updatedProducts = products.map((product) => {
        if (product._id === id) {
          return { ...product, isHidden: true } as Iproduct; // Cập nhật trạng thái ẩn
        }
        return product;
      });
      setProducts(updatedProducts);
  
      // Sau khi ẩn thành công, điều hướng sang màn hình danh sách sản phẩm ẩn
      navigate("/admin/dashboard/hidden-products");
    } catch (error) {
      console.error("Lỗi khi ẩn sản phẩm:", error);
    }
  };
  
  const updateProduct = (id: string, e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    navigate(`/admin/dashboard/${id}`);
  };

  const viewProductDetail = (id: string) => {
    navigate(`/admin/dashboard/product/details/${id}`);
  };

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    setCurrentPage(1);
  };

  const filteredProducts = products.filter((product) =>
    product.namePro.toLowerCase().includes(searchTerm.toLowerCase()) && !product.isHidden
  );

  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = filteredProducts.slice(
    indexOfFirstProduct,
    indexOfLastProduct
  );

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  // Chức năng điều hướng đến danh sách sản phẩm ẩn
  const goToHiddenProducts = () => {
    navigate("/admin/dashboard/hidden-products");
  };

  return (
    <div className="flex-1 p-4">
      <div className="mb-4 flex justify-between items-center">
        {/* Nút Danh sách ẩn */}
        <Button
          type="primary"
          onClick={goToHiddenProducts}
          className="mr-4"
        >
          Danh sách ẩn
        </Button>
        
        {/* Tìm kiếm */}
        <Search
          placeholder="Tìm kiếm sản phẩm"
          allowClear
          enterButton="Tìm kiếm"
          size="large"
          onChange={(e) => handleSearch(e.target.value)}
        />
      </div>
      <div className="overflow-x-auto shadow-md sm:rounded-lg">
        <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
          <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
            <tr>
              <th className="px-6 py-3" style={{ width: "200px" }}>Tên sản phẩm</th>
              <th className="px-6 py-3">Trạng thái</th>
              <th className="px-6 py-3">Giá</th>
              <th className="px-6 py-3">Số lượng</th>
              <th className="px-6 py-3">Danh mục</th>
              <th className="px-6 py-3">Hình ảnh</th>
              <th className="px-6 py-3">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {currentProducts.map((product) => (
              <tr
                key={product._id}
                className="odd:bg-white even:bg-gray-50 border-b dark:bg-gray-900 dark:border-gray-700"
                onClick={() => viewProductDetail(product._id)}
              >
                <td
                  className="px-6 py-4 font-medium text-gray-900 dark:text-white"
                  style={{
                    width: "200px",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    display: "-webkit-box",
                    WebkitBoxOrient: "vertical",
                    WebkitLineClamp: 2,
                  }}
                >
                  {product.namePro}
                </td>

                <td className="px-6 py-4">
                  {product.quantity > 0 ? "Còn hàng" : "Hết hàng"}
                </td>
                <td className="px-6 py-4">{product.price}VND</td>
                <td className="px-6 py-4">{product.quantity}</td>
                <td className="px-6 py-4">{product.listPro || "Chưa phân loại"}</td>
                <td className="px-6 py-4">
                  {product.imgPro ? (
                    Array.isArray(product.imgPro) && product.imgPro.length > 0 ? (
                      <img
                        src={product.imgPro[0]}
                        alt={`Product ${product.namePro}`}
                        className="w-16 h-16 object-cover rounded-lg"
                      />
                    ) : (
                      <span>Không có hình ảnh</span>
                    )
                  ) : (
                    <span>Không có hình ảnh</span>
                  )}
                </td>
                <td className="px-6 py-4">
                  <div className="flex">
                    <button
                      onClick={(e) => updateProduct(product._id, e)}
                      className="text-white bg-blue-600 hover:bg-blue-800 font-medium rounded-lg px-4 py-2 me-2"
                    >
                      Edit
                    </button>
                    <button
                      onClick={(e) => hideProductHandler(product._id, e)}
                      className="text-white bg-yellow-600 hover:bg-yellow-800 font-medium rounded-lg px-4 py-2 me-2"
                    >
                      Ẩn
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="mt-4 flex justify-center">
        <Pagination
          current={currentPage}
          pageSize={productsPerPage}
          total={filteredProducts.length}
          onChange={handlePageChange}
        />
      </div>
    </div>
  );
};

export default Dashboard;
